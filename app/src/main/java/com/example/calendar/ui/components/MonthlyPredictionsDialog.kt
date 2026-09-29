package com.example.calendar.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calendar.core.MonthlyPrediction
import com.example.ui.theme.HolidayPurple
import com.example.ui.theme.HolidayPurpleContainer
import com.example.ui.theme.OnHolidayPurpleContainer
import com.example.ui.theme.ScorpioAlert
import com.example.ui.theme.SuccessGreen

@Composable
fun MonthlyPredictionsDialog(
    prediction: MonthlyPrediction,
    fontScalePercent: Int = 100,
    isFa: Boolean = true,
    onDismiss: () -> Unit
) {
    val currentDensity = LocalDensity.current
    val dialogDensity = remember(currentDensity, fontScalePercent) {
        Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale * (fontScalePercent / 100f)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(
            LocalDensity provides dialogDensity,
            LocalLayoutDirection provides if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .testTag("monthly_predictions_dialog"),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, HolidayPurple.copy(alpha = 0.25f))
            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = HolidayPurpleContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = HolidayPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "پیشگوییهای ماه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = prediction.monthTitle.removePrefix("پیشگوییهای ماه – "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_predictions_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. رویدادهای نجومی
                    PredictionCard(
                        title = "رویدادهای نجومی",
                        iconEmoji = "🪐",
                        content = prediction.astronomicalEvents,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        accentColor = HolidayPurple
                    )

                    // 2. پیشگویی سنتی
                    PredictionCard(
                        title = "پیشگویی سنتی",
                        iconEmoji = "📜",
                        content = prediction.traditionalPrediction,
                        containerColor = HolidayPurpleContainer.copy(alpha = 0.35f),
                        accentColor = OnHolidayPurpleContainer
                    )

                    // 3. فرصتها
                    PredictionCard(
                        title = "فرصتها",
                        iconEmoji = "✨",
                        content = prediction.opportunities,
                        containerColor = SuccessGreen.copy(alpha = 0.08f),
                        accentColor = SuccessGreen
                    )

                    // 4. هشدارها
                    PredictionCard(
                        title = "هشدارها",
                        iconEmoji = "⚠️",
                        content = prediction.warnings,
                        containerColor = ScorpioAlert.copy(alpha = 0.08f),
                        accentColor = ScorpioAlert
                    )

                    // 5. ایام سعد
                    PredictionCard(
                        title = "ایام سعد",
                        iconEmoji = "🟢",
                        content = prediction.auspiciousDays,
                        containerColor = SuccessGreen.copy(alpha = 0.08f),
                        accentColor = SuccessGreen
                    )

                    // 6. ایام نحس
                    PredictionCard(
                        title = "ایام نحس",
                        iconEmoji = "🔴",
                        content = prediction.inauspiciousDays,
                        containerColor = ScorpioAlert.copy(alpha = 0.08f),
                        accentColor = ScorpioAlert
                    )

                    // 7. طالع هر ماه
                    PredictionCard(
                        title = "طالع هر ماه",
                        iconEmoji = "🔮",
                        content = prediction.monthlyHoroscope,
                        containerColor = HolidayPurpleContainer.copy(alpha = 0.45f),
                        accentColor = HolidayPurple
                    )

                    // Disclaimer rule 7
                    Text(
                        text = "(این بخش فرهنگی/سرگرمی است و مبنای علمی ندارد.)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }

                // Bottom Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HolidayPurple
                    )
                ) {
                    Text(
                        text = "بستن",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun PredictionCard(
    title: String,
    iconEmoji: String,
    content: String,
    containerColor: Color,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = iconEmoji, fontSize = 16.sp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontSize = 13.5.sp
                )
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 1.55.em,
                fontSize = 13.sp
            )
        }
    }
}
