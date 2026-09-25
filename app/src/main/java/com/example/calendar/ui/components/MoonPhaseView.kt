package com.example.calendar.ui.components

import androidx.compose.foundation.Canvas
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
import com.example.ui.theme.MoonSilver
import kotlin.math.cos

@Composable
fun MoonPhaseCanvas(
    moonInfo: MoonInfo,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val darkMoonColor = Color(0xFF1E293B)
    val lightMoonColor = MoonSilver
    val glowColor = AstroGold.copy(alpha = 0.3f)

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
        val k = (1.0 - cos(phaseAngleRad)) / 2.0 // illumination 0..1
        val isWaxing = moonInfo.phaseAngleDegrees < 180.0

        if (moonInfo.phaseType == MoonPhaseType.FULL_MOON) {
            drawCircle(
                color = lightMoonColor,
                radius = radius,
                center = center
            )
        } else if (moonInfo.phaseType != MoonPhaseType.NEW_MOON) {
            val path = Path()
            val arcRect = Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)

            if (isWaxing) {
                // Waxing: right side illuminated (or left depending on hem, standard right)
                path.arcTo(arcRect, 270f, 180f, false)
                // Elliptical terminator curve
                val termWidth = (radius * (2 * k - 1)).toFloat()
                val termRect = Rect(center.x - Math.abs(termWidth), center.y - radius, center.x + Math.abs(termWidth), center.y + radius)
                if (termWidth >= 0) {
                    path.arcTo(termRect, 90f, -180f, false)
                } else {
                    path.arcTo(termRect, 90f, 180f, false)
                }
            } else {
                // Waning: left side illuminated
                path.arcTo(arcRect, 90f, 180f, false)
                val termWidth = (radius * (2 * k - 1)).toFloat()
                val termRect = Rect(center.x - Math.abs(termWidth), center.y - radius, center.x + Math.abs(termWidth), center.y + radius)
                if (termWidth >= 0) {
                    path.arcTo(termRect, 270f, -180f, false)
                } else {
                    path.arcTo(termRect, 270f, 180f, false)
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
            Text(
                text = moonInfo.phaseType.titlePersian,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${moonInfo.illuminationPercent}% روشنایی • سن: ${moonInfo.ageDays} روز",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
