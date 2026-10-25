package com.example.calendar.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.R
import com.example.calendar.core.CalendarManager
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun mins(hhmm: String): Double? {
    val p = hhmm.trim().split(":")
    if (p.size < 2) return null
    val h = p[0].toIntOrNull() ?: return null
    val m = p[1].take(2).toIntOrNull() ?: return null
    return h * 60.0 + m
}

private fun nowMinutes(): Double {
    val t = LocalTime.now(CalendarManager.IRAN_ZONE)
    return t.hour * 60.0 + t.minute + t.second / 60.0
}

/**
 * The supplied spectrum picture, untouched, with a live sun / moon travelling along its curves:
 * sunrise (left) -> noon -> sunset (right), then under the horizon through midnight back to sunrise.
 */
@Composable
fun SunSpectrumImage(
    city: com.example.calendar.model.CityLocation,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableDoubleStateOf(nowMinutes()) }
    LaunchedEffect(Unit) {
        while (true) { now = nowMinutes(); delay(1000) }
    }
    // Always today's real solar times for the selected city (Iran date), recomputed when the city or the date changes
    val todayIran = java.time.LocalDate.now(CalendarManager.IRAN_ZONE)
    val times = remember(city, todayIran) {
        com.example.calendar.core.AstronomicalCalculator.calculateSolarTimes(
            com.example.calendar.core.JalaliCalendar.gregorianToJdn(todayIran.year, todayIran.monthValue, todayIran.dayOfMonth),
            city
        )
    }
    val srS = times.sunrise
    val noonS = times.noon
    val ssS = times.sunset
    val midS = times.midnight
    val inf = rememberInfiniteTransition(label = "glow")
    val pulse by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Reverse), label = "pulse")
    val spin by inf.animateFloat(0f, 360f, infiniteRepeatable(tween(30000, easing = LinearEasing)), label = "spin")
    val rise = mins(srS) ?: 360.0
    val set = mins(ssS) ?: 1080.0
    val t = now
    val noonM = (mins(noonS) ?: ((rise + set) / 2.0)).coerceIn(rise + 1, set - 1)
    val midM = mins(midS) ?: ((noonM + 720.0) % 1440.0)
    fun mod(v: Double) = ((v % 1440.0) + 1440.0) % 1440.0

    Box(modifier = modifier.fillMaxWidth().aspectRatio(2.25f).clip(RoundedCornerShape(12.dp))) {
        Image(
            painter = painterResource(R.drawable.sun_spectrum),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Curve geometry measured from the picture: x from sunrise to sunset, y offset from the horizon (fraction of height)
            val xRise = X_RISE * size.width
            val xSet = X_SET * size.width
            val yHor = Y_HOR * size.height
            fun curveY(tab: FloatArray, p: Double): Float {
                val f = (p.coerceIn(0.0, 1.0) * (tab.size - 1)).toFloat()
                val i = f.toInt().coerceAtMost(tab.size - 2)
                return yHor + (tab[i] + (tab[i + 1] - tab[i]) * (f - i)) * size.height
            }

            // Piecewise so the sun hits the sunrise / noon / sunset ends and the moon hits solar midnight exactly
            val isDay = t in rise..set
            val x: Float
            val y: Float
            if (isDay) {
                val p = if (t <= noonM) 0.5 * (t - rise) / (noonM - rise) else 0.5 + 0.5 * (t - noonM) / (set - noonM)
                x = xRise + (xSet - xRise) * p.toFloat()
                y = curveY(UP, p)
            } else {
                val sNow = mod(t - set)
                val sMid = mod(midM - set).coerceAtLeast(1.0)
                val sLen = mod(rise - set).coerceAtLeast(sMid + 1.0)
                val q = if (sNow <= sMid) 0.5 * sNow / sMid else 0.5 + 0.5 * (sNow - sMid) / (sLen - sMid)
                x = xSet - (xSet - xRise) * q.toFloat()
                y = curveY(LOW, 1.0 - q)
            }
            val c = Offset(x, y)
            if (isDay) {
                val p = (if (t <= noonM) 0.5 * (t - rise) / (noonM - rise) else 0.5 + 0.5 * (t - noonM) / (set - noonM)).coerceIn(0.0, 1.0).toFloat()
                // yellow until noon, then towards orange / red-orange at sunset
                val orange = Color(0xFFFF9A2E)
                val yellow = Color(0xFFFFE45C)
                val red = Color(0xFFFF4D4D)
                // sunrise orange -> full yellow at noon -> sunset red
                val col = if (p < 0.5f) lerpColor(orange, yellow, p * 2f) else lerpColor(yellow, red, (p - 0.5f) * 2f)
                sun(c, col, pulse, spin)
            } else {
                moon(c, pulse)
            }
        }
    }
}

