package com.example.calendar.core

import com.example.calendar.model.CityLocation

/** An Iranian province (plus a group for cities outside Iran) with the ids of its cities. */
data class Province(val namePersian: String, val nameEnglish: String, val cityIds: List<String>)

object Provinces {
    val ALL: List<Province> = listOf(
    Province("تهران", "Tehran", listOf("tehran", "eslamshahr", "pakdasht", "qarchak", "shahriar", "robatkarim", "varamin", "damavand", "firuzkuh", "rey", "lavasan", "pardis", "malard", "nasimshahr", "qods", "parand")),
    Province("البرز", "Alborz", listOf("karaj", "hashtgerd", "nazarabad", "taleghan")),
    Province("قزوین", "Qazvin", listOf("qazvin", "abyek", "buinzahra", "takestan")),
    Province("مرکزی", "Markazi", listOf("arak", "saveh", "tafresh", "khomeyn", "mahallat", "delijan")),
    Province("قم", "Qom", listOf("qom")),
    Province("گیلان", "Gilan", listOf("rasht", "bandaranzali", "lahijan", "langarud", "astara", "rudsar", "talesh", "fuman", "masal", "siahkal", "sowmeehsara", "astanehyeashrafiyeh", "rezvanshahr", "manjil")),
    Province("مازندران", "Mazandaran", listOf("sari", "amol", "babol", "babolsar", "behshahr", "chalus", "nowshahr", "tonekabon", "ramsar", "neka", "qaemshahr", "fereydunkenar", "mahmudabad", "juybar")),
    Province("گلستان", "Golestan", listOf("gorgan", "gonbadekavus", "bandartorkaman", "azadshahr", "aqqala", "kordkuy", "minudasht", "kalaleh")),
    Province("سمنان", "Semnan", listOf("semnan", "shahroud", "damghan", "garmsar")),
    Province("خراسان رضوی", "Razavi Khorasan", listOf("mashhad", "neyshabur", "sabzevar", "kashmar", "gonabad", "torbateheydarieh", "torbatejam", "taybad", "khaf", "sarakhs", "quchan", "fariman", "chenaran", "bajestan", "ferdows")),
    Province("خراسان شمالی", "North Khorasan", listOf("bojnord", "esfarayen", "shirvan")),
    Province("خراسان جنوبی", "South Khorasan", listOf("birjand", "qaen", "nehbandan", "sarbisheh", "darmian", "sarayan", "boshruyeh", "tabas")),
    Province("آذربایجان شرقی", "East Azerbaijan", listOf("tabriz", "ahar", "marand", "maragheh", "mianeh", "bostanabad", "hashtrud", "jolfa", "sarab", "shabestar", "bonab", "malekan")),
    Province("آذربایجان غربی", "West Azerbaijan", listOf("urmia", "khoy", "mahabad", "bukan", "miandoab", "salmas", "piranshahr", "oshnavieh", "naqadeh", "chaldoran", "maku", "shahindezh", "takab", "sardasht")),
    Province("اردبیل", "Ardabil", listOf("ardabil", "parsabad", "khalkhal", "meshkinshahr")),
    Province("زنجان", "Zanjan", listOf("zanjan", "abhar", "khodabandeh", "mahneshan")),
    Province("کردستان", "Kurdistan", listOf("sanandaj", "saqqez", "bijar", "divandarreh", "kamyaran", "marivan", "baneh", "qorveh")),
    Province("کرمانشاه", "Kermanshah", listOf("kermanshah", "paveh", "sarpolezahab", "qasreshirin", "gilanegharb", "eslamabadegharb", "kangavar", "sahneh", "harsin", "ravansar", "javanrud", "sonqor")),
    Province("همدان", "Hamedan", listOf("hamedan", "malayer", "nahavand", "asadabad", "tuyserkan", "razan", "bahar", "kabudarahang")),
    Province("لرستان", "Lorestan", listOf("khorramabad", "borujerd", "aligudarz", "dorud", "kuhdasht", "nurabad")),
    Province("ایلام", "Ilam", listOf("ilam", "mehran", "dehloran", "abdanan", "malekshahi")),
    Province("خوزستان", "Khuzestan", listOf("ahvaz", "abadan", "andimeshk", "behbahan", "bandarmahshahr", "dezful", "hendijan", "izeh", "masjedsoleyman", "ramhormoz", "shush", "shushtar", "susangerd", "haftkel", "baghmalek", "lali")),
    Province("کهگیلویه و بویراحمد", "Kohgiluyeh and Boyer-Ahmad", listOf("yasuj", "dehdasht", "gachsaran", "sisakht", "dena")),
    Province("چهارمحال و بختیاری", "Chaharmahal and Bakhtiari", listOf("shahrekord", "borujen", "farsan", "kiar", "ben", "sudjan", "hafshejan", "ardal", "koohrang", "lordegan", "saman")),
    Province("اصفهان", "Isfahan", listOf("isfahan", "kashan", "ardestan", "falavarjan", "golpayegan", "khansar", "khomeinishahr", "najafabad", "natanz", "shahinshahr", "shahreza", "zarrinshahr", "semirom", "aranvabidgol", "borkhar", "mobarakeh", "nikabad", "varzaneh", "badrud", "zavareh", "kuhpayeh", "chadegan", "fereydan", "fereydunshahr", "tiran", "karvan", "dehaghan", "kelishad", "anarak", "naein", "khur", "garmeh", "jandaq")),
    Province("یزد", "Yazd", listOf("yazd", "ardakan", "meybod", "taft")),
    Province("فارس", "Fars", listOf("shiraz", "abadeh", "darab", "eqlid", "estahban", "fasa", "firuzabad", "jahrom", "kazerun", "lar", "marvdasht", "neyriz", "qir", "khonj", "gerash", "mohr", "lamerd", "farashband", "sepidan", "mamasani", "kherameh", "sarvestan", "pasargadae", "ardakanfars", "bavanat")),
    Province("هرمزگان", "Hormozgan", listOf("bandarabbas", "kish", "qeshm", "bandarlengeh", "bastak", "hajiabad", "rudan", "minab", "jask")),
    Province("بوشهر", "Bushehr", listOf("bushehr", "borazjan", "genaveh", "khormuj", "deylam", "jam", "asaluyeh", "dayyer", "kangan")),
    Province("کرمان", "Kerman", listOf("kerman", "baft", "bam", "jiroft", "rafsanjan", "sirjan", "shahrebabak", "zarand", "bardsir", "anar")),
    Province("سیستان و بلوچستان", "Sistan and Baluchestan", listOf("zahedan", "zabol", "chabahar", "iranshahr", "khash", "nikshahr", "sarbaz", "konarak", "saravan"))
    )

    private val byCity: Map<String, Province> by lazy {
        ALL.flatMap { p -> p.cityIds.map { it to p } }.toMap()
    }

    fun cities(province: Province): List<CityLocation> =
        province.cityIds.mapNotNull { id -> AstronomicalCalculator.CITIES.find { it.id == id } }

    fun provinceOf(cityId: String): Province? = byCity[cityId]
}
