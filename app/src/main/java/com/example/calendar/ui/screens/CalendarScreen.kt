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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.CalendarManager
import com.example.calendar.core.IslamicCalendar
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.data.UserSettings
import com.example.calendar.model.AppLanguage
import com.example.calendar.model.CalendarType
import com.example.calendar.ui.components.CalendarGridView
import com.example.calendar.ui.components.DayDetailsView
import com.example.calendar.ui.components.TodaySpotlightOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun CalendarScreen(
    userSettings: UserSettings,
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

    var activeCalendarType by remember(defaultCalType) { mutableStateOf(defaultCalType) }

    val todayG = remember { CalendarManager.getTodayGregorian() }
    val todayJdn = remember { JalaliCalendar.gregorianToJdn(todayG.year, todayG.month, todayG.day) }
    var selectedJdn by remember { mutableStateOf(todayJdn) }

    // Displayed Year & Month in grid
    var currentYear by remember {
        mutableStateOf(
            when (activeCalendarType) {
                CalendarType.SOLAR_HIJRI -> JalaliCalendar.jdnToJalali(todayJdn).year
                CalendarType.GREGORIAN -> todayG.year
                CalendarType.LUNAR_HIJRI -> IslamicCalendar.jdnToIslamic(todayJdn).year
            }
        )
    }
    var currentMonth by remember {
        mutableStateOf(
            when (activeCalendarType) {
                CalendarType.SOLAR_HIJRI -> JalaliCalendar.jdnToJalali(todayJdn).month
                CalendarType.GREGORIAN -> todayG.month
                CalendarType.LUNAR_HIJRI -> IslamicCalendar.jdnToIslamic(todayJdn).month
            }
        )
    }

    // Initial pre-load when entering the calendar screen
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            CalendarManager.buildMonthGrid(activeCalendarType, currentYear, currentMonth, selectedJdn)
            CalendarManager.getFullDayInfo(selectedJdn, currentCity)
        }
        delay(120)
        isLoaded = true
    }

    val selectedDayInfo = remember(selectedJdn, currentCity) {
        CalendarManager.getFullDayInfo(selectedJdn, currentCity)
    }

    val todayDayInfo = remember(todayJdn, currentCity) {
        CalendarManager.getFullDayInfo(todayJdn, currentCity)
    }

    var userNote by remember(selectedJdn) {
        mutableStateOf(userSettings.getNote(selectedJdn))
    }

    // Coordinates tracking for today's cell & launch spotlight
    var rootLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var todayCenterOffset by remember { mutableStateOf<Offset?>(null) }
    var todayCellRadius by remember { mutableFloatStateOf(0f) }
    var indicatorCenterOffset by remember { mutableStateOf<Offset?>(null) }
    var indicatorCellRadius by remember { mutableFloatStateOf(0f) }
    var showIntroSpotlight by remember { mutableStateOf(true) }

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

    if (!isLoaded) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("calendar_initial_loading"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CircularProgressIndicator(
                    color = holidayColor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(38.dp)
                )
                Text(
                    text = if (appLanguage == AppLanguage.PERSIAN) "در حال بارگذاری تقویم..." else "Loading calendar...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned { rootLayoutCoordinates = it }
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
                // Upper part: Calendar Grid
                CalendarGridView(
                    calendarType = activeCalendarType,
                    currentYear = currentYear,
                    currentMonth = currentMonth,
                    selectedJdn = selectedJdn,
                    showSecondaryDates = showSecondaryDates,
                    useEnglishDayNumbers = useEnglishDayNumbers,
                    fontScalePercent = fontScalePercent,
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
                        // Align view year/month to selected date in the new calendar type
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
                    onTodayPositioned = if (showIntroSpotlight) { cellCoords ->
                        val root = rootLayoutCoordinates
                        if (root != null && root.isAttached && cellCoords.isAttached) {
                            val localPos = root.localPositionOf(cellCoords, Offset.Zero)
                            val newOffset = Offset(
                                localPos.x + cellCoords.size.width / 2f,
                                localPos.y + cellCoords.size.height / 2f
                            )
                            if (todayCenterOffset == null || (todayCenterOffset!! - newOffset).getDistance() > 1f) {
                                todayCenterOffset = newOffset
                                todayCellRadius = cellCoords.size.width / 2f
                            }
                        }
                    } else null,
                    onIndicatorPositioned = if (showIntroSpotlight) { cellCoords ->
                        val root = rootLayoutCoordinates
                        if (root != null && root.isAttached && cellCoords.isAttached) {
                            val localPos = root.localPositionOf(cellCoords, Offset.Zero)
                            val newOffset = Offset(
                                localPos.x + cellCoords.size.width / 2f,
                                localPos.y + cellCoords.size.height / 2f
                            )
                            if (indicatorCenterOffset == null || (indicatorCenterOffset!! - newOffset).getDistance() > 1f) {
                                indicatorCenterOffset = newOffset
                                indicatorCellRadius = cellCoords.size.width / 2f
                            }
                        }
                    } else null,
                    onShowTodaySpotlight = {
                        showIntroSpotlight = true
                    },
                    appLanguage = appLanguage,
                    holidayColor = holidayColor
                )

                // Lower part: Detailed info for the selected day (all dates + astronomy)
                DayDetailsView(
                    dayInfo = selectedDayInfo,
                    currentCity = currentCity,
                    userNote = userNote,
                    isFa = (appLanguage == AppLanguage.PERSIAN),
                    holidayColor = holidayColor,
                    onSaveNote = { newNote ->
                        userSettings.saveNote(selectedJdn, newNote)
                        userNote = newNote
                    },
                    onJumpToToday = handleGoToToday,
                    onSelectCity = { city ->
                        userSettings.setCity(city)
                    }
                )
            }

            // Ephemeral launch spotlight overlay (momentary circle + dashed arrow + rich paragraph, then fades out)
            TodaySpotlightOverlay(
                dayInfo = if (selectedJdn == todayJdn) todayDayInfo else selectedDayInfo,
                targetCenter = indicatorCenterOffset ?: todayCenterOffset,
                targetRadius = if (indicatorCellRadius > 0f) indicatorCellRadius else todayCellRadius,
                visible = showIntroSpotlight,
                isFa = (appLanguage == AppLanguage.PERSIAN),
                holidayColor = holidayColor,
                onDismiss = { showIntroSpotlight = false }
            )
        }
    }
}
