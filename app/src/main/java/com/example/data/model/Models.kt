package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.squareup.moshi.JsonClass

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val nama: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val lastActive: Timestamp? = null,
    val skorRealitasTerakhir: Double = 50.0
)

@IgnoreExtraProperties
@JsonClass(generateAdapter = true)
data class RealityMap(
    val keuangan: Int = 50,
    val keluarga: Int = 50,
    val karier: Int = 50,
    val relasi: Int = 50,
    val tujuan: Int = 50,
    val risiko: Int = 50,
    val psikologi: Int = 50,
    val kesehatan: Int = 50
) {
    fun toMap(): Map<String, Any> = mapOf(
        "keuangan" to keuangan,
        "keluarga" to keluarga,
        "karier" to karier,
        "relasi" to relasi,
        "tujuan" to tujuan,
        "risiko" to risiko,
        "psikologi" to psikologi,
        "kesehatan" to kesehatan
    )

    fun calculateAverageScore(): Double {
        val total = keuangan + keluarga + karier + relasi + tujuan + (100 - risiko) + psikologi + kesehatan
        return total / 8.0
    }
}

@IgnoreExtraProperties
@JsonClass(generateAdapter = true)
data class ScenarioItem(
    val nama: String = "",
    val deskripsi: String = "",
    val probabilitas: Int = 50,
    val risiko: Int = 50,
    val dampak: Int = 50
) {
    fun toMap(): Map<String, Any> = mapOf(
        "nama" to nama,
        "deskripsi" to deskripsi,
        "probabilitas" to probabilitas,
        "risiko" to risiko,
        "dampak" to dampak
    )
}

@IgnoreExtraProperties
@JsonClass(generateAdapter = true)
data class ZebraAction(
    val instruksi: String = "",
    val dampakPoin: Int = 10,
    val status: String = "pending" // "pending" | "done" | "failed"
) {
    fun toMap(): Map<String, Any> = mapOf(
        "instruksi" to instruksi,
        "dampakPoin" to dampakPoin,
        "status" to status
    )
}

@IgnoreExtraProperties
data class WeeklyEvaluationData(
    val status: String = "sama", // "lebih_baik" | "sama" | "lebih_buruk"
    val cerita: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "status" to status,
        "cerita" to cerita,
        "createdAt" to createdAt
    )
}

@IgnoreExtraProperties
data class SocraticSession(
    @DocumentId val id: String = "",
    val uid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    val ceritaPengguna: String = "",
    val diagnosis: String = "",
    val petaRealitas: Map<String, Any> = emptyMap(),
    val analisisAkarMasalah: List<String> = emptyList(),
    val masalahUtama: String = "",
    val tingkatRisiko: Double = 0.0,
    val tingkatKepercayaanAI: Double = 0.0,
    val skenario: List<Map<String, Any>> = emptyList(),
    val rekomendasiSkenario: String = "",
    val zebra: Map<String, Any> = emptyMap(),
    val evaluasiMingguan: Map<String, Any?>? = null
) {
    fun getParsedRealityMap(): RealityMap {
        return RealityMap(
            keuangan = (petaRealitas["keuangan"] as? Number)?.toInt() ?: 50,
            keluarga = (petaRealitas["keluarga"] as? Number)?.toInt() ?: 50,
            karier = (petaRealitas["karier"] as? Number)?.toInt() ?: 50,
            relasi = (petaRealitas["relasi"] as? Number)?.toInt() ?: 50,
            tujuan = (petaRealitas["tujuan"] as? Number)?.toInt() ?: 50,
            risiko = (petaRealitas["risiko"] as? Number)?.toInt() ?: 50,
            psikologi = (petaRealitas["psikologi"] as? Number)?.toInt() ?: 50,
            kesehatan = (petaRealitas["kesehatan"] as? Number)?.toInt() ?: 50
        )
    }

    fun getParsedScenarios(): List<ScenarioItem> {
        return skenario.map { map ->
            ScenarioItem(
                nama = map["nama"] as? String ?: "",
                deskripsi = map["deskripsi"] as? String ?: "",
                probabilitas = (map["probabilitas"] as? Number)?.toInt() ?: 50,
                risiko = (map["risiko"] as? Number)?.toInt() ?: 50,
                dampak = (map["dampak"] as? Number)?.toInt() ?: 50
            )
        }
    }

    fun getParsedZebra(): ZebraAction {
        return ZebraAction(
            instruksi = zebra["instruksi"] as? String ?: "",
            dampakPoin = (zebra["dampakPoin"] as? Number)?.toInt() ?: 10,
            status = zebra["status"] as? String ?: "pending"
        )
    }
}

@IgnoreExtraProperties
data class EvaluationRecord(
    @DocumentId val id: String = "",
    val uid: String = "",
    val sessionId: String = "",
    val status: String = "", // "lebih_baik" | "sama" | "lebih_buruk"
    val cerita: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
