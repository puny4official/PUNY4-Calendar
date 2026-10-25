package com.example.calendar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun CalendarScreen(
    userSettings: UserSettings,
    targetJdn: Long? = null,
    onTargetJdnConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    autoShowSpotlight: Boolean = false,
    onAutoSpotlightShown: () -> Unit = {}
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
    val todayG = rememberTodayGregorian() // Iran time; changes at Iran midnight
    val todayJdn: Long = remember(todayG) {
        JalaliCalendar.gregorianToJdn(todayG.year, todayG.month, todayG.day)
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
    var arrowDismissed by remember { mutableStateOf(false) }
    var spotlightToken by remember { mutableIntStateOf(0) }
    var dayMenuJdn by remember { mutableStateOf<Long?>(null) }
    var addEventJdn by remember { mutableStateOf<Long?>(null) }
    var personalVersion by remember { mutableIntStateOf(0) }
    val spotlightScope = rememberCoroutineScope()
    // [0] = this screen, [1] = today's cell. Plain holder (not state) so scrolling never triggers recomposition.
    val coordsHolder = remember { arrayOfNulls<LayoutCoordinates>(2) }

    // Shows the card; the arrow is anchored once, to where today's cell is at this moment.
    val triggerSpotlight: () -> Unit = {
        spotlightScope.launch {
            delay(150)
            var tries = 0
            while (tries < 10) {
                val cell = coordsHolder[1]
                val screen = coordsHolder[0]
                if (cell != null && cell.isAttached && screen != null && screen.isAttached) break
                delay(50)
                tries++
            }
            val cell = coordsHolder[1]
            val screen = coordsHolder[0]
            if (cell != null && screen != null && cell.isAttached && screen.isAttached) {
                todayCenterOffset = screen.localPositionOf(
                    cell, Offset(cell.size.width / 2f, cell.size.height / 2f)
                )
                todayCellRadius = min(cell.size.width, cell.size.height) / 2f
            } else {
                todayCenterOffset = null
            }
            arrowDismissed = false
            spotlightToken++
            showIntroSpotlight = true
        }
    }

    // Auto-show once, right after the loading screen hands over to the calendar.
    LaunchedEffect(autoShowSpotlight) {
        if (autoShowSpotlight) {
            delay(450)
            triggerSpotlight()
            onAutoSpotlightShown()
        }
    }

    // The card stays pinned at the top while scrolling, but the arrow disappears on the first scroll.
    LaunchedEffect(showIntroSpotlight, spotlightToken) {
        if (showIntroSpotlight) {
            snapshotFlow { listState.isScrollInProgress }.first { it }
            arrowDismissed = true
        }
    }

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

    // When the day changes while the app is open, follow it if the user was looking at "today".
    var lastTodayJdn by remember { mutableLongStateOf(todayJdn) }
    LaunchedEffect(todayJdn) {
        if (todayJdn != lastTodayJdn) {
            val wasOnToday = selectedJdn == lastTodayJdn
            lastTodayJdn = todayJdn
            if (wasOnToday) handleGoToToday()
        }
    }

    CosmicBackground(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordsHolder[0] = it }
            .testTag("calendar_screen_root")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("calendar_screen_scroll"),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "calendar_grid") {
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
                    onDateLongPressed = { jdn ->
                        selectedJdn = jdn
                        userNote = userSettings.getNote(jdn)
                        dayMenuJdn = jdn
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
                    onShowTodaySpotlight = { triggerSpotlight() },
                    onTodayPositioned = { coordsHolder[1] = it },
                    appLanguage = appLanguage,
                    holidayColor = holidayColor
                )

            }

            // Sections 1-2: tabs (day info / astronomy / solar + prayer times) with the selected card
            item(key = "details_section") {
                DetailsSection(
                    dayInfo = selectedDayInfo,
                    currentCity = currentCity,
                    renderFaDigits = (appLanguage == AppLanguage.PERSIAN && !useEnglishDayNumbers),
                    isFa = (appLanguage == AppLanguage.PERSIAN),
                    onJumpToToday = handleGoToToday,
                    onOpenCityPicker = { showCityPicker = true }
                )
            }

            // Section 3: Official Holidays Card (rendered if holiday or Friday)
            val officialHolidays = selectedDayInfo.events.filter { it.isHoliday }
            val isFriday = (selectedDayInfo.dayOfWeekPersian == "جمعه")
            if (officialHolidays.isNotEmpty() || isFriday) {
                item(key = "official_holidays") {
                    OfficialHolidaysCard(
                        dayInfo = selectedDayInfo,
                        holidayColor = holidayColor
                    )
                }
                }

                // Section 4: Global Events Card
                val globalEvents = selectedDayInfo.events.filter { it.type == EventType.INTERNATIONAL }
                if (globalEvents.isNotEmpty()) {
                item(key = "global_events") {
                    GlobalEventsCard(
                        dayInfo = selectedDayInfo
                    )
                }
                }

                // Section 5: National Occasions Card
                val nationalAndAstroEvents = selectedDayInfo.events.filter { !it.isHoliday && it.type != EventType.INTERNATIONAL }
                if (nationalAndAstroEvents.isNotEmpty()) {
                item(key = "national_occasions") {
                    NationalOccasionsCard(
                        dayInfo = selectedDayInfo
                    )
                }
                }

            item(key = "personal_events_$selectedJdn") {
                val faP = (appLanguage == AppLanguage.PERSIAN)
                val pEvents = remember(selectedJdn, personalVersion) { userSettings.getPersonalEvents(selectedJdn) }
                if (pEvents.isNotEmpty()) {
                    androidx.compose.material3.OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            androidx.compose.material3.Text(
                                if (faP) "⭐ مناسبت‌های شخصی" else "⭐ Personal occasions",
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            pEvents.forEachIndexed { idx, t ->
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    androidx.compose.material3.Text(t, modifier = Modifier.weight(1f))
                                    androidx.compose.material3.TextButton(onClick = {
                                        userSettings.removePersonalEvent(selectedJdn, idx)
                                        personalVersion++
                                    }) { androidx.compose.material3.Text(if (faP) "حذف" else "Delete") }
                                }
                            }
                        }
                    }
                }
            }

                // Section 6: Personal Notes Card
            item(key = "notes_$selectedJdn") {
                    NotesCard(
                    userNote = userNote,
                    dayInfo = selectedDayInfo,
                    isFa = (appLanguage == AppLanguage.PERSIAN),
                    onSaveNote = { newNote ->
                        userSettings.saveNote(selectedJdn, newNote)
                        userNote = newNote
                    }
                )
            }

            item(key = "bottom_spacing") { Spacer(modifier = Modifier.height(16.dp)) }
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

        dayMenuJdn?.let { jdn ->
            val faMenu = (appLanguage == AppLanguage.PERSIAN)
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { dayMenuJdn = null },
                confirmButton = {},
                title = { androidx.compose.material3.Text(if (faMenu) "این روز" else "This day") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        androidx.compose.material3.OutlinedButton(
                            onClick = { dayMenuJdn = null; triggerSpotlight() },
                            modifier = Modifier.fillMaxWidth()
                        ) { androidx.compose.material3.Text(if (faMenu) "نمایش مناسبت‌های این روز" else "Show this day's occasions") }
                        androidx.compose.material3.OutlinedButton(
                            onClick = { dayMenuJdn = null; addEventJdn = jdn },
                            modifier = Modifier.fillMaxWidth()
                        ) { androidx.compose.material3.Text(if (faMenu) "افزودن مناسبت شخصی" else "Add personal occasion") }
                    }
                }
            )
        }

        addEventJdn?.let { jdn ->
            val faAdd = (appLanguage == AppLanguage.PERSIAN)
            var title by remember(jdn) { mutableStateOf("") }
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { addEventJdn = null },
                title = { androidx.compose.material3.Text(if (faAdd) "مناسبت شخصی" else "Personal occasion") },
                text = {
                    androidx.compose.material3.OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        placeholder = { androidx.compose.material3.Text(if (faAdd) "مثلاً تولد دوست" else "e.g. Friend's birthday") }
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        userSettings.addPersonalEvent(jdn, title)
                        personalVersion++
                        addEventJdn = null
                        triggerSpotlight()
                    }) { androidx.compose.material3.Text(if (faAdd) "ذخیره" else "Save") }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { addEventJdn = null }) {
                        androidx.compose.material3.Text(if (faAdd) "انصراف" else "Cancel")
                    }
                }
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
                showDashedLines = !arrowDismissed,
                personalEvents = remember(selectedJdn, personalVersion) { userSettings.getPersonalEvents(if (selectedJdn == todayJdn) todayJdn else selectedJdn) },
                onDismiss = { showIntroSpotlight = false }
            )
        }
    }
}
