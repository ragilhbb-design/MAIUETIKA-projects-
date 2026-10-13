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
}
