package com.example.calendar.core

import com.example.calendar.model.GregorianDate
import com.example.calendar.model.JalaliDate
import kotlin.math.floor

object JalaliCalendar {

    val MONTH_NAMES_PERSIAN = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val WEEKDAY_NAMES_PERSIAN = listOf(
        "شنبه", "یک‌شنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    val WEEKDAY_SHORT_PERSIAN = listOf(
        "ش", "ی", "د", "س", "چ", "پ", "ج"
    )

    /**
     * Checks if a Jalali year is a leap year (سال کبیسه).
     */
    fun isLeapYear(year: Int): Boolean {
        val r = (year + 38) % 33
        return (r in listOf(1, 5, 9, 13, 17, 22, 26, 30))
    }

    /**
     * Days count in a specific Jalali month.
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    /**
     * Converts Gregorian Date to Julian Day Number (JDN).
     */
    fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        val a = floor((14.0 - month) / 12.0).toLong()
        val y = year + 4800L - a
        val m = month + 12L * a - 3L
        return day + floor((153.0 * m + 2.0) / 5.0).toLong() + 365L * y +
                floor(y / 4.0).toLong() - floor(y / 100.0).toLong() +
                floor(y / 400.0).toLong() - 32045L
    }

    /**
     * Converts Julian Day Number to Gregorian Date.
     */
    fun jdnToGregorian(jdn: Long): GregorianDate {
        val l = jdn + 68569L
        val n = floor((4.0 * l) / 146097.0).toLong()
        val l2 = l - floor((146097.0 * n + 3.0) / 4.0).toLong()
        val i = floor((4000.0 * (l2 + 1.0)) / 1461001.0).toLong()
        val l3 = l2 - floor((1461.0 * i) / 4.0).toLong() + 31L
        val j = floor((80.0 * l3) / 2447.0).toLong()
        val day = (l3 - floor((2447.0 * j) / 80.0)).toInt()
        val l4 = floor(j / 11.0).toLong()
        val month = (j + 2L - 12L * l4).toInt()
        val year = (100L * (n - 49L) + i + l4).toInt()
        return GregorianDate(year, month, day)
    }

    /**
     * Direct conversion from Gregorian to Jalali.
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
        val gDm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + gDm[gm - 1]
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm = if (days < 186) 1 + (days / 31) else 7 + ((days - 186) / 30)
        val jd = 1 + (if (days < 186) (days % 31) else ((days - 186) % 30))
        return JalaliDate(jy, jm, jd)
    }

    /**
     * Direct conversion from Jalali to Gregorian.
     */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): GregorianDate {
        val jy2 = jy + 1595
        var days = -355668 + (365 * jy2) + ((jy2 / 33) * 8) + (((jy2 % 33) + 3) / 4) + jd +
                (if (jm < 7) (jm - 1) * 31 else ((jm - 7) * 30) + 186)
        var gy = 400 * (days / 146097)
        days %= 146097
        if (days > 36524) {
            gy += 100 * (--days / 36524)
            days %= 36524
            if (days >= 365) days++
        }
        gy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
        }
        var gd = days + 1
        val salA = intArrayOf(0, 31, if ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 0
        while (gm < 13 && gd > salA[gm]) {
            gd -= salA[gm]
            gm++
        }
        return GregorianDate(gy, gm, gd)
    }

    /**
     * Converts Julian Day Number to Jalali Date.
     */
    fun jdnToJalali(jdn: Long): JalaliDate {
        val g = jdnToGregorian(jdn)
        return gregorianToJalali(g.year, g.month, g.day)
    }

    /**
     * Converts Jalali Date to Julian Day Number.
     */
    fun jalaliToJdn(year: Int, month: Int, day: Int): Long {
        val g = jalaliToGregorian(year, month, day)
        return gregorianToJdn(g.year, g.month, g.day)
    }

    /**
     * Day of week: 0 = Saturday (شنبه), 1 = Sunday (یک‌شنبه), ..., 6 = Friday (جمعه)
     */
    fun getDayOfWeekIndex(jdn: Long): Int {
        val dow = ((jdn + 2) % 7).toInt()
        return if (dow < 0) dow + 7 else dow
    }

    fun getDayOfYear(jalaliDate: JalaliDate): Int {
        var days = jalaliDate.day
        for (m in 1 until jalaliDate.month) {
            days += if (m <= 6) 31 else 30
        }
        return days
    }

    fun getTotalDaysInYear(year: Int): Int = if (isLeapYear(year)) 366 else 365

    fun getSeasonPersian(month: Int): String {
        return when (month) {
            1, 2, 3 -> "بهار"
            4, 5, 6 -> "تابستان"
            7, 8, 9 -> "پاییز"
            10, 11, 12 -> "زمستان"
            else -> "بهار"
        }
    }

    fun getSeasonEmoji(month: Int): String {
        return when (month) {
            1, 2, 3 -> "🌸"
            4, 5, 6 -> "☀️"
            7, 8, 9 -> "🍂"
            10, 11, 12 -> "❄️"
            else -> "🍂"
        }
    }

    fun getSeasonEmojiByName(season: String): String {
        return when (season) {
            "بهار" -> "🌸"
            "تابستان" -> "☀️"
            "پاییز" -> "🍂"
            "زمستان" -> "❄️"
            else -> "🍂"
        }
    }
}
