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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.UiState
import com.example.ui.components.ZebraPriorityCard
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.RiskLow
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite

@Composable
fun ZebraScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.SCENARIO)
    }

    val session = uiState.currentSession ?: run {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("Tidak ada sesi aktif.", color = SoftWhite)
        }
        return
    }

    val zebra = session.getParsedZebra()
    val scrollState = rememberScrollState()

    var evalStatus by remember { mutableStateOf("lebih_baik") }
    var evalStory by remember { mutableStateOf("") }

    if (uiState.showWeeklyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleWeeklyDialog(false) },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.EventNote,
                        contentDescription = null,
                        tint = GoldAccent
                    )
                    Text(
                        text = "Evaluasi Mingguan",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Bagaimana perkembangan hidup Anda setelah menjalani langkah ZEBRA minggu ini?",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateGray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "lebih_baik" to "Lebih Baik",
                            "sama" to "Sama Saja",
                            "lebih_buruk" to "Lebih Buruk"
                        ).forEach { (key, label) ->
                            FilterChip(
                                selected = evalStatus == key,
                                onClick = { evalStatus = key },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent,
                                    selectedLabelColor = ObsidianBlack,
                                    containerColor = ObsidianCard,
                                    labelColor = SoftWhite
                                ),
                                border = BorderStroke(1.dp, if (evalStatus == key) GoldAccent else ObsidianBorder),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = evalStory,
                        onValueChange = { evalStory = it },
                        placeholder = { Text("Ceritakan progres atau kendala yang dihadapi...", color = SlateGray, fontSize = 13.sp) },
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = SoftWhite,
                            unfocusedTextColor = SoftWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("weekly_evaluation_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitWeeklyEvaluation(evalStatus, evalStory)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = ObsidianBlack
                    ),
                    modifier = Modifier.testTag("submit_weekly_eval_button")
                ) {
                    Text("Simpan Evaluasi", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleWeeklyDialog(false) }) {
                    Text("Tutup", color = SlateGray)
                }
            },
            containerColor = ObsidianCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

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
                onClick = { viewModel.navigateTo(AppScreen.SCENARIO) },
                modifier = Modifier.testTag("back_to_scenario_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Skenario",
                    tint = GoldAccent
                )
            }

            Text(
                text = "ZEBRA (LANGKAH HARIAN)",
                style = MaterialTheme.typography.titleMedium,
                color = GoldLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.ARCHIVE) },
                modifier = Modifier.testTag("go_to_archive_top_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Ke Arsip",
                    tint = GoldAccent
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ZEBRA Priority Card
            ZebraPriorityCard(
                zebra = zebra,
                onStatusChange = { newStatus -> viewModel.updateZebraStatus(newStatus) },
                isUpdating = uiState.isZebraUpdating
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Evaluasi Mingguan Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = BorderStroke(1.dp, ObsidianBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Evaluasi Mingguan",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (session.evaluasiMingguan != null) {
                            Text(
                                text = "Telah Dievaluasi",
                                style = MaterialTheme.typography.labelSmall,
                                color = RiskLow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (session.evaluasiMingguan != null) {
                        val statusText = when (session.evaluasiMingguan["status"]) {
                            "lebih_baik" -> "Perkembangan: Lebih Baik (+5 Skor)"
                            "sama" -> "Perkembangan: Stabil/Sama (+1 Skor)"
                            else -> "Perkembangan: Menghadapi Kendala (-2 Skor)"
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        val catatan = session.evaluasiMingguan["cerita"] as? String ?: ""
                        if (catatan.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"$catatan\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateGray
                            )
                        }
                    } else {
                        Text(
                            text = "Setelah melakukan tindakan ZEBRA, catat apakah kondisi Anda menjadi Lebih Baik, Sama, atau Lebih Buruk untuk adaptasi strategi berikutnya.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateGray,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { viewModel.toggleWeeklyDialog(true) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                            border = BorderStroke(1.dp, GoldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_weekly_eval_button")
                        ) {
                            Text("Isi Evaluasi Mingguan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation to Archive and Trends
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.ARCHIVE) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ObsidianCard,
                        contentColor = SoftWhite
                    ),
                    border = BorderStroke(1.dp, ObsidianBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("zebra_to_archive_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lihat Arsip Sesi")
                }

                Button(
                    onClick = { viewModel.navigateTo(AppScreen.REALITY_TRENDS) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ObsidianCard,
                        contentColor = SoftWhite
                    ),
                    border = BorderStroke(1.dp, ObsidianBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("zebra_to_trends_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tren Realitas")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
