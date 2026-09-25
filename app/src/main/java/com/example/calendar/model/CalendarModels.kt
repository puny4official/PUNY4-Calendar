package com.example.calendar.model

enum class AppLanguage(val code: String, val titlePersian: String, val titleEnglish: String, val flagEmoji: String) {
    PERSIAN("fa", "فارسی", "Persian", "🇮🇷"),
    ENGLISH("en", "انگلیسی", "English", "🇺🇸");

    fun displayName(currentLang: AppLanguage): String = when (this) {
        PERSIAN -> if (currentLang == PERSIAN) "فارسی (Persian)" else "Persian (فارسی)"
        ENGLISH -> if (currentLang == PERSIAN) "انگلیسی (English)" else "English (انگلیسی)"
    }
}

enum class CalendarType(val titlePersian: String, val titleEnglish: String) {
    SOLAR_HIJRI("هجری شمسی", "Solar Hijri"),
    GREGORIAN("میلادی", "Gregorian"),
    LUNAR_HIJRI("هجری قمری", "Lunar Hijri")
}

data class JalaliDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = String.format("%04d/%02d/%02d", year, month, day)
}

data class GregorianDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = String.format("%04d-%02d-%02d", year, month, day)
}

data class IslamicDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = String.format("%04d/%02d/%02d", year, month, day)
}

enum class MoonPhaseType(val titlePersian: String, val titleEnglish: String) {
    NEW_MOON("ماه نو (محاق)", "New Moon"),
    WAXING_CRESCENT("هلال افزاینده", "Waxing Crescent"),
    FIRST_QUARTER("تربیع اول (یک‌چهارم)", "First Quarter"),
    WAXING_GIBBOUS("تحدب فزاینده", "Waxing Gibbous"),
    FULL_MOON("بدر کامل (ماه شب چهارده)", "Full Moon"),
    WANING_GIBBOUS("تحدب کاهنده", "Waning Gibbous"),
    THIRD_QUARTER("تربیع دوم (آخر)", "Third Quarter"),
    WANING_CRESCENT("هلال کاهنده", "Waning Crescent")
}

data class MoonInfo(
    val phaseType: MoonPhaseType,
    val illuminationPercent: Int,
    val ageDays: Double,
    val phaseAngleDegrees: Double,
    val moonZodiacName: String,
    val moonZodiacDegree: Double
)

enum class ZodiacElement(val titlePersian: String, val emoji: String, val nature: String) {
    FIRE("آتش", "🔥", "طبع گرم و خشک - شور، اراده و پویایی"),
    EARTH("خاک", "🌍", "طبع سرد و خشک - پایداری، بردباری و استواری"),
    AIR("باد (هوا)", "💨", "طبع گرم و تر - اندیشه، ارتباطات و آزادی"),
    WATER("آب", "💧", "طبع سرد و تر - احساس، شهود و آرامش")
}

data class YearAnimal(
    val namePersian: String,
    val nameAlternative: String,
    val emoji: String,
    val characteristics: String
)

data class ZodiacSign(
    val namePersian: String,
    val nameArabic: String,
    val nameEnglish: String,
    val symbol: String,
    val element: ZodiacElement,
    val rulingPlanet: String,
    val description: String
)

data class QamarDarAqrabStatus(
    val isInTropicalScorpio: Boolean,  // برج عقرب
    val isInSiderealScorpio: Boolean,  // صورت فلکی عقرب
    val moonLongitudeDegrees: Double,
    val statusSummary: String,
    val detailedAdvice: String
)

data class PlanetaryRuler(
    val dayOfWeekPersian: String,
    val planetNamePersian: String,
    val planetNameEnglish: String,
    val nature: String
)

data class SolarTimes(
    val dawn: String,
    val sunrise: String,
    val noon: String,
    val sunset: String,
    val maghrib: String,
    val midnight: String,
    val dayLengthFormatted: String,
    val nightLengthFormatted: String
)

enum class EventType {
    NATIONAL,
    RELIGIOUS,
    ASTRONOMY,
    INTERNATIONAL,
    PERSONAL
}

data class CalendarEvent(
    val title: String,
    val isHoliday: Boolean = false,
    val type: EventType = EventType.NATIONAL,
    val description: String = ""
)

data class CityLocation(
    val id: String,
    val namePersian: String,
    val nameEnglish: String = namePersian,
    val latitude: Double,
    val longitude: Double,
    val timezoneHours: Double = 3.5
) {
    fun name(lang: AppLanguage): String = if (lang == AppLanguage.PERSIAN) namePersian else nameEnglish
}

data class FullDayInfo(
    val gregorianDate: GregorianDate,
    val jalaliDate: JalaliDate,
    val islamicDate: IslamicDate,
    val dayOfWeekPersian: String,
    val dayOfWeekArabic: String,
    val dayOfWeekEnglish: String,
    val dayOfYearJalali: Int,
    val daysRemainingJalali: Int,
    val weekOfYearJalali: Int,
    val seasonPersian: String,
    val events: List<CalendarEvent>,
    val moonInfo: MoonInfo,
    val sunZodiac: ZodiacSign,
    val qamarDarAqrab: QamarDarAqrabStatus,
    val planetaryRuler: PlanetaryRuler,
    val solarTimes: SolarTimes,
    val isToday: Boolean,
    val yearAnimal: YearAnimal? = null,
    val monthElement: ZodiacElement? = null
)
