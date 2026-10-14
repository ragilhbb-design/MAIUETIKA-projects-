package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.EvaluationRecord
import com.example.data.model.SocraticSession
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val db: FirebaseFirestore,
    context: Context? = null
) {

    private val userSessionManager = context?.let { UserSessionManager(it) }
    private val localStorage = context?.let { LocalSessionStorage(it) }

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        context
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: userSessionManager?.getUserId()
            ?: "guest_user"
    }

    fun currentUserIdOrNull(): String? = auth.currentUser?.uid ?: userSessionManager?.getUserId()

    // Observe user profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val initialProfile = userSessionManager?.getUserProfile()
        emit(initialProfile)

        if (auth.currentUser != null) {
            val docRef = db.collection("users").document(userId)
            docRef.snapshots()
                .map { it.toObject(UserProfile::class.java) ?: initialProfile }
                .catch { e ->
                    Log.w("FirebaseRepository", "Firestore observeUserProfile fallback to local: ${e.message}")
                    emit(initialProfile)
                }
                .collect { emit(it) }
        }
    }

    // Save or update user profile
    suspend fun saveUserProfile(name: String, realityScore: Double = 50.0) {
        userSessionManager?.saveUser(name)
        userSessionManager?.setRealityScore(realityScore)

        if (auth.currentUser != null) {
            val uid = requireUserId()
            try {
                val docRef = db.collection("users").document(uid)
                val doc = docRef.get().await()
                if (!doc.exists()) {
                    val data = mapOf(
                        "nama" to name.ifBlank { "Pencari Kebijakan" },
                        "createdAt" to FieldValue.serverTimestamp(),
                        "lastActive" to FieldValue.serverTimestamp(),
                        "skorRealitasTerakhir" to realityScore
                    )
                    docRef.set(data).await()
                } else {
                    val updates = mapOf(
                        "nama" to name.ifBlank { doc.getString("nama") ?: "Pencari Kebijakan" },
                        "lastActive" to FieldValue.serverTimestamp(),
                        "skorRealitasTerakhir" to realityScore
                    )
                    docRef.update(updates).await()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Firestore saveUserProfile sync skipped: ${e.message}")
            }
        }
    }

    // Observe all sessions
    fun observeSessions(): Flow<List<SocraticSession>> = flow {
        if (localStorage != null) {
            localStorage.sessionsFlow.collect { localList ->
                emit(localList)
            }
        } else if (auth.currentUser != null) {
            val uid = requireUserId()
            val path = "sessions"
            val query = db.collection("sessions")
                .whereEqualTo("uid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)

            query.snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        doc.toObject(SocraticSession::class.java)
                    }
                }
                .catch { e ->
                    if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                    throw e
                }
                .collect { emit(it) }
        } else {
            emit(emptyList())
        }
    }

    // Save new Socratic session
    suspend fun saveSession(session: SocraticSession): String {
        val uid = requireUserId()
        val sessionWithUid = if (session.uid.isBlank()) session.copy(uid = uid) else session

        // Save locally first
        val localId = localStorage?.saveSession(sessionWithUid) ?: sessionWithUid.id

        // Update user reality score based on deterministic RBSS
        val rbssScore = sessionWithUid.getParsedRbss().totalRealityScore
        userSessionManager?.setRealityScore(rbssScore)

        if (auth.currentUser != null) {
            try {
                val docRef = db.collection("sessions").document(localId)
                val payload = mapOf(
                    "uid" to uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "ceritaPengguna" to sessionWithUid.ceritaPengguna,
                    "diagnosis" to sessionWithUid.diagnosis,
                    "petaRealitas" to sessionWithUid.petaRealitas,
                    "analisisAkarMasalah" to sessionWithUid.analisisAkarMasalah,
                    "masalahUtama" to sessionWithUid.masalahUtama,
                    "tingkatRisiko" to sessionWithUid.tingkatRisiko,
                    "tingkatKepercayaanAI" to sessionWithUid.tingkatKepercayaanAI,
                    "skenario" to sessionWithUid.skenario,
                    "rekomendasiSkenario" to sessionWithUid.rekomendasiSkenario,
                    "zebra" to sessionWithUid.zebra,
                    "rmaData" to sessionWithUid.rmaData,
                    "hosData" to sessionWithUid.hosData,
                    "kkzData" to sessionWithUid.kkzData,
                    "rbssData" to sessionWithUid.rbssData,
                    "evaluasiMingguan" to null
                )
                docRef.set(payload).await()

                val userRef = db.collection("users").document(uid)
                userRef.update(
                    mapOf(
                        "skorRealitasTerakhir" to rbssScore,
                        "lastActive" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Firestore saveSession sync skipped: ${e.message}")
            }
        }

        return localId
    }

    // Update entire session after recalibration
    suspend fun updateSession(session: SocraticSession) {
        val uid = requireUserId()
        localStorage?.updateSession(session)

        val newScore = session.getParsedRbss().totalRealityScore
        userSessionManager?.setRealityScore(newScore)

        if (auth.currentUser != null && session.id.isNotBlank()) {
            try {
                val docRef = db.collection("sessions").document(session.id)
                val payload = mapOf(
                    "petaRealitas" to session.petaRealitas,
                    "analisisAkarMasalah" to session.analisisAkarMasalah,
                    "zebra" to session.zebra,
                    "rmaData" to session.rmaData,
                    "hosData" to session.hosData,
                    "kkzData" to session.kkzData,
                    "rbssData" to session.rbssData
                )
                docRef.update(payload).await()

                val userRef = db.collection("users").document(uid)
                userRef.update(
                    mapOf(
                        "skorRealitasTerakhir" to newScore,
                        "lastActive" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Firestore updateSession skipped: ${e.message}")
            }
        }
    }

    // Update ZEBRA status ("done" or "pending" or "failed") with real-world observation
    suspend fun updateZebraStatus(
        sessionId: String,
        newStatus: String,
        observation: String = "",
        scoreDelta: Int = 10
    ) {
        val uid = requireUserId()
        localStorage?.updateZebraExecution(sessionId, newStatus, observation)

        val currentScore = userSessionManager?.getRealityScore() ?: 50.0
        val finalDelta = if (newStatus == "done") scoreDelta.toDouble() else -2.0
        userSessionManager?.setRealityScore((currentScore + finalDelta).coerceIn(10.0, 100.0))

        if (auth.currentUser != null) {
            try {
                val docRef = db.collection("sessions").document(sessionId)
                val doc = docRef.get().await()
                if (doc.exists()) {
                    val currentZebra = doc.get("zebra") as? Map<*, *> ?: emptyMap<String, Any>()
                    val updatedZebra = HashMap(currentZebra)
                    updatedZebra["status"] = newStatus
                    updatedZebra["observasiHasil"] = observation
                    updatedZebra["tanggalEksekusi"] = System.currentTimeMillis()
                    docRef.update("zebra", updatedZebra).await()

                    val userRef = db.collection("users").document(uid)
                    userRef.update(
                        mapOf(
                            "skorRealitasTerakhir" to FieldValue.increment(finalDelta),
                            "lastActive" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Firestore updateZebraStatus skipped: ${e.message}")
            }
        }
    }

    // Save weekly evaluation
    suspend fun saveWeeklyEvaluation(sessionId: String, status: String, cerita: String) {
        val uid = requireUserId()
        localStorage?.saveWeeklyEvaluation(sessionId, status, cerita)

        val scoreBonus = when (status) {
            "lebih_baik" -> 4.5
            "sama" -> 0.5
            "lebih_buruk" -> -3.0
            else -> 0.0
        }
        val currentScore = userSessionManager?.getRealityScore() ?: 50.0
        userSessionManager?.setRealityScore((currentScore + scoreBonus).coerceIn(10.0, 100.0))

        if (auth.currentUser != null) {
            try {
                val evalRef = db.collection("evaluations").document()
                val evalPayload = mapOf(
                    "uid" to uid,
                    "sessionId" to sessionId,
                    "status" to status,
                    "cerita" to cerita,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                evalRef.set(evalPayload).await()

                val sessionRef = db.collection("sessions").document(sessionId)
                val sessionEvalData = mapOf(
                    "status" to status,
                    "cerita" to cerita,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                sessionRef.update("evaluasiMingguan", sessionEvalData).await()

                val userRef = db.collection("users").document(uid)
                userRef.update(
                    mapOf(
                        "skorRealitasTerakhir" to FieldValue.increment(scoreBonus),
                        "lastActive" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Firestore saveWeeklyEvaluation skipped: ${e.message}")
            }
        }
    }

    // Observe evaluations
    fun observeEvaluations(): Flow<List<EvaluationRecord>> = flow {
        if (localStorage != null) {
            localStorage.evaluationsFlow.collect { list ->
                emit(list)
            }
        } else if (auth.currentUser != null) {
            val uid = requireUserId()
            val path = "evaluations"
            val query = db.collection("evaluations")
                .whereEqualTo("uid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)

            query.snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { it.toObject(EvaluationRecord::class.java) }
                }
                .catch { e ->
                    if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                    throw e
                }
                .collect { emit(it) }
        } else {
            emit(emptyList())
        }
    }
}
