package com.example.calendar.core

import com.example.calendar.model.*
import kotlin.math.*

object AstronomicalCalculator {

    val ZODIAC_SIGNS = listOf(
        ZodiacSign("حَمَل (بره)", "الحمل", "Aries", "♈", ZodiacElement.FIRE, "مریخ (بهرام)", "آغاز بهار، شور، حرکت و شجاعت"),
        ZodiacSign("ثَور (گاو)", "الثور", "Taurus", "♉", ZodiacElement.EARTH, "زهره (ناهید)", "پایداری، بردباری، زیبایی و برکت"),
        ZodiacSign("جَوزا (دوپیکر)", "الجوزاء", "Gemini", "♊", ZodiacElement.AIR, "عطارد (تیر)", "ارتباطات، هوش، تنوع و پویایی"),
        ZodiacSign("سَرَطان (خرچنگ)", "السرطان", "Cancer", "♋", ZodiacElement.WATER, "ماه", "عاطفه، خانواده، شهود و احساس"),
        ZodiacSign("اَسَد (شیر)", "الأسد", "Leo", "♌", ZodiacElement.FIRE, "خورشید", "شکوه، رهبری، خلاقیت و سخاوت"),
        ZodiacSign("سُنبُله (دوشیزه)", "العذراء", "Virgo", "♍", ZodiacElement.EARTH, "عطارد (تیر)", "دقت، تحلیل، نظم و خرد"),
        ZodiacSign("میزان (ترازو)", "المیزان", "Libra", "♎", ZodiacElement.AIR, "زهره (ناهید)", "اعتدال پاییزی، تعادل، عدالت و هماهنگی"),
        ZodiacSign("عَقرب (کژدم)", "العقرب", "Scorpio", "♏", ZodiacElement.WATER, "مریخ", "عمق، دگرگونی، اراده و رازوری"),
        ZodiacSign("قَوس (کمانگیر)", "القوس", "Sagittarius", "♐", ZodiacElement.FIRE, "مشتری (برجیس)", "امید، فلسفه، جهان‌بینی و سفر"),
        ZodiacSign("جَدی (بزغاله)", "الجدی", "Capricorn", "♑", ZodiacElement.EARTH, "زحل (کیوان)", "انقلاب زمستانی، استواری، هدفمندی و صبر"),
        ZodiacSign("دَلو (آب‌ریز)", "الدلو", "Aquarius", "♒", ZodiacElement.AIR, "زحل", "نوآوری، استقلال، آزادی و آرمانخواهی"),
        ZodiacSign("حوت (ماهی)", "الحوت", "Pisces", "♓", ZodiacElement.WATER, "مشتری", "شهود، همدلی، هنر و آرامش درونی")
    )

    val PLANETARY_RULERS = listOf(
        PlanetaryRuler("شنبه", "زُحَل (کیوان)", "Saturn", "سرد و خشک - نماد انضباط، صبوری و عمق تفکر"),
        PlanetaryRuler("یک‌شنبه", "خورشید (مهر)", "Sun", "گرم و خشک - نماد حیات، انرژی و درخشش"),
        PlanetaryRuler("دوشنبه", "ماه", "Moon", "سرد و تر - نماد احساسات، آرامش و تغییر"),
        PlanetaryRuler("سه‌شنبه", "مِرّیخ (بهرام)", "Mars", "گرم و خشک - نماد انگیزه، دلاوری و عمل"),
        PlanetaryRuler("چهارشنبه", "عُطارِد (تیر)", "Mercury", "معتدل - نماد اندیشه، سخنوری، دانش و تجارت"),
        PlanetaryRuler("پنج‌شنبه", "مُشتَری (برجیس)", "Jupiter", "گرم و تر - نماد برکت، توسعه، خرد و فراوانی"),
        PlanetaryRuler("جمعه", "زُهرَه (ناهید)", "Venus", "معتدل و تر - نماد مهرورزی، هنر، لطافت و شادابی")
    )

