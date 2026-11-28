package com.example.calendar.core

import com.example.calendar.model.IslamicDate
import kotlin.math.floor

object IslamicCalendar {

    val MONTH_NAMES_ARABIC = listOf(
        "محرّم", "صفر", "ربیع‌الأوّل",
        "ربیع‌الثّانی", "جمادی‌الأولی", "جمادی‌الثّانیة",
        "رجب", "شعبان", "رمضان",
        "شوّال", "ذی‌القعدة", "ذی‌الحجّة"
    )

    val WEEKDAY_NAMES_ARABIC = listOf(
        "السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة"
    )

    /**
     * Converts Julian Day Number to Islamic (Hijri Qamari) date using standard astronomical tabular algorithm.
     */
    /**
     * Offset (days) between the tabular algorithm and Iran's official (moon-sighting) calendar.
     * Verified: 24 Mehr 1405 = 5 Jumada I 1448 (Nurse Day) in the official calendar.
     */
    const val IRAN_OFFSET_DAYS = 1L

    /**
     * Before 1 Mehr 1405 the official calendar runs 2 days behind the Mehr-1405 alignment
     * (official: 25 Shawwal 1447 = 25 Farvardin 1405, i.e. 1 Shawwal = 1 Farvardin).
     */
    private val OFFSET_SWITCH_JDN: Long by lazy { JalaliCalendar.jalaliToJdn(1405, 7, 1) }

    /** From 28 Ordibehesht 1405 (1 Dhu al-Hijja; 6 Khordad = Eid al-Adha) the lag is 1 day. */
    private val OFFSET_SWITCH_1_JDN: Long by lazy { JalaliCalendar.jalaliToJdn(1405, 2, 28) }

    /** From 19 Azar 1405 (20 Azar = 1 Rajab; 2 Dey = 13 Rajab) the lag drops to 0. */
    private val OFFSET_SWITCH_2_JDN: Long by lazy { JalaliCalendar.jalaliToJdn(1405, 9, 19) }

    fun jdnToIslamic(jdn: Long): IslamicDate {
        val off = when {
            jdn < OFFSET_SWITCH_1_JDN -> IRAN_OFFSET_DAYS - 2L
            jdn < OFFSET_SWITCH_JDN -> IRAN_OFFSET_DAYS - 1L
            jdn < OFFSET_SWITCH_2_JDN -> IRAN_OFFSET_DAYS
            else -> IRAN_OFFSET_DAYS - 1L
        }
        val l = jdn + off - 1948440L + 10632L
        val n = floor((l - 1.0) / 10631.0).toLong()
        val l2 = l - 10631L * n + 354L
        val j = (floor((10985.0 - l2) / 5316.0) * floor((50.0 * l2) / 17719.0) +
                floor(l2 / 5670.0) * floor((43.0 * l2) / 15238.0)).toLong()
        val l3 = l2 - (floor((30.0 - j) / 15.0) * floor((17719.0 * j) / 50.0) +
                floor(j / 16.0) * floor((15238.0 * j) / 43.0)).toLong() + 29L
        val month = floor((24.0 * l3) / 709.0).toInt()
        val day = (l3 - floor((709.0 * month) / 24.0)).toInt()
        val year = (30L * n + j - 30L).toInt()
        return IslamicDate(year, month, day)
    }

    /**
     * Converts Islamic date to Julian Day Number.
     */
    fun islamicToJdn(year: Int, month: Int, day: Int): Long {
        return (day +
                Math.ceil(29.5 * (month - 1)).toLong() +
                (year - 1L) * 354L +
                floor((3L + (11L * year)) / 30.0).toLong() +
                1948440L) - 1L - IRAN_OFFSET_DAYS
    }

    fun isLeapYear(year: Int): Boolean {
        return ((11 * year + 14) % 30) < 11
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        return if (month % 2 == 1 || (month == 12 && isLeapYear(year))) 30 else 29
    }
}
