package com.example.calendar.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.CalendarManager
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.data.UserSettings
import com.example.calendar.ui.components.MoonPhaseCanvas
import com.example.calendar.ui.components.MoonPhaseProgressionBar
import com.example.ui.theme.AstroGold
import com.example.ui.theme.MoonPaleYellow
import com.example.ui.theme.ScorpioAlert
import com.example.ui.theme.SuccessGreen

@Composable
fun AstronomyScreen(
    userSettings: UserSettings,
    modifier: Modifier = Modifier
) {
    val todayG = remember { CalendarManager.getTodayGregorian() }
    val todayJdn = remember { JalaliCalendar.gregorianToJdn(todayG.year, todayG.month, todayG.day) }
    val currentCity by userSettings.selectedCity.collectAsState()

    val todayInfo = remember(todayJdn, currentCity) {
        CalendarManager.getFullDayInfo(todayJdn, currentCity)
    }

    // Calculate upcoming Qamar Dar Aqrab days in next 30 days
    val upcomingScorpioDays = remember(todayJdn) {
        val list = mutableListOf<Pair<Long, com.example.calendar.model.QamarDarAqrabStatus>>()
        for (i in 0..30) {
            val j = todayJdn + i
            val status = AstronomicalCalculator.checkQamarDarAqrab(j)
            if (status.isInTropicalScorpio || status.isInSiderealScorpio) {
                list.add(Pair(j, status))
            }
        }
        list
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("astronomy_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Moon Today
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "وضعیت ماه در آسمان امروز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    MoonPhaseCanvas(moonInfo = todayInfo.moonInfo, size = 96.dp)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Full moon progression bar (روند گام‌های ماه از هلال تا بدر کامل و محو شدن: 🌒 🌓 🌔 🌕 🌖 🌗 🌘)
                    MoonPhaseProgressionBar(currentPhase = todayInfo.moonInfo.phaseType)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${todayInfo.moonInfo.phaseType.emoji} ${todayInfo.moonInfo.phaseType.titlePersian}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MoonPaleYellow
                    )
                    Text(
                        text = "${todayInfo.moonInfo.phaseType.titleEnglish} • سن ماه: ${String.format(java.util.Locale.US, "%.1f", todayInfo.moonInfo.ageDays)} روز",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("درصد روشنایی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${todayInfo.moonInfo.illuminationPercent}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("زاویه فاز", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${todayInfo.moonInfo.phaseAngleDegrees.toInt()}°", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("برج قمر", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(todayInfo.moonInfo.moonZodiacName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

        // Qamar Dar Aqrab Tracker Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ScorpioAlert.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = ScorpioAlert, modifier = Modifier.size(20.dp))
                            }
                        }
                        Text(
                            text = "رصد و تقویم قمر در عقرب",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val isInNow = todayInfo.qamarDarAqrab.isInTropicalScorpio || todayInfo.qamarDarAqrab.isInSiderealScorpio
                    val badgeColor = if (isInNow) ScorpioAlert else SuccessGreen

                    Text(
                        text = "امروز: ${todayInfo.qamarDarAqrab.statusSummary}",
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "روزهای قمر در عقرب در یک ماه آینده:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (upcomingScorpioDays.isEmpty()) {
                        Text("در ۳۰ روز آینده موردی یافت نشد.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        upcomingScorpioDays.take(6).forEach { (jdn, status) ->
                            val jalali = JalaliCalendar.jdnToJalali(jdn)
                            val dow = JalaliCalendar.WEEKDAY_NAMES_PERSIAN[JalaliCalendar.getDayOfWeekIndex(jdn)]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$dow ${jalali.day} ${JalaliCalendar.MONTH_NAMES_PERSIAN[jalali.month - 1]}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (status.isInTropicalScorpio) "برج عقرب" else "صورت فلکی عقرب",
                                    color = ScorpioAlert,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 12 Zodiac Signs
        item {
            Text(
                text = "بروج دوازده‌گانه دایره‌البروج (Zodiac Signs)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        items(AstronomicalCalculator.ZODIAC_SIGNS) { sign ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = sign.symbol,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sign.namePersian,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = sign.nameEnglish,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "عنصر: ${sign.element.titlePersian} • سیاره حاکم: ${sign.rulingPlanet}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = sign.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
