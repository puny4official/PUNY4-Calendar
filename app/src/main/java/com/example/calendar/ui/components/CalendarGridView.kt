package com.example.calendar.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.CalendarManager
import com.example.calendar.core.IslamicCalendar
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.core.MonthlyPredictionHelper
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.calendar.model.AppLanguage
import com.example.calendar.model.CalendarType
import com.example.calendar.model.JalaliDate
import com.example.ui.theme.AstroGold
import com.example.ui.theme.HolidayPurple
import com.example.ui.theme.HolidayPurpleContainer
import com.example.ui.theme.HolidayPurpleLight
import com.example.ui.theme.OnHolidayPurpleContainer
import com.example.ui.theme.ScorpioAlert

@Composable
fun CalendarGridView(
    calendarType: CalendarType,
    currentYear: Int,
    currentMonth: Int,
    selectedJdn: Long,
    showSecondaryDates: Boolean,
    onDateSelected: (Long) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClicked: () -> Unit,
    onSelectYearMonth: (Int, Int) -> Unit,
    onCalendarTypeChanged: (CalendarType) -> Unit,
    modifier: Modifier = Modifier,
    appLanguage: AppLanguage = AppLanguage.PERSIAN,
    onTodayPositioned: ((LayoutCoordinates) -> Unit)? = null,
    onIndicatorPositioned: ((LayoutCoordinates) -> Unit)? = null,
    onShowTodaySpotlight: (() -> Unit)? = null
) {
    val isFa = (appLanguage == AppLanguage.PERSIAN)
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showMonthlyPredictionsDialog by remember { mutableStateOf(false) }

    val monthlyPrediction = remember(calendarType, currentYear, currentMonth) {
        MonthlyPredictionHelper.generatePrediction(calendarType, currentYear, currentMonth)
    }

    val cells = remember(calendarType, currentYear, currentMonth, selectedJdn) {
        CalendarManager.buildMonthGrid(calendarType, currentYear, currentMonth, selectedJdn)
    }

    val monthTitle = remember(calendarType, currentYear, currentMonth, appLanguage) {
        when (calendarType) {
            CalendarType.SOLAR_HIJRI -> "${JalaliCalendar.MONTH_NAMES_PERSIAN[currentMonth - 1]} $currentYear"
            CalendarType.GREGORIAN -> {
                if (isFa) {
                    "${CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN[currentMonth - 1]} $currentYear"
                } else {
                    "${java.time.Month.of(currentMonth).name.lowercase().replaceFirstChar { it.uppercase() }} $currentYear"
                }
            }
            CalendarType.LUNAR_HIJRI -> "${IslamicCalendar.MONTH_NAMES_ARABIC[currentMonth - 1]} $currentYear"
        }
    }

    // Solar year animal & month element calculations
    val currentSolarDate = remember(calendarType, currentYear, currentMonth, selectedJdn) {
        if (calendarType == CalendarType.SOLAR_HIJRI) {
            JalaliDate(currentYear, currentMonth, 1)
        } else {
            JalaliCalendar.jdnToJalali(selectedJdn)
        }
    }
    val yearAnimal = remember(currentSolarDate.year) {
        AstronomicalCalculator.getYearAnimal(currentSolarDate.year)
    }
    val monthElement = remember(currentSolarDate.month) {
        AstronomicalCalculator.getMonthElement(currentSolarDate.month)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calendar_grid_card"),
        shape = RoundedCornerShape(24.dp),
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
            // Calendar Type Switcher Chips & Monthly Predictions (Compact Single-Row Bar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calendar Types (Solar, Gregorian, Lunar)
                CalendarType.values().forEach { type ->
                    val isSelected = (type == calendarType)
                    val label = when (type) {
                        CalendarType.SOLAR_HIJRI -> if (isFa) "شمسی" else "Solar"
                        CalendarType.GREGORIAN -> if (isFa) "میلادی" else "Gregorian"
                        CalendarType.LUNAR_HIJRI -> if (isFa) "قمری" else "Lunar"
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) HolidayPurple else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) HolidayPurple else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                            .clickable { onCalendarTypeChanged(type) }
                            .testTag("calendar_chip_${type.name.lowercase()}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Monthly Predictions compact chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (showMonthlyPredictionsDialog) HolidayPurple else HolidayPurpleContainer.copy(alpha = 0.5f),
                    border = BorderStroke(
                        1.dp,
                        if (showMonthlyPredictionsDialog) HolidayPurple else HolidayPurple.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .weight(1.15f)
                        .height(30.dp)
                        .clickable { showMonthlyPredictionsDialog = true }
                        .testTag("monthly_predictions_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(text = "🔮", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isFa) "پیشگویی" else "Forecast",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showMonthlyPredictionsDialog) Color.White else OnHolidayPurpleContainer,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Today Action: Spotlight & Today Button
                FilledTonalIconButton(
                    onClick = {
                        onTodayClicked()
                        onShowTodaySpotlight?.invoke()
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color(0xFF1D4ED8).copy(alpha = 0.12f),
                        contentColor = Color(0xFF1D4ED8)
                    ),
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("today_spotlight_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = if (isFa) "اطلاعات و اوقات امروز" else "Today's Times & Spotlight",
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Month navigation header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // In Persian RTL: Next button (Arrow right in LTR or forward in RTL)
                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier.testTag("next_month_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "ماه بعد",
                        tint = HolidayPurple
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDatePickerDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "انتخاب ماه و سال",
                        tint = HolidayPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onPreviousMonth,
                    modifier = Modifier.testTag("prev_month_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ماه قبل",
                        tint = HolidayPurple
                    )
                }
            }

            // Year Animal & Month Element Header Banner (نماد سال و عنصر ماه جاری)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HolidayPurpleContainer.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, HolidayPurple.copy(alpha = 0.22f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isFa) "سال ${yearAnimal.namePersian} ${yearAnimal.emoji}" else "Year: ${yearAnimal.nameAlternative.ifBlank { yearAnimal.namePersian }} ${yearAnimal.emoji}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnHolidayPurpleContainer
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = OnHolidayPurpleContainer.copy(alpha = 0.5f)
                        )
                        Text(
                            text = if (isFa) "عنصر ماه: ${monthElement.titlePersian} ${monthElement.emoji}" else "Element: ${monthElement.name.lowercase().replaceFirstChar { it.uppercase() }} ${monthElement.emoji}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnHolidayPurpleContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Weekday Headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val weekdays = if (isFa) JalaliCalendar.WEEKDAY_SHORT_PERSIAN else listOf("Sa", "Su", "Mo", "Tu", "We", "Th", "Fr")
                weekdays.forEachIndexed { index, name ->
                    val isFriday = (index == 6)
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFriday) HolidayPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Grid rows
            val rows = cells.chunked(7)
            rows.forEach { rowCells ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rowCells.forEach { cell ->
                        DayCellView(
                            cell = cell,
                            showSecondaryDates = showSecondaryDates,
                            onDateSelected = onDateSelected,
                            onTodayPositioned = onTodayPositioned,
                            onIndicatorPositioned = onIndicatorPositioned,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Calendar symbols legend (فقط نماد قمر در عقرب)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scorpio legend indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScorpioIcon(
                        modifier = Modifier.size(13.dp),
                        tint = ScorpioAlert
                    )
                    Text(
                        text = "قمر در عقرب",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    if (showDatePickerDialog) {
        YearMonthPickerDialog(
            calendarType = calendarType,
            currentYear = currentYear,
            currentMonth = currentMonth,
            onDismiss = { showDatePickerDialog = false },
            onConfirm = { year, month ->
                onSelectYearMonth(year, month)
                showDatePickerDialog = false
            }
        )
    }

    if (showMonthlyPredictionsDialog) {
        MonthlyPredictionsDialog(
            prediction = monthlyPrediction,
            onDismiss = { showMonthlyPredictionsDialog = false }
        )
    }
}

@Composable
private fun DayCellView(
    cell: CalendarManager.CalendarGridCell,
    showSecondaryDates: Boolean,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onTodayPositioned: ((LayoutCoordinates) -> Unit)? = null,
    onIndicatorPositioned: ((LayoutCoordinates) -> Unit)? = null
) {
    if (!cell.isCurrentMonth) {
        // Only days of the current month remain visible; previous and next month days are not shown
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .padding(2.5.dp)
        )
        return
    }

    val isSelected = cell.isSelected
    val isToday = cell.isToday

    val targetBackground = if (isSelected) {
        HolidayPurple
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
    }
    val animatedBackground by animateColorAsState(
        targetValue = targetBackground,
        animationSpec = tween(durationMillis = 200),
        label = "cellBg"
    )

    val targetBorderColor = when {
        isSelected -> HolidayPurpleLight
        isToday -> HolidayPurple.copy(alpha = 0.85f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
    }
    val animatedBorderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(durationMillis = 200),
        label = "cellBorder"
    )
    val borderWidth = if (isSelected || isToday) 1.5.dp else 1.dp

    val textColor = when {
        isSelected -> Color.White
        cell.hasHoliday -> HolidayPurple
        else -> MaterialTheme.colorScheme.onSurface
    }

    val secondaryTextColor = if (isSelected) {
        Color.White.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.5.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isSelected && onIndicatorPositioned != null) {
                    Modifier.onGloballyPositioned { coords ->
                        onIndicatorPositioned(coords)
                        if (isToday && onTodayPositioned != null) {
                            onTodayPositioned(coords)
                        }
                    }
                } else if (isToday && onTodayPositioned != null) {
                    Modifier.onGloballyPositioned { coords ->
                        onTodayPositioned(coords)
                    }
                } else Modifier
            )
            .border(borderWidth, animatedBorderColor, RoundedCornerShape(12.dp))
            .background(animatedBackground)
            .clickable { onDateSelected(cell.jdn) }
            .testTag("day_cell_${cell.primaryNumber}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main day number
            Text(
                text = "${cell.primaryNumber}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                fontSize = 15.sp
            )

            // Secondary calendar dates
            if (showSecondaryDates) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cell.secondaryText1,
                        fontSize = 9.sp,
                        color = secondaryTextColor
                    )
                    Text(
                        text = "•",
                        fontSize = 7.sp,
                        color = secondaryTextColor.copy(alpha = 0.5f)
                    )
                    Text(
                        text = cell.secondaryText2,
                        fontSize = 9.sp,
                        color = secondaryTextColor
                    )
                }
            }
        }

        // Qamar Dar Aqrab symbol (نماد قمر در عقرب وکتوری بدون ایموجی و بدون دایره فیروزه‌ای)
        if (cell.isQamarDarAqrab) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 3.dp, end = 3.dp)
            ) {
                ScorpioIcon(
                    modifier = Modifier.size(9.5.dp),
                    tint = if (isSelected) Color.White else ScorpioAlert
                )
            }
        }

        // Today dot indicator if today is not currently selected
        if (isToday && !isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 3.dp)
                    .size(4.dp)
                    .background(HolidayPurple, CircleShape)
            )
        }
    }
}

