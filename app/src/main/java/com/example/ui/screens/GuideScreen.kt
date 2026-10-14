package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.ui.graphics.Color
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
                text = "ARSITEKTUR & PANDUAN",
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
                        text = "Reality Intelligence & Adaptive Decision System",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = SoftWhite,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "\"Diagnosis harus mendahului jawaban. Sistem harus membantu mengubah pemahaman pola menjadi ketepatan tindakan di dunia nyata.\"",
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

            // 5 Komponen Inti Arsitektur
            Text(
                text = "5 Komponen Arsitektur Inti MAIEUTIKA",
                style = MaterialTheme.typography.titleMedium,
                color = GoldLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            GuidePillarItem(
                number = "1",
                title = "RMA: Root Mechanism Analysis",
                desc = "Menganalisis mekanisme yang melahirkan masalah. Membedakan keinginan yang dinyatakan, kepentingan mendasari, bukti pendukung, hipotesis mekanisme, titik intervensi realistis, dan ketidakpastian.",
                icon = Icons.Default.AccountTree
            )

            GuidePillarItem(
                number = "2",
                title = "HOS: Human Observational System",
                desc = "Membaca kondisi nyata manusia dan lingkungan pada 8 dimensi objektif. Setiap data memiliki provenance yang jelas (Dilaporkan Pengguna, Kalkulasi Data, Observasi Nyata) dan mendeteksi data yang hilang.",
                icon = Icons.Default.Visibility
            )

            GuidePillarItem(
                number = "3",
                title = "OIE: Observasi - Interaksi - Eksekusi",
                desc = "Menghubungkan observasi dengan dialog sokratik elenchus dan eksekusi tindakan nyata (ZEBRA). Setiap aksi memuat tujuan, alasan, sumber daya, risiko, indikator hasil, dan batas evaluasi.",
                icon = Icons.AutoMirrored.Filled.FactCheck
            )

            GuidePillarItem(
                number = "4",
                title = "KKZ: Kalkulasi Kesiapan Zaman & Kondisi",
                desc = "Menghitung kesiapan riil pengguna: kapasitas energi mental, waktu harian, tingkat friksi lingkungan, kendala struktural, dan risiko ketergantungan agar aksi tidak melampaui daya dukung nyata.",
                icon = Icons.Default.Speed
            )

            GuidePillarItem(
                number = "5",
                title = "RBSS: Reality-Based Scoring System",
                desc = "Sistem penilaian realitas berbasis bukti matematis: 25% Ketercukupan Fakta + 25% Penyelarasan Persepsi + 30% Eksekusi Nyata + 20% Resiliensi Risiko. Dilengkapi jejak audit (audit trail) transparan.",
                icon = Icons.Default.Security
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Siklus Penutup
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = BorderStroke(1.dp, GoldAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Siklus Umpan Balik Berkelanjutan",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "RMA → HOS → OIE → KKZ → RBSS\n  ↓\nTINDAKAN NYATA (ZEBRA)\n  ↓\nOBSERVASI HASIL DI LAPANGAN\n  ↓\nPEMBARUAN ANALISIS & REKALIBRASI HIPOTESIS",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GoldAccent,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Text(
                        text = "Ketika pengguna menjalankan aksi dan mencatat fakta yang terjadi di dunia nyata, sistem menyerap bukti tersebut kembali ke dalam hipotesis mekanisme (RMA), memperbarui status verifikasi HOS, dan mengkalibrasi ulang skor realitas tanpa halusinasi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftWhite,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Kubah Etika
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = BorderStroke(1.dp, ObsidianBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFFD4AF37),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kubah Etika (Ethical Dome)",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MAIEUTIKA menolak memandu tindakan manipulatif, curang, atau destruktif. Solusi sejati hanya lahir dari kejujuran melihat kenyataan dan integritas moral.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateGray,
                        lineHeight = 18.sp
                    )
                }
            }

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
