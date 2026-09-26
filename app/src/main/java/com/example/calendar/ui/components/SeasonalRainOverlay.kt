package com.example.calendar.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data representation of a single falling seasonal emoji particle.
 */
private data class SeasonalParticle(
    val emoji: String,
    val xPercent: Float,
    val initialOffsetMs: Long,
    val durationMs: Long,
    val swayAmplitudePx: Float,
    val swayFrequency: Float,
    val swayPhase: Float,
    val sizePx: Float,
    val baseAlpha: Float,
    val rotationFactor: Float
)

/**
 * A delicate, non-intrusive rain shower of seasonal emojis in the calendar header.
 * Automatically adapts emojis based on the current season (بهار، تابستان، پاییز، زمستان).
 */
@Composable
fun SeasonalRainOverlay(
    season: String,
    modifier: Modifier = Modifier,
    particleCount: Int = 16
) {
    val density = LocalDensity.current

    // Select emoji pool matching the season
    val emojiList = remember(season) {
        when (season) {
            "بهار" -> listOf("🌸", "🌺", "🌷", "🍃", "🌸")
            "تابستان" -> listOf("☀️", "✨", "🌻", "🌤️", "☀️")
            "پاییز" -> listOf("🍂", "🍁", "🍃", "🌾", "🍂")
            "زمستان" -> listOf("❄️", "🌨️", "❄️", "⛄", "❄️")
            else -> listOf("🍂", "🍁", "🍃", "🌾", "🍂")
        }
    }

    // Generate fixed randomized particles when season or count changes
    val particles = remember(season, particleCount) {
        val random = Random(season.hashCode())
        Array(particleCount) { index ->
            val emoji = emojiList[index % emojiList.size]
            val xPercent = 0.04f + random.nextFloat() * 0.92f
            val initialOffsetMs = (random.nextFloat() * 4000f).toLong()
            val durationMs = 2800L + (random.nextFloat() * 1800L).toLong() // 2.8s to 4.6s to fall across header
            val swayAmplitudePx = with(density) { (8f + random.nextFloat() * 16f).dp.toPx() }
            val swayFrequency = 1.2f + random.nextFloat() * 1.5f
            val swayPhase = (random.nextFloat() * 2f * Math.PI).toFloat()
            val sizePx = with(density) { (13f + random.nextFloat() * 8f).sp.toPx() }
            val baseAlpha = 0.40f + random.nextFloat() * 0.45f // 40% to 85% opacity
            val rotationFactor = (random.nextFloat() * 2f - 1f) * 180f // subtle spin

            SeasonalParticle(
                emoji = emoji,
                xPercent = xPercent,
                initialOffsetMs = initialOffsetMs,
                durationMs = durationMs,
                swayAmplitudePx = swayAmplitudePx,
                swayFrequency = swayFrequency,
                swayPhase = swayPhase,
                sizePx = sizePx,
                baseAlpha = baseAlpha,
                rotationFactor = rotationFactor
            )
        }
    }

    // Frame synchronization
    var elapsedTimeMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        val startTime = System.nanoTime()
        while (true) {
            withFrameNanos { now ->
                elapsedTimeMs = (now - startTime) / 1_000_000L
            }
        }
    }

    val paint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (width <= 0f || height <= 0f) return@Canvas

        val nativeCanvas = drawContext.canvas.nativeCanvas
        val currentTime = elapsedTimeMs

        for (i in particles.indices) {
            val particle = particles[i]
            val totalTime = currentTime + particle.initialOffsetMs
            val cycleProgress = ((totalTime % particle.durationMs).toFloat()) / particle.durationMs.toFloat()

            // Calculate vertical position (falls from above header to slightly below header)
            val startY = -particle.sizePx
            val endY = height + particle.sizePx
            val currentY = startY + cycleProgress * (endY - startY)

            // Calculate horizontal sway (gentle sinusoidal wind drift)
            val angle = (cycleProgress * 2.0 * Math.PI * particle.swayFrequency.toDouble() + particle.swayPhase.toDouble())
            val sway = (Math.sin(angle) * particle.swayAmplitudePx.toDouble()).toFloat()
            val currentX = (particle.xPercent * width + sway).coerceIn(0f, width)

            // Rotation
            val currentRotation = cycleProgress * particle.rotationFactor

            // Alpha fade at top and bottom edges for natural seamless entrance/exit
            val edgeFade = when {
                cycleProgress < 0.15f -> cycleProgress / 0.15f
                cycleProgress > 0.85f -> (1f - cycleProgress) / 0.15f
                else -> 1f
            }
            val finalAlpha = (particle.baseAlpha * edgeFade * 255f).toInt().coerceIn(0, 255)

            paint.textSize = particle.sizePx
            paint.alpha = finalAlpha

            nativeCanvas.save()
            nativeCanvas.translate(currentX, currentY)
            nativeCanvas.rotate(currentRotation)
            nativeCanvas.drawText(particle.emoji, 0f, particle.sizePx * 0.35f, paint)
            nativeCanvas.restore()
        }
    }
}
