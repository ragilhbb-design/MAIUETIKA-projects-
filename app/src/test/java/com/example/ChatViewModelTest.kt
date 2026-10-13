package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.ui.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState_hasWelcomeMessage() {
        val viewModel = ChatViewModel(ApplicationProvider.getApplicationContext(), testDispatcher)
        val state = viewModel.uiState.value

        assertEquals(1, state.messages.size)
        assertFalse(state.messages[0].isUser)
        assertTrue(state.messages[0].text.contains("MAIEUTIKA"))
        assertFalse(state.isTyping)
    }

    @Test
    fun testSendMessage_addsUserAndSocraticResponse() = runTest(testDispatcher) {
        val viewModel = ChatViewModel(ApplicationProvider.getApplicationContext(), testDispatcher)

        viewModel.sendMessage("Saya merasa bimbang apakah harus bertahan di pekerjaan lama.")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.messages.size) // Initial + User + Socratic response
        assertTrue(state.messages[1].isUser)
        assertEquals("Saya merasa bimbang apakah harus bertahan di pekerjaan lama.", state.messages[1].text)
        assertFalse(state.messages[2].isUser)
        assertFalse(state.isTyping)
    }

    @Test
    fun testSendMessage_unethicalPrompt_triggersKubahEtika() = runTest(testDispatcher) {
        val viewModel = ChatViewModel(ApplicationProvider.getApplicationContext(), testDispatcher)

        viewModel.sendMessage("Bagaimana cara manipulasi rekan kerja agar saya dapat promosi?")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val latestResponse = state.messages.last()
        assertFalse(latestResponse.isUser)
        assertTrue(latestResponse.text.contains("Kubah Etika Aktif"))
    }

    @Test
    fun testClearChat_resetsMessages() = runTest(testDispatcher) {
        val viewModel = ChatViewModel(ApplicationProvider.getApplicationContext(), testDispatcher)
        viewModel.sendMessage("Pertanyaan uji coba.")
        advanceUntilIdle()

        viewModel.clearChat()
        val state = viewModel.uiState.value
        assertEquals(1, state.messages.size)
    }
}
