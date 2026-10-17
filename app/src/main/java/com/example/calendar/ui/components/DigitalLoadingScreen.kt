package com.example.calendar.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.annotation.RawRes
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import kotlinx.coroutines.delay

/** Total time the startup loading screen stays visible. */
private const val LOADING_DURATION_MS = 5000L

/**
 * Startup loading screen (black):
 *  1. app cover in the middle
 *  2. app name under it
 *  3. blue spinner video under the name
 *  4. fixed "Loading" text under the spinner, only its three dots come and go
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

            LoadingVideo(
                rawRes = R.raw.loading_spinner,
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

/** Silent, non-looping video clip on a black background. */
@Composable
private fun LoadingVideo(@RawRes rawRes: Int, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            VideoView(ctx).apply {
                setBackgroundColor(android.graphics.Color.BLACK)
                setVideoURI(Uri.parse("android.resource://${ctx.packageName}/$rawRes"))
                setOnPreparedListener { player ->
                    player.setVolume(0f, 0f)
                    player.isLooping = false
                    start()
                }
                setOnErrorListener { _, _, _ -> true } // never block the app on a video error
            }
        },
        onRelease = { view -> view.stopPlayback() }
    )
}
