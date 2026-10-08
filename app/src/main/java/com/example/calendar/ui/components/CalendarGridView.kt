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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.CalendarManager
import com.example.calendar.core.DigitFormatter
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

private val CellCornerShape = RoundedCornerShape(12.dp)

private val DayNumberBaseStyle = TextStyle(
    fontFamily = FontFamily.Default,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both
    )
)

private val SecondaryDateCompactStyle = TextStyle(
    fontFamily = FontFamily.Default,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both
    )
)

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
    useEnglishDayNumbers: Boolean = false,
    fontScalePercent: Int = 100,
    dayNumberScalePercent: Int = 100,
    holidayColor: Color = Color(0xFF8B5CF6L),
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

    val renderFaDigits = isFa && !useEnglishDayNumbers
    val monthTitle = remember(calendarType, currentYear, currentMonth, appLanguage, useEnglishDayNumbers) {
        val yearStr = DigitFormatter.toSystemDigits(currentYear, renderFaDigits)
        when (calendarType) {
            CalendarType.SOLAR_HIJRI -> "${JalaliCalendar.MONTH_NAMES_PERSIAN[currentMonth - 1]} $yearStr"
            CalendarType.GREGORIAN -> {
                if (isFa) {
                    "${CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN[currentMonth - 1]} $yearStr"
                } else {
                    "${java.time.Month.of(currentMonth).name.lowercase().replaceFirstChar { it.uppercase() }} $currentYear"
                }
            }
            CalendarType.LUNAR_HIJRI -> "${IslamicCalendar.MONTH_NAMES_ARABIC[currentMonth - 1]} $yearStr"
        }
    }

