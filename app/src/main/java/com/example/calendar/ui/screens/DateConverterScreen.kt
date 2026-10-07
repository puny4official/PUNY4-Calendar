package com.example.calendar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.CalendarManager
import com.example.calendar.core.IslamicCalendar
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.model.CalendarType
import com.example.calendar.model.GregorianDate
import com.example.calendar.model.IslamicDate
import com.example.calendar.model.JalaliDate
import com.example.calendar.ui.components.MoonPhaseCompactBadge

@Composable
fun DateConverterScreen(
    modifier: Modifier = Modifier
) {
    val todayG = remember { CalendarManager.getTodayGregorian() }
    val todayJ = remember { JalaliCalendar.gregorianToJalali(todayG.year, todayG.month, todayG.day) }
    val todayI = remember {
        val jdn = JalaliCalendar.gregorianToJdn(todayG.year, todayG.month, todayG.day)
        IslamicCalendar.jdnToIslamic(jdn)
    }

    var sourceCalendarType by remember { mutableStateOf(CalendarType.SOLAR_HIJRI) }

    var inputYear by remember { mutableStateOf("${todayJ.year}") }
    var inputMonth by remember { mutableStateOf("${todayJ.month}") }
    var inputDay by remember { mutableStateOf("${todayJ.day}") }

    val convertedResults = remember(sourceCalendarType, inputYear, inputMonth, inputDay) {
        val y = inputYear.toIntOrNull() ?: todayJ.year
        val m = (inputMonth.toIntOrNull() ?: todayJ.month).coerceIn(1, 12)
        val d = (inputDay.toIntOrNull() ?: todayJ.day).coerceIn(1, 31)

        val jdn: Long = when (sourceCalendarType) {
            CalendarType.SOLAR_HIJRI -> JalaliCalendar.jalaliToJdn(y, m, d)
            CalendarType.GREGORIAN -> JalaliCalendar.gregorianToJdn(y, m, d)
            CalendarType.LUNAR_HIJRI -> IslamicCalendar.islamicToJdn(y, m, d)
        }

        val resJ = JalaliCalendar.jdnToJalali(jdn)
        val resG = JalaliCalendar.jdnToGregorian(jdn)
        val resI = IslamicCalendar.jdnToIslamic(jdn)
        val dow = JalaliCalendar.WEEKDAY_NAMES_PERSIAN[JalaliCalendar.getDayOfWeekIndex(jdn)]
        val moon = AstronomicalCalculator.calculateMoonInfo(jdn)
        val zodiac = AstronomicalCalculator.ZODIAC_SIGNS[(resJ.month - 1).coerceIn(0, 11)]

        object {
            val jdn = jdn
            val jalali = resJ
            val gregorian = resG
            val islamic = resI
            val dayOfWeek = dow
            val moonInfo = moon
            val zodiacSign = zodiac
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("date_converter_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Text(
                            text = "تبدیل تاریخ بین همه تقویم‌ها",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "تقویم مبدأ را انتخاب کنید:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CalendarType.values().forEach { cType ->
                            val isSelected = (cType == sourceCalendarType)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    sourceCalendarType = cType
                                    // Reset inputs to today's date for that calendar
                                    when (cType) {
                                        CalendarType.SOLAR_HIJRI -> {
                                            inputYear = "${todayJ.year}"
                                            inputMonth = "${todayJ.month}"
                                            inputDay = "${todayJ.day}"
                                        }
                                        CalendarType.GREGORIAN -> {
                                            inputYear = "${todayG.year}"
                                            inputMonth = "${todayG.month}"
                                            inputDay = "${todayG.day}"
                                        }
                                        CalendarType.LUNAR_HIJRI -> {
                                            inputYear = "${todayI.year}"
                                            inputMonth = "${todayI.month}"
                                            inputDay = "${todayI.day}"
                                        }
                                    }
                                },
                                label = { Text(cType.titlePersian, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date Inputs: Year, Month, Day
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputYear,
                            onValueChange = { if (it.length <= 4) inputYear = it },
                            label = { Text("سال") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = inputMonth,
                            onValueChange = { if (it.length <= 2) inputMonth = it },
                            label = { Text("ماه") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = inputDay,
                            onValueChange = { if (it.length <= 2) inputDay = it },
                            label = { Text("روز") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Results Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "نتیجه تبدیل در تقویم‌های سه‌گانه:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Day of week
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "روز هفته: ${convertedResults.dayOfWeek}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Solar Hijri Result
                    ConvertedResultRow(
                        title = "هجری شمسی (خورشیدی)",
                        dateText = "${convertedResults.jalali.day} ${JalaliCalendar.MONTH_NAMES_PERSIAN[convertedResults.jalali.month - 1]} ${convertedResults.jalali.year}",
                        isSource = (sourceCalendarType == CalendarType.SOLAR_HIJRI)
                    )

                    // Gregorian Result
                    ConvertedResultRow(
                        title = "میلادی (گریگوری)",
                        dateText = "${convertedResults.gregorian.day} ${CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN[convertedResults.gregorian.month - 1].split(" ")[0]} ${convertedResults.gregorian.year}",
                        isSource = (sourceCalendarType == CalendarType.GREGORIAN)
                    )

                    // Islamic Result
                    ConvertedResultRow(
                        title = "هجری قمری (اسلامی)",
                        dateText = "${convertedResults.islamic.day} ${IslamicCalendar.MONTH_NAMES_ARABIC[convertedResults.islamic.month - 1]} ${convertedResults.islamic.year}",
                        isSource = (sourceCalendarType == CalendarType.LUNAR_HIJRI)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Astro preview for this date
                    Text(
                        text = "اطلاعات نجومی این تاریخ:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    MoonPhaseCompactBadge(moonInfo = convertedResults.moonInfo)

                    Text(
                        text = "برج خورشیدی: ${convertedResults.zodiacSign.namePersian} ${convertedResults.zodiacSign.symbol} (حاکم: ${convertedResults.zodiacSign.rulingPlanet})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ConvertedResultRow(
    title: String,
    dateText: String,
    isSource: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSource) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = dateText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
        if (isSource) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "مبدأ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
