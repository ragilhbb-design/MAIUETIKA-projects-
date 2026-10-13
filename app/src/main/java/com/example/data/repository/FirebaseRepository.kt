package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.EvaluationRecord
import com.example.data.model.SocraticSession
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyEvaluationData
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

class FirebaseRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in before accessing Firestore.")
    }

    fun currentUserIdOrNull(): String? = auth.currentUser?.uid

    // Observe user profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emit(null)
        val docRef = db.collection("users").document(userId)
        docRef.snapshots()
            .map { it.toObject(UserProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, path)
                throw e
            }
            .collect { emit(it) }
    }

    // Save or update user profile
    suspend fun saveUserProfile(name: String, realityScore: Double = 50.0) {
        val uid = requireUserId()
        val path = "users/$uid"
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
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    // Observe all sessions for the current user in real-time
    fun observeSessions(): Flow<List<SocraticSession>> = flow {
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
    }

    // Save new Socratic session
    suspend fun saveSession(session: SocraticSession): String {
        val uid = requireUserId()
        val path = "sessions"
        try {
            val docRef = db.collection("sessions").document()
            val payload = mapOf(
                "uid" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "ceritaPengguna" to session.ceritaPengguna,
                "diagnosis" to session.diagnosis,
                "petaRealitas" to session.petaRealitas,
                "analisisAkarMasalah" to session.analisisAkarMasalah,
                "masalahUtama" to session.masalahUtama,
                "tingkatRisiko" to session.tingkatRisiko,
                "tingkatKepercayaanAI" to session.tingkatKepercayaanAI,
                "skenario" to session.skenario,
                "rekomendasiSkenario" to session.rekomendasiSkenario,
                "zebra" to session.zebra,
                "evaluasiMingguan" to null
            )
            docRef.set(payload).await()

            // Update user reality score
            val avgScore = session.getParsedRealityMap().calculateAverageScore()
            val userRef = db.collection("users").document(uid)
            userRef.update(
                mapOf(
                    "skorRealitasTerakhir" to avgScore,
                    "lastActive" to FieldValue.serverTimestamp()
                )
            ).await()

            return docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    // Update ZEBRA status ("done" or "pending" or "failed")
    suspend fun updateZebraStatus(sessionId: String, newStatus: String, scoreDelta: Int) {
        val uid = requireUserId()
        val path = "sessions/$sessionId"
        try {
            val docRef = db.collection("sessions").document(sessionId)
            val doc = docRef.get().await()
            if (!doc.exists()) return

            val currentZebra = doc.get("zebra") as? Map<*, *> ?: emptyMap<String, Any>()
            val updatedZebra = HashMap(currentZebra)
            updatedZebra["status"] = newStatus

            docRef.update("zebra", updatedZebra).await()

            // Update user's score if completed
            if (newStatus == "done") {
                val userRef = db.collection("users").document(uid)
                userRef.update(
                    mapOf(
                        "skorRealitasTerakhir" to FieldValue.increment(scoreDelta.toDouble()),
                        "lastActive" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    // Save weekly evaluation
    suspend fun saveWeeklyEvaluation(sessionId: String, status: String, cerita: String) {
        val uid = requireUserId()
        val evalPath = "evaluations"
        val sessionPath = "sessions/$sessionId"
        try {
            // 1. Add to evaluations collection
            val evalRef = db.collection("evaluations").document()
            val evalPayload = mapOf(
                "uid" to uid,
                "sessionId" to sessionId,
                "status" to status,
                "cerita" to cerita,
                "createdAt" to FieldValue.serverTimestamp()
            )
            evalRef.set(evalPayload).await()

            // 2. Update session with evaluasiMingguan
            val sessionRef = db.collection("sessions").document(sessionId)
            val sessionEvalData = mapOf(
                "status" to status,
                "cerita" to cerita,
                "createdAt" to FieldValue.serverTimestamp()
            )
            sessionRef.update("evaluasiMingguan", sessionEvalData).await()

            // 3. Touch user lastActive
            val userRef = db.collection("users").document(uid)
            val scoreBonus = when (status) {
                "lebih_baik" -> 5.0
                "sama" -> 1.0
                "lebih_buruk" -> -2.0
                else -> 0.0
            }
            userRef.update(
                mapOf(
                    "skorRealitasTerakhir" to FieldValue.increment(scoreBonus),
                    "lastActive" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, evalPath)
            throw e
        }
    }

    // Observe evaluations for the user
    fun observeEvaluations(): Flow<List<EvaluationRecord>> = flow {
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
    }
}
