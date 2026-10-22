package com.example.calendar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.CalendarManager
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun minutesOf(hhmm: String): Double? {
    val p = hhmm.trim().split(":")
    if (p.size < 2) return null
    val h = p[0].toIntOrNull() ?: return null
    val m = p[1].take(2).toIntOrNull() ?: return null
    return h * 60.0 + m
}

/**
 * Live sun path: the sun starts at sunrise (right), climbs to solar noon, sets on the left,
 * then keeps moving under the horizon through the night towards the next sunrise.
 * It follows the real clock (Iran time) and repeats every day.
 */
@Composable
fun SunArcCard(
    sunrise: String,
    noon: String,
    sunset: String,
    modifier: Modifier = Modifier
) {
    var nowMin by remember { mutableDoubleStateOf(currentMinutes()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMin = currentMinutes()
            delay(1000)
        }
    }

    val rise = minutesOf(sunrise)
    val set = minutesOf(sunset)
    val noonMin = minutesOf(noon)

    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(124.dp)) {
        val w = maxWidth
        val xRightDp = w * 0.86f
        val xLeftDp = w * 0.14f

        Canvas(modifier = Modifier.fillMaxWidth().height(124.dp)) {
            val W = size.width
            val base = 92.dp.toPx()
            val amp = 66.dp.toPx()
            val dip = 20.dp.toPx()
            val xr = W * 0.86f
            val xl = W * 0.14f
            val lineColor = Color(0xFF4B5160)

            fun dayX(p: Double) = (xr - p * (xr - xl)).toFloat()
            fun dayY(p: Double) = (base - amp * sin(PI * p)).toFloat()
            fun nightX(p: Double) = (xl + p * (xr - xl)).toFloat()
            fun nightY(p: Double) = (base + dip * sin(PI * p)).toFloat()

            // day curve
            val curve = Path()
            for (i in 0..120) {
                val p = i / 120.0
                if (i == 0) curve.moveTo(dayX(p), dayY(p)) else curve.lineTo(dayX(p), dayY(p))
            }
            // faint night curve under the horizon
            val night = Path()
            for (i in 0..120) {
                val p = i / 120.0
                if (i == 0) night.moveTo(nightX(p), nightY(p)) else night.lineTo(nightX(p), nightY(p))
            }
            drawPath(night, lineColor.copy(alpha = 0.45f), style = Stroke(width = 1.5.dp.toPx()))
            drawPath(curve, lineColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

            // horizon
            drawLine(lineColor, Offset(0f, base), Offset(W, base), strokeWidth = 1.5.dp.toPx())
            // noon tick
            drawLine(lineColor, Offset(W / 2f, base - 2.dp.toPx()), Offset(W / 2f, base + 8.dp.toPx()), strokeWidth = 1.5.dp.toPx())
            // sunrise / sunset ticks
            drawLine(lineColor, Offset(xr, 36.dp.toPx()), Offset(xr, base), strokeWidth = 1.2.dp.toPx())
            drawLine(lineColor, Offset(xl, 36.dp.toPx()), Offset(xl, base), strokeWidth = 1.2.dp.toPx())

            if (rise == null || set == null) return@Canvas
            val dayLen = (set - rise).let { if (it <= 0) it + 1440.0 else it }
            val nightLen = 1440.0 - dayLen
            val sinceRise = ((nowMin - rise) % 1440.0 + 1440.0) % 1440.0

            val sunX: Float
            val sunY: Float
            val isDay = sinceRise <= dayLen
            if (isDay) {
                val p = (sinceRise / dayLen).coerceIn(0.0, 1.0)
                sunX = dayX(p); sunY = dayY(p)
                // filled region under the curve from sunrise to the sun
                val fill = Path()
                fill.moveTo(dayX(0.0), base)
                val steps = 80
                for (i in 0..steps) {
                    val q = p * i / steps
                    fill.lineTo(dayX(q), dayY(q))
                }
                fill.lineTo(sunX, base)
                fill.close()
                drawPath(
                    fill,
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3B6CB0).copy(alpha = 0.85f), Color(0xFF0E3A78).copy(alpha = 0.85f)),
                        startX = sunX, endX = xr
                    )
                )
            } else {
                val p = ((sinceRise - dayLen) / nightLen).coerceIn(0.0, 1.0)
                sunX = nightX(p); sunY = nightY(p)
            }

            // sun: glow + spikes + disc (dimmer under the horizon)
            val a = if (isDay) 1f else 0.55f
            val r = 11.dp.toPx()
            drawCircle(Color(0xFFFFC857).copy(alpha = 0.22f * a), radius = r * 2.1f, center = Offset(sunX, sunY))
            for (k in 0 until 14) {
                val ang = 2 * PI * k / 14
                drawLine(
                    Color(0xFFD9A441).copy(alpha = a),
                    Offset(sunX + (r * 0.95f * cos(ang)).toFloat(), sunY + (r * 0.95f * sin(ang)).toFloat()),
                    Offset(sunX + (r * 1.45f * cos(ang)).toFloat(), sunY + (r * 1.45f * sin(ang)).toFloat()),
                    strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round
                )
            }
            drawCircle(Color(0xFFFFD58A).copy(alpha = a), radius = r, center = Offset(sunX, sunY))
        }

        // labels above the sunrise / sunset ticks
        Text(
            text = "طلوع",
            color = Color(0xFFF59E0B),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.offset(x = xRightDp - 18.dp, y = 8.dp)
        )
        Text(
            text = "غروب",
            color = Color(0xFFEF4444),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.offset(x = xLeftDp - 18.dp, y = 8.dp)
        )
    }
}

private fun currentMinutes(): Double {
    val t = LocalTime.now(CalendarManager.IRAN_ZONE)
    return t.hour * 60.0 + t.minute + t.second / 60.0
}
