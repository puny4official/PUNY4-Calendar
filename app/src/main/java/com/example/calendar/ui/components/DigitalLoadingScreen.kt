package com.example.calendar.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

/** Total time the startup loading screen stays visible. */
private const val LOADING_DURATION_MS = 2000L

/**
 * Startup loading screen (black):
 *  1. app cover in the middle
 *  2. app name under it
 *  3. square-outline loader under the name
 *  4. fixed "Loading" text under the loader, only its three dots come and go
 *  5. "Version 1.0.0" at the bottom
 * Hands over to the app after 5 seconds.
 */
@Composable
fun DigitalLoadingScreen(
    modifier: Modifier = Modifier,
    onFinished: (() -> Unit)? = null
) {
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        delay(LOADING_DURATION_MS)
        currentOnFinished?.invoke()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("digital_loading_screen")
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.calendar_astro_icon),
                contentDescription = "App Cover",
                modifier = Modifier
                    .size(220.dp)
                    .testTag("digital_loading_cover"),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "PUNY4 Calendar",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("digital_loading_app_name")
            )

            SquareLoader(
                modifier = Modifier
                    .size(72.dp)
                    .testTag("digital_loading_spinner")
            )

            LoadingDotsText(modifier = Modifier.testTag("digital_loading_text"))
        }

        Text(
            text = "Version 1.0.0",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .testTag("digital_loading_version")
        )
    }
}

/** "Loading" stays fixed; the three dots appear one by one and reset, without shifting the text. */
@Composable
private fun LoadingDotsText(modifier: Modifier = Modifier) {
    var visibleDots by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            visibleDots = (visibleDots + 1) % 4 // 0, 1, 2, 3 dots, then back to 0
        }
    }

    // The app is RTL in Persian, which would put the dots on the left; force LTR so they sit after the text.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            val style = TextStyle(
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp
            )
            Text(text = "Loading", style = style)
            // Dots are always laid out (just transparent) so "Loading" never moves.
            for (i in 1..3) {
                Text(
                    text = ".",
                    style = style,
                    modifier = Modifier.alpha(if (i <= visibleDots) 1f else 0f)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Square-outline loader (port of the CSS ".loader" snippet, 1.5s linear loop)
// ---------------------------------------------------------------------------

private val LoaderColor = Color(0xFF046D8B)

// Keyframes: time, then the drawn fraction of the top / right / bottom / left edge.
private val LoaderKeyTimes = floatArrayOf(0f, 0.125f, 0.25f, 0.375f, 0.43f, 0.57f, 0.625f, 0.75f, 0.875f, 1f)
private val LoaderKeyTop = floatArrayOf(0f, 1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f)
private val LoaderKeyRight = floatArrayOf(0f, 0f, 1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f)
private val LoaderKeyBottom = floatArrayOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 0f, 0f)
private val LoaderKeyLeft = floatArrayOf(0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 0f)

private fun sampleKeys(values: FloatArray, t: Float): Float {
    for (i in 1 until LoaderKeyTimes.size) {
        if (t <= LoaderKeyTimes[i]) {
            val t0 = LoaderKeyTimes[i - 1]
            val span = LoaderKeyTimes[i] - t0
            val f = if (span <= 0f) 1f else (t - t0) / span
            return values[i - 1] + (values[i] - values[i - 1]) * f
        }
    }
    return values.last()
}

/** Draws one square outline (side [s], line [w]) that draws itself clockwise, then erases itself. */
private fun DrawScope.drawLoaderSquare(t: Float, s: Float, w: Float) {
    val top = sampleKeys(LoaderKeyTop, t) * s
    val right = sampleKeys(LoaderKeyRight, t) * s
    val bottom = sampleKeys(LoaderKeyBottom, t) * s
    val left = sampleKeys(LoaderKeyLeft, t) * s

    if (t < 0.5f) {
        // Edges grow from their start corner (top-left, top-right, bottom-right, bottom-left).
        drawRect(LoaderColor, Offset(0f, 0f), Size(top, w))
        drawRect(LoaderColor, Offset(s - w, 0f), Size(w, right))
        drawRect(LoaderColor, Offset(s - bottom, s - w), Size(bottom, w))
        drawRect(LoaderColor, Offset(0f, s - left), Size(w, left))
    } else {
        // Anchors flip at 50%, so the same edges shrink away in the drawing direction.
        drawRect(LoaderColor, Offset(s - top, 0f), Size(top, w))
        drawRect(LoaderColor, Offset(s - w, s - right), Size(w, right))
        drawRect(LoaderColor, Offset(0f, s - w), Size(bottom, w))
        drawRect(LoaderColor, Offset(0f, 0f), Size(w, left))
    }
}

@Composable
private fun SquareLoader(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "square_loader")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1500, easing = LinearEasing), RepeatMode.Restart),
        label = "square_loader_t"
    )

    Canvas(modifier = modifier) {
        // Three squares chained diagonally: total width = s + 2 * (s - w), with w = 4/25 of s.
        val s = size.width / 2.68f
        val w = s * 4f / 25f
        val d = s - w
        val origin = Offset((size.width - s) / 2f, (size.height - s) / 2f)
        val pivot = Offset(s / 2f, s / 2f)

        withTransform({ translate(origin.x, origin.y) }) {
            drawLoaderSquare(t, s, w)
        }
        withTransform({
            translate(origin.x + d, origin.y - d)
            rotate(90f, pivot)
        }) {
            drawLoaderSquare(t, s, w)
        }
        withTransform({
            translate(origin.x - d, origin.y + d)
            rotate(-90f, pivot)
        }) {
            drawLoaderSquare(t, s, w)
        }
    }
}
