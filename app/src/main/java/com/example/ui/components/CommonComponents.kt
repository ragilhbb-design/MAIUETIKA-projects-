package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ScenarioItem
import com.example.data.model.ZebraAction
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

@Composable
fun MaieutikaHeroHeader(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = if (compact) 8.dp else 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 54.dp else 84.dp)
                .clip(CircleShape)
                .border(BorderStroke(2.dp, GoldAccent), CircleShape)
                .background(Color(0xFF0A0A0F)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon_fg_1791858808821),
                contentDescription = "Emblem Kompas MAIEUTIKA",
                modifier = Modifier.size(if (compact) 44.dp else 70.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "MAIEUTIKA",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = if (compact) 22.sp else 28.sp,
                letterSpacing = 3.sp,
                color = GoldAccent
            )
        )

        Text(
            text = "Google Maps Kehidupan",
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = if (compact) 11.sp else 13.sp,
                letterSpacing = 1.sp,
                color = GoldLight
            )
        )

        if (!compact) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "\"Saya tidak akan memberi Anda jawaban.\nSaya akan membantu Anda melihat.\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    color = SlateGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun RootCauseFunnelView(
    rootCauses: List<String>,
    coreProblem: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = BorderStroke(1.dp, ObsidianBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Analisis Akar Masalah (4 Lapisan)",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            rootCauses.forEachIndexed { index, cause ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(GoldContainer)
                            .border(1.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = cause,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftWhite,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, GoldAccent, Color.Transparent)
                        )
                    )
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Core Problem
            Card(
                colors = CardDefaults.cardColors(containerColor = GoldContainer),
                border = BorderStroke(1.dp, GoldAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "MASALAH UTAMA (INTI PERSOALAN)",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = coreProblem,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SoftWhite,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun RiskAndConfidenceCard(
    riskPercentage: Double,
    confidencePercentage: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = BorderStroke(1.dp, ObsidianBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Risk
            Column(modifier = Modifier.weight(1f)) {
                val riskColor = when {
                    riskPercentage < 35 -> RiskLow
                    riskPercentage < 65 -> RiskMedium
                    else -> RiskHigh
                }
                val riskLabel = when {
                    riskPercentage < 35 -> "Terkendali"
                    riskPercentage < 65 -> "Waspada"
                    else -> "Kritis"
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = riskColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tingkat Risiko",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${riskPercentage.toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = riskColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "($riskLabel)",
                        style = MaterialTheme.typography.labelSmall,
                        color = riskColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (riskPercentage / 100f).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(5.dp)
                        .clip(CircleShape),
                    color = riskColor,
                    trackColor = ObsidianBorder
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(ObsidianBorder)
            )

            // Confidence
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Kepercayaan AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${confidencePercentage.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (confidencePercentage / 100f).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(5.dp)
                        .clip(CircleShape),
                    color = GoldAccent,
                    trackColor = ObsidianBorder
                )
            }
        }
    }
}

@Composable
fun ScenarioCardItem(
    scenario: ScenarioItem,
    isRecommended: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRecommended) ObsidianSurface else ObsidianCard
        ),
        border = BorderStroke(
            if (isRecommended) 1.5.dp else 1.dp,
            if (isRecommended) GoldAccent else ObsidianBorder
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = scenario.nama,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isRecommended) GoldAccent else SoftWhite,
                    fontWeight = FontWeight.Bold
                )

                if (isRecommended) {
                    Surface(
                        color = GoldContainer,
                        border = BorderStroke(1.dp, GoldAccent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "DIREKOMENDASIKAN",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = scenario.deskripsi,
                style = MaterialTheme.typography.bodyMedium,
                color = SoftWhite,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricPill(label = "Probabilitas", value = "${scenario.probabilitas}%", color = GoldAccent)
                MetricPill(
                    label = "Risiko",
                    value = "${scenario.risiko}%",
                    color = if (scenario.risiko > 60) RiskHigh else RiskMedium
                )
                MetricPill(label = "Dampak", value = "${scenario.dampak}%", color = RiskLow)
            }
        }
    }
}

@Composable
fun MetricPill(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = SlateGray, fontSize = 10.sp)
        Text(text = value, style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ZebraPriorityCard(
    zebra: ZebraAction,
    onStatusChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isUpdating: Boolean = false
) {
    val isDone = zebra.status == "done"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("zebra_action_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFF112518) else ObsidianCard
        ),
        border = BorderStroke(
            1.5.dp,
            if (isDone) RiskLow else GoldAccent
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = if (isDone) RiskLow else GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "ZEBRA (Prioritas Hari Ini)",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDone) RiskLow else GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = if (isDone) Color(0xFF1B3B24) else GoldContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "+${zebra.dampakPoin} Skor Realitas",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDone) RiskLow else GoldLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = zebra.instruksi,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                    color = SoftWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onStatusChange(if (isDone) "pending" else "done") },
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDone) RiskLow else GoldAccent,
                        contentColor = Color(0xFF0A0A0F)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("zebra_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDone) "Telah Selesai" else "Saya sudah melakukannya",
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!isDone) {
                    OutlinedButton(
                        onClick = { onStatusChange("failed") },
                        enabled = !isUpdating,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateGray),
                        border = BorderStroke(1.dp, ObsidianBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("zebra_cant_do_button")
                    ) {
                        Text(text = "Belum bisa")
                    }
                }
            }
        }
    }
}
