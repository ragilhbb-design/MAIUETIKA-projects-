package com.example.ui

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * ChatUiState represents the state of the Socratic dialogue.
 * Aliased to MaieutikaUiState for complete architectural harmony.
 */
typealias ChatUiState = MaieutikaUiState

/**
 * ChatViewModel provides complete backwards and forwards compatibility
 * for the Socratic chat dialogue co-partner experience.
 * It manages conversation history, Socratic question probing,
 * and Google AI SDK (GenerativeModel) communication.
 */
class ChatViewModel(
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MaieutikaViewModel(ioDispatcher) {

    constructor(application: Application) : this(Dispatchers.IO)
    constructor(application: Application, ioDispatcher: CoroutineDispatcher) : this(ioDispatcher)

    /**
     * Send user message to Socratic dialog
     */
    fun sendUserMessage(prompt: String) {
        sendMessage(prompt)
    }

    /**
     * Send probing prompt to deepen reflection
     */
    fun sendProbingQuestion(question: String) {
        sendProbingPrompt(question)
    }
}
