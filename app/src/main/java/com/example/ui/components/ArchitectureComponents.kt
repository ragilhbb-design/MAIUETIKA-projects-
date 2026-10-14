package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HosAnalysis
import com.example.data.model.InfoProvenance
import com.example.data.model.KkzCalculation
import com.example.data.model.RbssScore
import com.example.data.model.RmaAnalysis
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskMedium
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RmaComponentCard(
    rma: RmaAnalysis,
    modifier: Modifier = Modifier
) {
    var expandedHypotheses by remember { mutableStateOf(true) }
    var expandedInterests by remember { mutableStateOf(false) }
    var expandedInterventions by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rma_component_card"),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = BorderStroke(1.dp, ObsidianBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RMA: ROOT MECHANISM ANALYSIS",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Analisis Mekanisme Pembentuk Masalah",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Problem Statement
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RUMUSAN MASALAH INTI",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = rma.problemStatement,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftWhite,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mekanisme Hipotesis (Collapsible)
            Surface(
                color = Color(0xFF13131D),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedHypotheses = !expandedHypotheses }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hipotesis Mekanisme (${rma.mechanismHypotheses.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = SoftWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = if (expandedHypotheses) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expandedHypotheses) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rma.mechanismHypotheses.forEachIndexed { idx, hyp ->
                        val isVerified = hyp.contains("TERVERIFIKASI")
                        val isResistance = hyp.contains("KOREKSI")
                        Surface(
                            color = if (isVerified) Color(0xFF112518) else if (isResistance) Color(0xFF2A1515) else ObsidianSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isVerified) RiskLow else if (isResistance) RiskHigh else ObsidianBorder
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (isVerified) Color(0xFF1B3B24) else GoldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isVerified) RiskLow else GoldAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = hyp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SoftWhite,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Kepentingan (Declared vs Confirmed vs Possible)
            Surface(
                color = Color(0xFF13131D),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedInterests = !expandedInterests }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FactCheck,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Peta Kepentingan (Interests)",
                            style = MaterialTheme.typography.labelMedium,
                            color = SoftWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = if (expandedInterests) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expandedInterests) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (rma.declaredInterests.isNotEmpty()) {
                        SectionTagGroup(
                            title = "Keinginan yang Dinyatakan (Declared):",
                            items = rma.declaredInterests,
                            color = SlateGray
                        )
                    }
                    if (rma.confirmedInterests.isNotEmpty()) {
                        SectionTagGroup(
                            title = "Kepentingan Mendasari Terkonfirmasi:",
                            items = rma.confirmedInterests,
                            color = GoldAccent
                        )
                    }
                    if (rma.possibleInterests.isNotEmpty()) {
                        SectionTagGroup(
                            title = "Kepentingan Tersembunyi/Psikologis:",
                            items = rma.possibleInterests,
                            color = RiskMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Titik Intervensi & Pertanyaan Penyelidik
            Surface(
                color = Color(0xFF13131D),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedInterventions = !expandedInterventions }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Titik Intervensi & Pertanyaan Sokratik",
                            style = MaterialTheme.typography.labelMedium,
                            color = SoftWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = if (expandedInterventions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expandedInterventions) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (rma.potentialInterventionPoints.isNotEmpty()) {
                        Text(
                            text = "Titik Ungkit Intervensi:",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                        rma.potentialInterventionPoints.forEach { pt ->
                            Row(modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)) {
                                Text("• ", color = GoldAccent, fontWeight = FontWeight.Bold)
                                Text(pt, style = MaterialTheme.typography.bodySmall, color = SoftWhite)
                            }
                        }
                    }

                    if (rma.nextQuestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pertanyaan Penyelidik Selanjutnya:",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        rma.nextQuestions.forEach { q ->
                            Surface(
                                color = ObsidianSurface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "\"$q\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontStyle = FontStyle.Italic,
                                        color = GoldLight,
                                        lineHeight = 18.sp
                                    ),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTagGroup(title: String, items: List<String>, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        items.forEach { item ->
            Row(modifier = Modifier.padding(start = 6.dp, top = 1.dp, bottom = 1.dp)) {
                Text("– ", color = color, fontSize = 12.sp)
                Text(text = item, style = MaterialTheme.typography.bodySmall, color = SoftWhite, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun HosComponentCard(
    hos: HosAnalysis,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hos_component_card"),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = BorderStroke(1.dp, ObsidianBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "HOS: HUMAN OBSERVATIONAL SYSTEM",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Pemetaan Kondisi Empiris & Provenance Data",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateGray
                        )
                    }
                }

                Surface(
                    color = Color(0xFF13131D),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldAccent)
                ) {
                    Text(
                        text = "Adekuasi: ${hos.adequacyScore}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Daftar Dimensi dengan Provenance Badge
            Text(
                text = "STATUS BUKTI 8 DIMENSI REALITAS:",
                style = MaterialTheme.typography.labelSmall,
                color = SlateGray,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                hos.currentConditions.forEach { cond ->
                    Surface(
                        color = ObsidianSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ObsidianBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = cond.dimensi,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = SoftWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Provenance Badge
                                    val provenanceColor = when (cond.provenance) {
                                        InfoProvenance.USER_REPORTED.name -> Color(0xFF4A90E2)
                                        InfoProvenance.SYSTEM_CALCULATED.name -> GoldAccent
                                        InfoProvenance.DIRECTLY_OBSERVED.name -> RiskLow
                                        else -> SlateGray
                                    }
                                    val provenanceLabel = when (cond.provenance) {
                                        InfoProvenance.USER_REPORTED.name -> "Laporan Pengguna"
                                        InfoProvenance.SYSTEM_CALCULATED.name -> "Kalkulasi Data"
                                        InfoProvenance.DIRECTLY_OBSERVED.name -> "Observasi Nyata"
                                        else -> "AI Interpretasi"
                                    }
                                    Surface(
                                        color = provenanceColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(0.5.dp, provenanceColor)
                                    ) {
                                        Text(
                                            text = provenanceLabel,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = provenanceColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (cond.deskripsi.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = cond.deskripsi,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SlateGray,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${cond.skor}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (cond.skor < 45) RiskHigh else if (cond.skor > 70) RiskLow else GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = cond.verifikasiStatus,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = if (cond.verifikasiStatus.contains("Terverifikasi")) RiskLow else SlateGray
                                )
                            }
                        }
                    }
                }
            }

            // Inconsistencies / Missing Data Alert
            if (hos.inconsistencies.isNotEmpty() || hos.missingData.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = Color(0xFF1E1711),
                    border = BorderStroke(1.dp, RiskMedium),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = RiskMedium,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ketidakkonsistenan & Data Hilang Terdeteksi",
                                style = MaterialTheme.typography.labelSmall,
                                color = RiskMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        hos.inconsistencies.forEach { inc ->
                            Text(
                                text = "• $inc",
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftWhite,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        hos.missingData.forEach { miss ->
                            Text(
                                text = "• Perlu Bukti: $miss",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateGray,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KkzComponentCard(
    kkz: KkzCalculation,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kkz_component_card"),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = BorderStroke(1.dp, ObsidianBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "KKZ: KESIAPAN ZAMAN & KONDISI",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Kalkulasi Kapasitas & Friksi Eksternal",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateGray
                        )
                    }
                }

                Text(
                    text = "${kkz.readinessIndex}/100",
                    style = MaterialTheme.typography.titleLarge,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Indikator 4 Metrik
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricKkzCard(
                    title = "Kapasitas Mental",
                    value = "${kkz.mentalEnergyScore}%",
                    sub = kkz.internalResourceCapacity,
                    modifier = Modifier.weight(1f)
                )
                MetricKkzCard(
                    title = "Waktu Tersedia",
                    value = kkz.timeAvailability,
                    sub = "Fokus Harian",
                    modifier = Modifier.weight(1f)
                )
                MetricKkzCard(
                    title = "Friksi Eksternal",
                    value = kkz.externalFrictionLevel,
                    sub = "Resistensi",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vonis Kesiapan
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "VONIS KESIAPAN BERTINDAK",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = kkz.readinessVerdict,
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftWhite,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricKkzCard(
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF13131D),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ObsidianBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = SlateGray, fontSize = 9.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.labelMedium, color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(sub, style = MaterialTheme.typography.labelSmall, color = SlateGray, fontSize = 8.sp)
        }
    }
}

@Composable
fun RbssComponentCard(
    rbss: RbssScore,
    modifier: Modifier = Modifier
) {
    var showAuditTrail by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rbss_component_card"),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = BorderStroke(1.5.dp, GoldAccent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RBSS: REALITY-BASED SCORING",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Sistem Penilaian Objektif Tanpa Halusinasi",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateGray
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${String.format(java.util.Locale.US, "%.1f", rbss.totalRealityScore)}/100",
                        style = MaterialTheme.typography.headlineSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Skor Realitas Terverifikasi",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = SlateGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Formula Breakdown Bars
            ScoreFormulaRow(
                label = "1. Ketercukupan Fakta (25%)",
                score = rbss.informationAdequacyScore,
                color = Color(0xFF4A90E2)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScoreFormulaRow(
                label = "2. Penyelarasan Persepsi vs Kenyataan (25%)",
                score = rbss.alignmentScore,
                color = GoldLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScoreFormulaRow(
                label = "3. Eksekusi Tindakan Nyata (30%)",
                score = rbss.executionScore,
                color = RiskLow
            )
            Spacer(modifier = Modifier.height(8.dp))
            ScoreFormulaRow(
                label = "4. Ketahanan & Resiliensi Risiko (20%)",
                score = rbss.resilienceScore,
                color = GoldAccent
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dasar Perubahan Skor Terkini
            Surface(
                color = Color(0xFF13131D),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "DASAR PERUBAHAN SKOR SAAT INI",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = rbss.scoreChangeReason,
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Audit Trail Toggle
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAuditTrail = !showAuditTrail }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Jejak Audit Skor Empiris (${rbss.auditTrail.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = SoftWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = if (showAuditTrail) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showAuditTrail) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rbss.auditTrail.forEach { trail ->
                        Surface(
                            color = Color(0xFF0F0F16),
                            border = BorderStroke(0.5.dp, ObsidianBorder),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = trail,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = SlateGray
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreFormulaRow(label: String, score: Double, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = SoftWhite, fontSize = 11.sp)
            Text("${score.toInt()}/100", style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (score / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = color,
            trackColor = Color(0xFF181824)
        )
    }
}
