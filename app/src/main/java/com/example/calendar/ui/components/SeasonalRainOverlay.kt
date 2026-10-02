package com.example.calendar.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data representation of a single falling seasonal emoji particle.
 * Parameters are pre-calculated to ensure ZERO per-frame allocations.
 */
private class SeasonalParticle(
    val bitmapIndex: Int,
    val xPercent: Float,
    val initialOffsetMs: Long,
    val durationMs: Long,
    val swayAmplitudePx: Float,
    val swayFrequency: Float,
    val swayPhase: Float,
    val baseAlpha: Float,
    val rotationFactor: Float,
    val scaleFactor: Float
)

/**
 * High-performance, hardware-accelerated seasonal emoji rain overlay.
 * Uses pre-rasterized bitmap textures and pure DrawScope blitting
 * for silky-smooth, zero-lag 60fps/120fps animation.
 */
@Composable
fun SeasonalRainOverlay(
    season: String,
    modifier: Modifier = Modifier,
    particleCount: Int = 10
) {
    val density = LocalDensity.current

    val emojiList = remember(season) {
        when (season) {
            "بهار" -> listOf("🌸", "💐", "🌺")
            "تابستان" -> listOf("🌼", "🌻", "☀️")
            "پاییز" -> listOf("🍁", "🍂")
            "زمستان" -> listOf("❄️", "☃️")
            else -> listOf("🍁", "🍂")
        }
    }

    val emojiBitmaps: List<ImageBitmap> = remember(season, density) {
        val targetSizePx = with(density) { 26.dp.roundToPx() }.coerceAtLeast(16)
        val textPaint = AndroidPaint().apply {
            isAntiAlias = true
            textAlign = AndroidPaint.Align.CENTER
            textSize = targetSizePx * 0.72f
        }
        emojiList.map { emoji ->
            val bmp = Bitmap.createBitmap(targetSizePx, targetSizePx, Bitmap.Config.ARGB_8888)
            val cvs = AndroidCanvas(bmp)
            val yOffset = (targetSizePx / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
            cvs.drawText(emoji, targetSizePx / 2f, yOffset, textPaint)
            bmp.asImageBitmap()
        }
    }

    val particles = remember(season, particleCount, emojiBitmaps.size) {
        val random = Random(season.hashCode() + 42)
        val segmentWidth = 1.0f / particleCount.coerceAtLeast(1).toFloat()
        Array(particleCount) { index ->
            val bitmapIndex = index % emojiBitmaps.size
            val xPercent = ((index.toFloat() + random.nextFloat() * 0.96f) * segmentWidth).coerceIn(0.01f, 0.99f)
            val initialOffsetMs = (random.nextFloat() * 6000f).toLong()
            val durationMs = 3400L + (random.nextFloat() * 2000L).toLong()
            val swayAmplitudePx = with(density) { (6f + random.nextFloat() * 12f).dp.toPx() }
            val swayFrequency = 0.8f + random.nextFloat() * 1.4f
            val swayPhase = (random.nextFloat() * 2f * PI).toFloat()
            val baseAlpha = 0.65f + random.nextFloat() * 0.30f
            val rotationFactor = (random.nextFloat() * 2f - 1f) * 90f
            val scaleFactor = 0.75f + random.nextFloat() * 0.30f

            SeasonalParticle(
                bitmapIndex = bitmapIndex,
                xPercent = xPercent,
                initialOffsetMs = initialOffsetMs,
                durationMs = durationMs,
                swayAmplitudePx = swayAmplitudePx,
                swayFrequency = swayFrequency,
                swayPhase = swayPhase,
                baseAlpha = baseAlpha,
                rotationFactor = rotationFactor,
                scaleFactor = scaleFactor
            )
        }
    }

    // Keep animation progress in a State object.
    // Reading animState.value exclusively inside Canvas draws avoids recomposing the Composable!
    val infiniteTransition = rememberInfiniteTransition(label = "SeasonalRainTransition")
    val animProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 120_000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 120_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SeasonalRainProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (width <= 0f || height <= 0f || emojiBitmaps.isEmpty()) return@Canvas

        // Read animProgress here so ONLY the draw phase runs per-frame!
        val currentTime = animProgress.value.toLong()

        for (i in particles.indices) {
            val particle = particles[i]
            val bitmap = emojiBitmaps[particle.bitmapIndex]
            val totalTime = currentTime + particle.initialOffsetMs
            val cycleProgress = ((totalTime % particle.durationMs).toFloat()) / particle.durationMs.toFloat()

            val scaledW = (bitmap.width * particle.scaleFactor).toInt().coerceAtLeast(1)
            val scaledH = (bitmap.height * particle.scaleFactor).toInt().coerceAtLeast(1)

            val startY = -scaledH.toFloat()
            val endY = height + scaledH.toFloat()
            val currentY = startY + cycleProgress * (endY - startY)

            val angle = cycleProgress * 2f * PI.toFloat() * particle.swayFrequency + particle.swayPhase
            val sway = sin(angle) * particle.swayAmplitudePx
            val currentX = (particle.xPercent * width + sway).coerceIn(0f, width)

            val currentRotation = cycleProgress * particle.rotationFactor

            val edgeFade = when {
                cycleProgress < 0.16f -> cycleProgress / 0.16f
                cycleProgress > 0.84f -> (1f - cycleProgress) / 0.16f
                else -> 1f
            }
            val finalAlpha = (particle.baseAlpha * edgeFade).coerceIn(0f, 1f)

            rotate(currentRotation, pivot = Offset(currentX, currentY)) {
                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset((currentX - scaledW / 2f).toInt(), (currentY - scaledH / 2f).toInt()),
                    dstSize = IntSize(scaledW, scaledH),
                    alpha = finalAlpha
                )
            }
        }
    }
}