    /**
     * تقویم ۱۲ حیوانی سنتی ایران (دوره ۱۲ ساله ترکی-ایرانی):
     * موش، گاو، پلنگ (ببر)، خرگوش، نهنگ (اژدها)، مار، اسب، گوسفند، میمون، مرغ (خروس)، سگ، خوک.
     */
    val YEAR_ANIMALS = listOf(
        YearAnimal("موش", "موش", "🐀", "هوش، زیرکی، تدبیر اقتصادی و چابکی"),
        YearAnimal("گاو", "بقر", "🐂", "صبر، استواری، سخت‌کوشی و برکت"),
        YearAnimal("پلنگ", "ببر", "🐅", "شجاعت، جسارت، دلاوری و ابهت"),
        YearAnimal("خرگوش", "خرگوش", "🐇", "صلح‌طلبی، ظرافت، احتیاط و امید"),
        YearAnimal("نهنگ", "اژدها", "🐉", "شکوه، بخت و اقبال، عظمت و اقتدار"),
        YearAnimal("مار", "ثعبان", "🐍", "حکمت، ژرف‌اندیشی، آرامش و هوشیاری عمیق"),
        YearAnimal("اسب", "فرس", "🐎", "سرعت، آزادی‌خواهی، پویایی و پیروزی"),
        YearAnimal("گوسفند", "بز", "🐑", "خلق نیکو، مهرورزی، صلح و آرامش"),
        YearAnimal("میمون", "حمدونه", "🐒", "هوش سرشار، خلاقیت، نشاط و چاره‌جویی"),
        YearAnimal("مرغ", "خروس", "🐓", "سحرخیزی، وظیفه‌شناسی، آگاهی و روشنایی"),
        YearAnimal("سگ", "کلب", "🐕", "وفاداری، امانتداری، صداقت و پاسداری"),
        YearAnimal("خوک", "خنزیر", "🐖", "فراوانی، دست‌ودلبازی، سخاوت و آسایش")
    )

    /**
     * محاسبه حیوان نماد هر سال شمسی در چرخه ۱۲ ساله:
     * برای مثال: ۱۳۹۹ موش، ۱۴۰۰ گاو، ۱۴۰۱ ببر/پلنگ، ۱۴۰۲ خرگوش، ۱۴۰۳ نهنگ/اژدها، ۱۴۰۴ مار، ۱۴۰۵ اسب و...
     */
    fun getYearAnimal(solarYear: Int): YearAnimal {
        val index = ((solarYear - 7) % 12 + 12) % 12
        return YEAR_ANIMALS[index]
    }

    /**
     * عنصر هر ماه بر اساس گاه‌شماری و بروج دوازده‌گانه فلکی:
     * فروردین: آتش 🔥، اردیبهشت: خاک 🌍، خرداد: باد (هوا) 💨، تیر: آب 💧
     * مرداد: آتش 🔥، شهریور: خاک 🌍، مهر: باد (هوا) 💨، آبان: آب 💧
     * آذر: آتش 🔥، دی: خاک 🌍، بهمن: باد (هوا) 💨، اسفند: آب 💧
     */
    fun getMonthElement(month1to12: Int): ZodiacElement {
        return when (((month1to12 - 1).coerceIn(0, 11)) % 4) {
            0 -> ZodiacElement.FIRE  // فروردین، مرداد، آذر
            1 -> ZodiacElement.EARTH // اردیبهشت، شهریور، دی
            2 -> ZodiacElement.AIR   // خرداد، مهر، بهمن
            else -> ZodiacElement.WATER // تیر، آبان، اسفند
        }
    }

    val CITIES = listOf(
        CityLocation("tehran", "تهران", "Tehran", 35.6892, 51.3890, 3.5),
        CityLocation("mashhad", "مشهد", "Mashhad", 36.2605, 59.6168, 3.5),
        CityLocation("isfahan", "اصفهان", "Isfahan", 32.6546, 51.6680, 3.5),
        CityLocation("shiraz", "شیراز", "Shiraz", 29.5918, 52.5837, 3.5),
        CityLocation("tabriz", "تبریز", "Tabriz", 38.0800, 46.2919, 3.5),
        CityLocation("ahvaz", "اهواز", "Ahvaz", 31.3183, 48.6706, 3.5),
        CityLocation("qom", "قم", "Qom", 34.6401, 50.8764, 3.5),
        CityLocation("kermanshah", "کرمانشاه", "Kermanshah", 34.3277, 47.0778, 3.5),
        CityLocation("rasht", "رشت", "Rasht", 37.2809, 49.5832, 3.5),
        CityLocation("yazd", "یزد", "Yazd", 31.8974, 54.3569, 3.5),
        CityLocation("kerman", "کرمان", "Kerman", 30.2839, 57.0834, 3.5),
        CityLocation("zahedan", "زاهدان", "Zahedan", 29.4963, 60.8629, 3.5),
        CityLocation("bandarabbas", "بندرعباس", "Bandar Abbas", 27.1832, 56.2666, 3.5),
        CityLocation("dubai", "دبی", "Dubai", 25.2048, 55.2708, 4.0),
        CityLocation("istanbul", "استانبول", "Istanbul", 41.0082, 28.9784, 3.0)
    )

