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

    private val BASE_CITIES = listOf(
        CityLocation("tehran", "تهران", "Tehran", 35.6892, 51.3890, 3.5),
        CityLocation("mashhad", "مشهد", "Mashhad", 36.2605, 59.6168, 3.5),
        CityLocation("isfahan", "اصفهان", "Isfahan", 32.6546, 51.6680, 3.5),
        CityLocation("karaj", "کرج", "Karaj", 35.8400, 50.9391, 3.5),
        CityLocation("shiraz", "شیراز", "Shiraz", 29.5918, 52.5837, 3.5),
        CityLocation("tabriz", "تبریز", "Tabriz", 38.0800, 46.2919, 3.5),
        CityLocation("qom", "قم", "Qom", 34.6401, 50.8764, 3.5),
        CityLocation("ahvaz", "اهواز", "Ahvaz", 31.3183, 48.6706, 3.5),
        CityLocation("kermanshah", "کرمانشاه", "Kermanshah", 34.3277, 47.0778, 3.5),
        CityLocation("urmia", "ارومیه", "Urmia", 37.5527, 45.0761, 3.5),
        CityLocation("rasht", "رشت", "Rasht", 37.2809, 49.5832, 3.5),
        CityLocation("zahedan", "زاهدان", "Zahedan", 29.4963, 60.8629, 3.5),
        CityLocation("hamedan", "همدان", "Hamedan", 34.7989, 48.5150, 3.5),
        CityLocation("kerman", "کرمان", "Kerman", 30.2839, 57.0834, 3.5),
        CityLocation("yazd", "یزد", "Yazd", 31.8974, 54.3569, 3.5),
        CityLocation("ardabil", "اردبیل", "Ardabil", 38.2498, 48.2933, 3.5),
        CityLocation("bandarabbas", "بندرعباس", "Bandar Abbas", 27.1832, 56.2666, 3.5),
        CityLocation("arak", "اراک", "Arak", 34.0917, 49.6892, 3.5),
        CityLocation("zanjan", "زنجان", "Zanjan", 36.6736, 48.4787, 3.5),
        CityLocation("sanandaj", "سنندج", "Sanandaj", 35.3144, 46.9961, 3.5),
        CityLocation("qazvin", "قزوین", "Qazvin", 36.2688, 50.0041, 3.5),
        CityLocation("khorramabad", "خرم‌آباد", "Khorramabad", 33.4878, 48.3558, 3.5),
        CityLocation("gorgan", "گرگان", "Gorgan", 36.8427, 54.4439, 3.5),
        CityLocation("sari", "ساری", "Sari", 36.5659, 53.0586, 3.5),
        CityLocation("bushehr", "بوشهر", "Bushehr", 28.9234, 50.8203, 3.5),
        CityLocation("birjand", "بیرجند", "Birjand", 32.8663, 59.2211, 3.5),
        CityLocation("bojnord", "بجنورد", "Bojnord", 37.4747, 57.3290, 3.5),
        CityLocation("semnan", "سمنان", "Semnan", 35.5769, 53.3970, 3.5),
        CityLocation("ilam", "ایلام", "Ilam", 33.6374, 46.4227, 3.5),
        CityLocation("shahrekord", "شهرکرد", "Shahrekord", 32.3256, 50.8644, 3.5),
        CityLocation("yasuj", "یاسوج", "Yasuj", 30.6684, 51.5876, 3.5),
        CityLocation("kashan", "کاشان", "Kashan", 33.9850, 51.4100, 3.5),
        CityLocation("kish", "کیش", "Kish", 26.5578, 54.0194, 3.5),
        CityLocation("qeshm", "قشم", "Qeshm", 26.9585, 56.2718, 3.5),
        CityLocation("abadan", "آبادان", "Abadan", 30.3392, 48.3043, 3.5),
        CityLocation("abadeh", "آباده", "Abadeh", 31.1608, 52.6506, 3.5),
        CityLocation("abhar", "ابهر", "Abhar", 36.1467, 49.2178, 3.5),
        CityLocation("abyek", "آبیک", "Abyek", 36.0367, 50.5267, 3.5),
        CityLocation("ahar", "اهر", "Ahar", 38.4774, 47.0678, 3.5),
        CityLocation("aligudarz", "الیگودرز", "Aligudarz", 33.4000, 49.6900, 3.5),
        CityLocation("amol", "آمل", "Amol", 36.4696, 52.3507, 3.5),
        CityLocation("andimeshk", "اندیمشک", "Andimeshk", 32.4591, 48.3549, 3.5),
        CityLocation("ardakan", "اردکان", "Ardakan", 32.3100, 54.0200, 3.5),
        CityLocation("ardestan", "اردستان", "Ardestan", 33.3800, 52.3700, 3.5),
        CityLocation("astara", "آستارا", "Astara", 38.4294, 48.8745, 3.5),
        CityLocation("azadshahr", "آزادشهر", "Azadshahr", 37.0872, 55.1731, 3.5),
        CityLocation("babol", "بابل", "Babol", 36.5513, 52.6788, 3.5),
        CityLocation("babolsar", "بابلسر", "Babolsar", 36.7025, 52.6557, 3.5),
        CityLocation("baft", "بافت", "Baft", 29.2333, 56.6000, 3.5),
        CityLocation("bam", "بم", "Bam", 29.1061, 58.3570, 3.5),
        CityLocation("bandaranzali", "بندر انزلی", "Bandar Anzali", 37.4727, 49.4631, 3.5),
        CityLocation("bandarlengeh", "بندر لنگه", "Bandar Lengeh", 26.5579, 54.8807, 3.5),
        CityLocation("bandarmahshahr", "بندر ماهشهر", "Bandar Mahshahr", 30.5589, 49.1981, 3.5),
        CityLocation("bandartorkaman", "بندر ترکمن", "Bandar Torkaman", 36.9000, 54.0700, 3.5),
        CityLocation("behbahan", "بهبهان", "Behbahan", 30.5959, 50.2417, 3.5),
        CityLocation("behshahr", "بهشهر", "Behshahr", 36.6923, 53.5527, 3.5),
        CityLocation("bijar", "بیجار", "Bijar", 35.8667, 47.6000, 3.5),
        CityLocation("bukan", "بوکان", "Bukan", 36.5211, 46.2089, 3.5),
        CityLocation("borazjan", "برازجان", "Borazjan", 29.2667, 51.2167, 3.5),
        CityLocation("borujen", "بروجن", "Borujen", 31.9667, 51.2833, 3.5),
        CityLocation("borujerd", "بروجرد", "Borujerd", 33.8974, 48.7506, 3.5),
        CityLocation("bostanabad", "بستان‌آباد", "Bostanabad", 37.8528, 46.8353, 3.5),
        CityLocation("chabahar", "چابهار", "Chabahar", 25.2919, 60.6430, 3.5),
        CityLocation("chalus", "چالوس", "Chalus", 36.6556, 51.4207, 3.5),
        CityLocation("damavand", "دماوند", "Damavand", 35.7164, 52.0653, 3.5),
        CityLocation("damghan", "دامغان", "Damghan", 36.1683, 54.3481, 3.5),
        CityLocation("darab", "داراب", "Darab", 28.7519, 54.5444, 3.5),
        CityLocation("dehdasht", "دهدشت", "Dehdasht", 30.7936, 50.5644, 3.5),
        CityLocation("dezful", "دزفول", "Dezful", 32.3811, 48.4058, 3.5),
        CityLocation("divandarreh", "دیواندره", "Divandarreh", 35.9139, 47.0211, 3.5),
        CityLocation("gachsaran", "گچساران", "Gachsaran", 30.3586, 50.7983, 3.5),
        CityLocation("dorud", "دورود", "Dorud", 33.4953, 49.0617, 3.5),
        CityLocation("eslamshahr", "اسلامشهر", "Eslamshahr", 35.5522, 51.2353, 3.5),
        CityLocation("eqlid", "اقلید", "Eqlid", 30.9000, 52.6000, 3.5),
        CityLocation("estahban", "استهبان", "Estahban", 29.1267, 54.0422, 3.5),
        CityLocation("falavarjan", "فلاورجان", "Falavarjan", 32.5547, 51.5217, 3.5),
        CityLocation("fasa", "فسا", "Fasa", 28.9383, 53.6483, 3.5),
        CityLocation("fereydunshahr", "فریدونشهر", "Fereydunshahr", 32.9400, 50.1200, 3.5),
        CityLocation("firuzabad", "فیروزآباد", "Firuzabad", 28.8439, 52.5708, 3.5),
        CityLocation("firuzkuh", "فیروزکوه", "Firuzkuh", 35.7561, 52.7797, 3.5),
        CityLocation("garmsar", "گرمسار", "Garmsar", 35.2183, 52.3400, 3.5),
        CityLocation("gonbadekavus", "گنبد کاووس", "Gonbad-e Kavus", 37.2500, 55.1675, 3.5),
        CityLocation("gonabad", "گناباد", "Gonabad", 34.3528, 58.6836, 3.5),
        CityLocation("hashtgerd", "هشتگرد", "Hashtgerd", 35.9631, 50.6772, 3.5),
        CityLocation("hashtrud", "هشترود", "Hashtrud", 37.4778, 47.0508, 3.5),
        CityLocation("hendijan", "هندیجان", "Hendijan", 30.2386, 49.7131, 3.5),
        CityLocation("iranshahr", "ایرانشهر", "Iranshahr", 27.2025, 60.6848, 3.5),
        CityLocation("izeh", "ایذه", "Izeh", 31.8341, 49.8671, 3.5),
        CityLocation("jahrom", "جهرم", "Jahrom", 28.5000, 53.5603, 3.5),
        CityLocation("jask", "جاسک", "Jask", 25.6419, 57.7725, 3.5),
        CityLocation("jiroft", "جیرفت", "Jiroft", 28.6772, 57.7403, 3.5),
        CityLocation("jolfa", "جلفا", "Jolfa", 38.9411, 45.6306, 3.5),
        CityLocation("kamyaran", "کامیاران", "Kamyaran", 34.7956, 46.9356, 3.5),
        CityLocation("kangan", "کنگان", "Kangan", 27.8386, 52.0647, 3.5),
        CityLocation("kashmar", "کاشمر", "Kashmar", 35.2383, 58.4656, 3.5),
        CityLocation("kazerun", "کازرون", "Kazerun", 29.6194, 51.6542, 3.5),
        CityLocation("khalkhal", "خلخال", "Khalkhal", 37.6189, 48.5258, 3.5),
        CityLocation("khash", "خاش", "Khash", 28.2211, 61.2158, 3.5),
        CityLocation("khomeyn", "خمین", "Khomeyn", 33.6422, 50.0769, 3.5),
        CityLocation("khomeinishahr", "خمینی‌شهر", "Khomeini Shahr", 32.6833, 51.4833, 3.5),
        CityLocation("khoy", "خوی", "Khoy", 38.5503, 44.9521, 3.5),
        CityLocation("kuhdasht", "کوهدشت", "Kuhdasht", 33.5333, 47.6000, 3.5),
        CityLocation("lahijan", "لاهیجان", "Lahijan", 37.2078, 50.0042, 3.5),
        CityLocation("langarud", "لنگرود", "Langarud", 37.1969, 50.1539, 3.5),
        CityLocation("lar", "لار", "Lar", 27.6811, 54.3392, 3.5),
        CityLocation("mahabad", "مهاباد", "Mahabad", 36.7631, 45.7222, 3.5),
        CityLocation("mahallat", "محلات", "Mahallat", 33.9114, 50.4544, 3.5),
        CityLocation("malayer", "ملایر", "Malayer", 34.2969, 48.8236, 3.5),
        CityLocation("malekan", "ملکان", "Malekan", 37.1425, 46.1031, 3.5),
        CityLocation("manjil", "منجیل", "Manjil", 36.7436, 49.4017, 3.5),
        CityLocation("maragheh", "مراغه", "Maragheh", 37.3894, 46.2383, 3.5),
        CityLocation("marand", "مرند", "Marand", 38.4329, 45.7744, 3.5),
        CityLocation("marvdasht", "مرودشت", "Marvdasht", 29.8742, 52.8025, 3.5),
        CityLocation("masjedsoleyman", "مسجد سلیمان", "Masjed Soleyman", 31.9364, 49.3033, 3.5),
        CityLocation("mehran", "مهران", "Mehran", 33.1222, 46.1642, 3.5),
        CityLocation("meshkinshahr", "مشگین‌شهر", "Meshkinshahr", 38.3989, 47.6811, 3.5),
        CityLocation("meybod", "میبد", "Meybod", 32.2503, 54.0167, 3.5),
        CityLocation("miandoab", "میاندوآب", "Miandoab", 36.9694, 46.1028, 3.5),
        CityLocation("mianeh", "میانه", "Mianeh", 37.4211, 47.7153, 3.5),
        CityLocation("minab", "میناب", "Minab", 27.1467, 57.0794, 3.5),
        CityLocation("naein", "نایین", "Naein", 32.8597, 53.0886, 3.5),
        CityLocation("najafabad", "نجف‌آباد", "Najafabad", 32.6344, 51.3669, 3.5),
        CityLocation("nahavand", "نهاوند", "Nahavand", 34.1886, 48.3719, 3.5),
        CityLocation("neka", "نکا", "Neka", 36.6500, 53.3000, 3.5),
        CityLocation("neyriz", "نیریز", "Neyriz", 29.2000, 54.3000, 3.5),
        CityLocation("neyshabur", "نیشابور", "Neyshabur", 36.2133, 58.7961, 3.5),
        CityLocation("nowshahr", "نوشهر", "Nowshahr", 36.6486, 51.4964, 3.5),
        CityLocation("nurabad", "نورآباد", "Nurabad", 34.0733, 47.9728, 3.5),
        CityLocation("paveh", "پاوه", "Paveh", 35.0431, 46.3569, 3.5),
        CityLocation("parsabad", "پارس‌آباد", "Parsabad", 39.6482, 47.9174, 3.5),
        CityLocation("pakdasht", "پاکدشت", "Pakdasht", 35.4672, 51.6836, 3.5),
        CityLocation("piranshahr", "پیرانشهر", "Piranshahr", 36.7000, 45.1472, 3.5),
        CityLocation("qaemshahr", "قائم‌شهر", "Qaemshahr", 36.4636, 52.8608, 3.5),
        CityLocation("qasreshirin", "قصرشیرین", "Qasr-e Shirin", 34.5156, 45.5783, 3.5),
        CityLocation("qarchak", "قرچک", "Qarchak", 35.4283, 51.5794, 3.5),
        CityLocation("ramhormoz", "رامهرمز", "Ramhormoz", 31.2803, 49.6036, 3.5),
        CityLocation("ramsar", "رامسر", "Ramsar", 36.9031, 50.6592, 3.5),
        CityLocation("rafsanjan", "رفسنجان", "Rafsanjan", 30.4067, 55.9939, 3.5),
        CityLocation("robatkarim", "رباط کریم", "Robat Karim", 35.4847, 51.0828, 3.5),
        CityLocation("sabzevar", "سبزوار", "Sabzevar", 36.2126, 57.6819, 3.5),
        CityLocation("saqqez", "سقز", "Saqqez", 36.2499, 46.2735, 3.5),
        CityLocation("salmas", "سلماس", "Salmas", 38.1972, 44.7653, 3.5),
        CityLocation("semirom", "سمیرم", "Semirom", 31.4156, 51.5694, 3.5),
        CityLocation("shahinshahr", "شاهین‌شهر", "Shahin Shahr", 32.8606, 51.5517, 3.5),
        CityLocation("shahrebabak", "شهربابک", "Shahr-e Babak", 30.1167, 55.1167, 3.5),
        CityLocation("shahriar", "شهریار", "Shahriar", 35.6597, 51.0592, 3.5),
        CityLocation("shahroud", "شاهرود", "Shahroud", 36.4182, 54.9763, 3.5),
        CityLocation("shush", "شوش", "Shush", 32.1942, 48.2436, 3.5),
        CityLocation("shushtar", "شوشتر", "Shushtar", 32.0453, 48.8569, 3.5),
        CityLocation("sirjan", "سیرجان", "Sirjan", 29.4522, 55.6811, 3.5),
        CityLocation("sonqor", "سنقر", "Sonqor", 34.7833, 47.6000, 3.5),
        CityLocation("susangerd", "سوسنگرد", "Susangerd", 31.5603, 48.1806, 3.5),
        CityLocation("tafresh", "تفرش", "Tafresh", 34.6944, 50.0186, 3.5),
        CityLocation("takab", "تکاب", "Takab", 36.4000, 47.1133, 3.5),
        CityLocation("tabas", "طبس", "Tabas", 33.5958, 56.9244, 3.5),
        CityLocation("taybad", "تایباد", "Taybad", 34.7400, 60.7800, 3.5),
        CityLocation("torbateheydarieh", "تربت حیدریه", "Torbat-e Heydarieh", 35.2740, 59.2194, 3.5),
        CityLocation("torbatejam", "تربت جام", "Torbat-e Jam", 35.2442, 60.6225, 3.5),
        CityLocation("tonekabon", "تنکابن", "Tonekabon", 36.8164, 50.8736, 3.5),
        CityLocation("varamin", "ورامین", "Varamin", 35.3242, 51.6472, 3.5),
        CityLocation("zabol", "زابل", "Zabol", 31.0287, 61.5011, 3.5),
        CityLocation("zarand", "زرند", "Zarand", 30.8128, 56.5644, 3.5),
        CityLocation("zarrinshahr", "زرین‌شهر", "Zarrinshahr", 32.3833, 51.3667, 3.5),
        CityLocation("sarab", "سراب", "Sarab", 37.9408, 47.5361, 3.5),
        CityLocation("shabestar", "شبستر", "Shabestar", 38.1806, 45.7058, 3.5),
        CityLocation("bonab", "بناب", "Bonab", 37.3400, 46.0561, 3.5),
        CityLocation("sarpolezahab", "سرپل ذهاب", "Sarpol-e Zahab", 34.4614, 45.8628, 3.5),
        CityLocation("qorveh", "قروه", "Qorveh", 35.1667, 47.8000, 3.5),
        CityLocation("delijan", "دلیجان", "Delijan", 33.9908, 50.6808, 3.5),
        CityLocation("natanz", "نطنز", "Natanz", 33.5133, 51.9156, 3.5),
        CityLocation("golpayegan", "گلپایگان", "Golpayegan", 33.4539, 50.2883, 3.5),
        CityLocation("khansar", "خوانسار", "Khansar", 33.2186, 50.3156, 3.5),
        CityLocation("lordegan", "لردگان", "Lordegan", 31.5103, 50.8247, 3.5),
        CityLocation("taft", "تفت", "Taft", 31.7456, 54.2131, 3.5),
        CityLocation("bardsir", "بردسیر", "Bardsir", 29.9267, 56.5722, 3.5),
        CityLocation("anar", "انار", "Anar", 30.8833, 55.2667, 3.5),
        CityLocation("bavanat", "بوانات", "Bavanat", 30.1000, 53.4000, 3.5),
        CityLocation("qir", "قیر", "Qir", 28.4833, 53.0333, 3.5),
        CityLocation("khonj", "خنج", "Khonj", 27.8872, 53.4333, 3.5),
        CityLocation("gerash", "گراش", "Gerash", 27.6667, 54.1333, 3.5),
        CityLocation("bastak", "بستک", "Bastak", 27.1986, 54.3664, 3.5),
        CityLocation("hajiabad", "حاجی‌آباد هرمزگان", "Hajiabad", 28.3167, 55.9000, 3.5),
        CityLocation("rudan", "رودان", "Rudan", 27.4411, 57.1928, 3.5),
        CityLocation("nikshahr", "نیکشهر", "Nikshahr", 26.2269, 60.2142, 3.5),
        CityLocation("sarbaz", "سرباز", "Sarbaz", 26.6333, 61.2556, 3.5),
        CityLocation("konarak", "کنارک", "Konarak", 25.3597, 60.3797, 3.5),
        CityLocation("saravan", "سراوان", "Saravan", 27.3700, 62.3333, 3.5),
        CityLocation("khaf", "خواف", "Khaf", 34.5744, 60.1403, 3.5),
        CityLocation("sarakhs", "سرخس", "Sarakhs", 36.5458, 61.1583, 3.5),
        CityLocation("quchan", "قوچان", "Quchan", 37.1061, 58.5097, 3.5),
        CityLocation("fariman", "فریمان", "Fariman", 35.7056, 59.8486, 3.5),
        CityLocation("chenaran", "چناران", "Chenaran", 36.6444, 59.1214, 3.5),
        CityLocation("esfarayen", "اسفراین", "Esfarayen", 37.0756, 57.5106, 3.5),
        CityLocation("shirvan", "شیروان", "Shirvan", 37.4092, 57.9294, 3.5),
        CityLocation("bajestan", "بجستان", "Bajestan", 34.5192, 58.1839, 3.5),
        CityLocation("ferdows", "فردوس", "Ferdows", 34.0189, 58.1722, 3.5),
        CityLocation("genaveh", "گناوه", "Genaveh", 29.5792, 50.5172, 3.5),
        CityLocation("khormuj", "خورموج", "Khormuj", 28.6561, 51.3783, 3.5),
        CityLocation("deylam", "دیلم", "Deylam", 30.0553, 50.1625, 3.5),
        CityLocation("jam", "جم", "Jam", 27.8269, 52.3219, 3.5),
        CityLocation("asaluyeh", "عسلویه", "Asaluyeh", 27.4742, 52.6094, 3.5),
        CityLocation("dayyer", "دیر", "Dayyer", 27.8417, 51.9358, 3.5),
        CityLocation("gilanegharb", "گیلانغرب", "Gilan-e Gharb", 34.1422, 45.9206, 3.5),
        CityLocation("eslamabadegharb", "اسلام‌آباد غرب", "Eslamabad-e Gharb", 34.1094, 46.5278, 3.5),
        CityLocation("kangavar", "کنگاور", "Kangavar", 34.5042, 47.9653, 3.5),
        CityLocation("sahneh", "صحنه", "Sahneh", 34.4814, 47.6906, 3.5),
        CityLocation("harsin", "هرسین", "Harsin", 34.2711, 47.5864, 3.5),
        CityLocation("ravansar", "روانسر", "Ravansar", 34.7133, 46.6531, 3.5),
        CityLocation("javanrud", "جوانرود", "Javanrud", 34.8111, 46.4875, 3.5),
        CityLocation("marivan", "مریوان", "Marivan", 35.5197, 46.1764, 3.5),
        CityLocation("baneh", "بانه", "Baneh", 35.9975, 45.8853, 3.5),
        CityLocation("sardasht", "سردشت", "Sardasht", 36.1553, 45.4797, 3.5),
        CityLocation("oshnavieh", "اشنویه", "Oshnavieh", 37.0397, 45.0978, 3.5),
        CityLocation("naqadeh", "نقده", "Naqadeh", 36.9553, 45.3883, 3.5),
        CityLocation("chaldoran", "چالدران", "Chaldoran", 39.0644, 44.3844, 3.5),
        CityLocation("maku", "ماکو", "Maku", 39.2953, 44.5167, 3.5),
        CityLocation("shahindezh", "شاهین‌دژ", "Shahindezh", 36.6794, 46.5667, 3.5),
        CityLocation("khodabandeh", "خدابنده", "Khodabandeh", 36.1167, 48.5833, 3.5),
        CityLocation("mahneshan", "ماهنشان", "Mahneshan", 36.7444, 47.6722, 3.5),
        CityLocation("kabudarahang", "کبودرآهنگ", "Kabudarahang", 35.2056, 48.7194, 3.5),
        CityLocation("asadabad", "اسدآباد", "Asadabad", 34.7822, 48.1186, 3.5),
        CityLocation("tuyserkan", "تویسرکان", "Tuyserkan", 34.5517, 48.4472, 3.5),
        CityLocation("razan", "رزن", "Razan", 35.3833, 49.0333, 3.5),
        CityLocation("bahar", "بهار", "Bahar", 34.9086, 48.4442, 3.5),
        CityLocation("rudsar", "رودسر", "Rudsar", 37.1378, 50.2872, 3.5),
        CityLocation("talesh", "طوالش", "Talesh", 37.8000, 48.9000, 3.5),
        CityLocation("fuman", "فومن", "Fuman", 37.2233, 49.3144, 3.5),
        CityLocation("masal", "ماسال", "Masal", 37.3633, 49.1356, 3.5),
        CityLocation("siahkal", "سیاهکل", "Siahkal", 37.1561, 49.8689, 3.5),
        CityLocation("sowmeehsara", "صومعه‌سرا", "Sowme'eh Sara", 37.3103, 49.3200, 3.5),
        CityLocation("astanehyeashrafiyeh", "آستانه اشرفیه", "Astaneh-ye Ashrafiyeh", 37.2597, 49.9461, 3.5),
        CityLocation("rezvanshahr", "رضوانشهر", "Rezvanshahr", 37.5506, 49.1397, 3.5),
        CityLocation("aqqala", "آق‌قلا", "Aq Qala", 37.0153, 54.4547, 3.5),
        CityLocation("kordkuy", "کردکوی", "Kordkuy", 36.8000, 54.1000, 3.5),
        CityLocation("minudasht", "مینودشت", "Minudasht", 37.2292, 55.3731, 3.5),
        CityLocation("kalaleh", "کلاله", "Kalaleh", 37.3803, 55.4917, 3.5),
        CityLocation("fereydunkenar", "فریدونکنار", "Fereydunkenar", 36.6853, 52.5200, 3.5),
        CityLocation("mahmudabad", "محمودآباد", "Mahmudabad", 36.6342, 52.2650, 3.5),
        CityLocation("juybar", "جویبار", "Juybar", 36.6411, 52.9086, 3.5),
        CityLocation("nazarabad", "نظرآباد", "Nazarabad", 35.9548, 50.6086, 3.5),
        CityLocation("taleghan", "طالقان", "Taleghan", 36.1700, 50.7700, 3.5),
        CityLocation("saveh", "ساوه", "Saveh", 35.0213, 50.3566, 3.5),
        CityLocation("buinzahra", "بوئین‌زهرا", "Buin Zahra", 35.7667, 50.0500, 3.5),
        CityLocation("takestan", "تاکستان", "Takestan", 36.0694, 49.6958, 3.5),
        CityLocation("rey", "ری", "Rey", 35.5886, 51.4353, 3.5),
        CityLocation("lavasan", "لواسان", "Lavasan", 35.8186, 51.6444, 3.5),
        CityLocation("pardis", "پردیس", "Pardis", 35.7425, 51.7742, 3.5),
        CityLocation("malard", "ملارد", "Malard", 35.6658, 50.9767, 3.5),
        CityLocation("nasimshahr", "نسیم‌شهر", "Nasimshahr", 35.5436, 51.1550, 3.5),
        CityLocation("qods", "شهر قدس", "Qods", 35.7175, 51.1081, 3.5),
        CityLocation("parand", "پرند", "Parand", 35.4833, 50.9333, 3.5),
        CityLocation("malekshahi", "ملکشاهی", "Malekshahi", 33.3600, 46.5800, 3.5),
        CityLocation("abdanan", "آبدانان", "Abdanan", 32.9917, 47.4189, 3.5),
        CityLocation("dehloran", "دهلران", "Dehloran", 32.6942, 47.2678, 3.5),
        CityLocation("haftkel", "هفتکل", "Haftkel", 31.4500, 49.5667, 3.5),
        CityLocation("baghmalek", "باغ‌ملک", "Baghmalek", 31.5250, 49.8872, 3.5),
        CityLocation("lali", "لالی", "Lali", 32.3300, 49.0900, 3.5),
        CityLocation("sisakht", "سی‌سخت", "Sisakht", 30.8639, 51.4556, 3.5),
        CityLocation("dena", "دنا", "Dena", 30.9000, 51.4500, 3.5),
        CityLocation("pasargadae", "پاسارگاد", "Pasargadae", 30.2014, 53.1789, 3.5),
        CityLocation("ardakanfars", "اردکان فارس", "Ardakan Fars", 30.2744, 52.1550, 3.5),
        CityLocation("lamerd", "لامرد", "Lamerd", 27.3394, 53.1814, 3.5),
        CityLocation("mohr", "مهر", "Mohr", 27.5500, 52.9000, 3.5),
        CityLocation("farashband", "فراشبند", "Farashband", 28.8667, 52.0500, 3.5),
        CityLocation("sepidan", "سپیدان", "Sepidan", 30.2639, 51.9867, 3.5),
        CityLocation("mamasani", "ممسنی", "Mamasani", 30.2167, 51.5167, 3.5),
        CityLocation("kherameh", "خرامه", "Kherameh", 29.5000, 53.3000, 3.5),
        CityLocation("sarvestan", "سروستان", "Sarvestan", 29.2700, 53.2200, 3.5),
        CityLocation("qaen", "قائن", "Qaen", 33.7197, 59.1847, 3.5),
        CityLocation("nehbandan", "نهبندان", "Nehbandan", 31.5414, 60.0344, 3.5),
        CityLocation("sarbisheh", "سربیشه", "Sarbisheh", 32.5753, 59.7911, 3.5),
        CityLocation("darmian", "درمیان", "Darmian", 33.0200, 60.0200, 3.5),
        CityLocation("sarayan", "سرایان", "Sarayan", 33.8564, 58.5206, 3.5),
        CityLocation("boshruyeh", "بشرویه", "Boshruyeh", 33.8806, 57.4361, 3.5),
        CityLocation("anarak", "انارک", "Anarak", 33.3167, 53.7000, 3.5),
        CityLocation("khur", "خور و بیابانک", "Khur", 33.7750, 55.0850, 3.5),
        CityLocation("garmeh", "گرمه", "Garmeh", 33.3000, 56.3000, 3.5),
        CityLocation("jandaq", "جندق", "Jandaq", 34.0300, 54.4200, 3.5),
        CityLocation("aranvabidgol", "آران و بیدگل", "Aran va Bidgol", 34.0575, 51.4850, 3.5),
        CityLocation("borkhar", "برخوار", "Borkhar", 32.7500, 51.5500, 3.5),
        CityLocation("mobarakeh", "مبارکه", "Mobarakeh", 32.3436, 51.5042, 3.5),
        CityLocation("shahreza", "شهرضا", "Shahreza", 32.0092, 51.8625, 3.5),
        CityLocation("nikabad", "نیک‌آباد", "Nikabad", 32.4000, 51.8500, 3.5),
        CityLocation("varzaneh", "ورزنه", "Varzaneh", 32.4167, 52.6500, 3.5),
        CityLocation("badrud", "بادرود", "Badrud", 33.6600, 52.4800, 3.5),
        CityLocation("zavareh", "زواره", "Zavareh", 33.4800, 52.4800, 3.5),
        CityLocation("kuhpayeh", "کوهپایه", "Kuhpayeh", 32.7000, 52.4400, 3.5),
        CityLocation("chadegan", "چادگان", "Chadegan", 32.7700, 50.6300, 3.5),
        CityLocation("fereydan", "فریدن", "Fereydan", 33.0000, 50.1500, 3.5),
        CityLocation("tiran", "تیران", "Tiran", 32.7000, 51.1600, 3.5),
        CityLocation("karvan", "کرون", "Karvan", 32.5500, 51.0000, 3.5),
        CityLocation("dehaghan", "دهاقان", "Dehaghan", 31.9400, 51.6500, 3.5),
        CityLocation("kelishad", "کلیشاد", "Kelishad", 32.5000, 51.4000, 3.5),
        CityLocation("saman", "سامان", "Saman", 32.4500, 50.9200, 3.5),
        CityLocation("farsan", "فارسان", "Farsan", 32.2600, 50.5700, 3.5),
        CityLocation("kiar", "کیار", "Kiar", 32.2800, 50.8400, 3.5),
        CityLocation("ben", "بن", "Ben", 32.1900, 50.7300, 3.5),
        CityLocation("sudjan", "سودجان", "Sudjan", 32.1800, 50.6500, 3.5),
        CityLocation("hafshejan", "هفشجان", "Hafshejan", 32.3000, 50.7300, 3.5),
        CityLocation("ardal", "اردل", "Ardal", 31.9900, 50.6600, 3.5),
        CityLocation("koohrang", "کوهرنگ", "Koohrang", 32.4300, 50.1200, 3.5),
        CityLocation("jamkaran", "جمکران", "Jamkaran", 34.5858, 50.8947, 3.5),
        CityLocation("kahak", "کهک", "Kahak", 34.3917, 50.8625, 3.5),
        CityLocation("ashtian", "آشتیان", "Ashtian", 34.5236, 50.0072, 3.5),
        CityLocation("khondab", "خنداب", "Khondab", 34.3, 49.2, 3.5),
        CityLocation("komijan", "کمیجان", "Komijan", 34.7167, 49.3, 3.5),
        CityLocation("shazand", "شازند", "Shazand", 33.9333, 49.4, 3.5),
        CityLocation("zarandieh", "زرندیه", "Zarandieh", 35.3167, 50.4833, 3.5),
        CityLocation("farahan", "فراهان", "Farahan", 34.4, 49.7, 3.5),
        CityLocation("mamuniyeh", "ممونیه", "Mamuniyeh", 35.0833, 50.5, 3.5),
        CityLocation("bafq", "بافق", "Bafq", 31.6, 55.4, 3.5),
        CityLocation("mehriz", "مهریز", "Mehriz", 31.5919, 54.4306, 3.5),
        CityLocation("abarkuh", "ابرکوه", "Abarkuh", 31.1283, 53.2822, 3.5),
        CityLocation("ashkezar", "اشکذر", "Ashkezar", 31.9558, 54.2686, 3.5),
        CityLocation("behabad", "بهاباد", "Behabad", 31.8858, 56.1844, 3.5),
        CityLocation("heratyazd", "هرات یزد", "Herat Yazd", 30.0667, 54.2833, 3.5),
        CityLocation("sorkheh", "سرخه", "Sorkheh", 35.4639, 53.2167, 3.5),
        CityLocation("mehdishahr", "مهدی‌شهر", "Mehdishahr", 35.7, 53.35, 3.5),
        CityLocation("aradan", "آرادان", "Aradan", 35.25, 52.49, 3.5),
        CityLocation("meyami", "میامی", "Meyami", 36.4, 55.65, 3.5),
        CityLocation("eyvanekey", "ایوانکی", "Eyvanekey", 35.34, 52.06, 3.5),
        CityLocation("biarjomand", "بیارجمند", "Biarjomand", 36.1, 55.82, 3.5),
        CityLocation("eyvan", "ایوان", "Eyvan", 33.8271, 46.3082, 3.5),
        CityLocation("darrehshahr", "دره‌شهر", "Darreh Shahr", 33.1403, 47.3622, 3.5),
        CityLocation("chavar", "چوار", "Chavar", 33.7, 46.3, 3.5),
        CityLocation("badreh", "بدره", "Badreh", 33.0, 47.0, 3.5),
        CityLocation("azna", "ازنا", "Azna", 33.4586, 49.4586, 3.5),
        CityLocation("poledokhtar", "پلدختر", "Pol-e Dokhtar", 33.15, 47.7133, 3.5),
        CityLocation("aleshtar", "الشتر", "Aleshtar", 33.8546, 48.2563, 3.5),
        CityLocation("rumeshkhan", "رومشکان", "Rumeshkhan", 33.1, 47.9, 3.5),
        CityLocation("famenin", "فامنین", "Famenin", 35.1167, 49.35, 3.5),
        CityLocation("lalejin", "لالجین", "Lalejin", 34.9419, 48.4967, 3.5),
        CityLocation("khorramdarreh", "خرمدره", "Khorramdarreh", 36.2, 49.18, 3.5),
        CityLocation("soltaniyeh", "سلطانیه", "Soltaniyeh", 36.44, 48.8, 3.5),
        CityLocation("qeydar", "قیدار", "Qeydar", 36.1, 48.6, 3.5),
        CityLocation("avaj", "آوج", "Avaj", 35.5758, 49.2217, 3.5),
        CityLocation("mohammadieh", "محمدیه", "Mohammadieh", 36.1, 50.1, 3.5),
        CityLocation("eshtehard", "اشتهارد", "Eshtehard", 35.7225, 50.3669, 3.5),
        CityLocation("fardis", "فردیس", "Fardis", 35.7336, 50.9883, 3.5),
        CityLocation("mehrshahr", "مهرشهر", "Mehrshahr", 35.8333, 50.9667, 3.5),
        CityLocation("kamalshahr", "کمال‌شهر", "Kamalshahr", 35.8667, 50.8667, 3.5),
        CityLocation("mohammadshahr", "محمدشهر", "Mohammadshahr", 35.7167, 50.8833, 3.5),
        CityLocation("mahdasht", "ماهدشت", "Mahdasht", 35.7333, 50.8167, 3.5),
        CityLocation("shemiran", "شمیران", "Shemiran", 35.8, 51.45, 3.5),
        CityLocation("bumehen", "بومهن", "Bumehen", 35.73, 51.87, 3.5),
        CityLocation("rudehen", "رودهن", "Rudehen", 35.7333, 51.9, 3.5),
        CityLocation("baghershahr", "باقرشهر", "Baghershahr", 35.5333, 51.4, 3.5),
        CityLocation("kahrizak", "کهریزک", "Kahrizak", 35.52, 51.37, 3.5),
        CityLocation("pishva", "پیشوا", "Pishva", 35.3056, 51.7222, 3.5),
        CityLocation("qiamdasht", "قیامدشت", "Qiamdasht", 35.45, 51.75, 3.5),
        CityLocation("rudbar", "رودبار", "Rudbar", 36.8097, 49.4122, 3.5),
        CityLocation("shaft", "شفت", "Shaft", 37.1, 49.4, 3.5),
        CityLocation("khomam", "خمام", "Khomam", 37.4, 49.65, 3.5),
        CityLocation("lowshan", "لوشان", "Lowshan", 36.6, 49.45, 3.5),
        CityLocation("amlash", "املش", "Amlash", 37.0833, 50.1667, 3.5),
        CityLocation("kiashahr", "کیاشهر", "Kiashahr", 37.4, 50.0, 3.5),
        CityLocation("rostamabad", "رستم‌آباد", "Rostamabad", 36.9, 49.4, 3.5),
        CityLocation("galugah", "گلوگاه", "Galugah", 36.7333, 53.8, 3.5),
        CityLocation("polsefid", "پل سفید", "Pol Sefid", 36.1, 53.0, 3.5),
        CityLocation("kelardasht", "کلاردشت", "Kelardasht", 36.5, 51.15, 3.5),
        CityLocation("nur", "نور", "Nur", 36.5667, 52.0, 3.5),
        CityLocation("salmanshahr", "سلمان‌شهر", "Salmanshahr", 36.7, 51.2, 3.5),
        CityLocation("kiasar", "کیاسر", "Kiasar", 36.2, 53.5, 3.5),
        CityLocation("shirgah", "شیرگاه", "Shirgah", 36.3, 52.9, 3.5),
        CityLocation("abbasabad", "عباس‌آباد", "Abbasabad", 36.6, 51.07, 3.5),
        CityLocation("ramian", "رامیان", "Ramian", 37.0153, 55.1472, 3.5),
        CityLocation("galikash", "گالیکش", "Galikash", 37.2833, 55.4333, 3.5),
        CityLocation("gomishan", "گمیشان", "Gomishan", 37.0667, 54.1, 3.5),
        CityLocation("maravehtappeh", "مراوه‌تپه", "Maraveh Tappeh", 37.9, 55.95, 3.5),
        CityLocation("aliabadkatul", "علی‌آباد کتول", "Aliabad Katul", 36.9, 54.87, 3.5),
        CityLocation("bandargaz", "بندر گز", "Bandar Gaz", 36.7733, 53.9467, 3.5),
        CityLocation("kalat", "کلات", "Kalat", 37.0333, 59.7667, 3.5),
        CityLocation("dargaz", "درگز", "Dargaz", 37.4444, 59.1083, 3.5),
        CityLocation("roshtkhar", "رشتخوار", "Roshtkhar", 34.9764, 59.6167, 3.5),
        CityLocation("bardaskan", "بردسکن", "Bardaskan", 35.2667, 57.97, 3.5),
        CityLocation("khalilabad", "خلیل‌آباد", "Khalilabad", 35.2667, 58.2833, 3.5),
        CityLocation("torqabeh", "طرقبه", "Torqabeh", 36.2833, 59.3667, 3.5),
        CityLocation("joghatai", "جغتای", "Joghatai", 36.6, 57.1, 3.5),
        CityLocation("davarzan", "داورزن", "Davarzan", 36.35, 56.8, 3.5),
        CityLocation("bakharz", "باخرز", "Bakharz", 34.9731, 60.2, 3.5),
        CityLocation("salehabad", "صالح‌آباد", "Salehabad", 35.3, 60.6, 3.5),
        CityLocation("jowayin", "جوین", "Jowayin", 36.5, 57.5, 3.5),
        CityLocation("firuzeh", "فیروزه", "Firuzeh", 36.5, 58.7, 3.5),
        CityLocation("zaveh", "زاوه", "Zaveh", 35.2667, 59.5, 3.5),
        CityLocation("jajarm", "جاجرم", "Jajarm", 36.95, 56.38, 3.5),
        CityLocation("faruj", "فاروج", "Faruj", 37.2336, 58.2183, 3.5),
        CityLocation("manehvasamalqan", "مانه و سملقان", "Maneh va Samalqan", 37.6, 56.8, 3.5),
        CityLocation("khusf", "خوسف", "Khusf", 32.7792, 58.9, 3.5),
        CityLocation("zohan", "زهان", "Zohan", 33.8, 59.8, 3.5),
        CityLocation("ajabshir", "عجب‌شیر", "Ajabshir", 37.4778, 45.8933, 3.5),
        CityLocation("azarshahr", "آذرشهر", "Azarshahr", 37.75, 45.98, 3.5),
        CityLocation("osku", "اسکو", "Osku", 37.9167, 45.8, 3.5),
        CityLocation("heris", "هریس", "Heris", 38.2486, 47.1181, 3.5),
        CityLocation("kaleybar", "کلیبر", "Kaleybar", 38.8667, 47.0333, 3.5),
        CityLocation("varzaqan", "ورزقان", "Varzaqan", 38.5, 46.6, 3.5),
        CityLocation("hadishahr", "هادی‌شهر", "Hadishahr", 38.8333, 45.6, 3.5),
        CityLocation("tasuj", "تسوج", "Tasuj", 38.3, 45.3, 3.5),
        CityLocation("basmenj", "بسمنج", "Basmenj", 38.0, 46.2, 3.5),
        CityLocation("showt", "شوط", "Showt", 39.2167, 44.7667, 3.5),
        CityLocation("poldasht", "پلدشت", "Poldasht", 39.35, 45.0, 3.5),
        CityLocation("bazargan", "بازرگان", "Bazargan", 39.4, 44.3833, 3.5),
        CityLocation("silvana", "سیلوانه", "Silvana", 37.5, 44.9, 3.5),
        CityLocation("bilehsavar", "بیله‌سوار", "Bileh Savar", 39.3797, 48.3556, 3.5),
        CityLocation("germi", "گرمی", "Germi", 39.0211, 48.0833, 3.5),
        CityLocation("namin", "نمین", "Namin", 38.4286, 48.4828, 3.5),
        CityLocation("nir", "نیر", "Nir", 38.0372, 48.0033, 3.5),
        CityLocation("sareyn", "سرعین", "Sareyn", 38.1667, 48.0833, 3.5),
        CityLocation("dehgolan", "دهگلان", "Dehgolan", 35.2786, 47.4192, 3.5),
        CityLocation("sarvabad", "سروآباد", "Sarvabad", 35.3167, 46.35, 3.5),
        CityLocation("kerendegharb", "کرند غرب", "Kerend-e Gharb", 34.2833, 46.2333, 3.5),
        CityLocation("dalahu", "دالاهو", "Dalahu", 34.3, 46.3, 3.5),
        CityLocation("omidiyeh", "امیدیه", "Omidiyeh", 30.7, 49.7, 3.5),
        CityLocation("khorramshahr", "خرمشهر", "Khorramshahr", 30.4256, 48.1914, 3.5),
        CityLocation("hamidiyeh", "حمیدیه", "Hamidiyeh", 31.4833, 48.4333, 3.5),
        CityLocation("shadegan", "شادگان", "Shadegan", 30.65, 48.6667, 3.5),
        CityLocation("gotvand", "گتوند", "Gotvand", 32.25, 48.8167, 3.5),
        CityLocation("ramshir", "رامشیر", "Ramshir", 30.8942, 49.4106, 3.5),
        CityLocation("hoveyzeh", "هویزه", "Hoveyzeh", 31.4672, 48.0742, 3.5),
        CityLocation("bavi", "باوی", "Bavi", 31.7, 48.6, 3.5),
        CityLocation("landeh", "لنده", "Landeh", 30.98, 50.4, 3.5),
        CityLocation("basht", "باشت", "Basht", 30.3667, 51.15, 3.5),
        CityLocation("charam", "چرام", "Charam", 30.7, 50.75, 3.5),
        CityLocation("farrokhshahr", "فرخ‌شهر", "Farrokhshahr", 32.2744, 50.9967, 3.5),
        CityLocation("shalamzar", "شلمزار", "Shalamzar", 32.0, 50.8, 3.5),
        CityLocation("zarghan", "زرقان", "Zarghan", 29.78, 52.7, 3.5),
        CityLocation("evaz", "اوز", "Evaz", 27.7667, 54.0, 3.5),
        CityLocation("kavar", "کوار", "Kavar", 29.2, 52.7, 3.5),
        CityLocation("khorrambid", "خرم‌بید", "Khorrambid", 30.6, 53.3, 3.5),
        CityLocation("zarrindasht", "زرین‌دشت", "Zarrindasht", 28.3, 54.4, 3.5),
        CityLocation("khafr", "خفر", "Khafr", 28.8, 53.2, 3.5),
        CityLocation("beyza", "بیضا", "Beyza", 30.0, 52.4, 3.5),
        CityLocation("sirik", "سیریک", "Sirik", 26.5167, 57.1, 3.5),
        CityLocation("bandarkhamir", "بندر خمیر", "Bandar Khamir", 26.9442, 55.5894, 3.5),
        CityLocation("hormuz", "هرمز", "Hormuz", 27.0517, 56.4572, 3.5),
        CityLocation("parsian", "پارسیان", "Parsian", 27.2, 52.4, 3.5),
        CityLocation("abumusa", "ابوموسی", "Abu Musa", 25.8764, 55.0378, 3.5),
        CityLocation("ahram", "اهرم", "Ahram", 28.8833, 51.2833, 3.5),
        CityLocation("bandarrig", "بندر ریگ", "Bandar Rig", 29.4833, 50.6667, 3.5),
        CityLocation("kahnuj", "کهنوج", "Kahnuj", 27.95, 57.7, 3.5),
        CityLocation("manujan", "منوجان", "Manujan", 27.4, 57.5, 3.5),
        CityLocation("anbarabad", "عنبرآباد", "Anbarabad", 28.4667, 57.8, 3.5),
        CityLocation("narmashir", "نرماشیر", "Narmashir", 28.9833, 58.6833, 3.5),
        CityLocation("ravar", "راور", "Ravar", 31.2667, 56.8, 3.5),
        CityLocation("kuhbanan", "کوهبنان", "Kuhbanan", 31.4, 56.3, 3.5),
        CityLocation("golbaf", "گلباف", "Golbaf", 29.88, 57.7, 3.5),
        CityLocation("fahraj", "فهرج", "Fahraj", 28.95, 58.9, 3.5),
        CityLocation("mirjaveh", "میرجاوه", "Mirjaveh", 29.0, 61.4, 3.5),
        CityLocation("rask", "راسک", "Rask", 26.2333, 61.4, 3.5),
        CityLocation("qasreqand", "قصرقند", "Qasr-e Qand", 26.25, 60.7, 3.5),
        CityLocation("zahak", "زهک", "Zahak", 30.9, 61.6, 3.5),
        CityLocation("hirmand", "هیرمند", "Hirmand", 31.1, 61.5, 3.5),
        CityLocation("najaf", "نجف اشرف", "Najaf", 32.0000, 44.3333, 3.0),
        CityLocation("karbala", "کربلای معلی", "Karbala", 32.6160, 44.0249, 3.0),
        CityLocation("mecca", "مکه مکرمه", "Mecca", 21.4225, 39.8262, 3.0),
        CityLocation("medina", "مدینه منوره", "Medina", 24.5247, 39.5692, 3.0),
        CityLocation("dubai", "دبی", "Dubai", 25.2048, 55.2708, 4.0),
        CityLocation("istanbul", "استانبول", "Istanbul", 41.0082, 28.9784, 3.0)
    )

    /** Main cities plus the extra small cities. */
    val CITIES: List<CityLocation> by lazy { BASE_CITIES + ExtraCities.CITIES }

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
