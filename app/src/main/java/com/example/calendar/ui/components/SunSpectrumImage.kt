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

private const val X_RISE = 0.0801f
private const val X_SET = 0.9139f
private const val Y_HOR = 0.5028f
private val UP = floatArrayOf(0.0f, -0.005f, -0.0096f, -0.0165f, -0.0276f, -0.0411f, -0.0573f, -0.0756f, -0.0962f, -0.117f, -0.1379f, -0.159f, -0.1786f, -0.1974f, -0.2144f, -0.2298f, -0.2426f, -0.2534f, -0.2614f, -0.2691f, -0.2663f, -0.2712f, -0.2648f, -0.2567f, -0.2471f, -0.2346f, -0.2191f, -0.2021f, -0.1829f, -0.1624f, -0.1403f, -0.118f, -0.0953f, -0.073f, -0.0535f, -0.0373f, -0.0235f, -0.0137f, -0.0089f, -0.0075f, 0.0f)
private val LOW = floatArrayOf(0.0f, 0.0055f, 0.0111f, 0.0235f, 0.0395f, 0.0579f, 0.078f, 0.0994f, 0.122f, 0.1433f, 0.1636f, 0.1831f, 0.2009f, 0.2168f, 0.231f, 0.2433f, 0.2533f, 0.2612f, 0.267f, 0.2704f, 0.2702f, 0.2701f, 0.2675f, 0.2627f, 0.2551f, 0.2453f, 0.2336f, 0.2201f, 0.2047f, 0.1873f, 0.1678f, 0.1471f, 0.1256f, 0.1025f, 0.0798f, 0.0582f, 0.0387f, 0.0224f, 0.0099f, 0.005f, 0.0f)

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