private const val X_RISE = 0.0230f
private const val X_SET = 0.9693f
private const val Y_HOR = 0.5017f
private val UP = floatArrayOf(0.0f, -0.0085f, -0.0092f, -0.015f, -0.026f, -0.0397f, -0.0557f, -0.0746f, -0.0946f, -0.1155f, -0.1367f, -0.1574f, -0.1777f, -0.1963f, -0.2139f, -0.2291f, -0.2422f, -0.2534f, -0.261f, -0.2664f, -0.2671f, -0.2677f, -0.2639f, -0.2573f, -0.2475f, -0.2348f, -0.2197f, -0.2026f, -0.1836f, -0.1627f, -0.1408f, -0.1184f, -0.0955f, -0.0734f, -0.0537f, -0.0371f, -0.0232f, -0.014f, -0.0088f, -0.0085f, 0.0f)
private val LOW = floatArrayOf(0.0f, 0.0068f, 0.011f, 0.0241f, 0.0397f, 0.0577f, 0.0782f, 0.0998f, 0.1217f, 0.1434f, 0.1639f, 0.1835f, 0.2011f, 0.2176f, 0.2317f, 0.2438f, 0.254f, 0.2621f, 0.2682f, 0.2719f, 0.2721f, 0.2724f, 0.2692f, 0.2639f, 0.2565f, 0.2467f, 0.2352f, 0.2219f, 0.2063f, 0.1891f, 0.1698f, 0.149f, 0.127f, 0.1047f, 0.0814f, 0.0596f, 0.0402f, 0.0237f, 0.0108f, 0.0054f, 0.0f)

private fun lerpColor(a: Color, b: Color, f: Float) = androidx.compose.ui.graphics.lerp(a, b, f.coerceIn(0f, 1f))

private fun DrawScope.sun(c: Offset, col: Color, pulse: Float, spin: Float) {
    val r = size.width * 0.026f
    val k = 0.85f + 0.3f * pulse // breathing glow
    // wide breathing halo + bright inner glow
    drawCircle(Brush.radialGradient(listOf(col.copy(alpha = 0.50f * k), Color.Transparent), c, r * 5.4f * k), r * 5.4f * k, c)
    drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.45f * k), Color.Transparent), c, r * 2.5f), r * 2.5f, c)
    // 16 slowly rotating rays that shimmer
    for (i in 0 until 16) {
        val a = Math.toRadians(spin.toDouble()) + i * PI / 8
        val d = Offset(cos(a).toFloat(), sin(a).toFloat())
        val long = i % 2 == 0
        val shimmer = 0.65f + 0.35f * sin((pulse * 2f + i * 0.4f) * PI.toFloat()).let { (it + 1f) / 2f }
        val len = (if (long) 2.4f else 1.95f) * (0.92f + 0.12f * pulse)
        drawLine(
            col.copy(alpha = shimmer),
            c + d * (r * 1.45f), c + d * (r * len),
            strokeWidth = r * if (long) 0.20f else 0.14f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
    drawCircle(Brush.radialGradient(listOf(Color.White, Color(0xFFFFF3B0), col), c - Offset(r * 0.25f, r * 0.25f), r * 1.3f), r, c)
    drawCircle(Color.White.copy(alpha = 0.55f), r, c, style = androidx.compose.ui.graphics.drawscope.Stroke(width = r * 0.08f))
}

private fun DrawScope.moon(c: Offset, pulse: Float) {
    val r = size.width * 0.029f
    val tint = Color(0xFF8E9CFF)
    val k = 0.85f + 0.3f * pulse
    // breathing blue-white halo
    drawCircle(Brush.radialGradient(listOf(tint.copy(alpha = 0.50f * k), Color.Transparent), c, r * 5f * k), r * 5f * k, c)
    drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.30f * k), Color.Transparent), c, r * 2.6f), r * 2.6f, c)
    // full moon body with soft shading
    drawCircle(Brush.radialGradient(listOf(Color.White, Color(0xFFE4E8FF), Color(0xFFB9C3F5)), c - Offset(r * 0.3f, r * 0.3f), r * 1.5f), r, c)
    // craters
    val crater = Color(0xFF9AA6DD).copy(alpha = 0.45f)
    drawCircle(crater, r * 0.24f, c + Offset(-r * 0.35f, -r * 0.30f))
    drawCircle(crater, r * 0.16f, c + Offset(r * 0.38f, r * 0.05f))
    drawCircle(crater, r * 0.20f, c + Offset(-r * 0.10f, r * 0.45f))
    drawCircle(crater, r * 0.10f, c + Offset(r * 0.25f, -r * 0.50f))
    drawCircle(Color.White.copy(alpha = 0.5f), r, c, style = androidx.compose.ui.graphics.drawscope.Stroke(width = r * 0.07f))
    // twinkling stars: each fades in and out at its own phase
    val stars = listOf(Triple(-2.0f, -1.4f, 0.0f), Triple(2.0f, -1.6f, 0.33f), Triple(2.3f, 1.0f, 0.66f), Triple(-1.7f, 1.6f, 0.5f))
    for ((dx, dy, ph) in stars) {
        val tw = (sin((pulse + ph) * 2f * PI.toFloat()) + 1f) / 2f
        val sc = c + Offset(dx * r, dy * r)
        val kk = (0.22f + 0.28f * tw) * r
        val col = Color.White.copy(alpha = 0.35f + 0.65f * tw)
        drawLine(col, sc - Offset(kk, 0f), sc + Offset(kk, 0f), strokeWidth = r * 0.09f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(col, sc - Offset(0f, kk), sc + Offset(0f, kk), strokeWidth = r * 0.09f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    }
}
