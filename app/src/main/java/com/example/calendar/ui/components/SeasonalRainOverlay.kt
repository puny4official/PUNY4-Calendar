package com.example.calendar.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlin.math.ceil
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Mutable physics data; only the Canvas observes the frame counter.
private class SeasonalParticle(
    var x: Float = 0f,
    var y: Float = 0f,
    var baseSpeedY: Float = 60f,
    var driftSpeedX: Float = 10f,
    var windSensitivity: Float = 1f,
    var sinPhase: Float = 0f,
    var cosPhase: Float = 1f,
    var swayAmplitudePx: Float = 14f,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 30f,
    var lengthPx: Float = 25f,
    var slantX: Float = 0f,
    var emoji: String = "🌸",
    var isRaindrop: Boolean = false,
    var layer: Int = 1,
    var flipPhase: Float = 0f,
    var flipSpeed: Float = 0f,
    var isInitialized: Boolean = false
)

/** Decorative, touch-transparent particles with cached emoji sprites. */
@Composable
fun SeasonalRainOverlay(
    season: String,
    modifier: Modifier = Modifier,
    particleCount: Int = if (
        season.contains("بهار") || season.contains("پاییز") ||
        season.equals("Spring", ignoreCase = true) ||
        season.equals("Autumn", ignoreCase = true) ||
        season.equals("Fall", ignoreCase = true)
    ) 64 else 14,
    animationRunning: Boolean = true
) {
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isSpring = remember(season) {
        season.contains("بهار") || season.equals("Spring", ignoreCase = true)
    }
    val isSummer = remember(season) {
        season.contains("تابستان") || season.equals("Summer", ignoreCase = true)
    }
    val isAutumn = remember(season) {
        season.contains("پاییز") || season.equals("Autumn", ignoreCase = true) || season.equals("Fall", ignoreCase = true)
    }
    val isWinter = remember(season) {
        season.contains("زمستان") || season.equals("Winter", ignoreCase = true)
    }

    // Foliage Emojis for each season
    val foliageEmojis = remember(isSpring, isSummer, isAutumn, isWinter) {
        when {
            isSpring -> listOf("🌸", "💐", "🌺")
            isSummer -> listOf("🌻", "☀️", "🌼")
            isAutumn -> listOf("🍁", "🍂")
            isWinter -> listOf("🌧️", "❄️", "☃️")
            else -> listOf("🌸", "💐", "🌺")
        }
    }

    val hasRainlines = isSpring || isAutumn

    // Pre-calculated fixed pixel metrics to avoid runtime density lookups
    val foliageTextSizePx = remember(density) { with(density) { 12.sp.toPx() } }
    val rainStrokeWidthPx = remember(density) { with(density) { 2.dp.toPx() } }
    val gustMaxPx = remember(density) { with(density) { 170.dp.toPx() } }

    // Pre-allocated particles array
    val particles = remember(particleCount, foliageEmojis, hasRainlines, density) {
        Array(particleCount) { i ->
            val isDrop = hasRainlines && (i % 8 >= 1) // ~88% drops, few small leaves/flowers
            val emoji = if (isDrop) "" else foliageEmojis[i % foliageEmojis.size]

            // Depth layer: 0 = far (small, slow, faint), 1 = middle, 2 = near (long, fast, bright)
            val layer = when (Random.nextInt(10)) { in 0..3 -> 0; in 4..7 -> 1; else -> 2 }

            val speedY = if (isDrop) {
                with(density) {
                    when (layer) {
                        0 -> Random.nextInt(520, 640)
                        1 -> Random.nextInt(820, 980)
                        else -> Random.nextInt(1250, 1500)
                    }.dp.toPx()
                }
            } else {
                with(density) {
                    (Random.nextInt(35, 60) * when (layer) { 0 -> 0.7f; 1 -> 1f; else -> 1.5f }).dp.toPx()
                }
            }

            val rainLength = with(density) {
                when (layer) {
                    0 -> Random.nextInt(10, 14)
                    1 -> Random.nextInt(18, 24)
                    else -> Random.nextInt(30, 40)
                }.dp.toPx()
            }
            val swayPhase = Random.nextFloat() * (2f * PI.toFloat())
            val swayAmp = with(density) { (if (isDrop) 4.dp else 13.dp).toPx() }

            val windSens = if (isDrop) {
                Random.nextFloat() * 0.2f + 0.55f
            } else {
                Random.nextFloat() * 0.4f + 1.15f
            }

            SeasonalParticle(
                baseSpeedY = speedY,
                driftSpeedX = with(density) { Random.nextInt(-8, 12).dp.toPx() },
                windSensitivity = windSens,
                sinPhase = sin(swayPhase),
                cosPhase = cos(swayPhase),
                swayAmplitudePx = swayAmp,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = if (isDrop) 0f else (Random.nextFloat() * 50f - 25f),
                lengthPx = rainLength,
                slantX = 0f,
                emoji = emoji,
                isRaindrop = isDrop,
                layer = layer,
                flipPhase = Random.nextFloat() * 6.2831855f,
                flipSpeed = Random.nextFloat() * 2.2f + 1.2f
            )
        }
    }

    // Shared pre-allocated paint for text/emojis
    val textPaint = remember(foliageTextSizePx) {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = foliageTextSizePx
        }
    }

    // Rasterize emoji glyphs once per depth layer. Far leaves are drawn at half resolution and scaled up (soft focus),
    // near leaves are larger and sharp.
    val layerScale = remember { floatArrayOf(0.7f, 1f, 1.35f) }
    val emojiSprites = remember(foliageEmojis, foliageTextSizePx) {
        Array(3) { l ->
            val renderScale = if (l == 0) 0.5f else 1f
            val paint = Paint(textPaint).apply { textSize = foliageTextSizePx * layerScale[l] * renderScale }
            val side = ceil(paint.textSize * 1.8f).toInt().coerceAtLeast(1)
            foliageEmojis.associateWith { emoji ->
                Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888).also { bitmap ->
                    val baseline = side / 2f - (paint.ascent() + paint.descent()) / 2f
                    android.graphics.Canvas(bitmap).drawText(emoji, side / 2f, baseline, paint)
                }
            }
        }
    }
    val spriteDrawSize = remember(foliageTextSizePx) {
        FloatArray(3) { l -> ceil(foliageTextSizePx * layerScale[l] * 1.8f) }
    }
    val spritePaints = remember {
        val alphas = intArrayOf(120, 215, 255)
        Array(3) { l ->
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply { alpha = alphas[l] }
        }
    }
    val spriteRect = remember { android.graphics.RectF() }

    // Rain paints per depth layer: far (thin, faint) -> near (thick, bright), each with a thin bright "glass" core.
    val rainPaints = remember(density) {
        val widths = floatArrayOf(with(density) { 0.6.dp.toPx() }, with(density) { 1.1.dp.toPx() }, with(density) { 1.7.dp.toPx() })
        val alphas = intArrayOf(70, 120, 175)
        Array(3) { l ->
            Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.argb(alphas[l], 96, 180, 245)
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeWidth = widths[l]
            }
        }
    }
    val corePaints = remember(density) {
        val widths = floatArrayOf(0f, with(density) { 0.45.dp.toPx() }, with(density) { 0.75.dp.toPx() })
        val alphas = intArrayOf(0, 170, 255)
        Array(3) { l ->
            Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.argb(alphas[l], 235, 248, 255)
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeWidth = widths[l]
            }
        }
    }

    // Bright round "water bead" at the leading end of each streak (drawn as batched points)
    val headPaints = remember(density) {
        val sizes = floatArrayOf(with(density) { 1.1.dp.toPx() }, with(density) { 1.7.dp.toPx() }, with(density) { 2.4.dp.toPx() })
        val alphas = intArrayOf(120, 190, 245)
        Array(3) { l ->
            Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.argb(alphas[l], 215, 238, 255)
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeWidth = sizes[l]
            }
        }
    }
    val headBuffers = remember(particleCount) { Array(3) { FloatArray(particleCount * 2) } }

    // One float buffer per depth layer (4 floats per line: x1, y1, x2, y2)
    val rainBuffers = remember(particleCount) { Array(3) { FloatArray(particleCount * 4) } }
    val rainCounts = remember { IntArray(3) }

    // Draw-phase trigger state
    var frameTick by remember { mutableLongStateOf(0L) }
    val simulationTime = remember(particles) { floatArrayOf(0f) }

    // Lightweight physics loop
    LaunchedEffect(particles, animationRunning, lifecycleOwner, gustMaxPx) {
        if (!animationRunning) return@LaunchedEffect
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var lastNanoTime = 0L
            var totalTimeSec = simulationTime[0]

            while (isActive) {
                withFrameNanos { frameNanos ->
                    if (lastNanoTime == 0L) {
                        lastNanoTime = frameNanos
                        return@withFrameNanos
                    }
                    // Cap at ~60 fps (smooth) so 90/120 Hz screens don't do double work.
                    if (frameNanos - lastNanoTime < 15_000_000L) return@withFrameNanos
                    val dt = ((frameNanos - lastNanoTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    lastNanoTime = frameNanos
                    totalTimeSec += dt
                    simulationTime[0] = totalTimeSec

                    // Wind gust cycle (every 4.2 seconds for 1.6 seconds)
                    val gustPeriod = 4.2f
                    val cyclePos = totalTimeSec % gustPeriod
                    val gustDuration = 1.6f
                    val gustActive = cyclePos < gustDuration

                    val gustIntensity = if (gustActive) {
                        val progress = cyclePos / gustDuration
                        sin(progress * PI.toFloat()) * gustMaxPx
                    } else {
                        0f
                    }

                    // Global harmonic oscillation computed ONCE per frame
                    val swayAngle = totalTimeSec * 3f
                    val globalSin = sin(swayAngle)
                    val globalCos = cos(swayAngle)

                    // High-performance particle physics loop
                    val count = particles.size
                    for (i in 0 until count) {
                        val p = particles[i]
                        if (!p.isInitialized) continue

                        // Angle addition identity: sin(A + B) = sin(A)*cos(B) + cos(A)*sin(B)
                        // Zero transcendental calls inside this inner loop!
                        val naturalSway = (globalSin * p.cosPhase + globalCos * p.sinPhase) * p.swayAmplitudePx

                        val currentVx = p.driftSpeedX + naturalSway + (gustIntensity * p.windSensitivity)
                        p.x += currentVx * dt

                        val currentVy = p.baseSpeedY + (gustIntensity * 0.12f)
                        p.y += currentVy * dt

                        if (p.isRaindrop) {
                            p.slantX = (currentVx / currentVy * p.lengthPx * 0.75f)
                                .coerceIn(-p.lengthPx * 1.2f, p.lengthPx * 1.2f)
                        } else {
                            val extraRot = if (gustActive) gustIntensity * 0.45f else 0f
                            p.rotation = (p.rotation + (p.rotationSpeed + extraRot) * dt) % 360f
                            p.flipPhase += p.flipSpeed * dt
                        }
                    }

                    frameTick++
                }
            }
        }
    }

    // Direct Canvas Draw Phase
    Canvas(
        modifier = modifier.graphicsLayer()
    ) {
        @Suppress("UNUSED_VARIABLE")
        val tick = frameTick

        val canvasWidth = size.width
        val canvasHeight = size.height

        if (canvasWidth <= 0f || canvasHeight <= 0f) return@Canvas

        val nativeCanvas = drawContext.canvas.nativeCanvas

        val count = particles.size
        val segmentWidth = canvasWidth / count.coerceAtLeast(1)

        rainCounts[0] = 0; rainCounts[1] = 0; rainCounts[2] = 0

        for (i in 0 until count) {
            val p = particles[i]
            if (!p.isInitialized) {
                p.x = i * segmentWidth + Random.nextFloat() * segmentWidth
                p.y = Random.nextFloat() * canvasHeight
                p.isInitialized = true
            }

            // Boundary wrap-around
            val thresholdY = if (p.isRaindrop) p.lengthPx * 2 else foliageTextSizePx
            val offBottom = p.y > (canvasHeight + thresholdY)
            val offRight = p.x > (canvasWidth + 40f)
            val offLeft = p.x < -40f

            if (offBottom || offRight || offLeft) {
                p.y = -thresholdY - Random.nextFloat() * 20f
                p.x = Random.nextFloat() * canvasWidth

                if (!p.isRaindrop) {
                    p.emoji = foliageEmojis[Random.nextInt(foliageEmojis.size)]
                    p.rotation = Random.nextFloat() * 360f
                }
            }

            if (p.isRaindrop) {
                // Batch rain points into continuous buffer
                val buf = rainBuffers[p.layer]
                var idx = rainCounts[p.layer]
                buf[idx++] = p.x
                buf[idx++] = p.y
                buf[idx++] = p.x + p.slantX
                buf[idx++] = p.y + p.lengthPx
                rainCounts[p.layer] = idx
                val hb = headBuffers[p.layer]
                val hi = (idx shr 1) - 2
                hb[hi] = p.x + p.slantX
                hb[hi + 1] = p.y + p.lengthPx
            } else {
                // Render foliage with rotation + tumbling (3D flip) that grows with proximity
                val layer = p.layer
                val flip = if (layer == 0) 1f
                else kotlin.math.abs(cos(p.flipPhase)).coerceAtLeast(if (layer == 2) 0.28f else 0.5f)
                val half = spriteDrawSize[layer] / 2f
                nativeCanvas.save()
                nativeCanvas.rotate(p.rotation, p.x, p.y)
                nativeCanvas.scale(flip, 1f, p.x, p.y)
                spriteRect.set(p.x - half, p.y - half, p.x + half, p.y + half)
                nativeCanvas.drawBitmap(
                    emojiSprites[layer].getValue(p.emoji), null, spriteRect, spritePaints[layer]
                )
                nativeCanvas.restore()
            }
        }

        // Far -> near, so close drops overlap distant ones; each layer is one batched call plus a bright core.
        for (l in 0..2) {
            val n = rainCounts[l]
            if (n > 0) {
                nativeCanvas.drawLines(rainBuffers[l], 0, n, rainPaints[l])
                nativeCanvas.drawPoints(headBuffers[l], 0, n / 2, headPaints[l])
            }
        }
    }
}
