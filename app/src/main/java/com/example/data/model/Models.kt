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

enum class InfoProvenance {
    USER_REPORTED,
    SYSTEM_CALCULATED,
    DIRECTLY_OBSERVED,
    EXTERNAL_SOURCE,
    AI_INTERPRETATION,
    UNVERIFIED;

    fun getDisplayName(): String = when (this) {
        USER_REPORTED -> "Dilaporkan Pengguna"
        SYSTEM_CALCULATED -> "Dihitung dari Data"
        DIRECTLY_OBSERVED -> "Diamati Langsung"
        EXTERNAL_SOURCE -> "Sumber Eksternal"
        AI_INTERPRETATION -> "Interpretasi AI"
        UNVERIFIED -> "Belum Diverifikasi"
    }
}

@IgnoreExtraProperties
@JsonClass(generateAdapter = true)
data class ConditionItem(
    val dimensi: String = "",
    val deskripsi: String = "",
    val skor: Int = 50,
    val provenance: String = InfoProvenance.USER_REPORTED.name,
    val verifikasiStatus: String = "Belum diverifikasi"
) {
    fun toMap(): Map<String, Any> = mapOf(
        "dimensi" to dimensi,
        "deskripsi" to deskripsi,
        "skor" to skor,
        "provenance" to provenance,
        "verifikasiStatus" to verifikasiStatus
    )
}

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
data class RmaAnalysis(
    val problemStatement: String = "",
    val declaredInterests: List<String> = emptyList(),
    val confirmedInterests: List<String> = emptyList(),
    val possibleInterests: List<String> = emptyList(),
    val observedConditions: List<String> = emptyList(),
    val mechanismHypotheses: List<String> = emptyList(),
    val supportingEvidence: List<String> = emptyList(),
    val missingInformation: List<String> = emptyList(),
    val uncertainties: List<String> = emptyList(),
    val potentialInterventionPoints: List<String> = emptyList(),
    val possibleConsequences: List<String> = emptyList(),
    val nextQuestions: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "problemStatement" to problemStatement,
        "declaredInterests" to declaredInterests,
        "confirmedInterests" to confirmedInterests,
        "possibleInterests" to possibleInterests,
        "observedConditions" to observedConditions,
        "mechanismHypotheses" to mechanismHypotheses,
        "supportingEvidence" to supportingEvidence,
        "missingInformation" to missingInformation,
        "uncertainties" to uncertainties,
        "potentialInterventionPoints" to potentialInterventionPoints,
        "possibleConsequences" to possibleConsequences,
        "nextQuestions" to nextQuestions
    )
}

@IgnoreExtraProperties
@JsonClass(generateAdapter = true)
data class HosAnalysis(
    val currentConditions: List<ConditionItem> = emptyList(),
    val environmentalFactors: List<String> = emptyList(),
    val detectedChanges: List<String> = emptyList(),
    val missingData: List<String> = emptyList(),
    val inconsistencies: List<String> = emptyList(),
    val adequacyScore: Int = 65,
    val itemsToObserve: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "currentConditions" to currentConditions.map { it.toMap() },
        "environmentalFactors" to environmentalFactors,
        "detectedChanges" to detectedChanges,
        "missingData" to missingData,
        "inconsistencies" to inconsistencies,
        "adequacyScore" to adequacyScore,
        "itemsToObserve" to itemsToObserve
    )
}

@IgnoreExtraProperties
@JsonClass(generateAdapter = true)
data class KkzCalculation(
    val readinessIndex: Int = 65,
    val internalResourceCapacity: String = "Cukup",
    val mentalEnergyScore: Int = 60,
    val timeAvailability: String = "1-2 jam/hari",
    val externalFrictionLevel: String = "Sedang",
    val structuralConstraints: List<String> = emptyList(),
    val dependencyRisks: List<String> = emptyList(),
    val readinessVerdict: String = "Kondisi mendukung tindakan bertahap (Micro-Action)"
) {
    fun toMap(): Map<String, Any> = mapOf(
        "readinessIndex" to readinessIndex,
        "internalResourceCapacity" to internalResourceCapacity,
        "mentalEnergyScore" to mentalEnergyScore,
        "timeAvailability" to timeAvailability,
        "externalFrictionLevel" to externalFrictionLevel,
        "structuralConstraints" to structuralConstraints,
        "dependencyRisks" to dependencyRisks,
        "readinessVerdict" to readinessVerdict
    )
}

