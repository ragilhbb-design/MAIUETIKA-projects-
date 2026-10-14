package com.example.data.repository

import com.example.data.model.HosAnalysis
import com.example.data.model.KkzCalculation
import com.example.data.model.RbssScore
import com.example.data.model.RmaAnalysis
import com.example.data.model.ZebraAction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RbssEngine {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    /**
     * Menghitung skor realitas awal berdasarkan ketercukupan informasi,
     * keselarasan persepsi vs realitas, status eksekusi, dan resiliensi risiko.
     */
    fun calculateInitialRbss(
        rma: RmaAnalysis,
        hos: HosAnalysis,
        kkz: KkzCalculation,
        zebra: ZebraAction,
        tingkatRisiko: Double
    ): RbssScore {
        // 1. Information Adequacy Score (0 - 100)
        // Dihitung dari kelengkapan dimensi, minus penalti data yang hilang
        val baseAdequacy = hos.adequacyScore.toDouble()
        val missingDataPenalty = (hos.missingData.size * 4.0).coerceAtMost(25.0)
        val infoAdequacyScore = (baseAdequacy - missingDataPenalty).coerceIn(20.0, 95.0)

        // 2. Alignment Score (0 - 100)
        // Mengukur seberapa jernih persepsi pengguna vs kondisi objektif (kontradiksi/inkonsistensi)
        val inconsistencyPenalty = (hos.inconsistencies.size * 10.0).coerceAtMost(30.0)
        val confirmedInterestBonus = (rma.confirmedInterests.size * 5.0).coerceAtMost(20.0)
        val alignmentScore = (70.0 - inconsistencyPenalty + confirmedInterestBonus).coerceIn(25.0, 95.0)

        // 3. Execution Score (0 - 100)
        // Aksi nyata di dunia empiris: pending = baseline kesiapan, done = pembuktian
        val executionScore = when (zebra.status) {
            "done" -> 80.0
            "failed" -> 40.0
            else -> 50.0
        }

        // 4. Resilience Score (0 - 100)
        // Dihitung dari (100 - tingkat risiko) dikombinasikan dengan indeks kesiapan KKZ
        val riskFactor = (100.0 - tingkatRisiko).coerceIn(15.0, 90.0)
        val readinessFactor = kkz.readinessIndex.toDouble()
        val resilienceScore = ((riskFactor * 0.5) + (readinessFactor * 0.5)).coerceIn(20.0, 95.0)

        // Formula Total Reality Score:
        // 25% Informasi Adekuat + 25% Penyelarasan Fakta + 30% Eksekusi Nyata + 20% Resiliensi
        val totalScore = (
            (infoAdequacyScore * 0.25) +
            (alignmentScore * 0.25) +
            (executionScore * 0.30) +
            (resilienceScore * 0.20)
        )

        val timestamp = dateFormat.format(Date())
        val auditTrail = listOf(
            "[$timestamp] Inisialisasi Analisis Sistem: Skor Realitas Awal dihitung pada ${String.format(Locale.US, "%.1f", totalScore)} (Adekuasi: ${infoAdequacyScore.toInt()}, Penyelarasan: ${alignmentScore.toInt()}, Eksekusi Awal: ${executionScore.toInt()}, Resiliensi: ${resilienceScore.toInt()})"
        )

        return RbssScore(
            totalRealityScore = totalScore,
            informationAdequacyScore = infoAdequacyScore,
            alignmentScore = alignmentScore,
            executionScore = executionScore,
            resilienceScore = resilienceScore,
            scoreChangeReason = "Diagnosis Awal Sokratik & Pemetaan Realitas Terverifikasi",
            auditTrail = auditTrail
        )
    }

    /**
     * Memperbarui skor realitas saat tindakan nyata (ZEBRA) dieksekusi atau diobservasi di dunia nyata.
     * Mengeliminasi perubahan skor fiktif tanpa bukti empiris.
     */
    fun recalculateOnActionExecution(
        currentRbss: RbssScore,
        newStatus: String,
        observation: String,
        impactPoints: Int
    ): RbssScore {
        val timestamp = dateFormat.format(Date())
        val currentTrail = currentRbss.auditTrail.toMutableList()

        val newExecutionScore: Double
        val scoreDelta: Double
        val reason: String

        when (newStatus) {
            "done" -> {
                val observationQualityBonus = if (observation.trim().length > 30) 10.0 else 5.0
                newExecutionScore = (currentRbss.executionScore + impactPoints.toDouble() + observationQualityBonus).coerceAtMost(100.0)
                scoreDelta = (newExecutionScore - currentRbss.executionScore) * 0.30
                reason = "Tindakan nyata ZEBRA telah dilaksanakan dan hasil empiris tercatat di dunia nyata"
                currentTrail.add(
                    "[$timestamp] Aksi Selesai (+${String.format(Locale.US, "%.1f", scoreDelta)}): Eksekusi terverifikasi dengan observasi: \"${observation.take(60)}...\""
                )
            }
            "failed" -> {
                newExecutionScore = (currentRbss.executionScore - 5.0).coerceAtLeast(20.0)
                scoreDelta = (newExecutionScore - currentRbss.executionScore) * 0.30
                reason = "Tindakan mengalami hambatan/resistensi di dunia nyata; data empiris diperbarui untuk kalibrasi ulang"
                currentTrail.add(
                    "[$timestamp] Hambatan Aksi: Kendala terdeteksi, analisis perlu disesuaikan dengan kapasitas nyata"
                )
            }
            else -> {
                newExecutionScore = 50.0
                scoreDelta = 0.0
                reason = "Status tindakan diatur ke pending"
                currentTrail.add("[$timestamp] Status tindakan diperbarui ke antrean")
            }
        }

        val updatedTotal = (currentRbss.totalRealityScore + scoreDelta).coerceIn(10.0, 100.0)

        return currentRbss.copy(
            totalRealityScore = updatedTotal,
            executionScore = newExecutionScore,
            scoreChangeReason = reason,
            auditTrail = currentTrail
        )
    }

    /**
     * Memperbarui skor saat evaluasi mingguan diberikan
     */
    fun recalculateOnWeeklyEvaluation(
        currentRbss: RbssScore,
        evalStatus: String,
        evalStory: String
    ): RbssScore {
        val timestamp = dateFormat.format(Date())
        val currentTrail = currentRbss.auditTrail.toMutableList()

        val (delta, reason) = when (evalStatus) {
            "lebih_baik" -> Pair(4.5, "Evaluasi mingguan mencatat perkembangan positif nyata")
            "lebih_buruk" -> Pair(-3.0, "Evaluasi mingguan mencatat peningkatan friksi atau kemunduran kondisi")
            else -> Pair(0.5, "Evaluasi mingguan menunjukkan kondisi stabil terkendali")
        }

        val updatedAlignment = (currentRbss.alignmentScore + if (evalStory.length > 20) 5.0 else 0.0).coerceAtMost(100.0)
        val updatedTotal = (currentRbss.totalRealityScore + delta).coerceIn(10.0, 100.0)

        currentTrail.add(
            "[$timestamp] Evaluasi Mingguan ($evalStatus): ${reason}. Narasi: \"${evalStory.take(50)}...\""
        )

        return currentRbss.copy(
            totalRealityScore = updatedTotal,
            alignmentScore = updatedAlignment,
            scoreChangeReason = reason,
            auditTrail = currentTrail
        )
    }
}
