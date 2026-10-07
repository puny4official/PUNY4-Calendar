package com.example.calendar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.ScorpioAlert

/**
 * Astrological symbol for Scorpio (♏).
 * Drawn cleanly using Vector Canvas paths to completely avoid OS emoji styling
 * (eliminates unwanted turquoise/colored background circles and boxes).
 */
@Composable
fun ScorpioIcon(
    modifier: Modifier = Modifier,
    tint: Color = ScorpioAlert
) {
    Canvas(modifier = modifier) {
        val sx = size.width / 24f
        val sy = size.height / 24f
        val strokeWidth = (size.width * 0.13f).coerceIn(1.2f, 3.5f)

        val path = Path().apply {
            // First loop of 'm'
            moveTo(3.5f * sx, 18.5f * sy)
            lineTo(3.5f * sx, 9.5f * sy)
            cubicTo(3.5f * sx, 5.5f * sy, 9f * sx, 5.5f * sy, 9f * sx, 9.5f * sy)
            lineTo(9f * sx, 18.5f * sy)

            // Second loop of 'm'
            moveTo(9f * sx, 10.5f * sy)
            cubicTo(9f * sx, 5.5f * sy, 14.5f * sx, 5.5f * sy, 14.5f * sx, 9.5f * sy)
            lineTo(14.5f * sx, 17f * sy)

            // Tail curving up and right to form the scorpion sting / arrow
            cubicTo(14.5f * sx, 20.5f * sy, 17.5f * sx, 20.5f * sy, 19f * sx, 17.5f * sy)
            lineTo(22f * sx, 8.5f * sy)

            // Arrowhead at top-right
            moveTo(17f * sx, 9.5f * sy)
            lineTo(22f * sx, 8.5f * sy)
            lineTo(21.5f * sx, 14f * sy)
        }

        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
