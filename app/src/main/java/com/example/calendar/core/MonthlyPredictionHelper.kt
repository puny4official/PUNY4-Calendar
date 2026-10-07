package com.example.calendar.core

import com.example.calendar.model.CalendarType
import com.example.calendar.model.JalaliDate
import kotlin.math.abs

data class MonthlyPrediction(
    val monthTitle: String,
    val astronomicalEvents: String,
    val traditionalPrediction: String,
    val opportunities: String,
    val warnings: String,
    val auspiciousDays: String,
    val inauspiciousDays: String,
    val monthlyHoroscope: String
)

object MonthlyPredictionHelper {

    private val predictionCache = java.util.concurrent.ConcurrentHashMap<String, MonthlyPrediction>()

    fun clearCache() {
        predictionCache.clear()
    }

    fun generatePrediction(
        calendarType: CalendarType,
        year: Int,
        month: Int
    ): MonthlyPrediction {
        val key = "$calendarType-$year-$month"
        return predictionCache.getOrPut(key) {
            computePrediction(calendarType, year, month)
        }
    }

    private fun computePrediction(
        calendarType: CalendarType,
        year: Int,
        month: Int
    ): MonthlyPrediction {
        // We calculate based on the corresponding Solar Hijri month or Gregorian/Islamic month
        val solarMonth = when (calendarType) {
            CalendarType.SOLAR_HIJRI -> month
            CalendarType.GREGORIAN -> ((month + 8) % 12) + 1 // Approximate match
            CalendarType.LUNAR_HIJRI -> month
        }
        val solarYear = when (calendarType) {
            CalendarType.SOLAR_HIJRI -> year
            CalendarType.GREGORIAN -> year - 621
            CalendarType.LUNAR_HIJRI -> year - 42
        }

        val monthName = when (calendarType) {
            CalendarType.SOLAR_HIJRI -> JalaliCalendar.MONTH_NAMES_PERSIAN[(month - 1).coerceIn(0, 11)]
            CalendarType.GREGORIAN -> CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN[(month - 1).coerceIn(0, 11)]
            CalendarType.LUNAR_HIJRI -> IslamicCalendar.MONTH_NAMES_ARABIC[(month - 1).coerceIn(0, 11)]
        }
        val monthTitle = "پیشگوییهای ماه – $monthName $year"

        val daysInMonth = when (calendarType) {
            CalendarType.SOLAR_HIJRI -> JalaliCalendar.getDaysInMonth(year, month)
            CalendarType.GREGORIAN -> java.time.LocalDate.of(year, month, 1).lengthOfMonth()
            CalendarType.LUNAR_HIJRI -> IslamicCalendar.getDaysInMonth(year, month)
        }

        val qamarDays = mutableListOf<Int>()
        var newMoonDay: Int? = null
        var fullMoonDay: Int? = null
        var firstQuarterDay: Int? = null
        var thirdQuarterDay: Int? = null

        var minAge = 100.0
        var maxIllum = -1

        for (day in 1..daysInMonth) {
            val jdn = when (calendarType) {
                CalendarType.SOLAR_HIJRI -> JalaliCalendar.jalaliToJdn(year, month, day)
                CalendarType.GREGORIAN -> JalaliCalendar.gregorianToJdn(year, month, day)
                CalendarType.LUNAR_HIJRI -> IslamicCalendar.islamicToJdn(year, month, day)
            }

            val qStatus = AstronomicalCalculator.checkQamarDarAqrab(jdn)
            if (qStatus.isInTropicalScorpio || qStatus.isInSiderealScorpio) {
                qamarDays.add(day)
            }

            val moon = AstronomicalCalculator.calculateMoonInfo(jdn)
            if (moon.ageDays < minAge && moon.ageDays < 2.0) {
                minAge = moon.ageDays
                newMoonDay = day
            }
            if (moon.illuminationPercent > maxIllum && moon.illuminationPercent >= 90) {
                maxIllum = moon.illuminationPercent
                fullMoonDay = day
            }
            if (firstQuarterDay == null && abs(moon.ageDays - 7.38) <= 0.8) {
                firstQuarterDay = day
            }
            if (thirdQuarterDay == null && abs(moon.ageDays - 22.14) <= 0.8) {
                thirdQuarterDay = day
            }
        }

        val zodiacSign = AstronomicalCalculator.ZODIAC_SIGNS[(solarMonth - 1).coerceIn(0, 11)]
        val monthElement = AstronomicalCalculator.getMonthElement(solarMonth)
        val yearAnimal = AstronomicalCalculator.getYearAnimal(solarYear)

        // 1. رویدادهای نجومی
        val eventsBuilder = StringBuilder()
        eventsBuilder.append("خورشید در برج ${zodiacSign.nameArabic} (${zodiacSign.symbol})؛ ")
        if (newMoonDay != null) {
            eventsBuilder.append("ماه نو در $newMoonDay $monthName؛ ")
        }
        if (firstQuarterDay != null) {
            eventsBuilder.append("تربیع اول در $firstQuarterDay $monthName؛ ")
        }
        if (fullMoonDay != null) {
            eventsBuilder.append("ماه کامل (بدر) در $fullMoonDay $monthName؛ ")
        }
        if (thirdQuarterDay != null) {
            eventsBuilder.append("تربیع دوم در $thirdQuarterDay $monthName؛ ")
        }
        if (qamarDays.isNotEmpty()) {
            val qamarGrouped = groupConsecutiveDays(qamarDays, monthName)
            eventsBuilder.append("ایام قمر در عقرب در $qamarGrouped.")
        } else {
            eventsBuilder.append("قمر در عقرب در این بازه واقع نیست.")
        }
        val astronomicalEvents = eventsBuilder.toString()

        // 2. پیشگویی سنتی
        val traditionalPrediction = when (monthElement) {
            com.example.calendar.model.ZodiacElement.FIRE ->
                "ماهی با طبع گرم و پرانرژی تحت تأثیر عنصر آتش. در نیمه نخست ماه، برای آغاز برنامه‌های کاری و پیگیری اهداف شجاعانه شرایط مناسب‌تر است. در روزهای تقابل کواکب و عبور قمر از عقرب، احتیاط کنید و از تصمیم‌گیری‌های پرخاشگرانه بپرهیزید. توصیه می‌شود در مسائل خانوادگی و مالی صبر و تدبیر پیشه سازید."
            com.example.calendar.model.ZodiacElement.EARTH ->
                "ماهی با طبیعت پایدار، عمل‌گرا و متمرکز بر ساختن پایه‌های استوار اقتصادی. در دهه اول ماه، نظم‌بخشی به امور مالی، پس‌انداز و قراردادهای اداری مناسب‌تر است. در ایام قمر در عقرب، در خرید و فروش‌های کلان و شراکت‌های عجولانه احتیاط کنید. توصیه می‌شود برای سلامت جسمانی و آرامش روان وقت بیشتری اختصاص دهید."
            com.example.calendar.model.ZodiacElement.AIR ->
                "ماهی سرشار از پویایی فکری، ارتباطات و بازبینی روابط. دهه ابتدایی ماه برای تبادل افکار، آموزش، نگارش و حل‌وفصل مذاکرات معوق مناسب‌تر است. در مواجهه با نوسانات روحی و ایام احتیاط نجومی، از تعهدات احساسی بدون تفکر احتیاط کنید. توصیه می‌شود آرامش کلامی حفظ گردد."
            com.example.calendar.model.ZodiacElement.WATER ->
                "ماهی با غلبه احساسات عمیق، شهود و پیوندهای عاطفی. در روزهای رشد نور ماه، توجه به امور هنری، خانواده و آرامش درونی مناسب‌تر است. در روزهای گرفتگی و عبور ماه از بروج حساس، در امضای اسناد سرنوشت‌ساز احتیاط کنید. توصیه می‌شود از تنش و بدبینی دوری گزینید."
        }

        // 3. فرصتها
        val opportunities = when (monthElement) {
            com.example.calendar.model.ZodiacElement.FIRE ->
                "آغاز پروژه‌های نو، ابتکار عمل در حرفه، گسترش روابط اجتماعی، ارتقای انگیزه و پشتکار."
            com.example.calendar.model.ZodiacElement.EARTH ->
                "ساماندهی منابع مالی، تثبیت موقعیت شغلی، برنامه‌ریزی بلندمدت، نظم در سبک زندگی."
            com.example.calendar.model.ZodiacElement.AIR ->
                "گفت‌وگوهای سازنده، پیوند با دوستان تازه، یادگیری مهارتهای جدید، حل اختلافات قدیمی."
            com.example.calendar.model.ZodiacElement.WATER ->
                "تقویت آرامش معنوی، صلح و سازش خانوادگی، پرداختن به هنر و خلاقیت، همدلی با عزیزان."
        }

        // 4. هشدارها
        val warnings = when (monthElement) {
            com.example.calendar.model.ZodiacElement.FIRE ->
                "شتابزدگی در نتیجه‌گیری، بحث‌های هیجانی، کم‌توجهی به مشورت با دیگران."
            com.example.calendar.model.ZodiacElement.EARTH ->
                "سخت‌گیری بی‌مورد، ریسک‌های مالی حساب‌نشده، خستگی مفرط ناشی از کار زیاد."
            com.example.calendar.model.ZodiacElement.AIR ->
                "پراکندگی ذهن و فراموشی وظایف، تصمیم‌های متغیر، شایعه‌پذیری در کار."
            com.example.calendar.model.ZodiacElement.WATER ->
                "واکنش‌های بیش‌ازحد احساسی، بدگمانی، انفعال و به تعویق انداختن وظایف."
        }

        // 5. ایام سعد
        // Days after new moon, waxing moon and free from Qamar Dar Aqrab
        val potentialAuspicious = mutableListOf<Int>()
        for (d in listOf(5, 7, 11, 12, 16, 19, 21, 24, 26)) {
            if (d <= daysInMonth && d !in qamarDays) {
                potentialAuspicious.add(d)
            }
        }
        val auspiciousDays = if (potentialAuspicious.isNotEmpty()) {
            potentialAuspicious.take(4).joinToString("، ") + " $monthName."
        } else {
            "نیازمند طالع دقیق."
        }

        // 6. ایام نحس
        val inauspiciousDays = if (qamarDays.isNotEmpty()) {
            groupConsecutiveDays(qamarDays, monthName) + " (ایام قمر در عقرب و احتیاط سنتی)."
        } else {
            "روزهای پایانی ماه (محاق)."
        }

        // 7. طالع هر ماه
        val monthlyHoroscope = buildString {
            append("برج فلکی این ماه: ${zodiacSign.namePersian} ${zodiacSign.symbol}؛ ")
            append("عنصر حاکم: ${monthElement.titlePersian} (${monthElement.nature})؛ ")
            append("کوکب منسوب: ${zodiacSign.rulingPlanet}؛ ")
            append("سال نمادین: ${yearAnimal.namePersian} ${yearAnimal.emoji} (${yearAnimal.characteristics}). ")
            append("ویژگی طالع این ماه: ${zodiacSign.description} است و برای همراهی با گردش روزگار، ایجاد اعتدال میان کار و زندگی در این ماه کلید اصلی توفیق است.")
        }

        return MonthlyPrediction(
            monthTitle = monthTitle,
            astronomicalEvents = astronomicalEvents,
            traditionalPrediction = traditionalPrediction,
            opportunities = opportunities,
            warnings = warnings,
            auspiciousDays = auspiciousDays,
            inauspiciousDays = inauspiciousDays,
            monthlyHoroscope = monthlyHoroscope
        )
    }

    private fun groupConsecutiveDays(days: List<Int>, monthName: String): String {
        if (days.isEmpty()) return ""
        val sorted = days.distinct().sorted()
        val parts = mutableListOf<String>()
        var start = sorted[0]
        var prev = sorted[0]

        for (i in 1 until sorted.size) {
            val curr = sorted[i]
            if (curr == prev + 1) {
                prev = curr
            } else {
                if (start == prev) parts.add("$start")
                else parts.add("$start تا $prev")
                start = curr
                prev = curr
            }
        }
        if (start == prev) parts.add("$start")
        else parts.add("$start تا $prev")

        return parts.joinToString(" و ") + " $monthName"
    }
}
