package com.example.calendar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.calendar.core.CalendarManager
import com.example.calendar.core.IslamicCalendar
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.data.UserSettings
import com.example.calendar.model.AppLanguage
import com.example.calendar.model.CalendarType
import com.example.calendar.model.EventType
import com.example.calendar.ui.components.*

@Composable
fun CalendarScreen(
    userSettings: UserSettings,
    targetJdn: Long? = null,
    onTargetJdnConsumed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val defaultCalType by userSettings.calendarType.collectAsState()
    val currentCity by userSettings.selectedCity.collectAsState()
    val showSecondaryDates by userSettings.showSecondaryDates.collectAsState()
    val useEnglishDayNumbers by userSettings.useEnglishDayNumbers.collectAsState()
    val appLanguage by userSettings.appLanguage.collectAsState()
    val holidayColorLong by userSettings.holidayColorLong.collectAsState()
    val holidayColor = remember(holidayColorLong) { Color(holidayColorLong) }
    val fontScalePercent by userSettings.fontScalePercent.collectAsState()
    val dayNumberScalePercent by userSettings.dayNumberScalePercent.collectAsState()

    var activeCalendarType by remember(defaultCalType) { mutableStateOf(defaultCalType) }
    val todayJdn: Long = remember {
        val now = java.time.LocalDate.now()
        JalaliCalendar.gregorianToJdn(now.year, now.monthValue, now.dayOfMonth)
    }

    val initialYear: Int
    val initialMonth: Int
    when (defaultCalType) {
        CalendarType.SOLAR_HIJRI -> {
            val j = JalaliCalendar.jdnToJalali(todayJdn)
            initialYear = j.year
            initialMonth = j.month
        }
        CalendarType.GREGORIAN -> {
            val g = JalaliCalendar.jdnToGregorian(todayJdn)
            initialYear = g.year
            initialMonth = g.month
        }
        CalendarType.LUNAR_HIJRI -> {
            val i = IslamicCalendar.jdnToIslamic(todayJdn)
            initialYear = i.year
            initialMonth = i.month
        }
    }

    var currentYear by remember(defaultCalType) { mutableIntStateOf(initialYear) }
    var currentMonth by remember(defaultCalType) { mutableIntStateOf(initialMonth) }
    var selectedJdn by remember(defaultCalType) { mutableLongStateOf(todayJdn) }

    var userNote by remember(selectedJdn) {
        mutableStateOf(userSettings.getNote(selectedJdn))
    }

    var showCityPicker by remember { mutableStateOf(false) }

    LaunchedEffect(targetJdn) {
        targetJdn?.let { target ->
            selectedJdn = target
            userNote = userSettings.getNote(target)
            when (activeCalendarType) {
                CalendarType.SOLAR_HIJRI -> {
                    val j = JalaliCalendar.jdnToJalali(target)
                    currentYear = j.year
                    currentMonth = j.month
                }
                CalendarType.GREGORIAN -> {
                    val g = JalaliCalendar.jdnToGregorian(target)
                    currentYear = g.year
                    currentMonth = g.month
                }
                CalendarType.LUNAR_HIJRI -> {
                    val i = IslamicCalendar.jdnToIslamic(target)
                    currentYear = i.year
                    currentMonth = i.month
                }
            }
            onTargetJdnConsumed()
        }
    }

    val selectedDayInfo = remember(selectedJdn, currentCity) {
        CalendarManager.getFullDayInfo(selectedJdn, currentCity)
    }

    val todayDayInfo = remember(todayJdn, currentCity) {
        CalendarManager.getFullDayInfo(todayJdn, currentCity)
    }

    // Coordinates tracking for today's cell & spotlight
    var todayCenterOffset by remember { mutableStateOf<Offset?>(null) }
    var todayCellRadius by remember { mutableFloatStateOf(0f) }
    var indicatorCenterOffset by remember { mutableStateOf<Offset?>(null) }
    var indicatorCellRadius by remember { mutableFloatStateOf(0f) }
    var showIntroSpotlight by remember { mutableStateOf(false) }

    val handleGoToToday = {
        selectedJdn = todayJdn
        userNote = userSettings.getNote(todayJdn)
        when (activeCalendarType) {
            CalendarType.SOLAR_HIJRI -> {
                val j = JalaliCalendar.jdnToJalali(todayJdn)
                currentYear = j.year
                currentMonth = j.month
            }
            CalendarType.GREGORIAN -> {
                val g = JalaliCalendar.jdnToGregorian(todayJdn)
                currentYear = g.year
                currentMonth = g.month
            }
            CalendarType.LUNAR_HIJRI -> {
                val i = IslamicCalendar.jdnToIslamic(todayJdn)
                currentYear = i.year
                currentMonth = i.month
            }
        }
    }

    CosmicBackground(
    modifier = modifier
        .fillMaxSize()
        .testTag("calendar_screen_root")
) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .testTag("calendar_screen_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CalendarGridView(
                calendarType = activeCalendarType,
                currentYear = currentYear,
                currentMonth = currentMonth,
                selectedJdn = selectedJdn,
                showSecondaryDates = showSecondaryDates,
                useEnglishDayNumbers = useEnglishDayNumbers,
                fontScalePercent = fontScalePercent,
                dayNumberScalePercent = dayNumberScalePercent,
                onDateSelected = { jdn ->
                    selectedJdn = jdn
                    userNote = userSettings.getNote(jdn)
                },
                onPreviousMonth = {
                    if (currentMonth == 1) {
                        currentMonth = 12
                        currentYear--
                    } else {
                        currentMonth--
                    }
                },
                onNextMonth = {
                    if (currentMonth == 12) {
                        currentMonth = 1
                        currentYear++
                    } else {
                        currentMonth++
                    }
                },
                onTodayClicked = handleGoToToday,
                onSelectYearMonth = { y, m ->
                    currentYear = y
                    currentMonth = m
                },
                onCalendarTypeChanged = { newType ->
                    activeCalendarType = newType
                    when (newType) {
                        CalendarType.SOLAR_HIJRI -> {
                            val j = JalaliCalendar.jdnToJalali(selectedJdn)
                            currentYear = j.year
                            currentMonth = j.month
                        }
                        CalendarType.GREGORIAN -> {
                            val g = JalaliCalendar.jdnToGregorian(selectedJdn)
                            currentYear = g.year
                            currentMonth = g.month
                        }
                        CalendarType.LUNAR_HIJRI -> {
                            val i = IslamicCalendar.jdnToIslamic(selectedJdn)
                            currentYear = i.year
                            currentMonth = i.month
                        }
                    }
                },
                onShowTodaySpotlight = {
                    showIntroSpotlight = true
                },
                appLanguage = appLanguage,
                holidayColor = holidayColor
            )

            // Section 1: All Calendars Card
            AllCalendarsCard(
                dayInfo = selectedDayInfo,
                renderFaDigits = (appLanguage == AppLanguage.PERSIAN && !useEnglishDayNumbers),
                onJumpToToday = handleGoToToday
            )

            // Section 2: Astronomy & Prayer Times Card
            AstronomyCard(
                dayInfo = selectedDayInfo,
                currentCity = currentCity,
                renderFaDigits = (appLanguage == AppLanguage.PERSIAN && !useEnglishDayNumbers),
                isFa = (appLanguage == AppLanguage.PERSIAN),
                onOpenCityPicker = { showCityPicker = true }
            )

            // Section 3: Official Holidays Card (rendered if holiday or Friday)
            val officialHolidays = selectedDayInfo.events.filter { it.isHoliday }
            val isFriday = (selectedDayInfo.dayOfWeekPersian == "جمعه")
            if (officialHolidays.isNotEmpty() || isFriday) {
                OfficialHolidaysCard(
                    dayInfo = selectedDayInfo,
                    holidayColor = holidayColor
                )
            }

            // Section 4: Global Events Card
            val globalEvents = selectedDayInfo.events.filter { it.type == EventType.INTERNATIONAL }
            if (globalEvents.isNotEmpty()) {
                GlobalEventsCard(
                    dayInfo = selectedDayInfo
                )
            }

            // Section 5: National Occasions Card
            val nationalAndAstroEvents = selectedDayInfo.events.filter { !it.isHoliday && it.type != EventType.INTERNATIONAL }
            if (nationalAndAstroEvents.isNotEmpty()) {
                NationalOccasionsCard(
                    dayInfo = selectedDayInfo
                )
            }

            // Section 6: Personal Notes Card
            NotesCard(
                userNote = userNote,
                dayInfo = selectedDayInfo,
                isFa = (appLanguage == AppLanguage.PERSIAN),
                onSaveNote = { newNote ->
                    userSettings.saveNote(selectedJdn, newNote)
                    userNote = newNote
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Dedicated City Picker Dialog
        if (showCityPicker) {
            CityPickerDialog(
                visible = showCityPicker,
                currentCity = currentCity,
                isFa = (appLanguage == AppLanguage.PERSIAN),
                onSelectCity = { city ->
                    userSettings.setCity(city)
                    showCityPicker = false
                },
                onDismiss = { showCityPicker = false }
            )
        }

        if (showIntroSpotlight) {
            TodaySpotlightOverlay(
                dayInfo = if (selectedJdn == todayJdn) todayDayInfo else selectedDayInfo,
                targetCenter = indicatorCenterOffset ?: todayCenterOffset,
                targetRadius = if (indicatorCellRadius > 0f) indicatorCellRadius else todayCellRadius,
                visible = showIntroSpotlight,
                isFa = (appLanguage == AppLanguage.PERSIAN),
                holidayColor = holidayColor,
                showDashedLines = true,
                onDismiss = { showIntroSpotlight = false }
            )
        }
    }
}
