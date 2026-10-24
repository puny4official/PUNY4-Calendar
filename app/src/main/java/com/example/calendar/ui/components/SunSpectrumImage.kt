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

    Box(modifier = modifier.fillMaxWidth().aspectRatio(1776f / 592f).clip(RoundedCornerShape(12.dp))) {
        Image(
            painter = painterResource(R.drawable.sun_spectrum),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
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

            val isDay = t in rise..set
            val x: Float
            val y: Float
            if (isDay) {
                val p = ((t - rise) / (set - rise)).coerceIn(0.0, 1.0)
                x = xRise + (xSet - xRise) * p.toFloat()
                y = curveY(UP, p)
            } else {
                val len = 1440.0 - (set - rise)
                val since = if (t >= set) t - set else t + 1440.0 - set
                val p = (since / len).coerceIn(0.0, 1.0)
                x = xSet - (xSet - xRise) * p.toFloat()
                y = curveY(LOW, 1.0 - p)
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

private const val X_RISE = 0.0794f
private const val X_SET = 0.9139f
private const val Y_HOR = 0.5017f
private val UP = floatArrayOf(0.0f, -0.0085f, -0.0092f, -0.015f, -0.026f, -0.0397f, -0.0557f, -0.0746f, -0.0946f, -0.1155f, -0.1367f, -0.1574f, -0.1777f, -0.1963f, -0.2139f, -0.2291f, -0.2422f, -0.2534f, -0.261f, -0.2664f, -0.2671f, -0.2677f, -0.2639f, -0.2573f, -0.2475f, -0.2348f, -0.2197f, -0.2026f, -0.1836f, -0.1627f, -0.1408f, -0.1184f, -0.0955f, -0.0734f, -0.0537f, -0.0371f, -0.0232f, -0.014f, -0.0088f, -0.0085f, 0.0f)
private val LOW = floatArrayOf(0.0f, 0.0068f, 0.011f, 0.0241f, 0.0397f, 0.0577f, 0.0782f, 0.0998f, 0.1217f, 0.1434f, 0.1639f, 0.1835f, 0.2011f, 0.2176f, 0.2317f, 0.2438f, 0.254f, 0.2621f, 0.2682f, 0.2719f, 0.2721f, 0.2724f, 0.2692f, 0.2639f, 0.2565f, 0.2467f, 0.2352f, 0.2219f, 0.2063f, 0.1891f, 0.1698f, 0.149f, 0.127f, 0.1047f, 0.0814f, 0.0596f, 0.0402f, 0.0237f, 0.0108f, 0.0054f, 0.0f)

private fun lerpColor(a: Color, b: Color, f: Float) = androidx.compose.ui.graphics.lerp(a, b, f.coerceIn(0f, 1f))

private fun DrawScope.sun(c: Offset, col: Color) {
    val r = size.width * 0.0105f
    // layered soft glow
    drawCircle(Brush.radialGradient(listOf(col.copy(alpha = 0.40f), Color.Transparent), c, r * 4.6f), r * 4.6f, c)
    drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), c, r * 2.2f), r * 2.2f, c)
    // 16 rays, long and short alternating, round caps
    for (i in 0 until 16) {
        val a = i * PI / 8
        val d = Offset(cos(a).toFloat(), sin(a).toFloat())
        val long = i % 2 == 0
        drawLine(
            col.copy(alpha = if (long) 0.95f else 0.7f),
            c + d * (r * 1.45f), c + d * (r * if (long) 2.35f else 1.9f),
            strokeWidth = r * if (long) 0.20f else 0.14f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
    // body: warm gradient disc with a bright core and a thin rim
    drawCircle(Brush.radialGradient(listOf(Color.White, Color(0xFFFFF3B0), col), c - Offset(r * 0.25f, r * 0.25f), r * 1.3f), r, c)
    drawCircle(Color.White.copy(alpha = 0.55f), r, c, style = androidx.compose.ui.graphics.drawscope.Stroke(width = r * 0.08f))
}

private fun DrawScope.moon(c: Offset) {
    val r = size.width * 0.0115f
    val tint = Color(0xFF8E9CFF)
    drawCircle(Brush.radialGradient(listOf(tint.copy(alpha = 0.45f), Color.Transparent), c, r * 4.4f), r * 4.4f, c)
    val crescent = androidx.compose.ui.graphics.Path.combine(
        androidx.compose.ui.graphics.PathOperation.Difference,
        androidx.compose.ui.graphics.Path().apply { addOval(androidx.compose.ui.geometry.Rect(c, r)) },
        androidx.compose.ui.graphics.Path().apply { addOval(androidx.compose.ui.geometry.Rect(c + Offset(r * 0.62f, -r * 0.22f), r * 0.88f)) }
    )
    drawPath(crescent, Brush.verticalGradient(listOf(Color.White, Color(0xFFC9D2FF)), c.y - r, c.y + r))
    // twinkling stars (4-point)
    val stars = listOf(Triple(-1.9f, -1.3f, 0.42f), Triple(1.9f, -1.5f, 0.34f), Triple(2.1f, 1.0f, 0.3f), Triple(-1.5f, 1.6f, 0.26f))
    for ((dx, dy, k) in stars) {
        val sc = c + Offset(dx * r, dy * r)
        val kk = k * r
        drawLine(Color.White.copy(alpha = 0.9f), sc - Offset(kk, 0f), sc + Offset(kk, 0f), strokeWidth = r * 0.09f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(Color.White.copy(alpha = 0.9f), sc - Offset(0f, kk), sc + Offset(0f, kk), strokeWidth = r * 0.09f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    }
}
