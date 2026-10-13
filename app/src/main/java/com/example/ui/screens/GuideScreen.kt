package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite

@Composable
fun GuideScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.ONBOARDING)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.ONBOARDING) },
                modifier = Modifier.testTag("guide_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Beranda",
                    tint = GoldAccent
                )
            }

            Text(
                text = "PANDUAN & FILOSOFI",
                style = MaterialTheme.typography.titleMedium,
                color = GoldLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Book Title Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GoldContainer),
                border = BorderStroke(1.5.dp, GoldAccent),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "MAIEUTIKA",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 24.sp,
                            letterSpacing = 2.sp,
                            color = GoldLight
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Membangun AI yang Melihat Sebelum Menjawab",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = SoftWhite,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "\"Diagnosis harus mendahului jawaban. Kecepatan tanpa pemahaman adalah ilusi kognitif.\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            color = GoldLight,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pengantar Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = BorderStroke(1.dp, ObsidianBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Pengantar & Fondasi Konseptual",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fokus MAIEUTIKA adalah mentransformasikan log pemikiran dan percakapan manusia menjadi narasi sistematis sebagai fondasi kognitif. AI di sini tidak bertindak sebagai mesin penjawab otomatis, melainkan bidan pemikiran (maieutik) yang membantu Anda melahirkan kejernihan dari dalam diri sendiri.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftWhite,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bab 1 Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = BorderStroke(1.dp, ObsidianBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                            .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "1",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Bab 1: Krisis Cara Berpikir",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Perkembangan AI modern menghasilkan kemampuan menjawab yang luar biasa cepat, namun kecepatan tidak selalu identik dengan pemahaman sejati.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftWhite,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = Color(0xFF13131D),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Setiap keluhan dan pertanyaan dipandang sebagai gejala (symptom) yang memiliki kepentingan, mekanisme, dan konteks tersembunyi. Solusi tidak pernah dirumuskan sebelum diagnosis realitas selesai.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGray,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Kerangka Buku (9 Pilar)
            Text(
                text = "9 Kerangka Pilar MAIEUTIKA",
                style = MaterialTheme.typography.titleMedium,
                color = GoldLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            GuidePillarItem(
                number = "1",
                title = "Landasan Filosofis MAIEUTIKA",
                desc = "Metode Socrates: bidan kognitif yang membedah kepalsuan asumsi agar pengguna melihat kebenaran objektif.",
                icon = Icons.Default.Lightbulb
            )

            GuidePillarItem(
                number = "2",
                title = "Root Mechanism Analysis (RMA)",
                desc = "Analisis 4 tingkat mekanisme akar masalah yang mengerucut pada satu Masalah Utama yang dapat diintervensi.",
                icon = Icons.Default.Psychology
            )

            GuidePillarItem(
                number = "3",
                title = "Diagnosis Engine",
                desc = "Mesin penalaran yang menolak jawaban cepat dan memprioritaskan pemahaman mekanisme terdalam.",
                icon = Icons.Default.Visibility
            )

            GuidePillarItem(
                number = "4",
                title = "Reality Mapping (Peta Realitas)",
                desc = "Pemetaan 8 dimensi terukur: Keuangan, Keluarga, Karier, Relasi, Tujuan, Risiko, Psikologi, dan Kesehatan.",
                icon = Icons.Default.CompassCalibration
            )

            GuidePillarItem(
                number = "5",
                title = "Cognitive Operating System",
                desc = "Sistem operasi nalar yang membantu menertibkan kekacauan pikiran dan emosi ke dalam struktur keputusan.",
                icon = Icons.Default.AutoAwesome
            )

            GuidePillarItem(
                number = "6",
                title = "Arsitektur Aplikasi",
                desc = "Alur 5 tahap: Onboarding → Diagnosis Socrates → Simulasi Skenario → ZEBRA Hari Ini → Evaluasi Mingguan.",
                icon = Icons.Default.Timeline
            )

            GuidePillarItem(
                number = "7",
                title = "Validasi pada AI dan Manusia",
                desc = "Memvalidasi apakah rencana hipotetis bekerja di lapangan melalui umpan balik mingguan nyata.",
                icon = Icons.Default.CheckCircle
            )

            GuidePillarItem(
                number = "8",
                title = "Etika dan Tata Kelola (Kubah Etika)",
                desc = "Menolak tegas segala permintaan manipulatif, curang, atau destruktif; mengarahkan nalar pada integritas.",
                icon = Icons.Default.Security
            )

            GuidePillarItem(
                number = "9",
                title = "Blueprint Implementasi & ZEBRA",
                desc = "Menerjemahkan visi makro menjadi 1 tindakan mikro konkret hari ini yang menghasilkan peningkatan Skor Realitas.",
                icon = Icons.Default.Timeline
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun GuidePillarItem(
    number: String,
    title: String,
    desc: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = BorderStroke(1.dp, ObsidianBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(GoldContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldLight,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftWhite.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