Box(
    modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(Color(0xFF0D0820))
        .border(
            width = 1.dp,
            color = Color(0xFF8B5CFF).copy(alpha = 0.35f),
            shape = RoundedCornerShape(24.dp)
        )
        .testTag("calendar_grid_card")
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
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
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
                        Text(text = "🔮", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isFa) "پیشگویی" else "Forecast",
                            fontSize = 12.5.sp,
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
                // Month navigation: Next month arrow points to the right (→)
                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier.testTag("next_month_button")
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "ماه بعد",
                            tint = HolidayPurple
                        )
                    }
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
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 18.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "انتخاب ماه و سال",
                        tint = HolidayPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Month navigation: Previous month arrow points to the left (←)
                IconButton(
                    onClick = onPreviousMonth,
                    modifier = Modifier.testTag("prev_month_button")
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ماه قبل",
                            tint = HolidayPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFriday) HolidayPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFF1E1E22) else MaterialTheme.colorScheme.surfaceVariant
            )

            // Grid rows
            val rows = remember(cells) { cells.chunked(7) }
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
                            isFa = isFa,
                            useEnglishDayNumbers = useEnglishDayNumbers,
                            dayNumberScalePercent = dayNumberScalePercent,
                            holidayColor = holidayColor,
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
                        modifier = Modifier.size(15.dp),
                        tint = ScorpioAlert
                    )
                    Text(
                        text = "قمر در عقرب",
                        fontSize = 12.5.sp,
                        color = ScorpioAlert,
                        fontWeight = FontWeight.SemiBold
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
            useEnglishDayNumbers = useEnglishDayNumbers,
            isFa = isFa,
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
            fontScalePercent = fontScalePercent,
            isFa = isFa,
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
    isFa: Boolean = true,
    useEnglishDayNumbers: Boolean = false,
    dayNumberScalePercent: Int = 100,
    holidayColor: Color = Color(0xFF8B5CF6L),
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
    val isHoliday = cell.hasHoliday

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    // داخل نشانگر: بدون اینکه داخلش رو رنگی کنی (کاملاً شفاف یا هم‌رنگ پس‌زمینه بدون هیچ رنگ اضافی)
    val targetBackground = when {
        isSelected -> Color.Transparent
        isHoliday -> holidayColor.copy(alpha = 0.50f)
        else -> if (isDark) Color.Black else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
    }

    val targetBorderColor = when {
        isSelected -> Color(0xFF06B6D4) // فقط دور نشانگر فیروزه‌ای باشه
        isHoliday -> holidayColor.copy(alpha = 0.80f) // کادر شیشه‌ای به رنگ انتخابی کاربر
        isToday -> Color(0xFF06B6D4).copy(alpha = 0.65f)
        else -> if (isDark) Color(0xFF1E1E24) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
    }
    val borderWidth = if (isSelected) 2.5.dp else if (isHoliday) 1.5.dp else if (isToday) 1.5.dp else 1.dp

    val textColor = when {
        isSelected -> if (isDark) Color.White else Color.Black // وقتی در مود لایت است عدد نشانگر سیاه، و در دارک مود سفید
        isHoliday -> Color.White // نوشته داخل تعطیلات رسمی سفید
        else -> MaterialTheme.colorScheme.onSurface
    }

    val secondaryTextColor = when {
        isSelected -> if (isDark) Color.White.copy(alpha = 0.88f) else Color.Black.copy(alpha = 0.85f)
        isHoliday -> Color.White.copy(alpha = 0.88f) // نوشته‌های کوچک زیر روزهای تعطیل رسمی نیز سفید
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(targetBackground, CellCornerShape)
            .border(borderWidth, targetBorderColor, CellCornerShape)
            .clickable { onDateSelected(cell.jdn) }
            .testTag("day_cell_${cell.primaryNumber}"),
        contentAlignment = Alignment.Center
    ) {
        val renderFaDigits = isFa && !useEnglishDayNumbers

        val dayScale = (dayNumberScalePercent / 100f).coerceIn(0.80f, 1.60f)
        val baseFontSize = if (showSecondaryDates) 14.5f else 18f
        val dayFontSize = (baseFontSize * dayScale).sp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
                // Main day number (برگرفته از فونت پیش‌فرض خود گوشی و متمرکز در وسط بدون به هم ریختن)
                Text(
                    text = DigitFormatter.toSystemDigits(cell.primaryNumber, renderFaDigits),
                    style = DayNumberBaseStyle,
                    fontWeight = if (isSelected || isToday) FontWeight.ExtraBold else FontWeight.Bold,
                    color = textColor,
                    fontSize = dayFontSize,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )

                // Secondary calendar dates (میلادی و قمری در کنار هم بدون به هم چسبیدن و در وسط کادر)
                if (showSecondaryDates) {
                    Spacer(modifier = Modifier.height(if (dayScale > 1.25f) 0.5.dp else 1.5.dp))

                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = DigitFormatter.toSystemDigits(cell.secondaryText1, renderFaDigits),
                            style = SecondaryDateCompactStyle,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 8.8.sp,
                            color = secondaryTextColor,
                            maxLines = 1,
                            softWrap = false
                        )

                        // نقطه تفکیک‌کننده با فاصله استاندارد از دو عدد
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.5.dp)
                                .size(2.dp)
                                .background(secondaryTextColor.copy(alpha = 0.6f), CircleShape)
                        )

                        Text(
                            text = DigitFormatter.toSystemDigits(cell.secondaryText2, renderFaDigits),
                            style = SecondaryDateCompactStyle,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 8.8.sp,
                            color = secondaryTextColor,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

        // Qamar Dar Aqrab symbol (نماد قمر در عقرب وکتوری به رنگ قرمز بدون دایره فیروزه‌ای)
        if (cell.isQamarDarAqrab) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 2.dp)
            ) {
                ScorpioIcon(
                    modifier = Modifier.size(9.dp),
                    tint = ScorpioAlert
                )
            }
        }

        // Today dot indicator if today is not currently selected
        if (isToday && !isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.5.dp)
                    .size(3.5.dp)
                    .background(if (isHoliday) Color.White else HolidayPurple, CircleShape)
            )
        }
    }
}

@Composable
fun YearMonthPickerDialog(
    calendarType: CalendarType,
    currentYear: Int,
    currentMonth: Int,
    useEnglishDayNumbers: Boolean = true,
    isFa: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val renderFaDigits = isFa && !useEnglishDayNumbers
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
                // Year selector with animal (Right: Next Year →, Left: Prev Year ←)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear++ }) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "سال بعد")
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = DigitFormatter.toSystemDigits(selectedYear, renderFaDigits),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Default
                            ),
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
                    IconButton(onClick = { selectedYear-- }) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "سال قبل")
                        }
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
