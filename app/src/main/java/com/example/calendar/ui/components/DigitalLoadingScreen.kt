package com.example.calendar.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue

/** Total time the startup loading screen stays visible. */
private const val LOADING_DURATION_MS = 5000L

/**
 * Startup loading screen: plays the bundled loading video (res/raw/loading_video.mp4)
 * on a black background, then hands over to the app after 5 seconds.
 */
@Composable
fun DigitalLoadingScreen(
    modifier: Modifier = Modifier,
    onFinished: (() -> Unit)? = null
) {
    val context = LocalContext.current
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
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .testTag("digital_loading_video"),
            factory = { ctx ->
                VideoView(ctx).apply {
                    setBackgroundColor(android.graphics.Color.BLACK)
                    setVideoURI(Uri.parse("android.resource://${ctx.packageName}/${R.raw.loading_video}"))
                    setOnPreparedListener { player ->
                        player.setVolume(0f, 0f) // silent splash
                        player.isLooping = false
                        start()
                    }
                    setOnErrorListener { _, _, _ -> true } // never block the app on a video error
                }
            },
            onRelease = { view -> view.stopPlayback() }
        )
    }
}
