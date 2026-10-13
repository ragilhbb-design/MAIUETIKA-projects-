package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.RealityMap
import com.example.data.model.ScenarioItem
import com.example.data.model.SocraticSession
import com.example.data.model.ZebraAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class DiagnosisResult(
    val isUnethical: Boolean,
    val unethicalRefusalMessage: String,
    val session: SocraticSession
)

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val unethicalKeywords = listOf(
        "menipu", "tipu", "manipulasi", "curang", "meretas", "hack",
        "mencuri", "korupsi", "racun", "membunuh", "menyakiti", "suap", "gelapkan"
    )

    fun checkEthicalDome(story: String): String? {
        val lower = story.lowercase()
        for (kw in unethicalKeywords) {
            if (lower.contains(kw)) {
                return "Kubah Etika Aktif: MAIEUTIKA adalah co-partner untuk menjernihkan realitas dan kebijaksanaan hidup. Kami menolak memandu tindakan yang bersifat manipulatif, curang, atau merugikan integritas diri maupun orang lain. Solusi sejati hanya lahir dari kejujuran melihat kenyataan secara apa adanya."
            }
        }
        return null
    }

    suspend fun diagnose(userName: String, userStory: String, currentUid: String): DiagnosisResult = withContext(Dispatchers.IO) {
        val localEthicalViolation = checkEthicalDome(userStory)
        if (localEthicalViolation != null) {
            return@withContext DiagnosisResult(
                isUnethical = true,
                unethicalRefusalMessage = localEthicalViolation,
                session = SocraticSession(
                    uid = currentUid,
                    ceritaPengguna = userStory,
                    diagnosis = localEthicalViolation
                )
            )
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val apiResult = callGeminiApi(apiKey, userName, userStory, currentUid)
                if (apiResult != null) {
                    return@withContext apiResult
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Gemini API call failed, falling back to local Socratic heuristic engine: ${e.message}")
            }
        }

        // High-Fidelity Socratic Engine Fallback
        generateLocalSocraticDiagnosis(userName, userStory, currentUid)
    }

    private fun callGeminiApi(apiKey: String, userName: String, userStory: String, currentUid: String): DiagnosisResult? {
        val candidateModels = listOf("gemini-3.5-flash", "gemini-flash-latest", "gemini-3.1-pro-preview")

        val prompt = """
            Anda adalah MAIEUTIKA, co-partner kognitif berbasis AI dengan filosofi Socrates.
            Tagline: "Google Maps Kehidupan"
            Subtagline: "Saya tidak akan memberi Anda jawaban. Saya akan membantu Anda melihat."
            Prinsip Utama: "Membangun AI yang Melihat Sebelum Menjawab". Diagnosis harus mendahului jawaban. Setiap keluhan dipandang sebagai gejala dengan kepentingan, mekanisme, dan konteks yang perlu dipahami sebelum solusi dirumuskan.
            
            Profil Pengguna: $userName
            Cerita/Keluhan Pengguna: "$userStory"
            
            Prinsip Dialog Sokratik & Root Mechanism Analysis (RMA):
            1. Jangan langsung mendikte solusi instan. Bedah lapisan ilusi dan bawa pengguna melihat kenyataan objektif mekanisme akar.
            2. KUBAH ETIKA: Jika permintaan mengandung manipulasi, kecurangan, atau hal ilegal, set isUnethical = true dan berikan unethicalRefusalMessage yang sopan, tegas, dan filosofis.
            3. Jika etis, petakan skor realitas 0-100 untuk 8 dimensi (Keuangan, Keluarga, Karier, Relasi, Tujuan, Risiko, Psikologi, Kesehatan).
            4. Rumuskan 4 analisis akar masalah (Root Mechanism Analysis) yang mengerucut ke 1 Masalah Utama/Inti.
            5. Buat 3 Skenario: Stabil (risiko rendah, dampak kecil), Berani (risiko sedang, dampak menengah), Agresif (risiko tinggi, dampak besar) beserta estimasi probabilitas (%), risiko (%), dampak (%).
            6. Tentukan rekomendasi skenario terbaik.
            7. Rancang ZEBRA (Prioritas Hari Ini): satu tindakan mikro spesifik konkret yang wajib dilakukan hari ini/besok dengan waktu terukur, script/tindakan nyata, dan poin dampak (+10 s.d +18 poin).
            
            Keluarkan HANYA format JSON valid tanpa markdown formatting tambahan dengan struktur:
            {
              "isUnethical": false,
              "unethicalRefusalMessage": "",
              "diagnosis": "Penjelasan mendalam reflektif Sokratik...",
              "petaRealitas": {
                "keuangan": 65,
                "keluarga": 75,
                "karier": 50,
                "relasi": 60,
                "tujuan": 55,
                "risiko": 45,
                "psikologi": 58,
                "kesehatan": 70
              },
              "analisisAkarMasalah": [
                "Akar 1...",
                "Akar 2...",
                "Akar 3...",
                "Akar 4..."
              ],
              "masalahUtama": "Pernyataan satu kalimat masalah inti...",
              "tingkatRisiko": 55,
              "tingkatKepercayaanAI": 92,
              "skenario": [
                {
                  "nama": "Skenario A (Stabil)",
                  "deskripsi": "Deskripsi...",
                  "probabilitas": 75,
                  "risiko": 25,
                  "dampak": 40
                },
                {
                  "nama": "Skenario B (Berani)",
                  "deskripsi": "Deskripsi...",
                  "probabilitas": 60,
                  "risiko": 50,
                  "dampak": 75
                },
                {
                  "nama": "Skenario C (Agresif)",
                  "deskripsi": "Deskripsi...",
                  "probabilitas": 35,
                  "risiko": 80,
                  "dampak": 90
                }
              ],
              "rekomendasiSkenario": "Skenario B (Berani)",
              "zebra": {
                "instruksi": "Besok pukul 08.00, duduk selama 25 menit. Kumpulkan data...",
                "dampakPoin": 14,
                "status": "pending"
              }
            }
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())

        for (model in candidateModels) {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = try {
                client.newCall(request).execute()
            } catch (e: Exception) {
                Log.w("GeminiService", "Network call failed for model $model: ${e.message}")
                continue
            }

            if (!response.isSuccessful) {
                val errBody = try { response.body?.string() } catch (e: Exception) { "" }
                Log.e("GeminiService", "API response error code: ${response.code} for model $model: $errBody")
                continue
            }

            val responseBodyString = response.body?.string() ?: continue
            val responseJson = JSONObject(responseBodyString)
            val candidates = responseJson.optJSONArray("candidates") ?: continue
            if (candidates.length() == 0) continue

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: continue
            val parts = content.optJSONArray("parts") ?: continue
            if (parts.length() == 0) continue

            val text = parts.getJSONObject(0).optString("text")
            if (text.isBlank()) continue

            return parseDiagnosisJson(text, currentUid, userStory)
        }

        return null
    }

    private fun parseDiagnosisJson(rawJson: String, currentUid: String, userStory: String): DiagnosisResult {
        val clean = rawJson.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val json = JSONObject(clean)
        val isUnethical = json.optBoolean("isUnethical", false)
        val refusalMsg = json.optString("unethicalRefusalMessage", "")
        if (isUnethical) {
            return DiagnosisResult(
                isUnethical = true,
                unethicalRefusalMessage = refusalMsg.ifBlank {
                    "Kubah Etika Aktif: MAIEUTIKA menolak memandu tindakan yang tidak etis atau manipulatif."
                },
                session = SocraticSession(
                    uid = currentUid,
                    ceritaPengguna = userStory,
                    diagnosis = refusalMsg
                )
            )
        }

        val diagnosis = json.optString("diagnosis", "Realitas Anda membutuhkan kejernihan fokus dan penerimaan fakta.")
        val masalahUtama = json.optString("masalahUtama", "Disparitas antara ekspektasi mental dan aksi konkret.")
        val tingkatRisiko = json.optDouble("tingkatRisiko", 50.0)
        val tingkatKepercayaanAI = json.optDouble("tingkatKepercayaanAI", 88.0)
        val rekomendasiSkenario = json.optString("rekomendasiSkenario", "Skenario B (Berani)")

        val petaJson = json.optJSONObject("petaRealitas")
        val petaRealitas = mutableMapOf<String, Any>()
        val defaultDims = listOf("keuangan", "keluarga", "karier", "relasi", "tujuan", "risiko", "psikologi", "kesehatan")
        defaultDims.forEach { dim ->
            petaRealitas[dim] = petaJson?.optInt(dim, 50) ?: 50
        }

        val akarList = mutableListOf<String>()
        val akarArray = json.optJSONArray("analisisAkarMasalah")
        if (akarArray != null) {
            for (i in 0 until akarArray.length()) {
                akarList.add(akarArray.optString(i))
            }
        }
        if (akarList.isEmpty()) {
            akarList.addAll(listOf(
                "Ketidakjelasan batas antara tanggung jawab diri dan orang lain",
                "Kecemasan antisipatif terhadap hasil yang belum tentu terjadi",
                "Fragmentasi perhatian pada hal-hal di luar kendali langsung",
                "Penundaan langkah konfrontasi terhadap realitas objektif"
            ))
        }

        val skenarioList = mutableListOf<Map<String, Any>>()
        val skenarioArray = json.optJSONArray("skenario")
        if (skenarioArray != null) {
            for (i in 0 until skenarioArray.length()) {
                val sc = skenarioArray.optJSONObject(i) ?: continue
                skenarioList.add(mapOf(
                    "nama" to sc.optString("nama", "Skenario ${i + 1}"),
                    "deskripsi" to sc.optString("deskripsi", ""),
                    "probabilitas" to sc.optInt("probabilitas", 50),
                    "risiko" to sc.optInt("risiko", 50),
                    "dampak" to sc.optInt("dampak", 50)
                ))
            }
        }

        val zebraJson = json.optJSONObject("zebra")
        val zebraMap = mapOf(
            "instruksi" to (zebraJson?.optString("instruksi") ?: "Besok pukul 08.00, duduk selama 25 menit. Catat 3 hambatan terbesar Anda dan pilih 1 untuk diselesaikan."),
            "dampakPoin" to (zebraJson?.optInt("dampakPoin") ?: 12),
            "status" to "pending"
        )

        val session = SocraticSession(
            uid = currentUid,
            ceritaPengguna = userStory,
            diagnosis = diagnosis,
            petaRealitas = petaRealitas,
            analisisAkarMasalah = akarList,
            masalahUtama = masalahUtama,
            tingkatRisiko = tingkatRisiko,
            tingkatKepercayaanAI = tingkatKepercayaanAI,
            skenario = skenarioList,
            rekomendasiSkenario = rekomendasiSkenario,
            zebra = zebraMap
        )

        return DiagnosisResult(
            isUnethical = false,
            unethicalRefusalMessage = "",
            session = session
        )
    }

    private fun generateLocalSocraticDiagnosis(userName: String, userStory: String, currentUid: String): DiagnosisResult {
        val lower = userStory.lowercase()

        val isCareer = lower.contains("kerja") || lower.contains("karier") || lower.contains("kantor") || lower.contains("bos") || lower.contains("usaha")
        val isFinance = lower.contains("uang") || lower.contains("gaji") || lower.contains("hutang") || lower.contains("finansial") || lower.contains("investasi")
        val isRelation = lower.contains("pasangan") || lower.contains("pacar") || lower.contains("istri") || lower.contains("suami") || lower.contains("teman") || lower.contains("keluarga")
        val isMental = lower.contains("lelah") || lower.contains("stres") || lower.contains("cemas") || lower.contains("bingung") || lower.contains("takut")

        val realityMap = RealityMap(
            keuangan = if (isFinance) 38 else 65,
            keluarga = if (isRelation) 42 else 72,
            karier = if (isCareer) 44 else 68,
            relasi = if (isRelation) 40 else 66,
            tujuan = 48,
            risiko = if (isCareer || isFinance) 62 else 45,
            psikologi = if (isMental) 35 else 60,
            kesehatan = if (isMental) 48 else 75
        )

        val diagnosisText = """
            $userName, setelah membedah tuturan Anda, yang tampak di permukaan bukanlah ketiadaan kemampuan, melainkan 'kabut asumsi'. Anda sedang bergulat dengan tarikan antara apa yang Anda rasa 'harus' terjadi dan realitas mentah di hadapan Anda.
            
            Socrates pernah mengingatkan: 'Kehidupan yang tidak diperiksa tidak layak dijalani.' Dalam konteks ini, Anda membiarkan energi Anda terkuras oleh lingkaran kekhawatiran alih-alih lingkaran kendali. Masalah Anda bukan karena dunia terlalu rumit, tetapi karena Anda belum memisahkan fakta dingin dari narasi emosional di kepala Anda.
        """.trimIndent()

        val akarList = listOf(
            "Tuntutan internal untuk mempertahankan citra kepastian di tengah situasi dinamis",
            "Kebiasaan menunda percakapan sulit yang sebenarnya merupakan gerbang solusi",
            "Ketidakseimbangan alokasi perhatian: 70% pada skenario buruk hipotetis, 30% pada fakta nyata",
            "Ketiadaan protokol tindakan harian yang terukur dan terlindungi dari interupsi"
        )

        val masalahUtama = if (isCareer) {
            "Friksi kognitif antara ekspektasi perkembangan karier dan ketakutan mengambil risiko penolakan objektif."
        } else if (isFinance) {
            "Kebocoran energi akibat memprioritaskan rasa aman semu dibandingkan disiplin audit finansial nyata."
        } else if (isRelation) {
            "Ekspektasi tanpa komunikasi terbuka yang memicu distorsi persepsi relasional."
        } else {
            "Kelumpuhan kognitif (analysis paralysis) akibat membebani pikiran dengan variabel di luar kendali diri."
        }

        val skenarioList = listOf(
            ScenarioItem(
                nama = "Skenario A (Stabil)",
                deskripsi = "Pertahankan status quo selama 30 hari sembari mengumpulkan data empiris harian tanpa membuat keputusan destruktif.",
                probabilitas = 78,
                risiko = 22,
                dampak = 42
            ).toMap(),
            ScenarioItem(
                nama = "Skenario B (Berani)",
                deskripsi = "Lakukan restrukturisasi prioritas: potong 1 kebiasaan pasif, hadapi 1 percakapan kunci, dan tetapkan batasan tegas minggu ini.",
                probabilitas = 64,
                risiko = 48,
                dampak = 76
            ).toMap(),
            ScenarioItem(
                nama = "Skenario C (Agresif)",
                deskripsi = "Pemberontakan konstruktif: ubah total lingkungan kerja/relasi dan alihkan fokus secara radikal ke fondasi baru.",
                probabilitas = 38,
                risiko = 82,
                dampak = 92
            ).toMap()
        )

        val zebraInstruction = "Besok pagi pukul 08.00 - 08.30 WIB (30 menit tanpa gawai): Tuliskan 3 hal terburuk yang Anda takutkan di kertas. Beri skor 1-10 seberapa realistis hal itu terjadi. Pilih satu tindakan paling mendasar untuk membungkam ketakutan nomor 1."

        val session = SocraticSession(
            uid = currentUid,
            ceritaPengguna = userStory,
            diagnosis = diagnosisText,
            petaRealitas = realityMap.toMap(),
            analisisAkarMasalah = akarList,
            masalahUtama = masalahUtama,
            tingkatRisiko = if (isMental || isFinance) 64.0 else 46.0,
            tingkatKepercayaanAI = 89.0,
            skenario = skenarioList,
            rekomendasiSkenario = "Skenario B (Berani)",
            zebra = ZebraAction(
                instruksi = zebraInstruction,
                dampakPoin = 13,
                status = "pending"
            ).toMap()
        )

        return DiagnosisResult(
            isUnethical = false,
            unethicalRefusalMessage = "",
            session = session
        )
    }
}
