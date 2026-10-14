package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.EvaluationRecord
import com.example.data.model.SocraticSession
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Date
import java.util.UUID

class LocalSessionStorage(private val context: Context) {

    private val sessionsFile = File(context.filesDir, "maieutika_sessions.json")
    private val evaluationsFile = File(context.filesDir, "maieutika_evaluations.json")

    private val _sessionsFlow = MutableStateFlow<List<SocraticSession>>(emptyList())
    val sessionsFlow: Flow<List<SocraticSession>> = _sessionsFlow.asStateFlow()

    private val _evaluationsFlow = MutableStateFlow<List<EvaluationRecord>>(emptyList())
    val evaluationsFlow: Flow<List<EvaluationRecord>> = _evaluationsFlow.asStateFlow()

    init {
        loadSessions()
        loadEvaluations()
    }

    @Synchronized
    fun saveSession(session: SocraticSession): String {
        val generatedId = if (session.id.isNotBlank()) session.id else "ses_" + UUID.randomUUID().toString().take(10)
        val sessionToSave = session.copy(
            id = generatedId,
            createdAt = session.createdAt ?: Timestamp.now()
        )

        val currentList = _sessionsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == sessionToSave.id }
        if (index >= 0) {
            currentList[index] = sessionToSave
        } else {
            currentList.add(0, sessionToSave)
        }

