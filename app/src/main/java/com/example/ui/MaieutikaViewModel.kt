package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
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

data class MaieutikaUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Salam. Saya MAIEUTIKA, co-partner dialog Sokratik Anda.\n\nSaya tidak akan menyodorkan jawaban atau nasihat instan. Kebijaksanaan sejati lahir saat Anda sendiri yang menyibak ilusi dan menemukan kejelasan pikiran.\n\nApa dilema, kebingungan, atau asumsi yang ingin kita bedah bersama hari ini?",
            isUser = false
        )
    ),
    val isTyping: Boolean = false,
    val errorMessage: String? = null,
    val suggestedProbingQuestions: List<String> = listOf(
        "Apakah ini murni fakta atau interpretasi saya?",
        "Asumsi apa yang belum pernah saya uji?",
        "Apa skenario terburuk yang sebenarnya saya takuti?",
        "Di mana letak kendali saya dalam situasi ini?"
    ),
    val reflectionCount: Int = 0
)

open class MaieutikaViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    constructor(application: Application) : this(Dispatchers.IO)
    constructor(application: Application, ioDispatcher: CoroutineDispatcher) : this(ioDispatcher)

    private val _uiState = MutableStateFlow(MaieutikaUiState())
    val uiState: StateFlow<MaieutikaUiState> = _uiState.asStateFlow()

    private var generativeModel: GenerativeModel? = null
    private var socraticChat: Chat? = null

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    // Socratic Co-Partner System Instruction
    val systemPrompt: String = """
        Anda adalah MAIEUTIKA, mitra dialog kognitif berfilosofi Socrates (Socratic Co-Partner).
        Tagline: "Google Maps Kehidupan"
        Motto: "Saya tidak akan memberi Anda jawaban instan. Saya akan membantu Anda melihat."
        
        PERAN & PRINSIP UTAMA:
        1. Bertindaklah sebagai bidan pemikiran (Maieutics / Kebidanan Pemikiran).
        2. JANGAN PERNAH memberikan resep solusi langsung, tips/langkah instan (bullet list solusi), atau memberi tahu pengguna apa yang harus dilakukan.
        3. Diagnosis mendahului solusi: Bantu pengguna mengurai kompleksitas sebelum melompat ke kesimpulan.
        4. Terapkan Metode Elenchus & Root Mechanism Analysis (RMA):
           - Bongkar kontradiksi antara apa yang pengguna harapkan vs fakta realitas objektif.
           - Soroti asumsi tersembunyi yang pengguna anggap sebagai kebenaran mutlak.
           - Arahkan fokus ke akar psikologis atau emosional (ketakutan, kebutuhan validasi, penolakan atas ketidakpastian).
        
        STRUKTUR RESPON (SANGAT PENTING):
        - Bagian 1 (Refleksi Jernih): 2-3 kalimat singkat merangkum inti keresahan pengguna secara tenang, elegan, dan empatik tanpa menghakimi.
        - Bagian 2 (Pertanyaan Menyelidik / Probing Questions): Ajukan 1 atau maksimal 2 pertanyaan filosofis mendalam yang membuka sudut pandang baru dan menuntut refleksi diri yang jujur.
        - Bagian 3: Akhiri dengan memberi ruang bagi pengguna untuk merenung dan merespons.
        
        KUBAH ETIKA:
        Jika ada indikasi kecurangan, manipulasi, kebohongan, atau bahaya, tolak secara halus namun teguh, lalu tanyakan akar ketakutan di balik keinginan tersebut.
        
        Bahasa: Bahasa Indonesia yang jernih, tenang, bermartabat, dan bersahabat.
    """.trimIndent()

    init {
        try {
            initGeminiClient()
        } catch (t: Throwable) {
            Log.w("MaieutikaViewModel", "Safe init fallback: ${t.message}", t)
        }
    }

    fun initGoogleAiSdk() {
        initGeminiClient()
    }

    fun initGeminiClient() {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (t: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                // Initialize modern Gemini client configured with Socratic system instruction
                generativeModel = GenerativeModel(
                    modelName = "gemini-3.5-flash",
                    apiKey = apiKey,
                    systemInstruction = content { text(systemPrompt) },
                    generationConfig = generationConfig {
                        temperature = 0.7f
                        topP = 0.9f
                    }
                )
                socraticChat = generativeModel?.startChat()
            } catch (t: Throwable) {
                Log.w("MaieutikaViewModel", "GenerativeModel init error: ${t.message}. OkHttp REST fallback will be used.", t)
                generativeModel = null
                socraticChat = null
            }
        }
    }

    fun sendMessage(userPrompt: String) {
        val cleanPrompt = userPrompt.trim()
        if (cleanPrompt.isBlank()) return

        if (generativeModel == null) {
            try {
                initGeminiClient()
            } catch (ignored: Throwable) {}
        }

        val userMessage = ChatMessage(text = cleanPrompt, isUser = true)
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                isTyping = true,
                errorMessage = null,
                reflectionCount = currentState.reflectionCount + 1
            )
        }

        viewModelScope.launch {
            try {
                val responseText = processSocraticPrompt(cleanPrompt)
                val aiMessage = ChatMessage(text = responseText, isUser = false)
                _uiState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + aiMessage,
                        isTyping = false,
                        suggestedProbingQuestions = generateNextProbingSuggestions(cleanPrompt)
                    )
                }
            } catch (t: Throwable) {
                Log.e("MaieutikaViewModel", "Error in Socratic dialogue: ${t.message}", t)
                val fallbackResponse = generateLocalSocraticProbingResponse(cleanPrompt)
                val aiMessage = ChatMessage(text = fallbackResponse, isUser = false)
                _uiState.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + aiMessage,
                        isTyping = false,
                        suggestedProbingQuestions = generateNextProbingSuggestions(cleanPrompt)
                    )
                }
            }
        }
    }

    private suspend fun processSocraticPrompt(prompt: String): String = withContext(ioDispatcher) {
        // 1. Kubah Etika (Ethical Dome) check
        val lower = prompt.lowercase()
        val unethicalKeywords = listOf(
            "menipu", "tipu", "manipulasi", "curang", "meretas", "hack",
            "mencuri", "racun", "membunuh", "korupsi", "suap", "gelapkan"
        )
        if (unethicalKeywords.any { lower.contains(it) }) {
            return@withContext "Kubah Etika Aktif: MAIEUTIKA hadir sebagai co-partner untuk menjernihkan kesadaran, bukan memandu siasat yang merusak integritas atau merugikan orang lain.\n\nMari kita bedah secara jujur: Apa ketakutan atau kekosongan mendasar yang membuat Anda merasa harus mengambil jalan manipulasi ini?"
        }

        // 2. Google AI SDK (GenerativeModel Chat)
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
            Log.w("MaieutikaViewModel", "Google AI SDK attempt yielded: ${t.message}. Trying direct REST client.")
        }

        // 3. Fallback to Direct REST with Candidate Models
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
                Log.w("MaieutikaViewModel", "REST Socratic client failed: ${t.message}")
            }
        }

        // 4. Fallback to Socratic Heuristic Engine
        generateLocalSocraticProbingResponse(prompt)
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
                // Include recent history for continuous Socratic context
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
                put("topP", 0.9)
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())

        for (model in candidateModels) {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val request = Request.Builder().url(url).post(requestBody).build()
            val response = try {
                httpClient.newCall(request).execute()
            } catch (e: Exception) {
                Log.w("MaieutikaViewModel", "Network error for model $model: ${e.message}")
                continue
            }

            if (!response.isSuccessful) {
                val errBody = try { response.body?.string() } catch (e: Exception) { "" }
                Log.w("MaieutikaViewModel", "REST error code ${response.code} for $model: $errBody")
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

    fun generateLocalSocraticProbingResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("kenapa") || lower.contains("mengapa") -> {
                "Pertanyaan 'mengapa' sering kali menuntut pembenaran, bukan kejelasan.\n\nMari kita telaah lebih dalam: Sebelum mencari kambing hitam atau alasan di luar kendali Anda, sejauh mana situasi ini sebenarnya dipicu oleh harapan Anda yang tidak realistis terhadap orang lain?"
            }
            lower.contains("takut") || lower.contains("cemas") || lower.contains("khawatir") -> {
                "Kecemasan adalah bayangan masa depan yang Anda proyeksikan ke hari ini.\n\nJika skenario yang paling Anda cemaskan benar-benar terjadi besok pagi, apa sebenarnya hal paling esensial yang Anda takutkan akan hilang dari harga diri Anda?"
            }
            lower.contains("kerja") || lower.contains("karier") || lower.contains("kantor") || lower.contains("bos") -> {
                "Di persimpangan profesional ini, mari bedah motif di balik kegelisahan Anda:\n\nApakah beban ini terasa berat karena pekerjaannya yang melampaui kapasitas, atau karena Anda merasa pengorbanan ini tidak lagi bermakna bagi arah hidup yang Anda tuju?"
            }
            lower.contains("uang") || lower.contains("finansial") || lower.contains("gaji") || lower.contains("modal") -> {
                "Uang kerap kali menjadi simbol dari kebutuhan yang lebih dalam: rasa aman dan pengakuan.\n\nApakah angka finansial yang Anda kejar ini merupakan kebutuhan objektif untuk bertahan, atau perlindungan emosional untuk meredam rasa rendah diri?"
            }
            lower.contains("bimbang") || lower.contains("bingung") || lower.contains("pilih") -> {
                "Kebingungan muncul ketika kedua pilihan sama-sama memiliki konsekuensi yang enggan Anda tanggung.\n\nPilihan mana yang selama ini Anda hindari bukan karena itu keliru, melainkan karena Anda belum siap menghadapi rasa tidak nyamannya?"
            }
            lower.contains("marah") || lower.contains("kecewa") || lower.contains("sakit hati") -> {
                "Kekecewaan adalah selisih antara ekspektasi yang Anda buat sendiri dengan realitas perilaku orang lain.\n\nApakah Anda kecewa pada perbuatannya, atau pada kenyataan bahwa dia tidak bertindak sesuai skenario yang Anda bayangkan?"
            }
            else -> {
                "Mari kita amati persoalan ini dengan lensa objektif:\n\nJika kita singkirkan emosi sesaat dan asumsi yang belum terbukti, fakta mentah apa yang paling berat untuk Anda akui dalam perkara ini?"
            }
        }
    }

    private fun generateNextProbingSuggestions(prompt: String): List<String> {
        val lower = prompt.lowercase()
        return when {
            lower.contains("takut") || lower.contains("cemas") -> listOf(
                "Apa skenario terburuk yang paling saya takuti?",
                "Apakah ketakutan ini berdasar fakta atau bayangan?",
                "Apa yang masih berada dalam kendali saya?"
            )
            lower.contains("kerja") || lower.contains("karier") -> listOf(
                "Apa nilai hidup yang sedang dikorbankan di sini?",
                "Apakah saya bertahan demi rasa aman atau pertumbuhan?",
                "Apa langkah kecil paling jujur yang bisa saya ambil?"
            )
            lower.contains("bingung") || lower.contains("bimbang") -> listOf(
                "Konsekuensi mana yang paling enggan saya tanggung?",
                "Jika tidak ada orang yang menilai, apa yang saya pilih?",
                "Asumsi apa yang sedang membelenggu pikiran saya?"
            )
            else -> listOf(
                "Apa fakta objektif yang belum saya akui?",
                "Bagaimana jika asumsi utama saya ternyata keliru?",
                "Apa akar persoalan yang sebenarnya di sini?"
            )
        }
    }

    fun clearChat() {
        initGeminiClient()
        _uiState.value = MaieutikaUiState()
    }

    fun resetConversation() {
        clearChat()
    }

    fun sendProbingPrompt(question: String) {
        sendMessage(question)
    }

    fun selectSuggestedQuestion(question: String) {
        sendMessage(question)
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
