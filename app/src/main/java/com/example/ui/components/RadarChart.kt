package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RealityMap
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RealityRadarChart(
    realityMap: RealityMap,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFFD4AF37),
    gridColor: Color = Color(0x33D4AF37),
    textColor: Color = Color(0xFFEDEBF5)
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(realityMap) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900)
        )
    }

    val labels = listOf(
        "Keuangan",
        "Keluarga",
        "Karier",
        "Relasi",
        "Tujuan",
        "Risiko",
        "Psikologi",
        "Kesehatan"
    )

    val values = listOf(
        realityMap.keuangan,
        realityMap.keluarga,
        realityMap.karier,
        realityMap.relasi,
        realityMap.tujuan,
        realityMap.risiko,
        realityMap.psikologi,
        realityMap.kesehatan
    )

    val density = LocalDensity.current
    val labelTextSizePx = with(density) { 10.sp.toPx() }
    val valueTextSizePx = with(density) { 9.sp.toPx() }

    Box(
        modifier = modifier
            .height(280.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (min(size.width, size.height) / 2f) - 45.dp.toPx()
            if (maxRadius <= 10f) return@Canvas

            val numDimensions = 8
            val angleStep = (2 * Math.PI / numDimensions)

            // Draw concentric octagons (20%, 40%, 60%, 80%, 100%)
            val rings = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
            rings.forEach { ratio ->
                val ringRadius = maxRadius * ratio
                val ringPath = Path()
                for (i in 0 until numDimensions) {
                    val angle = -Math.PI / 2 + (i * angleStep)
                    val x = center.x + (ringRadius * cos(angle)).toFloat()
                    val y = center.y + (ringRadius * sin(angle)).toFloat()
                    if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
                }
                ringPath.close()
                drawPath(
                    path = ringPath,
                    color = gridColor,
                    style = Stroke(width = if (ratio == 1f) 1.5.dp.toPx() else 0.8.dp.toPx())
                )
            }

            // Draw spoke lines
            for (i in 0 until numDimensions) {
                val angle = -Math.PI / 2 + (i * angleStep)
                val endX = center.x + (maxRadius * cos(angle)).toFloat()
                val endY = center.y + (maxRadius * sin(angle)).toFloat()
                drawLine(
                    color = gridColor,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw Data Polygon
            val progress = animationProgress.value
            val dataPath = Path()
            val pointOffsets = mutableListOf<Offset>()

            for (i in 0 until numDimensions) {
                val score = (values[i] / 100f).coerceIn(0.05f, 1f) * progress
                val angle = -Math.PI / 2 + (i * angleStep)
                val dist = maxRadius * score
                val x = center.x + (dist * cos(angle)).toFloat()
                val y = center.y + (dist * sin(angle)).toFloat()
                val pt = Offset(x, y)
                pointOffsets.add(pt)
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()

            // Fill polygon with translucent gold
            drawPath(
                path = dataPath,
                color = accentColor.copy(alpha = 0.28f)
            )

            // Stroke polygon
            drawPath(
                path = dataPath,
                color = accentColor,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Draw vertex nodes
            pointOffsets.forEach { pt ->
                drawCircle(
                    color = Color(0xFF0A0A0F),
                    radius = 4.5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = accentColor,
                    radius = 3.5.dp.toPx(),
                    center = pt
                )
            }

            // Draw Labels and Values on native canvas
            val nativeCanvas = drawContext.canvas.nativeCanvas
            val labelPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#EDEBF5")
                textSize = labelTextSizePx
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }

            val valuePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#D4AF37")
                textSize = valueTextSizePx
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            for (i in 0 until numDimensions) {
                val angle = -Math.PI / 2 + (i * angleStep)
                val labelDistance = maxRadius + 22.dp.toPx()
                val lx = center.x + (labelDistance * cos(angle)).toFloat()
                val ly = center.y + (labelDistance * sin(angle)).toFloat()

                val label = labels[i]
                val value = values[i]

                nativeCanvas.drawText(label, lx, ly - 2f, labelPaint)
                nativeCanvas.drawText("$value/100", lx, ly + 14f, valuePaint)
            }
        }
    }
}
