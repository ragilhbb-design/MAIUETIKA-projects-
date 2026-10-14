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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.HosComponentCard
import com.example.ui.components.KkzComponentCard
import com.example.ui.components.RbssComponentCard
import com.example.ui.components.RealityRadarChart
import com.example.ui.components.RiskAndConfidenceCard
import com.example.ui.components.RmaComponentCard
import com.example.ui.components.RootCauseFunnelView
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite

@Composable
fun DiagnosisScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.ONBOARDING)
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

    val scrollState = rememberScrollState()
    val realityMap = session.getParsedRealityMap()
    val rma = session.getParsedRma()
    val hos = session.getParsedHos()
    val kkz = session.getParsedKkz()
    val rbss = session.getParsedRbss()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Ringkasan", "RMA (Mekanisme)", "HOS (Observasi)", "KKZ (Kesiapan)", "RBSS (Skor)")

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
                modifier = Modifier.testTag("back_to_onboarding_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Beranda",
                    tint = GoldAccent
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ARSITEKTUR MAIEUTIKA",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Reality Intelligence & Decision System",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateGray,
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.SCENARIO) },
                modifier = Modifier.testTag("go_to_scenario_top_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Ke Skenario",
                    tint = GoldAccent
                )
            }
        }

        // Sub Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(tabTitles) { index, title ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAccent,
                        selectedLabelColor = ObsidianBlack,
                        containerColor = ObsidianCard,
                        labelColor = SoftWhite
                    ),
                    border = BorderStroke(1.dp, if (selectedTab == index) GoldAccent else ObsidianBorder)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (selectedTab) {
                0 -> {
                    // Ringkasan & Refleksi
                    // Cerita pengguna quote
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = BorderStroke(1.dp, ObsidianBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tuturan Awal Anda",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "\"${session.ceritaPengguna}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftWhite,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Diagnosis Naratif
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = BorderStroke(1.5.dp, GoldAccent),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Refleksi Kognitif Sokratik",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = session.diagnosis,
                                style = MaterialTheme.typography.bodyLarge,
                                color = SoftWhite,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // PETA REALITAS: Radar Chart
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        border = BorderStroke(1.dp, ObsidianBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CompassCalibration,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Peta Realitas (8 Dimensi)",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = GoldLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "Skor RBSS: ${rbss.totalRealityScore.toInt()}/100",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            RealityRadarChart(
                                realityMap = realityMap,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Root Cause Funnel
                    RootCauseFunnelView(
                        rootCauses = session.analisisAkarMasalah,
                        coreProblem = session.masalahUtama
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Risk & AI Confidence Card
                    RiskAndConfidenceCard(
                        riskPercentage = session.tingkatRisiko,
                        confidencePercentage = session.tingkatKepercayaanAI
                    )
                }

                1 -> {
                    // RMA Component
                    RmaComponentCard(rma = rma)
                }

                2 -> {
                    // HOS Component
                    HosComponentCard(hos = hos)
                }

                3 -> {
                    // KKZ Component
                    KkzComponentCard(kkz = kkz)
                }

                4 -> {
                    // RBSS Component
                    RbssComponentCard(rbss = rbss)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action to Scenarios
            Button(
                onClick = { viewModel.navigateTo(AppScreen.SCENARIO) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = ObsidianBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("simulate_scenarios_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ViewCarousel,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lihat Simulasi Skenario (OIE)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
