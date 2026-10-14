package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ConditionItem
import com.example.data.model.HosAnalysis
import com.example.data.model.InfoProvenance
import com.example.data.model.KkzCalculation
import com.example.data.model.RealityMap
import com.example.data.model.RmaAnalysis
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

        // High-Fidelity Socratic Engine Fallback with Full Architecture
        generateLocalSocraticDiagnosis(userName, userStory, currentUid)
    }

    private fun callGeminiApi(apiKey: String, userName: String, userStory: String, currentUid: String): DiagnosisResult? {
        val candidateModels = listOf("gemini-2.5-flash", "gemini-2.5-pro", "gemini-1.5-flash")

        val prompt = """
            Anda adalah MAIEUTIKA, sistem Reality Intelligence & Adaptive Decision System berlandaskan metode Sokratik.
            Tagline: "Google Maps Kehidupan"
            Prinsip Utama: "Diagnosis Mendahului Jawaban. Membangun AI yang Melihat Sebelum Menjawab."
            
            Profil Pengguna: $userName
            Keluhan/Situasi: "$userStory"
            
            Lakukan analisis menyeluruh menggunakan 5 pilar inti arsitektur MAIEUTIKA:
            1. RMA (Root Mechanism Analysis):
               - problem_statement (Masalah inti objektif)
               - declared_interests (Keinginan pengguna yang tampak)
               - confirmed_interests (Kepentingan yang mendasari dan relevan)
               - possible_interests (Kepentingan tersembunyi/psikologis)
               - observed_conditions (Kondisi empiris yang teramati dari narasi)
               - mechanism_hypotheses (4 hipotesis mekanisme sebab-akibat pembentuk masalah)
               - supporting_evidence (Bukti pendukung dari cerita)
               - missing_information (Fakta penting yang belum diketahui)
               - uncertainties (Asumsi dan ketidakpastian)
               - potential_intervention_points (Titik ungkit intervensi realistis)
               - possible_consequences (Konsekuensi tindakan terhadap diri & pihak lain)
               - next_questions (Pertanyaan sokratik penyelidik berikutnya)
            
            2. HOS (Human Observational System):
               - current_conditions: array berisi 8 dimensi (Keuangan, Keluarga, Karier, Relasi, Tujuan, Risiko, Psikologi, Kesehatan) dengan nama dimensi, deskripsi ringkas kondisi, estimasi skor 0-100, provenance ("USER_REPORTED", "SYSTEM_CALCULATED", "AI_INTERPRETATION"), verifikasiStatus ("Belum diverifikasi" atau "Laporan Awal").
               - environmental_factors (Faktor lingkungan luar/eksternal)
               - detected_changes (Pola atau perubahan yang terdeteksi)
               - missing_data (Data apa saja yang belum tersedia)
               - inconsistencies (Ketidakkonsistenan antara harapan vs fakta)
               - adequacy_score (Tingkat ketercukupan informasi 0-100)
               - items_to_observe (Hal penting yang perlu diamati di dunia nyata)
            
            3. KKZ (Kalkulasi Kesiapan Zaman & Kondisi):
               - readiness_index (0-100 indeks kesiapan eksekusi)
               - internal_resource_capacity (Kapasitas energi, waktu, mental: "Rendah", "Cukup", atau "Optimal")
               - mental_energy_score (0-100)
               - time_availability ("30 menit/hari", "1-2 jam/hari", dsb)
               - external_friction_level ("Rendah", "Sedang", atau "Tinggi")
               - structural_constraints (Kendala struktural/hambatan nyata)
               - dependency_risks (Risiko ketergantungan pihak lain)
               - readiness_verdict (Kesimpulan kesiapan tindakan)
            
            4. OIE - Skenario & ZEBRA (Tindakan Nyata Prioritas Hari Ini):
               - 3 Skenario: Stabil, Berani, Agresif (nama, deskripsi, probabilitas %, risiko %, dampak %)
               - rekomendasi_skenario
               - zebra: tindakan konkret mikro dengan:
                 * instruksi (aksi terukur spesifik kapan dan bagaimana)
                 * tujuan
                 * alasan_pemilihan
                 * kondisi_mendasari
                 * sumber_daya_diperlukan
                 * kendala
                 * risiko
                 * indikator_hasil
                 * jangka_waktu_evaluasi ("24 Jam", "48 Jam")
                 * kondisi_peninjauan
                 * dampak_poin (10-18 poin)
                 * status ("pending")
            
            Keluarkan HANYA format JSON valid tanpa tanda markdown (tanpa ```json):
            {
              "isUnethical": false,
              "unethicalRefusalMessage": "",
              "diagnosis": "Penjelasan reflektif Sokratik mendalam...",
              "tingkatRisiko": 45,
              "tingkatKepercayaanAI": 90,
              "rma": {
                "problem_statement": "...",
                "declared_interests": ["..."],
                "confirmed_interests": ["..."],
                "possible_interests": ["..."],
                "observed_conditions": ["..."],
                "mechanism_hypotheses": ["..."],
                "supporting_evidence": ["..."],
                "missing_information": ["..."],
                "uncertainties": ["..."],
                "potential_intervention_points": ["..."],
                "possible_consequences": ["..."],
                "next_questions": ["..."]
              },
              "hos": {
                "current_conditions": [
                  {"dimensi": "Keuangan", "deskripsi": "...", "skor": 60, "provenance": "USER_REPORTED", "verifikasiStatus": "Belum diverifikasi"},
                  {"dimensi": "Keluarga", "deskripsi": "...", "skor": 70, "provenance": "USER_REPORTED", "verifikasiStatus": "Belum diverifikasi"},
                  {"dimensi": "Karier", "deskripsi": "...", "skor": 55, "provenance": "USER_REPORTED", "verifikasiStatus": "Belum diverifikasi"},
                  {"dimensi": "Relasi", "deskripsi": "...", "skor": 65, "provenance": "USER_REPORTED", "verifikasiStatus": "Belum diverifikasi"},
                  {"dimensi": "Tujuan", "deskripsi": "...", "skor": 50, "provenance": "USER_REPORTED", "verifikasiStatus": "Belum diverifikasi"},
                  {"dimensi": "Risiko", "deskripsi": "...", "skor": 45, "provenance": "SYSTEM_CALCULATED", "verifikasiStatus": "Belum diverifikasi"},
                  {"dimensi": "Psikologi", "deskripsi": "...", "skor": 55, "provenance": "USER_REPORTED", "verifikasiStatus": "Belum diverifikasi"},
                  {"dimensi": "Kesehatan", "deskripsi": "...", "skor": 68, "provenance": "USER_REPORTED", "verifikasiStatus": "Belum diverifikasi"}
                ],
                "environmental_factors": ["..."],
                "detected_changes": ["..."],
                "missing_data": ["..."],
                "inconsistencies": ["..."],
                "adequacy_score": 70,
                "items_to_observe": ["..."]
              },
              "kkz": {
                "readiness_index": 68,
                "internal_resource_capacity": "Cukup untuk Aksi Terfokus",
                "mental_energy_score": 62,
                "time_availability": "30-60 menit/hari",
                "external_friction_level": "Sedang",
                "structural_constraints": ["..."],
                "dependency_risks": ["..."],
                "readiness_verdict": "..."
              },
              "skenario": [
                {"nama": "Skenario A (Stabil)", "deskripsi": "...", "probabilitas": 75, "risiko": 25, "dampak": 45},
                {"nama": "Skenario B (Berani)", "deskripsi": "...", "probabilitas": 60, "risiko": 50, "dampak": 75},
                {"nama": "Skenario C (Agresif)", "deskripsi": "...", "probabilitas": 35, "risiko": 80, "dampak": 90}
              ],
              "rekomendasiSkenario": "Skenario B (Berani)",
              "zebra": {
                "instruksi": "...",
                "tujuan": "...",
                "alasanPemilihan": "...",
                "kondisiMendasari": "...",
                "sumberDayaDiperlukan": "...",
                "kendala": "...",
                "risiko": "...",
                "indikatorHasil": "...",
                "jangkaWaktuEvaluasi": "24 Jam",
                "kondisiPeninjauan": "...",
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
        val tingkatRisiko = json.optDouble("tingkatRisiko", 50.0)
        val tingkatKepercayaanAI = json.optDouble("tingkatKepercayaanAI", 88.0)
        val rekomendasiSkenario = json.optString("rekomendasiSkenario", "Skenario B (Berani)")

        // Parse RMA
        val rmaObj = json.optJSONObject("rma")
        val rma = if (rmaObj != null) {
            RmaAnalysis(
                problemStatement = rmaObj.optString("problem_statement", "Ketidakjelasan arah akibat persepsi yang belum teruji"),
                declaredInterests = jsonArrayToList(rmaObj.optJSONArray("declared_interests")),
                confirmedInterests = jsonArrayToList(rmaObj.optJSONArray("confirmed_interests")),
                possibleInterests = jsonArrayToList(rmaObj.optJSONArray("possible_interests")),
                observedConditions = jsonArrayToList(rmaObj.optJSONArray("observed_conditions")),
                mechanismHypotheses = jsonArrayToList(rmaObj.optJSONArray("mechanism_hypotheses")),
                supportingEvidence = jsonArrayToList(rmaObj.optJSONArray("supporting_evidence")),
                missingInformation = jsonArrayToList(rmaObj.optJSONArray("missing_information")),
                uncertainties = jsonArrayToList(rmaObj.optJSONArray("uncertainties")),
                potentialInterventionPoints = jsonArrayToList(rmaObj.optJSONArray("potential_intervention_points")),
                possibleConsequences = jsonArrayToList(rmaObj.optJSONArray("possible_consequences")),
                nextQuestions = jsonArrayToList(rmaObj.optJSONArray("next_questions"))
            )
        } else {
            RmaAnalysis(
                problemStatement = "Disparitas antara ekspektasi mental dan aksi konkret",
                mechanismHypotheses = listOf(
                    "Asumsi yang tidak diuji membatasi pilihan tindakan",
                    "Kecemasan antisipatif terhadap hasil yang belum tentu terjadi",
                    "Ketiadaan protokol tindakan harian yang terukur"
                )
            )
        }

        // Parse HOS
        val hosObj = json.optJSONObject("hos")
        val hosConditions = mutableListOf<ConditionItem>()
        val petaRealitas = mutableMapOf<String, Any>()

        if (hosObj != null) {
            val condArray = hosObj.optJSONArray("current_conditions")
            if (condArray != null) {
                for (i in 0 until condArray.length()) {
                    val c = condArray.optJSONObject(i) ?: continue
                    val dim = c.optString("dimensi")
                    val score = c.optInt("skor", 50)
                    hosConditions.add(
                        ConditionItem(
                            dimensi = dim,
                            deskripsi = c.optString("deskripsi", ""),
                            skor = score,
                            provenance = c.optString("provenance", InfoProvenance.USER_REPORTED.name),
                            verifikasiStatus = c.optString("verifikasiStatus", "Belum diverifikasi")
                        )
                    )
                    petaRealitas[dim.lowercase()] = score
                }
            }
        }

        if (hosConditions.isEmpty()) {
            val defaultDims = listOf("keuangan", "keluarga", "karier", "relasi", "tujuan", "risiko", "psikologi", "kesehatan")
            defaultDims.forEach { dim ->
                petaRealitas[dim] = 50
                hosConditions.add(
                    ConditionItem(
                        dimensi = dim.replaceFirstChar { it.uppercase() },
                        deskripsi = "Estimasi baseline dimensi $dim",
                        skor = 50,
                        provenance = InfoProvenance.USER_REPORTED.name,
                        verifikasiStatus = "Belum diverifikasi"
                    )
                )
            }
        }

        val hos = HosAnalysis(
            currentConditions = hosConditions,
            environmentalFactors = jsonArrayToList(hosObj?.optJSONArray("environmental_factors")),
            detectedChanges = jsonArrayToList(hosObj?.optJSONArray("detected_changes")),
            missingData = jsonArrayToList(hosObj?.optJSONArray("missing_data")),
            inconsistencies = jsonArrayToList(hosObj?.optJSONArray("inconsistencies")),
            adequacyScore = hosObj?.optInt("adequacy_score", 65) ?: 65,
            itemsToObserve = jsonArrayToList(hosObj?.optJSONArray("items_to_observe"))
        )

        // Parse KKZ
        val kkzObj = json.optJSONObject("kkz")
        val kkz = KkzCalculation(
            readinessIndex = kkzObj?.optInt("readiness_index", 65) ?: 65,
            internalResourceCapacity = kkzObj?.optString("internal_resource_capacity", "Cukup untuk Aksi Terfokus") ?: "Cukup untuk Aksi Terfokus",
            mentalEnergyScore = kkzObj?.optInt("mental_energy_score", 60) ?: 60,
            timeAvailability = kkzObj?.optString("time_availability", "30-60 menit/hari") ?: "30-60 menit/hari",
            externalFrictionLevel = kkzObj?.optString("external_friction_level", "Sedang") ?: "Sedang",
            structuralConstraints = jsonArrayToList(kkzObj?.optJSONArray("structural_constraints")),
            dependencyRisks = jsonArrayToList(kkzObj?.optJSONArray("dependency_risks")),
            readinessVerdict = kkzObj?.optString("readiness_verdict", "Kondisi siap untuk uji intervensi mikro.") ?: "Kondisi siap untuk uji intervensi mikro."
        )

        // Parse ZEBRA (OIE Eksekusi)
        val zebraJson = json.optJSONObject("zebra")
        val zebra = ZebraAction(
            instruksi = zebraJson?.optString("instruksi") ?: "Besok pukul 08.00, duduk selama 25 menit tanpa gawai. Tuliskan 3 hambatan terbesar dan pilih 1 untuk diuji.",
            tujuan = zebraJson?.optString("tujuan") ?: "Mendapatkan bukti empiris pertama di dunia nyata",
            alasanPemilihan = zebraJson?.optString("alasanPemilihan") ?: "Aksi dengan risiko terkecil namun memberikan kejernihan terbesar",
            kondisiMendasari = zebraJson?.optString("kondisiMendasari") ?: "Kebutuhan memisahkan asumsi dari fakta",
            sumberDayaDiperlukan = zebraJson?.optString("sumberDayaDiperlukan") ?: "Waktu 25 menit, buku catatan",
            kendala = zebraJson?.optString("kendala") ?: "Godaan menunda karena rasa cemas semu",
            risiko = zebraJson?.optString("risiko") ?: "Ketidaknyamanan menghadapi fakta",
            indikatorHasil = zebraJson?.optString("indikatorHasil") ?: "Catatan konkret 3 fakta objektif",
            jangkaWaktuEvaluasi = zebraJson?.optString("jangkaWaktuEvaluasi") ?: "24 Jam",
            kondisiPeninjauan = zebraJson?.optString("kondisiPeninjauan") ?: "Bila terjadi resistensi tak terduga",
            dampakPoin = zebraJson?.optInt("dampakPoin", 14) ?: 14,
            status = "pending"
        )

        // Parse Skenario
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

        // Calculate RBSS using deterministic formula
        val rbss = RbssEngine.calculateInitialRbss(rma, hos, kkz, zebra, tingkatRisiko)

        val session = SocraticSession(
            uid = currentUid,
            ceritaPengguna = userStory,
            diagnosis = diagnosis,
            petaRealitas = petaRealitas,
            analisisAkarMasalah = rma.mechanismHypotheses,
            masalahUtama = rma.problemStatement,
            tingkatRisiko = tingkatRisiko,
            tingkatKepercayaanAI = tingkatKepercayaanAI,
            skenario = skenarioList,
            rekomendasiSkenario = rekomendasiSkenario,
            zebra = zebra.toMap().filterValues { it != null }.mapValues { it.value!! },
            rmaData = rma.toMap(),
            hosData = hos.toMap(),
            kkzData = kkz.toMap(),
            rbssData = rbss.toMap()
        )

        return DiagnosisResult(
            isUnethical = false,
            unethicalRefusalMessage = "",
            session = session
        )
    }

    private fun jsonArrayToList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            val s = arr.optString(i)
            if (s.isNotBlank()) list.add(s)
        }
        return list
    }

    private fun generateLocalSocraticDiagnosis(userName: String, userStory: String, currentUid: String): DiagnosisResult {
        val lower = userStory.lowercase()

        val isCareer = lower.contains("kerja") || lower.contains("karier") || lower.contains("kantor") || lower.contains("bos") || lower.contains("usaha") || lower.contains("bisnis")
        val isFinance = lower.contains("uang") || lower.contains("gaji") || lower.contains("hutang") || lower.contains("finansial") || lower.contains("modal") || lower.contains("investasi")
        val isRelation = lower.contains("pasangan") || lower.contains("pacar") || lower.contains("istri") || lower.contains("suami") || lower.contains("teman") || lower.contains("keluarga")
        val isMental = lower.contains("lelah") || lower.contains("stres") || lower.contains("cemas") || lower.contains("bingung") || lower.contains("takut") || lower.contains("putus asa")

        // 1. RMA
        val problemStatement = when {
            isCareer -> "Friksi kognitif antara ekspektasi lonjakan karier dan keengganan mengambil risiko penolakan objektif."
            isFinance -> "Kebocoran energi akibat memprioritaskan rasa aman semu dibandingkan audit fakta finansial nyata."
            isRelation -> "Ekspektasi batin tanpa komunikasi asertif yang memicu distorsi persepsi relasional."
            isMental -> "Kelumpuhan analisis (analysis paralysis) akibat membebani pikiran dengan variabel di luar kendali langsung."
            else -> "Disparitas antara narasi ideal dalam pikiran dan tindakan konfrontasi terhadap realitas objektif."
        }

        val declaredInterests = listOf(
            "Menginginkan kepastian dan hasil positif sesegera mungkin",
            "Menghindari konflik atau rasa bersalah terhadap lingkungan"
        )
        val confirmedInterests = listOf(
            "Mempertahankan stabilitas psikologis dan martabat diri",
            "Mendapatkan kendali kembali atas alokasi waktu dan energi harian"
        )
        val possibleInterests = listOf(
            "Kebutuhan validasi eksternal sebelum berani mengambil sikap tegas",
            "Kekhawatiran akan citra kegagalan di mata orang-orang terdekat"
        )
        val observedConditions = listOf(
            "Beban masalah telah dirasakan secara berulang dan menyita perhatian pengguna",
            "Pengguna telah mencoba berpikir berulang kali namun belum menemukan titik ungkit konkret",
            "Terdapat hambatan friksi mental dalam memulai langkah pertama"
        )
        val mechanismHypotheses = listOf(
            "Mekanisme 1 (Kabut Asumsi): Menganggap prasangka atau skenario buruk sebagai fakta yang pasti terjadi",
            "Mekanisme 2 (Kebocoran Energi): Mengalokasikan 70% pikiran pada hal di luar kendali diri",
            "Mekanisme 3 (Penundaan Konfrontasi): Menunda percakapan atau tindakan kunci yang sebenarnya merupakan gerbang solusi",
            "Mekanisme 4 (Ketiadaan Protokol Harian): Tidak adanya batasan waktu dan tindakan mikro yang terlindungi"
        )
        val supportingEvidence = listOf(
            "Narasi yang diungkapkan pengguna menunjukkan pola kelelahan antisipatif",
            "Belum ada data empiris terbaru yang diverifikasi langsung dalam 7 hari terakhir"
        )
        val missingInformation = listOf(
            "Respon nyata pihak terkait jika batasan baru dikomunikasikan secara asertif",
            "Batas daya tahan finansial atau mental yang sebenarnya jika skenario terburuk terjadi"
        )
        val uncertainties = listOf(
            "Apakah penolakan yang ditakuti benar-benar akan terjadi atau hanya ilusi kognitif?",
            "Seberapa besar kapasitas dukungan dari orang-orang terdekat yang belum pernah diminta bantuan?"
        )
        val potentialInterventionPoints = listOf(
            "Titik Ungkit 1: Hentikan pemikiran abstrak, beralih ke audit tulisan di atas kertas",
            "Titik Ungkit 2: Lakukan satu percakapan klarifikasi berdurasi singkat tanpa menuntut hasil sempurna",
            "Titik Ungkit 3: Blokade 30 menit besok pagi sebagai benteng fokus tanpa gawai"
        )
        val possibleConsequences = listOf(
            "Konsekuensi Positif: Penurunan tingkat kecemasan sebesar 40% setelah fakta tercatat jelas",
            "Konsekuensi Negatif Sementara: Rasa tidak nyaman singkat saat mengkonfrontasi kenyataan mentah"
        )
        val nextQuestions = listOf(
            "Apakah kesimpulan Anda saat ini dibangun atas fakta yang telah Anda buktikan sendiri, atau hanya cerita di kepala Anda?",
            "Apa yang paling Anda takuti jika Anda bersikap apa adanya hari ini?"
        )

        val rma = RmaAnalysis(
            problemStatement = problemStatement,
            declaredInterests = declaredInterests,
            confirmedInterests = confirmedInterests,
            possibleInterests = possibleInterests,
            observedConditions = observedConditions,
            mechanismHypotheses = mechanismHypotheses,
            supportingEvidence = supportingEvidence,
            missingInformation = missingInformation,
            uncertainties = uncertainties,
            potentialInterventionPoints = potentialInterventionPoints,
            possibleConsequences = possibleConsequences,
            nextQuestions = nextQuestions
        )

        // 2. HOS
        val financeScore = if (isFinance) 40 else 65
        val familyScore = if (isRelation) 45 else 72
        val careerScore = if (isCareer) 44 else 68
        val relationScore = if (isRelation) 42 else 66
        val goalScore = 52
        val riskScore = if (isCareer || isFinance) 62 else 45
        val psychScore = if (isMental) 38 else 62
        val healthScore = if (isMental) 50 else 74

        val currentConditions = listOf(
            ConditionItem("Keuangan", "Arus kas dan beban kebutuhan", financeScore, InfoProvenance.USER_REPORTED.name, "Laporan Awal"),
            ConditionItem("Keluarga", "Keseimbangan peran dan ekspektasi keluarga", familyScore, InfoProvenance.USER_REPORTED.name, "Laporan Awal"),
            ConditionItem("Karier", "Kejelasan arah profesional dan tanggung jawab kerja", careerScore, InfoProvenance.USER_REPORTED.name, "Laporan Awal"),
            ConditionItem("Relasi", "Kualitas komunikasi dan batas personal", relationScore, InfoProvenance.USER_REPORTED.name, "Laporan Awal"),
            ConditionItem("Tujuan", "Konsistensi langkah dengan nilai hidup esensial", goalScore, InfoProvenance.USER_REPORTED.name, "Laporan Awal"),
            ConditionItem("Risiko", "Potensi kerentanan terhadap tekanan eksternal", riskScore, InfoProvenance.SYSTEM_CALCULATED.name, "Dihitung dari Pola"),
            ConditionItem("Psikologi", "Tingkat ketenangan mental dan kejernihan persepsi", psychScore, InfoProvenance.USER_REPORTED.name, "Laporan Awal"),
            ConditionItem("Kesehatan", "Energi fisik dan pemulihan istirahat", healthScore, InfoProvenance.USER_REPORTED.name, "Laporan Awal")
        )

        val hos = HosAnalysis(
            currentConditions = currentConditions,
            environmentalFactors = listOf(
                "Tuntutan lingkungan sekitar yang serba cepat dan menuntut hasil instan",
                "Arus informasi digital yang memperbesar bias perbandingan sosial"
            ),
            detectedChanges = listOf(
                "Pola penumpukan beban kognitif yang belum diimbangi pelepasan aksi konkret"
            ),
            missingData = listOf(
                "Data tertulis mengenai rincian angka/fakta riil situasi terkini",
                "Respon empiris pihak luar terhadap usulan solusi"
            ),
            inconsistencies = listOf(
                "Keinginan mendapatkan rasa damai, tetapi secara konsisten memikirkan variabel di luar kendali"
            ),
            adequacyScore = 65,
            itemsToObserve = listOf(
                "Bagaimana reaksi emosional Anda saat pertama kali menuliskan ketakutan terbesar secara hitam di atas putih?",
                "Apakah hambatan yang Anda hadapi bersumber dari fakta lingkungan atau narasi kecemasan diri?"
            )
        )

        val petaRealitas = mapOf(
            "keuangan" to financeScore,
            "keluarga" to familyScore,
            "karier" to careerScore,
            "relasi" to relationScore,
            "tujuan" to goalScore,
            "risiko" to riskScore,
            "psikologi" to psychScore,
            "kesehatan" to healthScore
        )

        // 3. KKZ
        val readinessIndex = (100 - riskScore + 20).coerceIn(45, 85)
        val kkz = KkzCalculation(
            readinessIndex = readinessIndex,
            internalResourceCapacity = if (isMental) "Perlu Penataan Energi" else "Cukup untuk Aksi Terfokus",
            mentalEnergyScore = if (isMental) 48 else 68,
            timeAvailability = "30-45 menit/hari (tanpa gangguan)",
            externalFrictionLevel = if (isCareer || isFinance) "Sedang ke Tinggi" else "Sedang",
            structuralConstraints = listOf(
                "Keterbatasan waktu luang akibat rutinitas wajib harian",
                "Kebiasaan lama menunda konfrontasi terhadap fakta yang tidak menyenangkan"
            ),
            dependencyRisks = listOf(
                "Ketergantungan pada respon atau persetujuan pihak lain"
            ),
            readinessVerdict = "Kondisi saat ini sangat memadai untuk tindakan mikro terfokus (Micro-Action). Hindari lompatan radikal sebelum fakta empiris terkumpul."
        )

        // 4. OIE - Skenario & ZEBRA
        val skenarioList = listOf(
            ScenarioItem(
                nama = "Skenario A (Stabil)",
                deskripsi = "Pertahankan status quo selama 14 hari sembari mengumpulkan data empiris harian tanpa membuat keputusan impulsif.",
                probabilitas = 78,
                risiko = 22,
                dampak = 42
            ).toMap(),
            ScenarioItem(
                nama = "Skenario B (Berani)",
                deskripsi = "Lakukan restrukturisasi batas personal: potong 1 kebiasaan penguras energi, lakukan 1 percakapan kunci, dan audit fakta riil.",
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

        val zebraInstruction = "Besok pagi pukul 08.00 - 08.30 WIB (30 menit tanpa gawai): Tuliskan di kertas 3 hal terburuk yang Anda takutkan. Beri skor 1-10 seberapa realistis hal itu terjadi berdasarkan fakta nyata (bukan asumsi). Pilih satu tindakan paling mendasar untuk membungkam ketakutan nomor 1."

        val zebra = ZebraAction(
            instruksi = zebraInstruction,
            tujuan = "Membedah kabut asumsi menjadi data faktual yang dapat dikendalikan",
            alasanPemilihan = "Langkah berbiaya nol dengan friksi terendah namun memiliki dampak psikologis tertinggi untuk membuka kebuntuan",
            kondisiMendasari = "Kecemasan pengguna saat ini didominasi oleh skenario abstrak di kepala yang belum pernah diuji di atas kertas",
            sumberDayaDiperlukan = "Kertas, pulpen, waktu hening 30 menit terlindung dari notifikasi gawai",
            kendala = "Kecenderungan membuka ponsel atau menganggap sepele latihan penulisan fakta",
            risiko = "Munculnya rasa tegang sesaat ketika harus jujur melihat ketakutan terbesar",
            indikatorHasil = "Satu lembar kertas berisi daftar 3 ketakutan terukur dan 1 komitmen aksi nyata",
            jangkaWaktuEvaluasi = "24 Jam",
            kondisiPeninjauan = "Jika tingkat stres berada di atas ambang toleransi, persingkat durasi menjadi 15 menit",
            dampakPoin = 14,
            status = "pending"
        )

        // 5. RBSS
        val rbss = RbssEngine.calculateInitialRbss(rma, hos, kkz, zebra, riskScore.toDouble())

        val diagnosisText = """
            $userName, setelah membedah tuturan Anda melalui pisau bedah Sokratik, yang tampak di permukaan bukanlah ketiadaan kemampuan, melainkan 'kabut asumsi'. Anda sedang bergulat dengan tarikan antara apa yang Anda rasa 'harus' terjadi dan realitas mentah di hadapan Anda.
            
            Socrates pernah mengingatkan: 'Kehidupan yang tidak diperiksa tidak layak dijalani.' Dalam konteks ini, Anda membiarkan energi Anda terkuras oleh lingkaran kekhawatiran alih-alih lingkaran kendali. Masalah Anda bukan karena dunia terlalu rumit, tetapi karena Anda belum memisahkan fakta dingin dari narasi emosional di kepala Anda.
        """.trimIndent()

        val session = SocraticSession(
            uid = currentUid,
            ceritaPengguna = userStory,
            diagnosis = diagnosisText,
            petaRealitas = petaRealitas,
            analisisAkarMasalah = rma.mechanismHypotheses,
            masalahUtama = rma.problemStatement,
            tingkatRisiko = riskScore.toDouble(),
            tingkatKepercayaanAI = 89.0,
            skenario = skenarioList,
            rekomendasiSkenario = "Skenario B (Berani)",
            zebra = zebra.toMap().filterValues { it != null }.mapValues { it.value!! },
            rmaData = rma.toMap(),
            hosData = hos.toMap(),
            kkzData = kkz.toMap(),
            rbssData = rbss.toMap()
        )

        return DiagnosisResult(
            isUnethical = false,
            unethicalRefusalMessage = "",
            session = session
        )
    }

    /**
     * SIKLUS PENUTUP: TINDAKAN NYATA -> OBSERVASI HASIL -> PEMBARUAN ANALISIS
     * Mengkalibrasi ulang sesi berdasarkan bukti observasi dunia nyata yang dilaporkan pengguna.
     */
    fun recalibrateAnalysisWithObservation(
        session: SocraticSession,
        observationText: String,
        outcomeStatus: String
    ): SocraticSession {
        val currentRma = session.getParsedRma()
        val currentHos = session.getParsedHos()
        val currentKkz = session.getParsedKkz()
        val currentZebra = session.getParsedZebra()
        val currentRbss = session.getParsedRbss()

        // 1. Perbarui ZEBRA dengan observasi hasil
        val updatedZebra = currentZebra.copy(
            status = outcomeStatus,
            observasiHasil = observationText,
            tanggalEksekusi = System.currentTimeMillis()
        )

        // 2. Perbarui RMA: Validasi hipotesis mekanisme berdasarkan hasil observasi nyata
        val updatedHypotheses = currentRma.mechanismHypotheses.mapIndexed { idx, hyp ->
            if (idx == 0) {
                if (outcomeStatus == "done") {
                    "$hyp [TERVERIFIKASI EMPIRIS: Intervensi membuktikan fakta baru]"
                } else {
                    "$hyp [KOREKSI MEKANISME: Ditemukan hambatan struktural baru]"
                }
            } else hyp
        }

        val updatedSupportingEvidence = currentRma.supportingEvidence.toMutableList().apply {
            add("Observasi Nyata Pengguna: \"${observationText.take(100)}\"")
        }

        val updatedRma = currentRma.copy(
            mechanismHypotheses = updatedHypotheses,
            supportingEvidence = updatedSupportingEvidence
        )

        // 3. Perbarui HOS: Tingkatkan status verifikasi kondisi terkait
        val updatedConditions = currentHos.currentConditions.map { cond ->
            if (cond.dimensi.equals("Psikologi", ignoreCase = true) || cond.dimensi.equals("Karier", ignoreCase = true)) {
                cond.copy(
                    verifikasiStatus = if (outcomeStatus == "done") "Terverifikasi lewat Aksi Nyata" else "Terdeteksi Resistensi",
                    skor = if (outcomeStatus == "done") (cond.skor + 6).coerceAtMost(95) else cond.skor
                )
            } else cond
        }

        val updatedHos = currentHos.copy(
            currentConditions = updatedConditions,
            adequacyScore = (currentHos.adequacyScore + 8).coerceAtMost(95),
            detectedChanges = currentHos.detectedChanges.toMutableList().apply {
                add("Hasil uji tindakan nyata tercatat: $outcomeStatus")
            }
        )

        // 4. Perbarui KKZ: Resiliensi dan kesiapan meningkat setelah pembuktian
        val updatedKkz = currentKkz.copy(
            readinessIndex = (currentKkz.readinessIndex + (if (outcomeStatus == "done") 7 else 2)).coerceAtMost(95),
            mentalEnergyScore = (currentKkz.mentalEnergyScore + (if (outcomeStatus == "done") 8 else -2)).coerceIn(30, 95),
            readinessVerdict = if (outcomeStatus == "done") {
                "Siklus pembuktian berhasil. Realitas kini lebih jernih dan pengguna siap melangkah ke tindakan terstruktur berikutnya."
            } else {
                "Tindakan mengungkap friksi nyata. Sesuaikan ekspektasi dan sederhanakan skala intervensi."
            }
        )

        // 5. Perbarui RBSS secara deterministik
        val updatedRbss = RbssEngine.recalculateOnActionExecution(
            currentRbss = currentRbss,
            newStatus = outcomeStatus,
            observation = observationText,
            impactPoints = updatedZebra.dampakPoin
        )

        val updatedPetaRealitas = HashMap(session.petaRealitas)
        updatedConditions.forEach {
            updatedPetaRealitas[it.dimensi.lowercase()] = it.skor
        }

        return session.copy(
            petaRealitas = updatedPetaRealitas,
            analisisAkarMasalah = updatedRma.mechanismHypotheses,
            zebra = updatedZebra.toMap().filterValues { it != null }.mapValues { it.value!! },
            rmaData = updatedRma.toMap(),
            hosData = updatedHos.toMap(),
            kkzData = updatedKkz.toMap(),
            rbssData = updatedRbss.toMap()
        )
    }
}