@IgnoreExtraProperties
@JsonClass(generateAdapter = true)
data class RbssScore(
    val totalRealityScore: Double = 50.0,
    val informationAdequacyScore: Double = 50.0,
    val alignmentScore: Double = 50.0,
    val executionScore: Double = 50.0,
    val resilienceScore: Double = 50.0,
    val scoreChangeReason: String = "Penetapan diagnosis awal",
    val auditTrail: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "totalRealityScore" to totalRealityScore,
        "informationAdequacyScore" to informationAdequacyScore,
        "alignmentScore" to alignmentScore,
        "executionScore" to executionScore,
        "resilienceScore" to resilienceScore,
        "scoreChangeReason" to scoreChangeReason,
        "auditTrail" to auditTrail
    )
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
    val tujuan: String = "",
    val alasanPemilihan: String = "",
    val kondisiMendasari: String = "",
    val sumberDayaDiperlukan: String = "",
    val kendala: String = "",
    val risiko: String = "",
    val indikatorHasil: String = "",
    val jangkaWaktuEvaluasi: String = "",
    val kondisiPeninjauan: String = "",
    val dampakPoin: Int = 10,
    val status: String = "pending", // "pending" | "done" | "failed"
    val observasiHasil: String = "",
    val tanggalEksekusi: Long? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "instruksi" to instruksi,
        "tujuan" to tujuan,
        "alasanPemilihan" to alasanPemilihan,
        "kondisiMendasari" to kondisiMendasari,
        "sumberDayaDiperlukan" to sumberDayaDiperlukan,
        "kendala" to kendala,
        "risiko" to risiko,
        "indikatorHasil" to indikatorHasil,
        "jangkaWaktuEvaluasi" to jangkaWaktuEvaluasi,
        "kondisiPeninjauan" to kondisiPeninjauan,
        "dampakPoin" to dampakPoin,
        "status" to status,
        "observasiHasil" to observasiHasil,
        "tanggalEksekusi" to tanggalEksekusi
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
    val evaluasiMingguan: Map<String, Any?>? = null,
    val rmaData: Map<String, Any> = emptyMap(),
    val hosData: Map<String, Any> = emptyMap(),
    val kkzData: Map<String, Any> = emptyMap(),
    val rbssData: Map<String, Any> = emptyMap()
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
            tujuan = zebra["tujuan"] as? String ?: "Verifikasi hipotesis aksi di dunia nyata",
            alasanPemilihan = zebra["alasanPemilihan"] as? String ?: "Aksi dengan friksi terkecil dan dampak terukur",
            kondisiMendasari = zebra["kondisiMendasari"] as? String ?: "Kondisi saat ini membutuhkan pembuktian empiris langsung",
            sumberDayaDiperlukan = zebra["sumberDayaDiperlukan"] as? String ?: "Waktu 20-30 menit, catatan",
            kendala = zebra["kendala"] as? String ?: "Distraksi kognitif atau penundaan",
            risiko = zebra["risiko"] as? String ?: "Ketidaknyamanan menghadapi kenyataan",
            indikatorHasil = zebra["indikatorHasil"] as? String ?: "Catatan observasi konkret terkumpul",
            jangkaWaktuEvaluasi = zebra["jangkaWaktuEvaluasi"] as? String ?: "24 Jam",
            kondisiPeninjauan = zebra["kondisiPeninjauan"] as? String ?: "Bila muncul resistensi tak terduga",
            dampakPoin = (zebra["dampakPoin"] as? Number)?.toInt() ?: 10,
            status = zebra["status"] as? String ?: "pending",
            observasiHasil = zebra["observasiHasil"] as? String ?: "",
            tanggalEksekusi = (zebra["tanggalEksekusi"] as? Number)?.toLong()
        )
    }

    fun getParsedRma(): RmaAnalysis {
        if (rmaData.isEmpty()) {
            return RmaAnalysis(
                problemStatement = masalahUtama.ifBlank { ceritaPengguna.take(100) },
                mechanismHypotheses = analisisAkarMasalah,
                observedConditions = listOf("Gejala awal dilaporkan oleh pengguna pada konsultasi"),
                uncertainties = listOf("Ketercukupan bukti faktual masih membutuhkan uji tindakan")
            )
        }
        @Suppress("UNCHECKED_CAST")
        return RmaAnalysis(
            problemStatement = rmaData["problemStatement"] as? String ?: masalahUtama,
            declaredInterests = (rmaData["declaredInterests"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            confirmedInterests = (rmaData["confirmedInterests"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            possibleInterests = (rmaData["possibleInterests"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            observedConditions = (rmaData["observedConditions"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            mechanismHypotheses = (rmaData["mechanismHypotheses"] as? List<*>)?.mapNotNull { it as? String } ?: analisisAkarMasalah,
            supportingEvidence = (rmaData["supportingEvidence"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            missingInformation = (rmaData["missingInformation"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            uncertainties = (rmaData["uncertainties"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            potentialInterventionPoints = (rmaData["potentialInterventionPoints"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            possibleConsequences = (rmaData["possibleConsequences"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            nextQuestions = (rmaData["nextQuestions"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        )
    }

    fun getParsedHos(): HosAnalysis {
        val conditionsRaw = (hosData["currentConditions"] as? List<*>)?.mapNotNull { item ->
            val m = item as? Map<*, *> ?: return@mapNotNull null
            ConditionItem(
                dimensi = m["dimensi"] as? String ?: "",
                deskripsi = m["deskripsi"] as? String ?: "",
                skor = (m["skor"] as? Number)?.toInt() ?: 50,
                provenance = m["provenance"] as? String ?: InfoProvenance.USER_REPORTED.name,
                verifikasiStatus = m["verifikasiStatus"] as? String ?: "Belum diverifikasi"
            )
        } ?: emptyList()

        @Suppress("UNCHECKED_CAST")
        return HosAnalysis(
            currentConditions = if (conditionsRaw.isNotEmpty()) conditionsRaw else buildDefaultConditions(),
            environmentalFactors = (hosData["environmentalFactors"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            detectedChanges = (hosData["detectedChanges"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            missingData = (hosData["missingData"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            inconsistencies = (hosData["inconsistencies"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            adequacyScore = (hosData["adequacyScore"] as? Number)?.toInt() ?: 65,
            itemsToObserve = (hosData["itemsToObserve"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        )
    }

    private fun buildDefaultConditions(): List<ConditionItem> {
        val rMap = getParsedRealityMap()
        return listOf(
            ConditionItem("Keuangan", "Estimasi stabilitas arus kas dan beban finansial", rMap.keuangan, InfoProvenance.USER_REPORTED.name),
            ConditionItem("Keluarga", "Dukungan dan dinamika lingkungan keluarga", rMap.keluarga, InfoProvenance.USER_REPORTED.name),
            ConditionItem("Karier", "Kejelasan arah profesional dan kapasitas kompetensi", rMap.karier, InfoProvenance.USER_REPORTED.name),
            ConditionItem("Relasi", "Kualitas hubungan sosial dan jaringan kerja", rMap.relasi, InfoProvenance.USER_REPORTED.name),
            ConditionItem("Tujuan", "Ketajaman kompas prioritas hidup", rMap.tujuan, InfoProvenance.USER_REPORTED.name),
            ConditionItem("Risiko", "Potensi kerentanan terhadap gejolak", rMap.risiko, InfoProvenance.SYSTEM_CALCULATED.name),
            ConditionItem("Psikologi", "Resiliensi mental dan kejernihan persepsi", rMap.psikologi, InfoProvenance.USER_REPORTED.name),
            ConditionItem("Kesehatan", "Kebugaran fisik dan energi biologis harian", rMap.kesehatan, InfoProvenance.USER_REPORTED.name)
        )
    }

    fun getParsedKkz(): KkzCalculation {
        @Suppress("UNCHECKED_CAST")
        return KkzCalculation(
            readinessIndex = (kkzData["readinessIndex"] as? Number)?.toInt() ?: (100 - tingkatRisiko.toInt()).coerceIn(20, 95),
            internalResourceCapacity = kkzData["internalResourceCapacity"] as? String ?: "Cukup untuk Aksi Terfokus",
            mentalEnergyScore = (kkzData["mentalEnergyScore"] as? Number)?.toInt() ?: 65,
            timeAvailability = kkzData["timeAvailability"] as? String ?: "30-60 menit/hari",
            externalFrictionLevel = kkzData["externalFrictionLevel"] as? String ?: "Sedang",
            structuralConstraints = (kkzData["structuralConstraints"] as? List<*>)?.mapNotNull { it as? String } ?: listOf("Keterbatasan waktu harian", "Resistensi kebiasaan lama"),
            dependencyRisks = (kkzData["dependencyRisks"] as? List<*>)?.mapNotNull { it as? String } ?: listOf("Ketergantungan pada respon pihak lain"),
            readinessVerdict = kkzData["readinessVerdict"] as? String ?: "Rekomendasi: Lakukan tindakan mikro ZEBRA untuk membuka kebuntuan tanpa mengambil risiko fatal."
        )
    }

    fun getParsedRbss(): RbssScore {
        @Suppress("UNCHECKED_CAST")
        val auditTrail = (rbssData["auditTrail"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val rMap = getParsedRealityMap()
        val defaultAvg = rMap.calculateAverageScore()

        return RbssScore(
            totalRealityScore = (rbssData["totalRealityScore"] as? Number)?.toDouble() ?: defaultAvg,
            informationAdequacyScore = (rbssData["informationAdequacyScore"] as? Number)?.toDouble() ?: 60.0,
            alignmentScore = (rbssData["alignmentScore"] as? Number)?.toDouble() ?: 65.0,
            executionScore = (rbssData["executionScore"] as? Number)?.toDouble() ?: (if (getParsedZebra().status == "done") 75.0 else 45.0),
            resilienceScore = (rbssData["resilienceScore"] as? Number)?.toDouble() ?: (100.0 - tingkatRisiko).coerceIn(20.0, 90.0),
            scoreChangeReason = rbssData["scoreChangeReason"] as? String ?: "Diagnosis Sokratik Terpetakan",
            auditTrail = if (auditTrail.isNotEmpty()) auditTrail else listOf("Inisialisasi sistem penilaian berbasis realitas objektif")
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