    /**
     * Calculates Moon information: Phase, Illumination %, Moon age, Moon Longitude, Moon Zodiac.
     */
    fun calculateMoonInfo(jdn: Long): MoonInfo {
        val jd = jdn.toDouble() + 0.5 // Midday UTC
        val synodicMonth = 29.53058867
        val newMoonEpochJd = 2451549.26 // Reference new moon Jan 6, 2000

        var ageDays = (jd - newMoonEpochJd) % synodicMonth
        if (ageDays < 0) ageDays += synodicMonth

        val phaseAngleDegrees = (ageDays / synodicMonth) * 360.0
        val illuminationPercent = round((1.0 - cos(Math.toRadians(phaseAngleDegrees))) / 2.0 * 100.0).toInt().coerceIn(0, 100)

        val phaseType = when {
            ageDays < 1.84 || ageDays >= 27.68 -> MoonPhaseType.NEW_MOON
            ageDays < 5.53 -> MoonPhaseType.WAXING_CRESCENT
            ageDays < 9.22 -> MoonPhaseType.FIRST_QUARTER
            ageDays < 12.92 -> MoonPhaseType.WAXING_GIBBOUS
            ageDays < 16.61 -> MoonPhaseType.FULL_MOON
            ageDays < 20.30 -> MoonPhaseType.WANING_GIBBOUS
            ageDays < 23.99 -> MoonPhaseType.THIRD_QUARTER
            else -> MoonPhaseType.WANING_CRESCENT
        }

        // Moon ecliptic longitude approximation
        val t = (jd - 2451545.0) / 36525.0
        val lPrime = normalizeDegrees(218.3164477 + 481267.88123421 * t)
        val m = normalizeDegrees(357.5291092 + 35999.0502909 * t)
        val mPrime = normalizeDegrees(134.9633964 + 477198.8675055 * t)
        val d = normalizeDegrees(297.8501921 + 445267.1114034 * t)

        val moonLon = normalizeDegrees(
            lPrime +
                    6.289 * sin(Math.toRadians(mPrime)) +
                    1.274 * sin(Math.toRadians(2 * d - mPrime)) +
                    0.658 * sin(Math.toRadians(2 * d)) -
                    0.186 * sin(Math.toRadians(m))
        )

        val zodiacIndex = (moonLon / 30.0).toInt().coerceIn(0, 11)
        val zodiacDegree = moonLon % 30.0

        return MoonInfo(
            phaseType = phaseType,
            illuminationPercent = illuminationPercent,
            ageDays = round(ageDays * 10.0) / 10.0,
            phaseAngleDegrees = phaseAngleDegrees,
            moonZodiacName = ZODIAC_SIGNS[zodiacIndex].namePersian,
            moonZodiacDegree = round(zodiacDegree * 10.0) / 10.0
        )
    }

    /**
     * Determines Qamar Dar Aqrab (قمر در عقرب).
     * Tropical Scorpio (برج عقرب): 210° to 240°
     * Sidereal Scorpio (صورت فلکی عقرب): approx 234° to 258°
     */
    fun checkQamarDarAqrab(jdn: Long): QamarDarAqrabStatus {
        val jd = jdn.toDouble() + 0.5
        val t = (jd - 2451545.0) / 36525.0
        val lPrime = normalizeDegrees(218.3164477 + 481267.88123421 * t)
        val mPrime = normalizeDegrees(134.9633964 + 477198.8675055 * t)
        val d = normalizeDegrees(297.8501921 + 445267.1114034 * t)
        val moonLon = normalizeDegrees(
            lPrime + 6.289 * sin(Math.toRadians(mPrime)) + 1.274 * sin(Math.toRadians(2 * d - mPrime))
        )

        val inTropical = moonLon in 210.0..240.0
        val inSidereal = moonLon in 234.0..258.0

        val summary = when {
            inTropical && inSidereal -> "قمر هم در برج و هم در صورت فلکی عقرب است"
            inTropical -> "قمر در برج عقرب است"
            inSidereal -> "قمر در صورت فلکی عقرب است"
            moonLon in 207.0..210.0 -> "قمر در آستانه ورود به برج عقرب است"
            else -> "قمر در عقرب نیست"
        }

        val advice = when {
            inTropical || inSidereal ->
                "بر اساس سنت‌های کهن و نجوم اسلامی، در زمان عبور ماه از عقرب توصیه شده از کارهای اساسی چون عقد ازدواج، آغاز سفر طولانی، و معاملات مهم احتیاط شود."
            else ->
                "امروز قمر خارج از محدوده برج و صورت عقرب قرار دارد و شرایط نجومی از این منظر عادی و مساعد است."
        }

        return QamarDarAqrabStatus(
            isInTropicalScorpio = inTropical,
            isInSiderealScorpio = inSidereal,
            moonLongitudeDegrees = round(moonLon * 10.0) / 10.0,
            statusSummary = summary,
            detailedAdvice = advice
        )
    }

