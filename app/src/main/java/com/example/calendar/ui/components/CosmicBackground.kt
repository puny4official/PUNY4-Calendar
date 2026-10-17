package com.example.calendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
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

/**
 * Screen background that follows the theme:
 *  - dark mode: pure AMOLED black with faint stars
 *  - light mode: plain white
 */
@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val isDark = backgroundColor.luminance() < 0.5f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (isDark) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer()
                    .drawWithCache {
                        onDrawBehind {
                            CosmicStars.forEach { star ->
                                drawCircle(
                                    color = Color.White.copy(alpha = star.alpha),
                                    radius = star.radius,
                                    center = Offset(size.width * star.x, size.height * star.y)
                                )
                            }
                        }
                    }
            )
        }

        content()
    }
}
