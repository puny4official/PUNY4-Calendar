package com.example.calendar.model

import androidx.compose.runtime.Immutable

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
    LUNAR_HIJRI("هجری قمری", "Lunar Hijri"),
    IMPERIAL("شاهنشاهی (باستان)", "Imperial (Ancient)")
}

/** Imperial Iranian (Shahanshahi) year = Solar Hijri year + 1180; months and days are identical to the Solar Hijri calendar. */
const val IMPERIAL_YEAR_OFFSET = 1180

/** Ancient Iranian (Avestan) month names, used only in the Imperial (Ancient) calendar. */
val ANCIENT_MONTH_NAMES = listOf(
    "فْرَوَشی", "اَشا-وَهیشتَ", "هَئوروَتات", "تیشْتْریه", "اَمِرِتات", "خْشَتْرَ-وَئیریه",
    "میثْرَ", "اَپَم", "آتَر", "دَثوش", "وُهومَنه", "سْپِنتَ آرمَئیتی"
)

@Immutable
data class JalaliDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = String.format("%04d/%02d/%02d", year, month, day)
}

@Immutable
data class GregorianDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = String.format("%04d-%02d-%02d", year, month, day)
}

@Immutable
data class IslamicDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String = String.format("%04d/%02d/%02d", year, month, day)
}

enum class MoonPhaseType(val titlePersian: String, val titleEnglish: String, val emoji: String) {
    NEW_MOON("ماه نو (محاق)", "New Moon", "🌑"),
    WAXING_CRESCENT("هلال افزاینده", "Waxing Crescent", "🌒"),
    FIRST_QUARTER("تربیع اول (یک‌چهارم)", "First Quarter", "🌓"),
    WAXING_GIBBOUS("تحدب فزاینده", "Waxing Gibbous", "🌔"),
    FULL_MOON("بدر کامل (ماه شب چهارده)", "Full Moon", "🌕"),
    WANING_GIBBOUS("تحدب کاهنده", "Waning Gibbous", "🌖"),
    THIRD_QUARTER("تربیع دوم (آخر)", "Third Quarter", "🌗"),
    WANING_CRESCENT("هلال کاهنده", "Waning Crescent", "🌘")
}

@Immutable
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

@Immutable
data class YearAnimal(
    val namePersian: String,
    val nameAlternative: String,
    val emoji: String,
    val characteristics: String
)

@Immutable
data class ZodiacSign(
    val namePersian: String,
    val nameArabic: String,
    val nameEnglish: String,
    val symbol: String,
    val element: ZodiacElement,
    val rulingPlanet: String,
    val description: String
)

@Immutable
data class QamarDarAqrabStatus(
    val isInTropicalScorpio: Boolean,  // برج عقرب
    val isInSiderealScorpio: Boolean,  // صورت فلکی عقرب
    val moonLongitudeDegrees: Double,
    val statusSummary: String,
    val detailedAdvice: String
)

@Immutable
data class PlanetaryRuler(
    val dayOfWeekPersian: String,
    val planetNamePersian: String,
    val planetNameEnglish: String,
    val nature: String
)

@Immutable
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

@Immutable
data class CalendarEvent(
    val title: String,
    val isHoliday: Boolean = false,
    val type: EventType = EventType.NATIONAL,
    val description: String = ""
)

@Immutable
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

@Immutable
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
    val seasonEmoji: String = "🍂",
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

enum class CalendarCategory(val titleFa: String, val titleEn: String) {
    ALL("همه", "All"),
    HOLIDAYS("تعطیلات", "Holidays"),
    SOLAR("شمسی", "Solar"),
    ISLAMIC("قمری / مذهبی", "Islamic"),
    GLOBAL("بین‌المللی", "Global"),
    ASTRONOMICAL("نجومی", "Astronomical")
}

data class SearchableCalendarEvent(
    val title: String,
    val isHoliday: Boolean,
    val eventType: EventType,
    val calendarCategory: CalendarCategory,
    val month: Int,
    val day: Int,
    val formattedDate: String
)


/** Ancient Iranian weekday names (Saturday first), used only in the Imperial (Ancient) calendar. */
val ANCIENT_WEEKDAY_NAMES = listOf(
    "کِیوان‌شید", "مِهرشید", "مَه‌شید", "بَهرام‌شید", "تیرشید", "هُرمُزشید", "آدینه"
)

/** "تیرشید (چهارشنبه)": ancient name followed by the usual name in parentheses. */
fun ancientWeekdayLabel(persianName: String, persianNames: List<String>): String {
    val idx = persianNames.indexOf(persianName)
    return if (idx in ANCIENT_WEEKDAY_NAMES.indices) "${ANCIENT_WEEKDAY_NAMES[idx]} ($persianName)" else persianName
}

/** Ancient Iranian names of the 30 days of the month with their meaning (index 0 = day 1). */
val ANCIENT_DAY_NAMES: List<Pair<String, String>> = listOf(
    "اُرمَزد" to "بزرگداشت اهورامزدا و خورشید",
    "بَهْمَن" to "پاسداشت منش نیک و خردمندی",
    "اَردیبِهِشت" to "گرامیداشت راستی، پاکی و نگهبانی از آتش",
    "شَهریوَر" to "ستایش شهریاری بر نفس و دادگری",
    "سِپَندارمَذ" to "بزرگداشت زن و زمین، نماد عشق و باروری و فروتنی",
    "خُرداد" to "پاسداشت آب، تندرستی و رسایی",
    "اَمُرداد" to "ستایش جاودانگی، زندگی و پاسداشت طبیعت و زمین",
    "دِی بِه آذَر" to "نیایش خداوند و گرامیداشت آتش",
    "آذَر" to "بزرگداشت آتش و روشنایی",
    "آبان" to "ستایش آب به عنوان منبع حیات و برکت",
    "خور" to "روز تولد خورشید",
    "ماه" to "ستایش ماه و نور آسمانی",
    "تیر" to "گرامیداشت باران و پیروزی بر خشکسالی",
    "گوش" to "نماد شنوایی و نگهبانی از چهارپایان",
    "دِی بِه مِهر" to "نیایش آفریدگار و مهربانی",
    "مِهر" to "پاسداشت مهر و پیروزی فریدون بر ضحاک",
    "سُروش" to "فرمانبرداری و نگهبانی از راستی و نظم جهان",
    "رَشْن" to "پاسداشت دادگری و راستی",
    "فَروَردین" to "ستایش ارواح نگهبان و فروهر",
    "بَهرام" to "ستایش پیروزی و کامیابی",
    "رام" to "ستایش شادی و آرامش و خوشی",
    "باد" to "ستایش باد و نماد جریان و حرکت",
    "دِی بِه دین" to "ستایش آفریدگار و وجدان",
    "دین" to "ستایش وجدان، بینش و راستی",
    "اَرد" to "ستایش خوشبختی، ثروت و برکت",
    "اَشتاد" to "ستایش راستی و درستی",
    "آسمان" to "ستایش آسمان به عنوان نماد بلندی و فراخی",
    "زامیاد" to "نماد استواری و بخشندگی",
    "مانترِه سِپَند" to "ستایش گفتار مقدس و نیایش پاک",
    "اَنارام" to "ستایش فروغ و روشنایی بی‌پایان"
)
