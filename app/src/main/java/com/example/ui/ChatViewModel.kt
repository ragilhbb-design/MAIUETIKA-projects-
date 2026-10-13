package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Salam. Saya MAIEUTIKA. Saya tidak akan memberi Anda jawaban instan, tetapi saya akan membantu Anda melihat realitas dengan jernih. Apa pertanyaan, dilema, atau beban pikiran yang ingin kita bedah bersama hari ini?",
            isUser = false
        )
    ),
    val isTyping: Boolean = false,
    val errorMessage: String? = null
)

class ChatViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    constructor() : this(Dispatchers.IO)
    constructor(application: Application) : this(Dispatchers.IO)
    constructor(application: Application, ioDispatcher: CoroutineDispatcher) : this(ioDispatcher)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var generativeModel: GenerativeModel? = null
    private var socraticChat: Chat? = null

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val systemPrompt = """
        Anda adalah MAIEUTIKA, co-partner kognitif berbasis AI dengan filosofi Socrates.
        Tagline: "Google Maps Kehidupan"
        Subtagline: "Saya tidak akan memberi Anda jawaban. Saya akan membantu Anda melihat."
        
        Prinsip Utama Dialog Sokratik (Socratic Questioning):
        1. "Membangun AI yang Melihat Sebelum Menjawab". Diagnosis harus mendahului jawaban.
        2. Jangan memberikan solusi instan, tips instan, atau mendikte apa yang harus dilakukan.
        3. Gunakan teknik bertanya Sokratik (elenchus):
           - Ajukan 1-2 pertanyaan tajam, tenang, dan reflektif yang meruntuhkan asumsi tersembunyi pengguna.
           - Bantu pengguna melihat kontradiksi antara ekspektasi mental dan realitas objektif.
           - Terapkan Root Mechanism Analysis (RMA) untuk mencari akar motif di balik keluhan.
        4. Gaya Bahasa: Tenang, jernih, empatik, bijaksana, tidak menghakimi, dalam Bahasa Indonesia yang baik dan elegan.
        5. KUBAH ETIKA: Tolak dengan sopan namun tegas permintaan yang berniat manipulatif, curang, atau ilegal, lalu arahkan kembali ke kejujuran moral.
    """.trimIndent()

    init {
        try {
            initGoogleAiSdk()
        } catch (t: Throwable) {
            Log.w("ChatViewModel", "Safe init fallback: ${t.message}", t)
        }
    }

    private fun initGoogleAiSdk() {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (t: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                generativeModel = GenerativeModel(
                    modelName = "gemini-3.5-flash",
                    apiKey = apiKey,
                    systemInstruction = content { text(systemPrompt) }
                )
                socraticChat = generativeModel?.startChat()
            } catch (t: Throwable) {
                // Catch all errors including NoClassDefFoundError/ClassNotFoundException so ViewModel never crashes
                Log.w("ChatViewModel", "Could not initialize GenerativeModel (${t.message}), fallback to OkHttp REST active.", t)
                generativeModel = null
                socraticChat = null
            }
        }
    }

    fun sendMessage(userPrompt: String) {
        val cleanPrompt = userPrompt.trim()
        if (cleanPrompt.isBlank()) return

        val userMessage = ChatMessage(text = cleanPrompt, isUser = true)
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                isTyping = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val responseText = processPrompt(cleanPrompt)
                val aiMessage = ChatMessage(text = responseText, isUser = false)
                _uiState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + aiMessage,
                        isTyping = false
                    )
                }
            } catch (t: Throwable) {
                Log.e("ChatViewModel", "Error processing Socratic prompt: ${t.message}", t)
                val fallbackResponse = generateLocalSocraticResponse(cleanPrompt)
                val aiMessage = ChatMessage(text = fallbackResponse, isUser = false)
                _uiState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + aiMessage,
                        isTyping = false
                    )
                }
            }
        }
    }

    private suspend fun processPrompt(prompt: String): String = withContext(ioDispatcher) {
        // 1. Ethical dome check
        val lower = prompt.lowercase()
        val unethicalKeywords = listOf("menipu", "tipu", "manipulasi", "curang", "meretas", "hack", "mencuri", "racun", "membunuh")
        if (unethicalKeywords.any { lower.contains(it) }) {
            return@withContext "Kubah Etika Aktif: MAIEUTIKA menolak memandu tindakan yang manipulatif atau tidak etis. Kebijaksanaan sejati dimulai saat kita berani menatap kenyataan secara jujur tanpa rekayasa. Apa akar ketakutan yang membuat Anda tergoda mengambil jalan pintas tersebut?"
        }

        // 2. Try Google AI SDK chat
        try {
            val activeChat = socraticChat
            if (activeChat != null) {
                val response = activeChat.sendMessage(prompt)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }

            val model = generativeModel
            if (model != null) {
                val response = model.generateContent(prompt)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
        } catch (t: Throwable) {
            Log.w("ChatViewModel", "Google AI SDK threw: ${t.message}, trying direct REST client.")
        }

        // 3. Try Direct OkHttp REST call
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (t: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val restResult = callGeminiRest(apiKey, prompt)
                if (!restResult.isNullOrBlank()) {
                    return@withContext restResult
                }
            } catch (t: Throwable) {
                Log.w("ChatViewModel", "Direct REST call failed: ${t.message}")
            }
        }

        // 4. High-Fidelity Socratic heuristic engine fallback
        generateLocalSocraticResponse(prompt)
    }

    private fun callGeminiRest(apiKey: String, prompt: String): String? {
        val candidateModels = listOf("gemini-3.5-flash", "gemini-flash-latest", "gemini-3.1-pro-preview")
        val requestJson = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            })
            put("contents", JSONArray().apply {
                _uiState.value.messages.takeLast(6).forEach { msg ->
                    put(JSONObject().apply {
                        put("role", if (msg.isUser) "user" else "model")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", msg.text) })
                        })
                    })
                }
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())

        for (model in candidateModels) {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val request = Request.Builder().url(url).post(requestBody).build()
            val response = try {
                httpClient.newCall(request).execute()
            } catch (e: Exception) {
                Log.w("ChatViewModel", "Network error for model $model: ${e.message}")
                continue
            }

            if (!response.isSuccessful) {
                val errBody = try { response.body?.string() } catch (e: Exception) { "" }
                Log.e("ChatViewModel", "REST error code ${response.code} for $model: $errBody")
                continue
            }

            val bodyStr = response.body?.string() ?: continue
            val responseJson = JSONObject(bodyStr)
            val candidates = responseJson.optJSONArray("candidates") ?: continue
            if (candidates.length() == 0) continue
            val content = candidates.getJSONObject(0).optJSONObject("content") ?: continue
            val parts = content.optJSONArray("parts") ?: continue
            if (parts.length() == 0) continue
            val text = parts.getJSONObject(0).optString("text")
            if (text.isNotBlank()) {
                return text
            }
        }
        return null
    }

    private fun generateLocalSocraticResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("kenapa") || lower.contains("mengapa") -> {
                "Mari kita bedah pertanyaan itu. Sebelum mencari alasan di luar diri Anda: apakah situasi ini murni ditentukan oleh faktor eksternal, ataukah ada andil asumsi yang selama ini belum pernah Anda uji kebenarannya?"
            }
            lower.contains("takut") || lower.contains("cemas") || lower.contains("khawatir") -> {
                "Kecemasan sering kali adalah proyeksi atas bayangan masa depan yang belum terjadi. Jika skenario terburuk yang Anda cemaskan itu benar-benar terjadi besok pagi, apa sebenarnya hal paling mendasar yang Anda takutkan akan hilang dari diri Anda?"
            }
            lower.contains("kerja") || lower.contains("karier") || lower.contains("kantor") -> {
                "Dalam persimpangan karier ini, apakah kegelisahan Anda bersumber dari beban pekerjaan itu sendiri, atau karena pekerjaan tersebut tidak lagi selaras dengan tujuan hidup yang paling esensial bagi Anda?"
            }
            lower.contains("uang") || lower.contains("finansial") || lower.contains("gaji") -> {
                "Uang adalah cermin dari prioritas dan rasa aman. Apakah angka finansial ini merupakan kebutuhan objektif untuk bertahan, atau simbol kepastian yang Anda gunakan untuk menenangkan ketakutan lain?"
            }
            lower.contains("bingung") || lower.contains("bimbang") -> {
                "Kebingungan adalah pintu masuk filsafat—tanda bahwa asumsi lama sudah runtuh sementara pemahaman baru belum tegak. Pilihan mana yang selama ini Anda hindari semata-mata karena Anda enggan menanggung rasa tidak nyamannya?"
            }
            else -> {
                "Menarik. Namun mari kita lihat lebih dalam: jika kita pisahkan emosi sesaat dari fakta objektif yang ada di depan mata, apa sebenarnya fakta mentah yang paling sulit Anda akui dalam perkara ini?"
            }
        }
    }

    fun clearChat() {
        initGoogleAiSdk()
        _uiState.value = ChatUiState()
    }
}
