package com.example.calendar.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.AstroGold
import com.example.ui.theme.HolidayPurple
import com.example.ui.theme.ScorpioAlert

private data class GuideTopic(
    val titlePersian: String,
    val titleEnglish: String,
    val descriptionPersian: String,
    val descriptionEnglish: String,
    val icon: ImageVector,
    val iconTint: Color,
    val tipsPersian: List<String>,
    val tipsEnglish: List<String>
)

@Composable
fun AppGuideDialog(
    isFa: Boolean,
    fontScalePercent: Int = 100,
    onDismiss: () -> Unit
) {
    val topics = remember {
        listOf(
            GuideTopic(
                titlePersian = "۱. گاه‌شماری‌های سه‌گانه و نوار ابزار فشرده",
                titleEnglish = "1. Triple Calendars & Compact Toolbar",
                descriptionPersian = "پشتیبانی همزمان از تقویم‌های هجری شمسی، میلادی و هجری قمری با طراحی خلوت، سریع و یکپارچه.",
                descriptionEnglish = "Simultaneous support for Solar Hijri, Gregorian, and Lunar Hijri calendars in a clean, unified view.",
                icon = Icons.Default.CalendarMonth,
                iconTint = Color(0xFF2563EB),
                tipsPersian = listOf(
                    "نوار ابزار تک‌ردیفه: گزینه‌های «شمسی»، «میلادی»، «قمری» و «پیشگویی 🔮» همگی در یک ردیف فشرده بدون نیاز به اسکرول در دسترس هستند.",
                    "نمایش اختصاصی ماه جاری: در جدول تقویم فقط روزهای متعلق به همان ماه نشان داده می‌شوند و روزهای ماه قبل و بعد پنهان شده‌اند.",
                    "انتخاب سریع سال و ماه: با ضربه روی نام ماه و سال بالای تقویم، پنجره انتخاب سریع برای جهش به هر سال و ماه باز می‌شود.",
                    "نشانگر روز انتخاب‌شده: روز لمس‌شده با کادر فیروزه‌ای چشم‌نواز و شفاف دور آن مشخص می‌گردد."
                ),
                tipsEnglish = listOf(
                    "Single-Row Toolbar: 'Solar', 'Gregorian', 'Lunar', and 'Forecast 🔮' are all instantly accessible in a compact header row.",
                    "Current Month Focus: Only days of the current month are displayed; surrounding months are hidden for a clean look.",
                    "Quick Month/Year Jump: Tap the current month & year title to jump to any date across years in seconds.",
                    "Selected Day Indicator: The tapped day is cleanly highlighted with a vibrant turquoise border."
                )
            ),
            GuideTopic(
                titlePersian = "۲. ویرایش ظاهر و تم رنگی تعطیلات رسمی",
                titleEnglish = "2. Appearance & Holiday Colors Customization",
                descriptionPersian = "امکان شخصی‌سازی کامل رنگ تعطیلات رسمی، کادرها و نمایه‌های تقویم مطابق با سلیقه شخصی شما.",
                descriptionEnglish = "Full customization of official holiday colors, glassy borders, and calendar themes to match your personal taste.",
                icon = Icons.Default.Palette,
                iconTint = Color(0xFF8B5CF6),
                tipsPersian = listOf(
                    "دسترسی از منوی کشویی: از منوی همبرگری (سمت راست بالا) یا صفحه تنظیمات، گزینه «ویرایش ظاهر» را انتخاب نمایید.",
                    "۱۶ پالت رنگی آماده: انتخاب از میان انواع رنگ‌های جذاب و اصیل شامل بنفش رویایی، یاقوتی درباری، زرشکی، یشمی ایرانی، فیروزه‌ای، لاجوردی، کهربایی، زمردی و...",
                    "اسلایدر طیف رنگین‌کمانی دلخواه: با اسلایدر رنگین‌کمان می‌توانید دقیقاً هر رنگ خاص و بی‌نهایت دلخواهی را برای تعطیلات برگزینید.",
                    "کادر شیشه‌ای و نوشته سفید: روزهای تعطیل رسمی به صورت شیشه‌ای به رنگ منتخب شما و با نوشته‌های کاملاً خوانای سفید نمایش می‌یابند.",
                    "پیش‌نمایش زنده: در پنجره ویرایش ظاهر، نمونه روز تعطیل و برچسب رسمی به صورت زنده نمایش داده می‌شود."
                ),
                tipsEnglish = listOf(
                    "Drawer & Settings Access: Tap 'Appearance' in the hamburger navigation drawer or the Settings screen.",
                    "16 Preset Color Themes: Pick from a curated palette including Dreamy Purple, Royal Ruby, Persian Jade, Persian Turquoise, Azure, Emerald, Amber, and more.",
                    "Rainbow Custom Color Slider: Freely choose any precise custom hue using the continuous rainbow color slider.",
                    "Glassy Cells & White Text: Holiday cells adopt your chosen color with an elegant glassy finish and crisp white typography.",
                    "Live Preview: Directly see a live preview of how holiday days and badges will look before closing the dialog."
                )
            ),
            GuideTopic(
                titlePersian = "۳. اوقات خورشیدی و اذان‌های شرعی هر شهر",
                titleEnglish = "3. Solar Times & Azan for Any City",
                descriptionPersian = "محاسبه دقیق اوقات فلکی خورشید و اذان‌های شش‌گانه شرعی به افق شهر انتخابی شما.",
                descriptionEnglish = "Precise astronomical solar times and six prayer times (Azan) calculated for your selected city horizon.",
                icon = Icons.Default.Mosque,
                iconTint = Color(0xFF0D9488),
                tipsPersian = listOf(
                    "کل اوقات خورشیدی: شامل زمان دقیق طلوع، غروب، ظهر نجومی، طول روز، طول شب و زاویه تابش خورشید با انیمیشن کمان خورشید.",
                    "اذان‌ها و اوقات شرعی شش‌گانه: دقیقاً در زیر اوقات خورشیدی، زمان اذان صبح، طلوع آفتاب، اذان ظهر، غروب آفتاب، اذان مغرب و نیمه‌شب شرعی با ارقام فارسی آورده شده است.",
                    "تغییر شهر با جستجوی سریع: با زدن دکمه «تغییر شهر» در کنار اوقات شرعی، می‌توانید در میان تمام ۳۱ استان ایران، اماکن متبرکه (کربلا، نجف، مکه، مدینه) و شهرهای زیارتی فوراً جستجو و شهر دلخواه را انتخاب فرمایید."
                ),
                tipsEnglish = listOf(
                    "Solar Times: Detailed sunrise, sunset, solar noon, day length, night length, and sun altitude angle with a visual arc.",
                    "Six Prayer Times (Azan): Located directly beneath solar times, showing Fajr, Sunrise, Dhuhr, Sunset, Maghrib, and Midnight.",
                    "Quick City Picker & Search: Tap 'Change City' to instantly search across all 31 Iranian provincial capitals, holy pilgrimage cities (Najaf, Karbala, Mecca, Medina), and international locations."
                )
            ),
            GuideTopic(
                titlePersian = "۴. قمر در عقرب با رنگ قرمز و اطلاعات نجومی",
                titleEnglish = "4. Moon in Scorpio (Red) & Astronomy",
                descriptionPersian = "رصد و تقویم وضعیت قمر در عقرب با رنگ قرمز اختصاصی، فازهای ماه و بروج فلکی.",
                descriptionEnglish = "Moon-in-Scorpio tracking in designated vibrant red, lunar phases, and zodiac constellations.",
                icon = Icons.Default.Nightlight,
                iconTint = ScorpioAlert,
                tipsPersian = listOf(
                    "رنگ قرمز نماد قمر در عقرب: نماد اختصاصی عقرب (♏) در جدول تقویم، بنر روزانه، راهنمای پایین جدول و صفحه نجوم با رنگ قرمز متمایز شده است تا به سرعت و وضوح قابل تشخیص باشد.",
                    "برج و صورت فلکی: تفکیک دقیق قمر در برج عقرب (تروپیکال) و صورت فلکی عقرب (سایدرال) بر اساس محاسبات علمی معتبر.",
                    "فاز زنده ماه: سن ماه، درصد روشنایی و نام هلال تا بدر کامل در بالای صفحه نجوم و جزئیات روز به صورت زنده نمایش می‌یابد.",
                    "حیوان سال و عنصر ماه: حیوان تقویم دوازده‌حیوانی و عنصر چهارگانه ماه جاری در نوار سربرگ بالای تقویم نمایان است."
                ),
                tipsEnglish = listOf(
                    "Distinct Red Theme for Scorpio: The custom Scorpio symbol (♏) in calendar cells, daily banners, bottom legend, and the Astronomy tab is rendered in vibrant red.",
                    "Sign vs Constellation: Clear distinction between Tropical Scorpio sign and Sidereal Scorpio constellation based on rigorous celestial algorithms.",
                    "Live Moon Phase: Illumination percentage, lunar age in days, and astronomical moon phase name.",
                    "Year Animal & Zodiac Element: The 12-animal year sign and the 4-element nature of the current month shown in the header."
                )
            ),
            GuideTopic(
                titlePersian = "۵. تعطیلات و مناسبت‌های امروز با مکانیزم استوری",
                titleEnglish = "5. Today's Holidays & Occasions (Story Mode)",
                descriptionPersian = "کارت شناور هوشمند برای مرور سریع تعطیلات و مناسبت‌های روز جاری با مکانیزم استوری.",
                descriptionEnglish = "Smart floating card for quick daily holiday and occasion overview featuring story playback.",
                icon = Icons.Default.AutoAwesome,
                iconTint = Color(0xFFD946EF),
                tipsPersian = listOf(
                    "خط پیشرفت استوری ۳ ثانیه‌ای: هنگام ورود به برنامه، یک خط پیشرفت نئونی در پایین کارت پر می‌شود و پس از ۳ ثانیه محو می‌گردد.",
                    "نگه‌داشتن دست (Hold-to-Pause): با فشردن و نگه‌داشتن انگشت روی کادر، زمان و خط پیشرفت بلافاصله متوقف و فریز می‌شوند تا فرصت کافی برای مطالعه داشته باشید.",
                    "فراخوانی مجدد: هر زمان بخواهید، با لمس آیکون ستاره در نوار ابزار تقویم، این کادر مجدداً فراخوانی می‌شود."
                ),
                tipsEnglish = listOf(
                    "3-Second Story Progress: Fills a smooth progress bar at the bottom over 3 seconds before auto-dismissing.",
                    "Hold to Pause: Touch and hold anywhere on the card to pause time and freeze the progress line indefinitely.",
                    "Reopen Anytime: Tap the star button on the calendar toolbar to bring back the occasions overlay whenever you wish."
                )
            ),
            GuideTopic(
                titlePersian = "۶. پیشگویی‌های ماهانه و طالع کواکب",
                titleEnglish = "6. Monthly Astrological Forecasts",
                descriptionPersian = "روایات، طالع‌بینی سنتی نجومی، روزهای سعد و نحس و احکام کواکب هر ماه خورشیدی.",
                descriptionEnglish = "Traditional astrological forecasts, auspicious/inauspicious days, and seasonal guidance.",
                icon = Icons.Default.Psychology,
                iconTint = AstroGold,
                tipsPersian = listOf(
                    "دسترسی سریع 🔮: با زدن دکمه «پیشگویی» در نوار ابزار بالای تقویم، پنجره فال و طالع ماه جاری گشوده می‌شود.",
                    "روزهای سعد و نحس: نمایش ایام برکت و روزهای نیازمند احتیاط در کارهای اساسی.",
                    "توصیه‌های سلامت و سبک زندگی: راهنمایی‌های حکمای کهن برای تصمیم‌گیری‌ها، سلامت تن و روان و برنامه‌ریزی فصلی."
                ),
                tipsEnglish = listOf(
                    "Quick Access 🔮: Tap the 'Forecast' pill on the top toolbar to reveal traditional celestial predictions for the month.",
                    "Auspicious & Caution Days: Clear view of auspicious days and days recommended for extra prudence.",
                    "Wellness & Lifestyle: Historical astrological advice for seasonal wellbeing, health, and mindful living."
                )
            ),
            GuideTopic(
                titlePersian = "۷. ابزارها، قطب‌نما، یادداشت‌گذاری و تنظیمات",
                titleEnglish = "7. Tools, Qibla Compass, Notes & Settings",
                descriptionPersian = "تبدیل تاریخ سه‌طرفه، جهت‌یابی قبله با سنسور، یادداشت اختصاصی و تنظیمات پیشرفته.",
                descriptionEnglish = "Three-way date conversion, sensor-driven Qibla compass, personal daily notes, and advanced settings.",
                icon = Icons.Default.Settings,
                iconTint = Color(0xFF6366F1),
                tipsPersian = listOf(
                    "تبدیل تاریخ: در منوی کشویی گزینه «تبدیل تاریخ» را انتخاب کنید؛ با وارد کردن هر تاریخ، معادل آن در ۲ گاه‌شماری دیگر بلافاصله ظاهر می‌شود.",
                    "قطب‌نما و قبله‌نما: در صفحه اختصاصی جهت‌یاب، با چرخش گوشی زاویه قطب‌نما و جهت دقیق قبله مشخص می‌شود.",
                    "یادداشت روزانه: با لمس هر روز و نوشتن در کادر «یادداشت»، متن شما برای آن روز ذخیره می‌شود.",
                    "تنظیمات قلم و بارش فصلی: در تنظیمات امکان تغییر اندازه قلم اعداد تقویم، فعال/غیرفعال‌سازی بارش زیبای فصلی و انتخاب زبان (فارسی و انگلیسی) وجود دارد."
                ),
                tipsEnglish = listOf(
                    "Date Converter: Access 'Date Converter' from the drawer to convert dates instantly between Solar, Gregorian, and Lunar.",
                    "Qibla Compass: Sensor-driven compass pointing to the Kaaba direction based on your location coordinates.",
                    "Daily Notes: Select any date to record personal notes preserved in local storage.",
                    "Font Sizing & Seasonal Rain: Customize calendar digit font sizes, toggle animated seasonal rain effect, and switch between Persian and English."
                )
            )
        )
    }

    val currentDensity = LocalDensity.current
    val dialogDensity = remember(currentDensity, fontScalePercent) {
        Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale * (fontScalePercent / 100f)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(
            LocalDensity provides dialogDensity,
            LocalLayoutDirection provides if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .testTag("app_guide_dialog"),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black,
                            modifier = Modifier.size(42.dp),
                            shadowElevation = 3.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.calendar_astro_icon),
                                contentDescription = "PUNY4 Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isFa) "راهنمای جامع برنامه" else "PUNY4 App Guide",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF9333EA),
                                    border = BorderStroke(1.dp, Color(0xFFE879F9)),
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "!",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (isFa) "آشنایی با قابلیت‌ها، تقویم‌ها و ابزارها" else "Features, calendars, and tips",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isFa) "بستن" else "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(topics) { topic ->
                        GuideTopicCard(topic = topic, isFa = isFa)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Dismiss Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("guide_dialog_dismiss_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isFa) "متوجه شدم و بازگشت به برنامه" else "Got it, return to Calendar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun GuideTopicCard(
    topic: GuideTopic,
    isFa: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = topic.iconTint.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = topic.icon,
                            contentDescription = null,
                            tint = topic.iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = if (isFa) topic.titlePersian else topic.titleEnglish,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = if (isFa) topic.descriptionPersian else topic.descriptionEnglish,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 1.5.em
            )

            val tips = if (isFa) topic.tipsPersian else topic.tipsEnglish
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            ) {
                tips.forEach { tip ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "•",
                            color = topic.iconTint,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 1.5.em
                        )
                    }
                }
            }
        }
    }
}
