package com.example.calendar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.model.MoonInfo
import com.example.calendar.model.MoonPhaseType
import com.example.ui.theme.AstroGold
import com.example.ui.theme.MoonPaleYellow
import kotlin.math.cos

@Composable
fun MoonPhaseCanvas(
    moonInfo: MoonInfo,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val darkMoonColor = Color(0xFF1E293B)
    val lightMoonColor = MoonPaleYellow
    val glowColor = MoonPaleYellow.copy(alpha = 0.4f)

    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        // Subtle glow aura
        drawCircle(
            color = glowColor,
            radius = radius + 2.dp.toPx(),
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Base dark disc
        drawCircle(
            color = darkMoonColor,
            radius = radius,
            center = center
        )

        // Calculate illuminated portion based on phase angle
        val phaseAngleRad = Math.toRadians(moonInfo.phaseAngleDegrees)
        val k = (1.0 - cos(phaseAngleRad)) / 2.0 // illumination fraction 0.0 .. 1.0
        val isWaxing = moonInfo.phaseAngleDegrees < 180.0

        if (moonInfo.phaseType == MoonPhaseType.FULL_MOON || k >= 0.99) {
            // Full Moon (بدر کامل): entire disc is illuminated
            drawCircle(
                color = lightMoonColor,
                radius = radius,
                center = center
            )
        } else if (moonInfo.phaseType != MoonPhaseType.NEW_MOON && k > 0.01) {
            val path = Path()
            val arcRect = Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
            val rx = (radius * Math.abs(2.0 * k - 1.0)).toFloat().coerceAtLeast(0.1f)
            val termRect = Rect(center.x - rx, center.y - radius, center.x + rx, center.y + radius)

            if (isWaxing) {
                // Waxing (رشد فزاینده ماه به سمت بدر): right side illuminated
                // Outer right semicircle: Top (270°) to Bottom (90°) clockwise (+180°)
                path.arcTo(arcRect, 270f, 180f, false)
                if (k < 0.5) {
                    // Waxing Crescent (هلال افزاینده): terminator curves into right side
                    path.arcTo(termRect, 90f, -180f, false)
                } else {
                    // Waxing Gibbous (تحدب فزاینده): terminator bulges into left side
                    path.arcTo(termRect, 90f, 180f, false)
                }
            } else {
                // Waning (محو شدن و کاهش ماه از بدر کامل تا ماه نو): left side illuminated
                // Outer left semicircle: Top (270°) to Bottom (90°) counter-clockwise (-180°)
                path.arcTo(arcRect, 270f, -180f, false)
                if (k >= 0.5) {
                    // Waning Gibbous (تحدب کاهنده): terminator bulges into right side (mostly full, fading)
                    path.arcTo(termRect, 90f, -180f, false)
                } else {
                    // Waning Crescent (هلال کاهنده رو به محاق): terminator curves into left side (fading crescent)
                    path.arcTo(termRect, 90f, 180f, false)
                }
            }
            path.close()
            drawPath(path = path, color = lightMoonColor)
        }

        // Rim border
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
fun MoonPhaseProgressionBar(
    currentPhase: MoonPhaseType,
    modifier: Modifier = Modifier
) {
    // Phases in progression from crescent to full moon and fading: 🌒 🌓 🌔 🌕 🌖 🌗 🌘
    val phases = listOf(
        MoonPhaseType.WAXING_CRESCENT,
        MoonPhaseType.FIRST_QUARTER,
        MoonPhaseType.WAXING_GIBBOUS,
        MoonPhaseType.FULL_MOON,
        MoonPhaseType.WANING_GIBBOUS,
        MoonPhaseType.THIRD_QUARTER,
        MoonPhaseType.WANING_CRESCENT
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        phases.forEach { phase ->
            val isCurrent = phase == currentPhase
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = phase.emoji,
                    fontSize = if (isCurrent) 22.sp else 16.sp,
                    color = if (isCurrent) MoonPaleYellow else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MoonPaleYellow, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                } else {
                    Spacer(modifier = Modifier.size(6.dp))
                }
            }
        }
    }
}

@Composable
fun MoonPhaseCompactBadge(
    moonInfo: MoonInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MoonPhaseCanvas(moonInfo = moonInfo, size = 32.dp)
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = moonInfo.phaseType.emoji,
                    fontSize = 14.sp
                )
                Text(
                    text = moonInfo.phaseType.titlePersian,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "${moonInfo.illuminationPercent}% روشنایی • سن: ${String.format(java.util.Locale.US, "%.1f", moonInfo.ageDays)} روز",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