@Composable
fun YearMonthPickerDialog(
    calendarType: CalendarType,
    currentYear: Int,
    currentMonth: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }

    val monthNames = remember(calendarType) {
        when (calendarType) {
            CalendarType.SOLAR_HIJRI -> JalaliCalendar.MONTH_NAMES_PERSIAN
            CalendarType.GREGORIAN -> CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN
            CalendarType.LUNAR_HIJRI -> IslamicCalendar.MONTH_NAMES_ARABIC
        }
    }

    val dialogYearAnimal = remember(calendarType, selectedYear) {
        if (calendarType == CalendarType.SOLAR_HIJRI) {
            AstronomicalCalculator.getYearAnimal(selectedYear)
        } else null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "انتخاب ماه و سال (${calendarType.titlePersian})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Year selector with animal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "سال قبل")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$selectedYear",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = HolidayPurple
                        )
                        if (dialogYearAnimal != null) {
                            Text(
                                text = "سال ${dialogYearAnimal.namePersian} ${dialogYearAnimal.emoji}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { selectedYear++ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "سال بعد")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Months 3x4 grid with element
                val chunkedMonths = monthNames.chunked(3)
                chunkedMonths.forEachIndexed { rowIndex, rowList ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowList.forEachIndexed { colIndex, mName ->
                            val mNum = rowIndex * 3 + colIndex + 1
                            val isChosen = (mNum == selectedMonth)
                            val element = if (calendarType == CalendarType.SOLAR_HIJRI) {
                                AstronomicalCalculator.getMonthElement(mNum)
                            } else null

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedMonth = mNum },
                                color = if (isChosen) HolidayPurple else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = mName.split(" ")[0],
                                        textAlign = TextAlign.Center,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (element != null) {
                                        Text(
                                            text = "${element.titlePersian} ${element.emoji}",
                                            fontSize = 9.sp,
                                            color = if (isChosen) Color.White.copy(alpha = 0.85f)
                                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedYear, selectedMonth) },
                colors = ButtonDefaults.buttonColors(containerColor = HolidayPurple)
            ) {
                Text("تأیید", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
