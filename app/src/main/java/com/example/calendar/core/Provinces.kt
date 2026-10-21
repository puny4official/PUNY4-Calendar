package com.example.calendar.core

import com.example.calendar.model.CityLocation

/** An Iranian province (plus a group for cities outside Iran) with the ids of its cities. */
data class Province(val namePersian: String, val nameEnglish: String, val cityIds: List<String>)

object Provinces {
    private val BASE: List<Province> = listOf(
    Province("تهران", "Tehran", listOf("tehran", "eslamshahr", "pakdasht", "qarchak", "shahriar", "robatkarim", "varamin", "damavand", "firuzkuh", "rey", "lavasan", "pardis", "malard", "nasimshahr", "qods", "parand", "shemiran", "bumehen", "rudehen", "baghershahr", "kahrizak", "pishva", "qiamdasht")),
    Province("البرز", "Alborz", listOf("karaj", "hashtgerd", "nazarabad", "taleghan", "eshtehard", "fardis", "mehrshahr", "kamalshahr", "mohammadshahr", "mahdasht")),
    Province("قزوین", "Qazvin", listOf("qazvin", "abyek", "buinzahra", "takestan", "avaj", "mohammadieh")),
    Province("مرکزی", "Markazi", listOf("arak", "saveh", "tafresh", "khomeyn", "mahallat", "delijan", "ashtian", "khondab", "komijan", "shazand", "zarandieh", "farahan", "mamuniyeh")),
    Province("قم", "Qom", listOf("qom", "jamkaran", "kahak")),
    Province("گیلان", "Gilan", listOf("rasht", "bandaranzali", "lahijan", "langarud", "astara", "rudsar", "talesh", "fuman", "masal", "siahkal", "sowmeehsara", "astanehyeashrafiyeh", "rezvanshahr", "manjil", "rudbar", "shaft", "khomam", "lowshan", "amlash", "kiashahr", "rostamabad")),
    Province("مازندران", "Mazandaran", listOf("sari", "amol", "babol", "babolsar", "behshahr", "chalus", "nowshahr", "tonekabon", "ramsar", "neka", "qaemshahr", "fereydunkenar", "mahmudabad", "juybar", "galugah", "polsefid", "kelardasht", "nur", "salmanshahr", "kiasar", "shirgah", "abbasabad")),
    Province("گلستان", "Golestan", listOf("gorgan", "gonbadekavus", "bandartorkaman", "azadshahr", "aqqala", "kordkuy", "minudasht", "kalaleh", "ramian", "galikash", "gomishan", "maravehtappeh", "aliabadkatul", "bandargaz")),
    Province("سمنان", "Semnan", listOf("semnan", "shahroud", "damghan", "garmsar", "sorkheh", "mehdishahr", "aradan", "meyami", "eyvanekey", "biarjomand")),
    Province("خراسان رضوی", "Razavi Khorasan", listOf("mashhad", "neyshabur", "sabzevar", "kashmar", "gonabad", "torbateheydarieh", "torbatejam", "taybad", "khaf", "sarakhs", "quchan", "fariman", "chenaran", "bajestan", "ferdows", "kalat", "dargaz", "roshtkhar", "bardaskan", "khalilabad", "torqabeh", "joghatai", "davarzan", "bakharz", "salehabad", "jowayin", "firuzeh", "zaveh")),
    Province("خراسان شمالی", "North Khorasan", listOf("bojnord", "esfarayen", "shirvan", "jajarm", "faruj", "manehvasamalqan")),
    Province("خراسان جنوبی", "South Khorasan", listOf("birjand", "qaen", "nehbandan", "sarbisheh", "darmian", "sarayan", "boshruyeh", "tabas", "khusf", "zohan")),
    Province("آذربایجان شرقی", "East Azerbaijan", listOf("tabriz", "ahar", "marand", "maragheh", "mianeh", "bostanabad", "hashtrud", "jolfa", "sarab", "shabestar", "bonab", "malekan", "ajabshir", "azarshahr", "osku", "heris", "kaleybar", "varzaqan", "hadishahr", "tasuj", "basmenj")),
    Province("آذربایجان غربی", "West Azerbaijan", listOf("urmia", "khoy", "mahabad", "bukan", "miandoab", "salmas", "piranshahr", "oshnavieh", "naqadeh", "chaldoran", "maku", "shahindezh", "takab", "sardasht", "showt", "poldasht", "bazargan", "silvana")),
    Province("اردبیل", "Ardabil", listOf("ardabil", "parsabad", "khalkhal", "meshkinshahr", "bilehsavar", "germi", "namin", "nir", "sareyn")),
    Province("زنجان", "Zanjan", listOf("zanjan", "abhar", "khodabandeh", "mahneshan", "khorramdarreh", "soltaniyeh", "qeydar")),
    Province("کردستان", "Kurdistan", listOf("sanandaj", "saqqez", "bijar", "divandarreh", "kamyaran", "marivan", "baneh", "qorveh", "dehgolan", "sarvabad")),
    Province("کرمانشاه", "Kermanshah", listOf("kermanshah", "paveh", "sarpolezahab", "qasreshirin", "gilanegharb", "eslamabadegharb", "kangavar", "sahneh", "harsin", "ravansar", "javanrud", "sonqor", "kerendegharb", "dalahu")),
    Province("همدان", "Hamedan", listOf("hamedan", "malayer", "nahavand", "asadabad", "tuyserkan", "razan", "bahar", "kabudarahang", "famenin", "lalejin")),
    Province("لرستان", "Lorestan", listOf("khorramabad", "borujerd", "aligudarz", "dorud", "kuhdasht", "nurabad", "azna", "poledokhtar", "aleshtar", "rumeshkhan")),
    Province("ایلام", "Ilam", listOf("ilam", "mehran", "dehloran", "abdanan", "malekshahi", "eyvan", "darrehshahr", "chavar", "badreh")),
    Province("خوزستان", "Khuzestan", listOf("ahvaz", "abadan", "andimeshk", "behbahan", "bandarmahshahr", "dezful", "hendijan", "izeh", "masjedsoleyman", "ramhormoz", "shush", "shushtar", "susangerd", "haftkel", "baghmalek", "lali", "omidiyeh", "khorramshahr", "hamidiyeh", "shadegan", "gotvand", "ramshir", "hoveyzeh", "bavi")),
    Province("کهگیلویه و بویراحمد", "Kohgiluyeh and Boyer-Ahmad", listOf("yasuj", "dehdasht", "gachsaran", "sisakht", "dena", "landeh", "basht", "charam")),
    Province("چهارمحال و بختیاری", "Chaharmahal and Bakhtiari", listOf("shahrekord", "borujen", "farsan", "kiar", "ben", "sudjan", "hafshejan", "ardal", "koohrang", "lordegan", "saman", "farrokhshahr", "shalamzar")),
    Province("اصفهان", "Isfahan", listOf("isfahan", "kashan", "ardestan", "falavarjan", "golpayegan", "khansar", "khomeinishahr", "najafabad", "natanz", "shahinshahr", "shahreza", "zarrinshahr", "semirom", "aranvabidgol", "borkhar", "mobarakeh", "nikabad", "varzaneh", "badrud", "zavareh", "kuhpayeh", "chadegan", "fereydan", "fereydunshahr", "tiran", "karvan", "dehaghan", "kelishad", "anarak", "naein", "khur", "garmeh", "jandaq")),
    Province("یزد", "Yazd", listOf("yazd", "ardakan", "meybod", "taft", "bafq", "mehriz", "abarkuh", "ashkezar", "behabad", "heratyazd")),
    Province("فارس", "Fars", listOf("shiraz", "abadeh", "darab", "eqlid", "estahban", "fasa", "firuzabad", "jahrom", "kazerun", "lar", "marvdasht", "neyriz", "qir", "khonj", "gerash", "mohr", "lamerd", "farashband", "sepidan", "mamasani", "kherameh", "sarvestan", "pasargadae", "ardakanfars", "bavanat", "zarghan", "evaz", "kavar", "khorrambid", "zarrindasht", "khafr", "beyza")),
    Province("هرمزگان", "Hormozgan", listOf("bandarabbas", "kish", "qeshm", "bandarlengeh", "bastak", "hajiabad", "rudan", "minab", "jask", "sirik", "bandarkhamir", "hormuz", "parsian", "abumusa")),
    Province("بوشهر", "Bushehr", listOf("bushehr", "borazjan", "genaveh", "khormuj", "deylam", "jam", "asaluyeh", "dayyer", "kangan", "ahram", "bandarrig")),
    Province("کرمان", "Kerman", listOf("kerman", "baft", "bam", "jiroft", "rafsanjan", "sirjan", "shahrebabak", "zarand", "bardsir", "anar", "kahnuj", "manujan", "anbarabad", "narmashir", "ravar", "kuhbanan", "golbaf", "fahraj")),
    Province("سیستان و بلوچستان", "Sistan and Baluchestan", listOf("zahedan", "zabol", "chabahar", "iranshahr", "khash", "nikshahr", "sarbaz", "konarak", "saravan", "mirjaveh", "rask", "qasreqand", "zahak", "hirmand"))
    )

    val ALL: List<Province> by lazy {
        BASE.map { it.copy(cityIds = it.cityIds + ExtraCities.idsFor(it.nameEnglish)) }
    }

    private val byCity: Map<String, Province> by lazy {
        ALL.flatMap { p -> p.cityIds.map { it to p } }.toMap()
    }

    private val byId: Map<String, CityLocation> by lazy { AstronomicalCalculator.CITIES.associateBy { it.id } }

    fun cities(province: Province): List<CityLocation> =
        province.cityIds.mapNotNull { byId[it] }

    fun provinceOf(cityId: String): Province? = byCity[cityId]
}