    /**
     * Solar Times (طلوع، غروب، ظهر، اذان‌ها، طول روز و شب)
     */
    fun calculateSolarTimes(jdn: Long, city: CityLocation): SolarTimes {
        val lat = city.latitude
        val lng = city.longitude
        val tz = city.timezoneHours

        val n = (jdn - 2451545L).toDouble() + 0.0008
        val jStar = n - (lng / 360.0)
        val m = normalizeDegrees(357.5291 + 0.98560028 * jStar)
        val c = 1.9148 * sin(Math.toRadians(m)) + 0.02 * sin(Math.toRadians(2 * m)) + 0.0003 * sin(Math.toRadians(3 * m))
        val lambda = normalizeDegrees(m + c + 180.0 + 102.9372)

        val jTransit = 2451545.0 + jStar + 0.0053 * sin(Math.toRadians(m)) - 0.0069 * sin(Math.toRadians(2 * lambda))
        val sinDelta = sin(Math.toRadians(lambda)) * sin(Math.toRadians(23.44))
        val delta = Math.toDegrees(asin(sinDelta))

        fun hourAngle(altitude: Double): Double? {
            val cosOmega = (sin(Math.toRadians(altitude)) - sin(Math.toRadians(lat)) * sin(Math.toRadians(delta))) /
                    (cos(Math.toRadians(lat)) * cos(Math.toRadians(delta)))
            if (cosOmega < -1.0 || cosOmega > 1.0) return null
            return Math.toDegrees(acos(cosOmega))
        }

        val omegaSun = hourAngle(-0.833) ?: 90.0
        val omegaDawn = hourAngle(-17.7) ?: 108.0 // Astronomical dawn / Fajr
        val omegaMaghrib = hourAngle(-4.5) ?: 94.0 // Maghrib twilight

        val solarNoonHours = ((jTransit - jdn + 0.5) * 24.0 + tz) % 24.0
        val sunriseHours = solarNoonHours - (omegaSun / 15.0)
        val sunsetHours = solarNoonHours + (omegaSun / 15.0)
        val dawnHours = solarNoonHours - (omegaDawn / 15.0)
        val maghribHours = solarNoonHours + (omegaMaghrib / 15.0)
        val midnightHours = (sunsetHours + (dawnHours + 24.0 - sunsetHours) / 2.0) % 24.0

        val dayMinutes = round((sunsetHours - sunriseHours) * 60.0).toInt().coerceAtLeast(0)
        val nightMinutes = (24 * 60) - dayMinutes

        return SolarTimes(
            dawn = formatHoursToTime(dawnHours),
            sunrise = formatHoursToTime(sunriseHours),
            noon = formatHoursToTime(solarNoonHours),
            sunset = formatHoursToTime(sunsetHours),
            maghrib = formatHoursToTime(maghribHours),
            midnight = formatHoursToTime(midnightHours),
            dayLengthFormatted = "${dayMinutes / 60} ساعت و ${dayMinutes % 60} دقیقه",
            nightLengthFormatted = "${nightMinutes / 60} ساعت و ${nightMinutes % 60} دقیقه"
        )
    }

    private fun normalizeDegrees(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0) d += 360.0
        return d
    }

    private fun formatHoursToTime(hours: Double): String {
        var h = hours % 24.0
        if (h < 0) h += 24.0
        val hour = h.toInt()
        val minute = ((h - hour) * 60.0).roundToInt()
        val adjustedHour = if (minute == 60) (hour + 1) % 24 else hour
        val adjustedMinute = if (minute == 60) 0 else minute
        return String.format("%02d:%02d", adjustedHour, adjustedMinute)
    }
}
