package com.example.calendar.ui.components

import com.example.ui.theme.cardFrameColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.AstroGold
import com.example.ui.theme.HolidayPurple
import com.example.ui.theme.HolidayPurpleLight
import com.example.ui.theme.ScorpioAlert

private data class GuideTopic(
    val titlePersian: String,
    val titleEnglish: String,
    val descriptionPersian: String,
    val descriptionEnglish: String,
    val icon: ImageVector,
    val iconTint: Color,
    val tipsPersian: List<String>,
    val tipsEnglish: List<String>
)

@Composable
fun AppGuideDialog(
    isFa: Boolean,
    fontScalePercent: Int = 100,
    onDismiss: () -> Unit
) {
    val topics = remember {
        listOf(
            GuideTopic(
                titlePersian = "ماندگاری نوتیف تقویم",
                titleEnglish = "Keeping the calendar notification alive",
                descriptionPersian = "برخی گوشی‌ها (شیائومی، هوآوی، سامسونگ و ...) برنامه‌هایی را که چند وقت باز نشده‌اند با «بهینه‌سازی باتری» می‌بندند و جلوی آلارم‌هایشان را می‌گیرند. برای اینکه نوتیف تقویم همیشه پین بماند و ساعت ۱۲ شب تاریخ دقیق عوض شود، این دو تنظیم را انجام دهید.",
                descriptionEnglish = "Some phones (Xiaomi, Huawei, Samsung, etc.) close apps that haven't been opened for a while with battery optimization and block their alarms. To keep the calendar notification pinned and switch the date exactly at 12 AM, apply these two settings.",
                icon = Icons.Default.Settings,
                iconTint = Color(0xFF6366F1),
                tipsPersian = listOf(
                    "تنظیمات گوشی ← برنامه‌ها ← PUNY4 Calendar ← باتری: گزینه «بدون محدودیت» را انتخاب کنید.",
                    "در همان بخش، «شروع خودکار» (Autostart) را روشن کنید."
                ),
                tipsEnglish = listOf(
                    "Phone Settings → Apps → PUNY4 Calendar → Battery: choose \"No restrictions\".",
                    "In the same place, turn on \"Autostart\"."
                )
            )
        )
    }

    val currentDensity = LocalDensity.current
    val dialogDensity = remember(currentDensity, fontScalePercent) {
        Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale // already scaled at the app root
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
                    .testTag("app_guide_dialog"),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, cardFrameColor),
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black,
                            modifier = Modifier.size(42.dp),
                            shadowElevation = 3.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.calendar_astro_icon),
                                contentDescription = "PUNY4 Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isFa) "راهنمای جامع برنامه" else "PUNY4 App Guide",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = HolidayPurple,
                                    border = BorderStroke(1.dp, HolidayPurpleLight),
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "?",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isFa) "بستن" else "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = cardFrameColor)
                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(topics) { topic ->
                        GuideTopicCard(topic = topic, isFa = isFa)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Dismiss Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("guide_dialog_dismiss_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isFa) "متوجه شدم و بازگشت به برنامه" else "Got it, return to Calendar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun GuideTopicCard(
    topic: GuideTopic,
    isFa: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, cardFrameColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = topic.iconTint.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = topic.icon,
                            contentDescription = null,
                            tint = topic.iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = if (isFa) topic.titlePersian else topic.titleEnglish,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = if (isFa) topic.descriptionPersian else topic.descriptionEnglish,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 1.5.em
            )

            val tips = if (isFa) topic.tipsPersian else topic.tipsEnglish
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            ) {
                tips.forEach { tip ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "•",
                            color = topic.iconTint,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 1.5.em
                        )
                    }
                }
            }
        }
    }
}
