package com.example.calendar.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
 * Pure black digital loading screen displayed at app startup:
 * - 100% black background ("فقط و فقط یک صفحه سیاه")
 * - Center: App logo image with subtle digital pulse aura
 * - Bottom: Digital animation loading bar + "Loading ..." text
 */
@Composable
fun DigitalLoadingScreen(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "digital_loading_anim")

    // Pulsing aura around the logo
    val logoPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse_alpha"
    )

    val logoPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse_scale"
    )

    // Digital segmented progress animation (0 to 1 looping)
    val digitalProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "digital_progress"
    )

    // Animated dots for "Loading ..." (cycling: "Loading", "Loading .", "Loading ..", "Loading ...")
    val dotCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_dots"
    )

    val dotsText = when (dotCycle.toInt() % 4) {
        1 -> " ."
        2 -> " .."
        3 -> " ..."
        else -> ""
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("digital_loading_screen")
    ) {
        // -------------------------------------------------------------
        // CENTER: APP LOGO WITH DIGITAL AURA (ENLARGED)
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .testTag("digital_loading_logo_container"),
            contentAlignment = Alignment.Center
        ) {
            // Ambient digital purple/violet glow behind logo
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(44.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFA855F7).copy(alpha = 0.45f * logoPulseAlpha),
                                Color(0xFF6366F1).copy(alpha = 0.20f * logoPulseAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Logo image container with modern rounded corners and cyber border (enlarged)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(32.dp), spotColor = Color(0xFFA855F7))
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black)
                    .border(
                        width = 1.8.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFA855F7).copy(alpha = logoPulseAlpha),
                                Color(0xFF8B5CF6).copy(alpha = 0.6f),
                                Color(0xFF06B6D4).copy(alpha = 0.35f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.calendar_astro_icon),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

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
                        isActive -> 0.75f
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