        _sessionsFlow.value = currentList
        persistSessions(currentList)
        return generatedId
    }

    @Synchronized
    fun updateSession(updatedSession: SocraticSession) {
        val currentList = _sessionsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedSession.id }
        if (index >= 0) {
            currentList[index] = updatedSession
            _sessionsFlow.value = currentList
            persistSessions(currentList)
        }
    }

    @Synchronized
    fun updateZebraStatus(sessionId: String, newStatus: String): SocraticSession? {
        val currentList = _sessionsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == sessionId }
        if (index < 0) return null

        val current = currentList[index]
        val updatedZebra = HashMap(current.zebra)
        updatedZebra["status"] = newStatus
        val updated = current.copy(zebra = updatedZebra)

        currentList[index] = updated
        _sessionsFlow.value = currentList
        persistSessions(currentList)
        return updated
    }

    @Synchronized
    fun updateZebraExecution(
        sessionId: String,
        newStatus: String,
        observation: String
    ): SocraticSession? {
        val currentList = _sessionsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == sessionId }
        if (index < 0) return null

        val current = currentList[index]
        val updatedZebra = HashMap(current.zebra)
        updatedZebra["status"] = newStatus
        updatedZebra["observasiHasil"] = observation
        updatedZebra["tanggalEksekusi"] = System.currentTimeMillis()

        val updated = current.copy(zebra = updatedZebra)
        currentList[index] = updated
        _sessionsFlow.value = currentList
        persistSessions(currentList)
        return updated
    }

    @Synchronized
    fun saveWeeklyEvaluation(sessionId: String, status: String, cerita: String): EvaluationRecord {
        val evalId = "eval_" + UUID.randomUUID().toString().take(10)
        val now = Timestamp.now()
        val newEval = EvaluationRecord(
            id = evalId,
            sessionId = sessionId,
            status = status,
            cerita = cerita,
            createdAt = now
        )

        // Add to evaluations
        val evalList = _evaluationsFlow.value.toMutableList()
        evalList.add(0, newEval)
        _evaluationsFlow.value = evalList
        persistEvaluations(evalList)

        // Update session's evaluasiMingguan
        val currentSessions = _sessionsFlow.value.toMutableList()
        val sIndex = currentSessions.indexOfFirst { it.id == sessionId }
        if (sIndex >= 0) {
            val session = currentSessions[sIndex]
            val evalMap: Map<String, Any?> = mapOf(
                "status" to status,
                "cerita" to cerita,
                "createdAt" to now
            )
            currentSessions[sIndex] = session.copy(evaluasiMingguan = evalMap)
            _sessionsFlow.value = currentSessions
            persistSessions(currentSessions)
        }

        return newEval
    }

    private fun loadSessions() {
        try {
            if (!sessionsFile.exists()) return
            val jsonStr = sessionsFile.readText()
            val array = JSONArray(jsonStr)
            val list = mutableListOf<SocraticSession>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(parseSession(obj))
            }
            _sessionsFlow.value = list
        } catch (e: Exception) {
            Log.e("LocalSessionStorage", "Failed to load sessions: ${e.message}", e)
        }
    }

    private fun persistSessions(list: List<SocraticSession>) {
        try {
            val array = JSONArray()
            for (session in list) {
                array.put(serializeSession(session))
            }
            sessionsFile.writeText(array.toString())
        } catch (e: Exception) {
            Log.e("LocalSessionStorage", "Failed to persist sessions: ${e.message}", e)
        }
    }

    private fun loadEvaluations() {
        try {
            if (!evaluationsFile.exists()) return
            val jsonStr = evaluationsFile.readText()
            val array = JSONArray(jsonStr)
            val list = mutableListOf<EvaluationRecord>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val millis = obj.optLong("createdAtMillis", System.currentTimeMillis())
                list.add(
                    EvaluationRecord(
                        id = obj.optString("id"),
                        uid = obj.optString("uid"),
                        sessionId = obj.optString("sessionId"),
                        status = obj.optString("status"),
                        cerita = obj.optString("cerita"),
                        createdAt = Timestamp(Date(millis))
                    )
                )
            }
            _evaluationsFlow.value = list
        } catch (e: Exception) {
            Log.e("LocalSessionStorage", "Failed to load evaluations: ${e.message}", e)
        }
    }

    private fun persistEvaluations(list: List<EvaluationRecord>) {
        try {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("uid", item.uid)
                    put("sessionId", item.sessionId)
                    put("status", item.status)
                    put("cerita", item.cerita)
                    put("createdAtMillis", item.createdAt?.toDate()?.time ?: System.currentTimeMillis())
                }
                array.put(obj)
            }
            evaluationsFile.writeText(array.toString())
        } catch (e: Exception) {
            Log.e("LocalSessionStorage", "Failed to persist evaluations: ${e.message}", e)
        }
    }

    private fun serializeSession(s: SocraticSession): JSONObject {
        return JSONObject().apply {
            put("id", s.id)
            put("uid", s.uid)
            put("createdAtMillis", s.createdAt?.toDate()?.time ?: System.currentTimeMillis())
            put("ceritaPengguna", s.ceritaPengguna)
            put("diagnosis", s.diagnosis)
            put("masalahUtama", s.masalahUtama)
            put("tingkatRisiko", s.tingkatRisiko)
            put("tingkatKepercayaanAI", s.tingkatKepercayaanAI)
            put("rekomendasiSkenario", s.rekomendasiSkenario)

            // Peta Realitas
            put("petaRealitas", JSONObject(s.petaRealitas))

            // Analisis Akar Masalah
            put("analisisAkarMasalah", JSONArray(s.analisisAkarMasalah))

            // Skenario
            val skenArray = JSONArray()
            for (sken in s.skenario) {
                skenArray.put(JSONObject(sken))
            }
            put("skenario", skenArray)

            // Zebra
            put("zebra", JSONObject(s.zebra))

            // Evaluasi Mingguan
            s.evaluasiMingguan?.let { evalMap ->
                val evalObj = JSONObject().apply {
                    put("status", evalMap["status"] ?: "")
                    put("cerita", evalMap["cerita"] ?: "")
                    val time = (evalMap["createdAt"] as? Timestamp)?.toDate()?.time
                        ?: System.currentTimeMillis()
                    put("createdAtMillis", time)
                }
                put("evaluasiMingguan", evalObj)
            }

            // MAIEUTIKA Architecture Components
            if (s.rmaData.isNotEmpty()) {
                put("rmaData", mapToJsonObject(s.rmaData))
            }
            if (s.hosData.isNotEmpty()) {
                put("hosData", mapToJsonObject(s.hosData))
            }
            if (s.kkzData.isNotEmpty()) {
                put("kkzData", mapToJsonObject(s.kkzData))
            }
            if (s.rbssData.isNotEmpty()) {
                put("rbssData", mapToJsonObject(s.rbssData))
            }
        }
    }

    private fun mapToJsonObject(map: Map<String, Any>): JSONObject {
        val json = JSONObject()
        for ((k, v) in map) {
            when (v) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    json.put(k, mapToJsonObject(v as Map<String, Any>))
                }
                is List<*> -> {
                    val arr = JSONArray()
                    for (item in v) {
                        when (item) {
                            is Map<*, *> -> {
                                @Suppress("UNCHECKED_CAST")
                                arr.put(mapToJsonObject(item as Map<String, Any>))
                            }
                            else -> arr.put(item)
                        }
                    }
                    json.put(k, arr)
                }
                else -> json.put(k, v)
            }
        }
        return json
    }

    private fun jsonObjectToMap(json: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = json.get(key)
            when (value) {
                is JSONObject -> map[key] = jsonObjectToMap(value)
                is JSONArray -> map[key] = jsonArrayToList(value)
                else -> map[key] = value
            }
        }
        return map
    }

    private fun jsonArrayToList(arr: JSONArray): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until arr.length()) {
            val item = arr.get(i)
            when (item) {
                is JSONObject -> list.add(jsonObjectToMap(item))
                is JSONArray -> list.add(jsonArrayToList(item))
                else -> list.add(item)
            }
        }
        return list
    }

    private fun parseSession(obj: JSONObject): SocraticSession {
        val millis = obj.optLong("createdAtMillis", System.currentTimeMillis())
        val petaRealitasMap = mutableMapOf<String, Any>()
        obj.optJSONObject("petaRealitas")?.let { pObj ->
            pObj.keys().forEach { k -> petaRealitasMap[k] = pObj.get(k) }
        }

        val akarList = mutableListOf<String>()
        obj.optJSONArray("analisisAkarMasalah")?.let { aArr ->
            for (i in 0 until aArr.length()) {
                akarList.add(aArr.getString(i))
            }
        }

        val skenarioList = mutableListOf<Map<String, Any>>()
        obj.optJSONArray("skenario")?.let { sArr ->
            for (i in 0 until sArr.length()) {
                val sObj = sArr.getJSONObject(i)
                val map = mutableMapOf<String, Any>()
                sObj.keys().forEach { k -> map[k] = sObj.get(k) }
                skenarioList.add(map)
            }
        }

        val zebraMap = mutableMapOf<String, Any>()
        obj.optJSONObject("zebra")?.let { zObj ->
            zObj.keys().forEach { k -> zebraMap[k] = zObj.get(k) }
        }

        var evalMap: Map<String, Any?>? = null
        obj.optJSONObject("evaluasiMingguan")?.let { eObj ->
            val eMillis = eObj.optLong("createdAtMillis", System.currentTimeMillis())
            evalMap = mapOf(
                "status" to eObj.optString("status"),
                "cerita" to eObj.optString("cerita"),
                "createdAt" to Timestamp(Date(eMillis))
            )
        }

        val rmaData = obj.optJSONObject("rmaData")?.let { jsonObjectToMap(it) } ?: emptyMap()
        val hosData = obj.optJSONObject("hosData")?.let { jsonObjectToMap(it) } ?: emptyMap()
        val kkzData = obj.optJSONObject("kkzData")?.let { jsonObjectToMap(it) } ?: emptyMap()
        val rbssData = obj.optJSONObject("rbssData")?.let { jsonObjectToMap(it) } ?: emptyMap()

        return SocraticSession(
            id = obj.optString("id"),
            uid = obj.optString("uid"),
            createdAt = Timestamp(Date(millis)),
            ceritaPengguna = obj.optString("ceritaPengguna"),
            diagnosis = obj.optString("diagnosis"),
            petaRealitas = petaRealitasMap,
            analisisAkarMasalah = akarList,
            masalahUtama = obj.optString("masalahUtama"),
            tingkatRisiko = obj.optDouble("tingkatRisiko", 0.0),
            tingkatKepercayaanAI = obj.optDouble("tingkatKepercayaanAI", 0.0),
            skenario = skenarioList,
            rekomendasiSkenario = obj.optString("rekomendasiSkenario"),
            zebra = zebraMap,
            evaluasiMingguan = evalMap,
            rmaData = rmaData,
            hosData = hosData,
            kkzData = kkzData,
            rbssData = rbssData
        )
    }
}
