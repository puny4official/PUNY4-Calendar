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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.AstroGold
import com.example.ui.theme.HolidayPurple

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
    onDismiss: () -> Unit
) {
    val topics = remember {
        listOf(
            GuideTopic(
                titlePersian = "۱. گاه‌شماری‌های سه‌گانه و نوار ابزار فشرده",
                titleEnglish = "1. Triple Calendars & Compact Toolbar",
                descriptionPersian = "پشتیبانی همزمان از تقویم‌های هجری شمسی، میلادی و هجری قمری با طراحی خلوت و یکپارچه.",
                descriptionEnglish = "Simultaneous support for Solar Hijri, Gregorian, and Lunar Hijri calendars in a clean, unified view.",
                icon = Icons.Default.CalendarMonth,
                iconTint = Color(0xFF2563EB),
                tipsPersian = listOf(
                    "نوار ابزار تک‌ردیفه: گزینه‌های «شمسی»، «میلادی»، «قمری» و «پیشگویی 🔮» همگی در یک ردیف فشرده بدون نیاز به اسکرول در دسترس هستند.",
                    "نمایش اختصاصی ماه جاری: در جدول تقویم فقط روزهای متعلق به همان ماه نشان داده می‌شوند و روزهای ماه قبل و بعد برای جلوگیری از شلوغی پنهان شده‌اند.",
                    "انتخاب سریع سال و ماه: با ضربه روی نام ماه و سال بالای تقویم، پنجره انتخاب سریع برای جهش به هر سال و ماه باز می‌شود.",
                    "تاریخ‌های فرعی: در صورت فعال بودن در تنظیمات، اعداد کوچک زیر هر روز تاریخ معادل دو تقویم دیگر را نشان می‌دهند."
                ),
                tipsEnglish = listOf(
                    "Single-Row Toolbar: 'Solar', 'Gregorian', 'Lunar', and 'Forecast 🔮' are all instantly accessible in a compact header row.",
                    "Current Month Focus: Only days of the current month are displayed; surrounding months are hidden for a clean look.",
                    "Quick Month/Year Jump: Tap the current month & year title to jump to any date across years in seconds.",
                    "Secondary Dates: Small indicators beneath each day show equivalent dates in the other two calendars."
                )
            ),
            GuideTopic(
                titlePersian = "۲. صفحه اوقات امروز با مکانیزم استوری اینستاگرام",
                titleEnglish = "2. Today's Times with Instagram Story Mechanism",
                descriptionPersian = "نمایش هوشمند اوقات شرعی، خورشیدی، تعطیلات و مناسبت‌های امروز با مکانیزم استوری.",
                descriptionEnglish = "Smart card showing prayer times, solar hours, holidays, and occasions with an Instagram Story mechanism.",
                icon = Icons.Default.AutoAwesome,
                iconTint = Color(0xFF9333EA),
                tipsPersian = listOf(
                    "خط پیشرفت استوری ۳ ثانیه‌ای: با ورود به برنامه، یک خط بنفش نئونی در پایین کادر در عرض ۳ ثانیه پر می‌شود و سپس کادر به آرامی محو می‌گردد.",
                    "نگه‌داشتن دست (Hold-to-Pause): با فشردن و نگه‌داشتن انگشت روی کادر، زمان و خط پیشرفت در همان لحظه متوقف و فریز می‌شوند تا فرصت کافی برای مطالعه داشته باشید.",
                    "اتصال خط‌چین هوشمند: فلش منحنی و حلقه خط‌چین چشمک‌زن، مستقیماً به روزی از جدول که نشانگر روی آن قرار دارد وصل می‌شوند.",
                    "فراخوانی مجدد: هر زمان که بخواهید، با لمس دکمه ستاره آبی (اوقات امروز) در نوار ابزار تقویم، این کادر مجدداً نمایش داده می‌شود."
                ),
                tipsEnglish = listOf(
                    "3-Second Story Progress: On opening, a neon purple progress line fills at the bottom over 3 seconds before auto-dismissing.",
                    "Hold to Pause: Press & hold your finger anywhere on the card to pause time and freeze the progress line indefinitely.",
                    "Dashed Arrow Connection: The pulsing dashed circle and arrow connect dynamically to the indicated day on the grid.",
                    "Reopen Anytime: Tap the blue star button on the calendar toolbar to bring back the times overlay at any time."
                )
            ),
            GuideTopic(
                titlePersian = "۳. پیشگویی‌های ماهانه و طالع کواکب",
                titleEnglish = "3. Monthly Astrological Forecasts",
                descriptionPersian = "روایات، طالع‌بینی سنتی نجومی و احکام کواکب مربوط به هر ماه خورشیدی.",
                descriptionEnglish = "Traditional astrological forecasts, celestial guidance, and monthly fortunes.",
                icon = Icons.Default.Psychology,
                iconTint = AstroGold,
                tipsPersian = listOf(
                    "دسترسی سریع 🔮: با زدن دکمه «پیشگویی» در نوار ابزار بالای تقویم، پنجره فال و طالع ماه جاری گشوده می‌شود.",
                    "توصیه‌های سلامت و سبک زندگی: راهنمایی‌های حکمای کهن برای تصمیم‌گیری‌ها، سلامت تن و روان و برنامه‌ریزی هر فصل."
                ),
                tipsEnglish = listOf(
                    "Quick Access 🔮: Tap the 'Forecast' pill on the top toolbar to reveal traditional celestial predictions for the month.",
                    "Wellness & Lifestyle: Historical astrological advice for seasonal wellbeing, health, and mindful decision making."
                )
            ),
            GuideTopic(
                titlePersian = "۴. اطلاعات نجومی، فاز ماه و قمر در عقرب",
                titleEnglish = "4. Astronomy, Moon Phases & Scorpio",
                descriptionPersian = "محاسبات زنده فلکی، وضعیت قمر در عقرب و چرخه روشنایی ماه.",
                descriptionEnglish = "Real-time celestial calculations, Moon-in-Scorpio status, and lunar illumination cycles.",
                icon = Icons.Default.Nightlight,
                iconTint = Color(0xFFD97706),
                tipsPersian = listOf(
                    "قمر در عقرب: در سربرگ «نجوم»، وضعیت ورود و خروج ماه از برج عقرب و صورت فلکی عقرب با تاریخ و ساعت دقیق اعلام می‌شود.",
                    "نماد در تقویم: روزهایی که ماه در عقرب است با نماد اختصاصی عقرب در تقویم مشخص شده‌اند.",
                    "فاز ماه: سن ماه، درصد روشنایی و نام نجومی هلال تا بدر کامل به صورت زنده محاسبه می‌گردد.",
                    "نماد حیوان سال و عنصر ماه: حیوان تقویم دوازده‌حیوانی (مثلاً مار، اژدها و...) و عنصر چهارگانه ماه جاری بالای صفحه نشان داده می‌شود."
                ),
                tipsEnglish = listOf(
                    "Qamar Dar Aqrab: The Astronomy tab tracks lunar transit through the Scorpio sign and constellation with exact ingress/egress hours.",
                    "Calendar Symbol: Days during Scorpio moon are marked with a distinct scorpio icon on the calendar grid.",
                    "Moon Phase: Lunar age, illumination percentage, and astronomical phase (New Moon to Full Moon).",
                    "Year Animal & Zodiac Element: Year animal sign (e.g. Snake, Dragon) and nature element displayed in the header."
                )
            ),
            GuideTopic(
                titlePersian = "۵. ابزار تبدیل دقیق تاریخ‌ها",
                titleEnglish = "5. Accurate Three-Way Date Converter",
                descriptionPersian = "تبدیل بی‌درنگ هر تاریخ بین هجری شمسی، میلادی و قمری با استاندارد روز ژولیوسی (JDN).",
                descriptionEnglish = "Instant three-way conversion between Solar Hijri, Gregorian, and Lunar Hijri using standard Julian Day Numbers.",
                icon = Icons.Default.SyncAlt,
                iconTint = Color(0xFF059669),
                tipsPersian = listOf(
                    "تبدیل آنی: در منوی کشویی گزینه «تبدیل تاریخ» را انتخاب کنید؛ با وارد کردن هر تاریخ، معادل آن در ۲ تقویم دیگر بلافاصله ظاهر می‌شود.",
                    "کبیسه‌سنج: سال‌های کبیسه و روز دقیق هفته به صورت هوشمند و بدون خطا محاسبه می‌گردند."
                ),
                tipsEnglish = listOf(
                    "Instant Conversion: Open 'Date Converter' from the drawer; type any date to see instant equivalents in the other two systems.",
                    "Leap Year & Weekday: Automatically verifies leap years and exact day of the week."
                )
            ),
            GuideTopic(
                titlePersian = "۶. یادداشت‌گذاری، قطب‌نما و تنظیمات",
                titleEnglish = "6. Notes, Qibla Compass & Settings",
                descriptionPersian = "ثبت یادداشت اختصاصی برای هر روز، جهت‌یابی قبله و شخصی‌سازی کامل برنامه.",
                descriptionEnglish = "Daily personal notes, sensor-based Qibla compass, and personalized preferences.",
                icon = Icons.Default.Settings,
                iconTint = Color(0xFF6366F1),
                tipsPersian = listOf(
                    "یادداشت روزانه: با لمس هر روز و نوشتن در کادر «یادداشت»، متن شما برای آن روز ذخیره می‌شود.",
                    "قطب‌نما و قبله‌نما: در صفحه اختصاصی جهت‌یاب، با چرخش گوشی زاویه قطب‌نما و جهت دقیق قبله مشخص می‌شود.",
                    "زبان و ظاهر: از منوی تنظیمات می‌توانید زبان برنامه را بین فارسی 🇮🇷 و انگلیسی 🇺🇸 و پوسته را بین تاریک و روشن تغییر دهید.",
                    "دایره بنفش نئونی راهنما (!): هر زمان نیاز به راهنمایی داشتید با لمس این دایره در بالای تقویم پنجره حاضر را مشاهده فرمایید."
                ),
                tipsEnglish = listOf(
                    "Daily Notes: Select any day to write and persist personal notes directly onto that date.",
                    "Qibla Compass: Sensor-driven compass pointing to the Kaaba direction based on your location.",
                    "Language & Theme: Switch between Persian and English, and toggle Light/Dark mode in Settings.",
                    "Neon Purple Guide Badge (!): Tap the neon purple circle at the top anytime to reopen this comprehensive guide."
                )
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .testTag("app_guide_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
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
                lineHeight = 19.sp
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
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}
