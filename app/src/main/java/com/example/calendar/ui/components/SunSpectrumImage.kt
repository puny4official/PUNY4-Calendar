package com.example.calendar.ui.components

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

private var lastToday: Triple<String, String, String>? = null

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
    sunrise: String,
    noon: String,
    sunset: String,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableDoubleStateOf(nowMinutes()) }
    LaunchedEffect(Unit) {
        while (true) { now = nowMinutes(); delay(1000) }
    }
    // The spectrum always shows today: other days keep the last times seen for today and follow the live clock
    if (isToday) lastToday = Triple(sunrise, noon, sunset)
    val (srS, _, ssS) = lastToday ?: Triple(sunrise, noon, sunset)
    val rise = mins(srS) ?: 360.0
    val set = mins(ssS) ?: 1080.0
    val t = now

    Box(modifier = modifier.fillMaxWidth().aspectRatio(2172f / 724f).clip(RoundedCornerShape(12.dp))) {
        Image(
            painter = painterResource(R.drawable.sun_spectrum),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Geometry measured on the picture (fractions of its width / height)
            val xRise = 0.0737f * size.width
            val xSet = 0.8416f * size.width
            val xMid = 0.4581f * size.width
            val yHor = 0.4627f * size.height
            val ampDay = 0.2459f * size.height
            val ampNight = 0.2514f * size.height
            fun bell(u: Double) = ((1 + cos(PI * u)) / 2).toFloat() // 1 at centre, 0 at the horizon ends

            val isDay = t in rise..set
            val x: Float
            val y: Float
            if (isDay) {
                val p = ((t - rise) / (set - rise)).coerceIn(0.0, 1.0)
                x = xRise + (xSet - xRise) * p.toFloat()
                y = yHor - ampDay * bell(2 * p - 1)
            } else {
                val len = 1440.0 - (set - rise)
                val since = if (t >= set) t - set else t + 1440.0 - set
                val p = (since / len).coerceIn(0.0, 1.0)
                x = xSet - (xSet - xRise) * p.toFloat()
                y = yHor + ampNight * bell(2 * p - 1)
            }
            val c = Offset(x, y)
            if (isDay) {
                val p = ((t - rise) / (set - rise)).coerceIn(0.0, 1.0).toFloat()
                // yellow until noon, then towards orange / red-orange at sunset
                val col = if (p < 0.5f) Color(0xFFFFE066) else lerpColor(Color(0xFFFFE066), Color(0xFFFF7043), (p - 0.5f) * 2f)
                sun(c, col)
            } else {
                moon(c)
            }
        }
    }
}

private fun lerpColor(a: Color, b: Color, f: Float) = androidx.compose.ui.graphics.lerp(a, b, f.coerceIn(0f, 1f))

private fun DrawScope.sun(c: Offset, col: Color) {
    val r = 7.dp.toPx()
    drawCircle(Brush.radialGradient(listOf(col.copy(alpha = 0.55f), Color.Transparent), c, r * 3.2f), r * 3.2f, c)
    for (i in 0 until 12) {
        val a = i * PI / 6
        val d = Offset(cos(a).toFloat(), sin(a).toFloat())
        drawLine(col, c + d * (r * 1.35f), c + d * (r * 1.9f), strokeWidth = 1.4.dp.toPx())
    }
    drawCircle(col, r, c)
    drawCircle(Color.White.copy(alpha = 0.6f), r * 0.45f, c - Offset(r * 0.2f, r * 0.2f))
}

private fun DrawScope.moon(c: Offset) {
    val r = 7.dp.toPx()
    drawCircle(Brush.radialGradient(listOf(Color(0xFF9FB4FF).copy(alpha = 0.5f), Color.Transparent), c, r * 3f), r * 3f, c)
    drawCircle(Color.White, r, c)
    drawCircle(Color(0xFFD5DAF2), r * 0.22f, c + Offset(-r * 0.3f, -r * 0.2f))
    drawCircle(Color(0xFFD5DAF2), r * 0.16f, c + Offset(r * 0.3f, r * 0.25f))
}
