package com.example.calendar.core

import com.example.calendar.model.*
import java.time.LocalDate

object CalendarManager {

    val GREGORIAN_MONTH_NAMES_PERSIAN = listOf(
        "ژانویه (January)", "فوریه (February)", "مارس (March)",
        "آوریل (April)", "مه (May)", "ژوئن (June)",
        "ژوئیه (July)", "اوت (August)", "سپتامبر (September)",
        "اکتبر (October)", "نوامبر (November)", "دسامبر (December)"
    )

    val GREGORIAN_WEEKDAY_NAMES_ENGLISH = listOf(
        "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    )

    fun getTodayGregorian(): GregorianDate {
        val now = LocalDate.now()
        return GregorianDate(now.year, now.monthValue, now.dayOfMonth)
    }

    fun getTodayJalali(): JalaliDate {
        val g = getTodayGregorian()
        return JalaliCalendar.gregorianToJalali(g.year, g.month, g.day)
    }

    private val dayInfoCache = java.util.concurrent.ConcurrentHashMap<String, FullDayInfo>()
    private val monthBaseCache = java.util.concurrent.ConcurrentHashMap<String, List<CalendarGridCell>>()

    fun clearCache() {
        dayInfoCache.clear()
        monthBaseCache.clear()
    }

    fun getFullDayInfo(jdn: Long, city: CityLocation = AstronomicalCalculator.CITIES[0]): FullDayInfo {
        val cacheKey = "$jdn-${city.id}"
        return dayInfoCache.getOrPut(cacheKey) {
            computeFullDayInfo(jdn, city)
        }
    }

    private fun computeFullDayInfo(jdn: Long, city: CityLocation): FullDayInfo {
        val gregorian = JalaliCalendar.jdnToGregorian(jdn)
        val jalali = JalaliCalendar.jdnToJalali(jdn)
        val islamic = IslamicCalendar.jdnToIslamic(jdn)

        val dowIndex = JalaliCalendar.getDayOfWeekIndex(jdn)
        val dowPersian = JalaliCalendar.WEEKDAY_NAMES_PERSIAN[dowIndex]
        val dowArabic = IslamicCalendar.WEEKDAY_NAMES_ARABIC[dowIndex]
        val dowEnglish = GREGORIAN_WEEKDAY_NAMES_ENGLISH[dowIndex]

        val dayOfYear = JalaliCalendar.getDayOfYear(jalali)
        val totalDays = JalaliCalendar.getTotalDaysInYear(jalali.year)
        val daysRemaining = totalDays - dayOfYear
        val weekOfYear = ((dayOfYear - 1) / 7) + 1
        val season = JalaliCalendar.getSeasonPersian(jalali.month)
        val seasonEmoji = JalaliCalendar.getSeasonEmoji(jalali.month)

        val events = EventsRepository.getEvents(jalali, islamic, gregorian)
        val moonInfo = AstronomicalCalculator.calculateMoonInfo(jdn)
        val sunZodiac = AstronomicalCalculator.ZODIAC_SIGNS[(jalali.month - 1).coerceIn(0, 11)]
        val qamarDarAqrab = AstronomicalCalculator.checkQamarDarAqrab(jdn)
        val planetaryRuler = AstronomicalCalculator.PLANETARY_RULERS[dowIndex]
        val solarTimes = AstronomicalCalculator.calculateSolarTimes(jdn, city)

        val todayG = getTodayGregorian()
        val isToday = gregorian.year == todayG.year && gregorian.month == todayG.month && gregorian.day == todayG.day

        return FullDayInfo(
            gregorianDate = gregorian,
            jalaliDate = jalali,
            islamicDate = islamic,
            dayOfWeekPersian = dowPersian,
            dayOfWeekArabic = dowArabic,
            dayOfWeekEnglish = dowEnglish,
            dayOfYearJalali = dayOfYear,
            daysRemainingJalali = daysRemaining,
            weekOfYearJalali = weekOfYear,
            seasonPersian = season,
            seasonEmoji = seasonEmoji,
            events = events,
            moonInfo = moonInfo,
            sunZodiac = sunZodiac,
            qamarDarAqrab = qamarDarAqrab,
            planetaryRuler = planetaryRuler,
            solarTimes = solarTimes,
            isToday = isToday,
            yearAnimal = AstronomicalCalculator.getYearAnimal(jalali.year),
            monthElement = AstronomicalCalculator.getMonthElement(jalali.month)
        )
    }

    fun getFullDayInfoForJalali(year: Int, month: Int, day: Int, city: CityLocation): FullDayInfo {
        val jdn = JalaliCalendar.jalaliToJdn(year, month, day)
        return getFullDayInfo(jdn, city)
    }

    fun getFullDayInfoForGregorian(year: Int, month: Int, day: Int, city: CityLocation): FullDayInfo {
        val jdn = JalaliCalendar.gregorianToJdn(year, month, day)
        return getFullDayInfo(jdn, city)
    }

    fun getFullDayInfoForIslamic(year: Int, month: Int, day: Int, city: CityLocation): FullDayInfo {
        val jdn = IslamicCalendar.islamicToJdn(year, month, day)
        return getFullDayInfo(jdn, city)
    }

    data class CalendarGridCell(
        val jdn: Long,
        val isCurrentMonth: Boolean,
        val isSelected: Boolean,
        val isToday: Boolean,
        val primaryNumber: Int,
        val secondaryText1: String, // e.g. Gregorian day
        val secondaryText2: String, // e.g. Lunar day
        val isFriday: Boolean,
        val hasHoliday: Boolean,
        val hasAstroEvent: Boolean,
        val isQamarDarAqrab: Boolean = false
    )

    /**
     * Generates a 7x(4..6) month grid for the given calendar type and date.
     */
    fun buildMonthGrid(
        calendarType: CalendarType,
        year: Int,
        month: Int,
        selectedJdn: Long
    ): List<CalendarGridCell> {
        val cacheKey = "$calendarType-$year-$month"
        val baseCells = monthBaseCache.getOrPut(cacheKey) {
            computeMonthBaseGrid(calendarType, year, month)
        }
        val todayG = getTodayGregorian()
        val todayJdn = JalaliCalendar.gregorianToJdn(todayG.year, todayG.month, todayG.day)

        return baseCells.map { cell ->
            cell.copy(
                isSelected = (cell.jdn == selectedJdn),
                isToday = (cell.jdn == todayJdn)
            )
        }
    }

    private fun computeMonthBaseGrid(
        calendarType: CalendarType,
        year: Int,
        month: Int
    ): List<CalendarGridCell> {
        val cells = mutableListOf<CalendarGridCell>()

        when (calendarType) {
            CalendarType.SOLAR_HIJRI -> {
                val daysInMonth = JalaliCalendar.getDaysInMonth(year, month)
                val firstJdn = JalaliCalendar.jalaliToJdn(year, month, 1)
                val startDayOfWeek = JalaliCalendar.getDayOfWeekIndex(firstJdn) // 0=Sat ... 6=Fri

                // Leading days from previous month
                for (i in startDayOfWeek downTo 1) {
                    val prevJdn = firstJdn - i
                    cells.add(createCell(prevJdn, false, -1L, -1L, calendarType))
                }

                // Days of current month
                for (day in 1..daysInMonth) {
                    val jdn = JalaliCalendar.jalaliToJdn(year, month, day)
                    cells.add(createCell(jdn, true, -1L, -1L, calendarType))
                }

                // Trailing days to complete grid to multiple of 7
                val remaining = (7 - (cells.size % 7)) % 7
                val lastJdn = JalaliCalendar.jalaliToJdn(year, month, daysInMonth)
                for (i in 1..remaining) {
                    val nextJdn = lastJdn + i
                    cells.add(createCell(nextJdn, false, -1L, -1L, calendarType))
                }
            }

            CalendarType.GREGORIAN -> {
                val tempDate = LocalDate.of(year, month, 1)
                val daysInMonth = tempDate.lengthOfMonth()
                val firstJdn = JalaliCalendar.gregorianToJdn(year, month, 1)
                val startDayOfWeek = JalaliCalendar.getDayOfWeekIndex(firstJdn)

                for (i in startDayOfWeek downTo 1) {
                    val prevJdn = firstJdn - i
                    cells.add(createCell(prevJdn, false, -1L, -1L, calendarType))
                }

                for (day in 1..daysInMonth) {
                    val jdn = JalaliCalendar.gregorianToJdn(year, month, day)
                    cells.add(createCell(jdn, true, -1L, -1L, calendarType))
                }

                val remaining = (7 - (cells.size % 7)) % 7
                val lastJdn = JalaliCalendar.gregorianToJdn(year, month, daysInMonth)
                for (i in 1..remaining) {
                    val nextJdn = lastJdn + i
                    cells.add(createCell(nextJdn, false, -1L, -1L, calendarType))
                }
            }

            CalendarType.LUNAR_HIJRI -> {
                val daysInMonth = IslamicCalendar.getDaysInMonth(year, month)
                val firstJdn = IslamicCalendar.islamicToJdn(year, month, 1)
                val startDayOfWeek = JalaliCalendar.getDayOfWeekIndex(firstJdn)

                for (i in startDayOfWeek downTo 1) {
                    val prevJdn = firstJdn - i
                    cells.add(createCell(prevJdn, false, -1L, -1L, calendarType))
                }

                for (day in 1..daysInMonth) {
                    val jdn = IslamicCalendar.islamicToJdn(year, month, day)
                    cells.add(createCell(jdn, true, -1L, -1L, calendarType))
                }

                val remaining = (7 - (cells.size % 7)) % 7
                val lastJdn = IslamicCalendar.islamicToJdn(year, month, daysInMonth)
                for (i in 1..remaining) {
                    val nextJdn = lastJdn + i
                    cells.add(createCell(nextJdn, false, -1L, -1L, calendarType))
                }
            }
        }

        return cells
    }

    private fun createCell(
        jdn: Long,
        isCurrentMonth: Boolean,
        selectedJdn: Long,
        todayJdn: Long,
        calendarType: CalendarType
    ): CalendarGridCell {
        val j = JalaliCalendar.jdnToJalali(jdn)
        val g = JalaliCalendar.jdnToGregorian(jdn)
        val i = IslamicCalendar.jdnToIslamic(jdn)
        val dow = JalaliCalendar.getDayOfWeekIndex(jdn)
        val isFriday = (dow == 6)

        val events = EventsRepository.getEvents(j, i, g)
        val hasHoliday = isFriday || events.any { it.isHoliday }
        val hasAstro = events.any { it.type == EventType.ASTRONOMY }
        val qamarStatus = AstronomicalCalculator.checkQamarDarAqrab(jdn)
        val isQamar = qamarStatus.isInTropicalScorpio || qamarStatus.isInSiderealScorpio

        val primaryNum: Int
        val sec1: String
        val sec2: String

        when (calendarType) {
            CalendarType.SOLAR_HIJRI -> {
                primaryNum = j.day
                sec1 = "${g.day}"
                sec2 = "${i.day}"
            }
            CalendarType.GREGORIAN -> {
                primaryNum = g.day
                sec1 = "${j.day}"
                sec2 = "${i.day}"
            }
            CalendarType.LUNAR_HIJRI -> {
                primaryNum = i.day
                sec1 = "${j.day}"
                sec2 = "${g.day}"
            }
        }

        return CalendarGridCell(
            jdn = jdn,
            isCurrentMonth = isCurrentMonth,
            isSelected = (jdn == selectedJdn),
            isToday = (jdn == todayJdn),
            primaryNumber = primaryNum,
            secondaryText1 = sec1,
            secondaryText2 = sec2,
            isFriday = isFriday,
            hasHoliday = hasHoliday,
            hasAstroEvent = hasAstro,
            isQamarDarAqrab = isQamar
        )
    }
}
