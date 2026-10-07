package com.example.calendar.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Pure black digital loading screen displayed exclusively at app startup:
 * - 100% black background ("فقط و فقط یک صفحه سیاه")
 * - Center: App cover image
 * - Bottom: Digital animation loading bar that smoothly fills 0 -> 100%, then transitions immediately
 */
@Composable
fun DigitalLoadingScreen(
    modifier: Modifier = Modifier,
    onFinished: (() -> Unit)? = null
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Smooth progressive filling from 0 to 100%
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
        )
        // Immediately when filled 100%, enter app seamlessly
        onFinished?.invoke()
    }

    val digitalProgress = progress.value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("digital_loading_screen")
    ) {
        // -------------------------------------------------------------
        // CENTER: APP COVER
        // -------------------------------------------------------------
        Image(
            painter = painterResource(id = R.drawable.calendar_astro_icon),
            contentDescription = "App Cover",
            modifier = Modifier
                .align(Alignment.Center)
                .size(240.dp)
                .testTag("digital_loading_cover"),
            contentScale = ContentScale.Fit
        )

        // -------------------------------------------------------------
        // BOTTOM: DIGITAL ANIMATION LOADING + "Loading ..."
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 54.dp, start = 24.dp, end = 24.dp)
                .testTag("digital_loading_bottom_section"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Digital Segmented Progress Bar (10 glowing cyber blocks)
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("digital_progress_segments")
            ) {
                val segmentCount = 10
                for (i in 0 until segmentCount) {
                    val segmentThreshold = i.toFloat() / segmentCount.toFloat()
                    val isActive = digitalProgress >= segmentThreshold
                    val isLead = (digitalProgress - segmentThreshold) in 0f..(1f / segmentCount)

                    val segmentColor = when {
                        isLead -> Color(0xFF00F5FF)
                        isActive -> Color(0xFF06B6D4)
                        else -> Color(0xFF1E293B)
                    }

                    val glowAlpha = when {
                        isLead -> 1f
                        isActive -> 0.85f
                        else -> 0.25f
                    }

                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(segmentColor.copy(alpha = glowAlpha))
                    )
                }
            }

            // Text: "Loading ..."
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Loading ...",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.8.sp,
                    modifier = Modifier.testTag("loading_english_text")
                )
            }
        }
    }
}
