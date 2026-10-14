package com.example

import com.example.data.model.RealityMap
import com.example.data.model.ScenarioItem
import com.example.data.model.ZebraAction
import com.example.data.repository.GeminiService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MaieutikaUnitTest {

    @Test
    fun testRealityMapCalculation() {
        val map = RealityMap(
            keuangan = 80,
            keluarga = 80,
            karier = 80,
            relasi = 80,
            tujuan = 80,
            risiko = 20, // 100 - 20 = 80
            psikologi = 80,
            kesehatan = 80
        )
        val avg = map.calculateAverageScore()
        assertEquals(80.0, avg, 0.01)
    }

    @Test
    fun testKubahEtikaDetection_detectsUnethicalPrompt() {
        val service = GeminiService()
        val unethicalPrompt = "Bagaimana cara manipulasi rekan kerja agar saya dapat promosi?"
        val warning = service.checkEthicalDome(unethicalPrompt)
        assertNotNull(warning)
        assertTrue(warning!!.contains("Kubah Etika Aktif"))
    }

    @Test
    fun testKubahEtikaDetection_allowsEthicalPrompt() {
        val service = GeminiService()
        val ethicalPrompt = "Saya merasa bimbang apakah harus bertahan di pekerjaan lama atau mulai usaha sendiri."
        val warning = service.checkEthicalDome(ethicalPrompt)
        assertNull(warning)
    }

    @Test
    fun testScenarioItemConversion() {
        val scenario = ScenarioItem(
            nama = "Skenario B (Berani)",
            deskripsi = "Langkah terukur dengan keberanian",
            probabilitas = 65,
            risiko = 45,
            dampak = 75
        )
        val map = scenario.toMap()
        assertEquals("Skenario B (Berani)", map["nama"])
        assertEquals(65, map["probabilitas"])
        assertEquals(45, map["risiko"])
        assertEquals(75, map["dampak"])
    }

    @Test
    fun testZebraActionStatus() {
        val action = ZebraAction(
            instruksi = "Duduk 30 menit",
            dampakPoin = 14,
            status = "pending"
        )
        assertEquals("pending", action.status)
        assertEquals(14, action.dampakPoin)
    }

    @Test
    fun testChatViewModelInstantiationAndInitialState() {
        val chatViewModel = com.example.ui.ChatViewModel()
        val state = chatViewModel.uiState.value
        assertNotNull(state)
        assertTrue(state.messages.isNotEmpty())
        assertEquals(false, state.messages.first().isUser)
        assertTrue(state.messages.first().text.contains("MAIEUTIKA"))
        assertEquals(false, state.isTyping)
        assertNull(state.errorMessage)
    }

    @Test
    fun testMaieutikaViewModel_initialStateAndProbingQuestions() {
        val maieutikaViewModel = com.example.ui.MaieutikaViewModel()
        val state = maieutikaViewModel.uiState.value
        assertNotNull(state)
        assertEquals(1, state.messages.size)
        assertEquals(false, state.messages.first().isUser)
        assertTrue(state.messages.first().text.contains("MAIEUTIKA"))
        // Confirms it declares not providing immediate answers
        assertTrue(state.messages.first().text.contains("tidak akan menyodorkan jawaban"))
        assertEquals(false, state.isTyping)
        assertNull(state.errorMessage)
        assertTrue(state.suggestedProbingQuestions.isNotEmpty())
    }

    @Test
    fun testMaieutikaViewModel_systemPromptConfiguredAsSocraticCoPartner() {
        val maieutikaViewModel = com.example.ui.MaieutikaViewModel()
        val prompt = maieutikaViewModel.systemPrompt
        assertTrue(prompt.contains("Socratic Co-Partner"))
        assertTrue(prompt.contains("JANGAN PERNAH memberikan resep solusi langsung"))
        assertTrue(prompt.contains("Pertanyaan Menyelidik / Probing Questions"))
        assertTrue(prompt.contains("KUBAH ETIKA"))
    }

    @Test
    fun testMaieutikaViewModel_socraticProbingHeuristicsAskQuestions() {
        val maieutikaViewModel = com.example.ui.MaieutikaViewModel()
        val response = maieutikaViewModel.generateLocalSocraticProbingResponse("Saya sangat cemas dan takut gagal.")
        assertTrue(response.contains("?")) // Must ask probing question
        assertTrue(response.contains("Kecemasan"))
    }

    @Test
    fun testRbssEngineDeterministicScoring() {
        val rma = com.example.data.model.RmaAnalysis(
            confirmedInterests = listOf("Kejernihan pikiran", "Stabilitas emosional")
        )
        val hos = com.example.data.model.HosAnalysis(
            adequacyScore = 80,
            missingData = listOf("Data respon bos"),
            inconsistencies = listOf("Takut tapi menolak bertindak")
        )
        val kkz = com.example.data.model.KkzCalculation(
            readinessIndex = 70
        )
        val zebra = com.example.data.model.ZebraAction(
            status = "pending"
        )
        val rbss = com.example.data.repository.RbssEngine.calculateInitialRbss(
            rma = rma,
            hos = hos,
            kkz = kkz,
            zebra = zebra,
            tingkatRisiko = 40.0
        )

        assertTrue(rbss.totalRealityScore in 30.0..95.0)
        assertTrue(rbss.informationAdequacyScore > 50.0)
        assertTrue(rbss.auditTrail.isNotEmpty())
        assertTrue(rbss.auditTrail.first().contains("Inisialisasi Analisis Sistem"))
    }

    @Test
    fun testRbssEngineExecutionWithObservationUpdatesAuditTrail() {
        val initialRbss = com.example.data.model.RbssScore(
            totalRealityScore = 55.0,
            executionScore = 50.0,
            auditTrail = listOf("Init")
        )
        val updated = com.example.data.repository.RbssEngine.recalculateOnActionExecution(
            currentRbss = initialRbss,
            newStatus = "done",
            observation = "Telah menuliskan 3 ketakutan di buku catatan pagi ini",
            impactPoints = 14
        )
        assertTrue(updated.totalRealityScore > initialRbss.totalRealityScore)
        assertTrue(updated.executionScore > initialRbss.executionScore)
        assertEquals(2, updated.auditTrail.size)
        assertTrue(updated.auditTrail.last().contains("Aksi Selesai"))
    }

    @Test
    fun testRecalibrationLoopUpdatesHypothesesAndHos() {
        val service = GeminiService()
        val initialSession = com.example.data.model.SocraticSession(
            id = "test_sess_1",
            masalahUtama = "Kabut persepsi awal",
            analisisAkarMasalah = listOf("Asumsi tidak diuji di lapangan")
        )
        val recalibrated = service.recalibrateAnalysisWithObservation(
            session = initialSession,
            observationText = "Langkah telah diuji dan menghasilkan bukti positif",
            outcomeStatus = "done"
        )

        val rma = recalibrated.getParsedRma()
        assertTrue(rma.mechanismHypotheses.first().contains("TERVERIFIKASI EMPIRIS"))
        assertEquals("done", recalibrated.getParsedZebra().status)
        assertTrue(recalibrated.getParsedRbss().auditTrail.size >= 2)
    }
}
