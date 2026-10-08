package com.example.calendar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class CosmicStar(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float
)

private val CosmicStars = List(90) {
    val random = Random(7000 + it * 17)

    CosmicStar(
        x = random.nextFloat(),
        y = random.nextFloat(),
        radius = 0.55f + random.nextFloat() * 1.45f,
        alpha = 0.18f + random.nextFloat() * 0.62f
    )
}

@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF05030D))
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            // Main purple cosmic glow
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF35146E),
                        Color(0xFF130A2B),
                        Color(0xFF05030D)
                    ),
                    center = Offset(
                        size.width * 0.50f,
                        size.height * 0.08f
                    ),
                    radius = size.maxDimension * 0.90f
                )
            )

            // Secondary blue cosmic glow
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF182B62).copy(alpha = 0.30f),
                        Color.Transparent
                    ),
                    center = Offset(
                        size.width * 0.12f,
                        size.height * 0.70f
                    ),
                    radius = size.width * 0.75f
                )
            )

            // Stars
            CosmicStars.forEach { star ->

                drawCircle(
                    color = Color.White.copy(
                        alpha = star.alpha
                    ),
                    radius = star.radius,
                    center = Offset(
                        size.width * star.x,
                        size.height * star.y
                    )
                )
            }
        }

        content()
    }
}
