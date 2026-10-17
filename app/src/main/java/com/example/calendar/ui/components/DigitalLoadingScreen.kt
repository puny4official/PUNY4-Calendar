package com.example.calendar.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
 *  4. small "Loading" text video under the spinner
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

            LoadingVideo(
                rawRes = R.raw.loading_text,
                modifier = Modifier
                    .size(110.dp)
                    .testTag("digital_loading_text")
            )
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
