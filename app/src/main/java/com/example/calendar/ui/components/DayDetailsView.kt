package com.example.calendar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.DigitFormatter
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.model.CityLocation
import com.example.calendar.model.EventType
import com.example.calendar.model.FullDayInfo
import com.example.ui.theme.*

@Composable
fun DayDetailsView(
    dayInfo: FullDayInfo,
    currentCity: CityLocation,
    userNote: String,
    onSaveNote: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFa: Boolean = true,
    onJumpToToday: (() -> Unit)? = null
) {
    var isEditingNote by remember(dayInfo.jalaliDate) { mutableStateOf(false) }
    var noteText by remember(dayInfo.jalaliDate, userNote) { mutableStateOf(userNote) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("day_details_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ----------------------------------------------------
        // SECTION 1: ALL CALENDARS DATES CARD (همه تاریخ‌ها)
        // ----------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("all_calendars_card"),
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
                // Header: Day of week & Tag
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
                            shape = CircleShape,
                            color = HolidayPurpleContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = HolidayPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = dayInfo.dayOfWeekPersian,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${dayInfo.dayOfWeekArabic} • ${dayInfo.dayOfWeekEnglish}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (dayInfo.isToday) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SuccessGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "امروز",
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    } else if (onJumpToToday != null) {
                        Surface(
                            onClick = onJumpToToday,
                            shape = RoundedCornerShape(10.dp),
                            color = HolidayPurpleContainer,
                            border = BorderStroke(1.dp, HolidayPurple.copy(alpha = 0.35f)),
                            modifier = Modifier.testTag("jump_to_today_badge_details")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "امروز",
                                    color = OnHolidayPurpleContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "بازگشت به تاریخ امروز",
                                    tint = HolidayPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // The 3 Calendars Rows
                CalendarDateRow(
                    icon = Icons.Default.WbSunny,
                    iconTint = AstroGold,
                    calendarName = "هجری شمسی",
                    dateString = "${DigitFormatter.toSystemDigits(dayInfo.jalaliDate.day, isFa)} ${JalaliCalendar.MONTH_NAMES_PERSIAN[dayInfo.jalaliDate.month - 1]} ${DigitFormatter.toSystemDigits(dayInfo.jalaliDate.year, isFa)}",
                    subtitle = "فصل ${dayInfo.seasonPersian} ${dayInfo.seasonEmoji}"
                )

                Spacer(modifier = Modifier.height(10.dp))

                CalendarDateRow(
                    icon = Icons.Default.Public,
                    iconTint = PrimaryLight,
                    calendarName = "میلادی",
                    dateString = "${DigitFormatter.toSystemDigits(dayInfo.gregorianDate.day, isFa)} ${dayInfo.gregorianDate.month} ${DigitFormatter.toSystemDigits(dayInfo.gregorianDate.year, isFa)}",
                    subtitle = "${dayInfo.gregorianDate} (${dayInfo.dayOfWeekEnglish})"
                )

                Spacer(modifier = Modifier.height(10.dp))

                CalendarDateRow(
                    icon = Icons.Default.Nightlight,
                    iconTint = TertiaryLight,
                    calendarName = "هجری قمری",
                    dateString = "${DigitFormatter.toSystemDigits(dayInfo.islamicDate.day, isFa)} ${com.example.calendar.core.IslamicCalendar.MONTH_NAMES_ARABIC[dayInfo.islamicDate.month - 1]} ${DigitFormatter.toSystemDigits(dayInfo.islamicDate.year, isFa)}",
                    subtitle = "یوم ${dayInfo.dayOfWeekArabic}"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Time Statistics Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatisticPill(
                        label = "روز سال",
                        value = "${DigitFormatter.toSystemDigits(dayInfo.dayOfYearJalali, isFa)} از ${DigitFormatter.toSystemDigits(365, isFa)}",
                        modifier = Modifier.weight(1f)
                    )
                    StatisticPill(
                        label = "مانده تا عید",
                        value = "${DigitFormatter.toSystemDigits(dayInfo.daysRemainingJalali, isFa)} روز",
                        modifier = Modifier.weight(1f)
                    )
                    StatisticPill(
                        label = "شماره هفته",
                        value = "هفته ${DigitFormatter.toSystemDigits(dayInfo.weekOfYearJalali, isFa)}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ----------------------------------------------------
        // SECTION 2: ASTRONOMICAL INFORMATION (اطلاعات نجومی روز)
        // ----------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("astronomy_info_card"),
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
                // Section Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AstroGold.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AstroGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "اطلاعات نجومی این روز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Moon Phase Tile
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MoonPhaseCanvas(moonInfo = dayInfo.moonInfo, size = 48.dp)
                        Column {
                            Text(
                                text = "فاز ماه: ${dayInfo.moonInfo.phaseType.titlePersian}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${dayInfo.moonInfo.phaseType.titleEnglish} • سن: ${DigitFormatter.toSystemDigits(dayInfo.moonInfo.ageDays, isFa)} روز",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Default),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "موقعیت در آسمان: برج ${dayInfo.moonInfo.moonZodiacName} (${DigitFormatter.toSystemDigits(dayInfo.moonInfo.moonZodiacDegree, isFa)}°)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontFamily = FontFamily.Default),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${DigitFormatter.toSystemDigits(dayInfo.moonInfo.illuminationPercent, isFa)}%",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sun Zodiac & Planetary Ruler Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Zodiac
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "برج فلکی خورشید",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dayInfo.sunZodiac.symbol,
                                    fontSize = 18.sp,
                                    color = AstroGold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dayInfo.sunZodiac.namePersian,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "عنصر: ${dayInfo.sunZodiac.element.titlePersian} • حاکم: ${dayInfo.sunZodiac.rulingPlanet}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Planetary Ruler
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "کوکب حاکم بر امروز",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dayInfo.planetaryRuler.planetNamePersian,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = dayInfo.planetaryRuler.nature,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Year Animal & Month Element Row
                val currentYearAnimal = dayInfo.yearAnimal ?: AstronomicalCalculator.getYearAnimal(dayInfo.jalaliDate.year)
                val currentMonthElement = dayInfo.monthElement ?: AstronomicalCalculator.getMonthElement(dayInfo.jalaliDate.month)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Year Animal
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = HolidayPurpleContainer.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, HolidayPurple.copy(alpha = 0.22f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "حیوان نماد سال ${dayInfo.jalaliDate.year}",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnHolidayPurpleContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = currentYearAnimal.emoji, fontSize = 20.sp)
                                Text(
                                    text = "سال ${currentYearAnimal.namePersian}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnHolidayPurpleContainer
                                )
                            }
                            Text(
                                text = currentYearAnimal.characteristics,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                color = OnHolidayPurpleContainer.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Month Element
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "عنصر ماه ${JalaliCalendar.MONTH_NAMES_PERSIAN[dayInfo.jalaliDate.month - 1]}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = currentMonthElement.emoji, fontSize = 20.sp)
                                Text(
                                    text = currentMonthElement.titlePersian,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = currentMonthElement.nature,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Qamar Dar Aqrab (قمر در عقرب) Status Banner
                val isInScorpio = dayInfo.qamarDarAqrab.isInTropicalScorpio || dayInfo.qamarDarAqrab.isInSiderealScorpio
                val bannerColor = if (isInScorpio) ScorpioAlert.copy(alpha = 0.12f) else SuccessGreen.copy(alpha = 0.12f)
                val bannerTextColor = if (isInScorpio) ScorpioAlert else SuccessGreen

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(bannerColor)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isInScorpio) {
                            ScorpioIcon(
                                modifier = Modifier.size(20.dp),
                                tint = bannerTextColor
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = bannerTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = dayInfo.qamarDarAqrab.statusSummary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = bannerTextColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayInfo.qamarDarAqrab.detailedAdvice,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ----------------------------------------------------
                // کل اوقات خورشیدی (بدون اوقات اذان)
                // ----------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "کل اوقات خورشیدی (${currentCity.namePersian})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SolarTimeItem("طلوع آفتاب", DigitFormatter.toSystemDigits(dayInfo.solarTimes.sunrise, isFa), Icons.Default.WbSunny)
                    SolarTimeItem("ظهر خورشیدی", DigitFormatter.toSystemDigits(dayInfo.solarTimes.noon, isFa), Icons.Default.LightMode)
                    SolarTimeItem("غروب آفتاب", DigitFormatter.toSystemDigits(dayInfo.solarTimes.sunset, isFa), Icons.Default.Bedtime)
                    SolarTimeItem("نیمه‌شب خورشیدی", DigitFormatter.toSystemDigits(dayInfo.solarTimes.midnight, isFa), Icons.Default.NightsStay)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "طول روز: ${DigitFormatter.toSystemDigits(dayInfo.solarTimes.dayLengthFormatted, isFa)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Default),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "طول شب: ${DigitFormatter.toSystemDigits(dayInfo.solarTimes.nightLengthFormatted, isFa)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Default),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // ----------------------------------------------------
        // SECTION 3: تعطیلات رسمی ایران (با رنگ بنفش و متن بولد شده)
        // ----------------------------------------------------
        val officialHolidays = dayInfo.events.filter { it.isHoliday }
        val isFriday = (dayInfo.dayOfWeekPersian == "جمعه")
        val hasOfficialHoliday = officialHolidays.isNotEmpty() || isFriday

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("iran_official_holidays_card"),
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
                        color = HolidayPurpleContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = null,
                                tint = HolidayPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "تعطیلات رسمی ایران",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HolidayPurple
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                if (hasOfficialHoliday) {
                    if (isFriday) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(HolidayPurple)
                                )
                                Text(
                                    text = "جمعه (تعطیل رسمی پایان هفته)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = HolidayPurple
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HolidayPurpleContainer
                            ) {
                                Text(
                                    text = "تعطیل رسمی",
                                    color = OnHolidayPurpleContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    officialHolidays.forEach { holidayEvent ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(HolidayPurple)
                                )
                                Text(
                                    text = holidayEvent.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = HolidayPurple
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HolidayPurpleContainer
                            ) {
                                Text(
                                    text = "تعطیل رسمی کشور",
                                    color = OnHolidayPurpleContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "امروز در تقویم رسمی کشور تعطیل نیست.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ----------------------------------------------------
        // SECTION 4: مناسبت‌های جهانی و بین‌المللی (متن بولد شده با رنگ طبیعی)
        // ----------------------------------------------------
        val globalEvents = dayInfo.events.filter { it.type == EventType.INTERNATIONAL }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("global_events_card"),
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
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "مناسبت‌های جهانی و بین‌المللی",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                if (globalEvents.isNotEmpty()) {
                    globalEvents.forEach { gEvent ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = gEvent.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "مناسبت جهانی",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "مناسبت جهانی خاصی برای این تاریخ ثبت نشده است.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ----------------------------------------------------
        // SECTION 5: سایر مناسبت‌های ملی و مذهبی (متن بولد شده با رنگ طبیعی)
        // ----------------------------------------------------
        val nationalAndAstroEvents = dayInfo.events.filter { !it.isHoliday && it.type != EventType.INTERNATIONAL }

        if (nationalAndAstroEvents.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("national_occasions_card"),
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
                            color = AstroGold.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = AstroGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "مناسبت‌های ملی و تقویمی ایران",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )

                    nationalAndAstroEvents.forEach { nEvent ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (nEvent.type == EventType.ASTRONOMY) AstroGold else MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = nEvent.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (nEvent.type == EventType.ASTRONOMY) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AstroGold.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = "رویداد نجومی",
                                        color = AstroGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ----------------------------------------------------
        // SECTION 4: USER PERSONAL NOTE CARD (یادداشت این روز)
        // ----------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("notes_card"),
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "یادداشت و رویداد شخصی",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (!isEditingNote) {
                        TextButton(onClick = { isEditingNote = true }) {
                            Text(if (userNote.isBlank()) "افزودن" else "ویرایش")
                        }
                    }
                }

                if (isEditingNote) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("یادداشت یا برنامه خود را برای این تاریخ بنویسید...") },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            noteText = userNote
                            isEditingNote = false
                        }) {
                            Text("انصراف")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            onSaveNote(noteText)
                            isEditingNote = false
                        }) {
                            Text("ذخیره یادداشت")
                        }
                    }
                } else if (userNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = userNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDateRow(
    icon: ImageVector,
    iconTint: Color,
    calendarName: String,
    dateString: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = calendarName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Default),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatisticPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SolarTimeItem(
    title: String,
    time: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = time,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
