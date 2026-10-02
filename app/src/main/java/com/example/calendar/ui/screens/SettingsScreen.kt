package com.example.calendar.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.DailyNotificationHelper
import com.example.calendar.data.UserSettings
import com.example.calendar.model.AppLanguage
import com.example.calendar.model.CalendarType
import com.example.calendar.model.CityLocation
import com.example.calendar.ui.components.AppearanceDialog
import com.example.ui.theme.HolidayPurple

@Composable
fun SettingsScreen(
    userSettings: UserSettings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage by userSettings.appLanguage.collectAsState()
    val calendarType by userSettings.calendarType.collectAsState()
    val selectedCity by userSettings.selectedCity.collectAsState()
    val showSecondaryDates by userSettings.showSecondaryDates.collectAsState()
    val showSeasonalRain by userSettings.showSeasonalRain.collectAsState()
    val showPinnedNotification by userSettings.showPinnedNotification.collectAsState()
    val useEnglishDayNumbers by userSettings.useEnglishDayNumbers.collectAsState()
    val holidayColorLong by userSettings.holidayColorLong.collectAsState()

    val isFa = (appLanguage == AppLanguage.PERSIAN)
    var showCityDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }

    // Launcher for Android 13+ POST_NOTIFICATIONS runtime permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            userSettings.setShowPinnedNotification(true)
            DailyNotificationHelper.showPinnedDailyNotification(
                context = context,
                appLanguage = appLanguage,
                useEnglishDigits = useEnglishDayNumbers,
                city = selectedCity
            )
        } else {
            userSettings.setShowPinnedNotification(false)
            DailyNotificationHelper.cancelDailyNotification(context)
        }
    }

    val toggleNotification: () -> Unit = {
        val willEnable = !showPinnedNotification
        if (willEnable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    userSettings.setShowPinnedNotification(true)
                    DailyNotificationHelper.showPinnedDailyNotification(
                        context = context,
                        appLanguage = appLanguage,
                        useEnglishDigits = useEnglishDayNumbers,
                        city = selectedCity
                    )
                } else {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                userSettings.setShowPinnedNotification(true)
                DailyNotificationHelper.showPinnedDailyNotification(
                    context = context,
                    appLanguage = appLanguage,
                    useEnglishDigits = useEnglishDayNumbers,
                    city = selectedCity
                )
            }
        } else {
            userSettings.setShowPinnedNotification(false)
            DailyNotificationHelper.cancelDailyNotification(context)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ----------------------------------------------------
        // 1. LANGUAGE SETTINGS (بخش زبان برنامه)
        // ----------------------------------------------------
        item {
            SettingsCategoryCard(
                icon = Icons.Default.Language,
                iconTint = MaterialTheme.colorScheme.primary,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                title = if (isFa) "زبان برنامه" else "App Language",
                subtitle = if (isFa) "انتخاب زبان رابط کاربری (فارسی یا انگلیسی)" else "Choose application interface language",
                cardTag = "settings_card_language"
            ) {
                AppLanguage.values().forEach { lang ->
                    val isSelected = (lang == appLanguage)
                    val label = when (lang) {
                        AppLanguage.PERSIAN -> if (isFa) "فارسی (Persian)" else "Persian (فارسی)"
                        AppLanguage.ENGLISH -> if (isFa) "انگلیسی (English)" else "English (انگلیسی)"
                    }
                    val flag = lang.flagEmoji

                    SelectableOptionRow(
                        title = "$flag  $label",
                        subtitle = if (lang == AppLanguage.PERSIAN) {
                            if (isFa) "راست‌چین (RTL) به همراه ماه‌های فارسی" else "Right-to-Left (RTL) with Persian layout"
                        } else {
                            if (isFa) "چپ‌چین (LTR) به زبان بین‌المللی" else "Left-to-Right (LTR) international interface"
                        },
                        isSelected = isSelected,
                        onClick = { userSettings.setAppLanguage(lang) },
                        testTag = "language_option_${lang.code}"
                    )
                    if (lang != AppLanguage.values().last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // ----------------------------------------------------
        // 2. DEFAULT CALENDAR (بخش تقویم پیش‌فرض اولیه)
        // ----------------------------------------------------
        item {
            SettingsCategoryCard(
                icon = Icons.Default.CalendarMonth,
                iconTint = MaterialTheme.colorScheme.secondary,
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                title = if (isFa) "تقویم پیش‌فرض اولیه" else "Default Calendar",
                subtitle = if (isFa) "گاه‌شماری اصلی در هنگام باز شدن برنامه" else "Primary calendar displayed on app launch",
                cardTag = "settings_card_default_calendar"
            ) {
                CalendarType.values().forEach { cType ->
                    val isSelected = (cType == calendarType)
                    val title = if (isFa) cType.titlePersian else cType.titleEnglish
                    val desc = when (cType) {
                        CalendarType.SOLAR_HIJRI -> if (isFa) "گاه‌شماری رسمی ایران با ۱۲ ماه خورشیدی" else "Iranian official Solar calendar (12 months)"
                        CalendarType.GREGORIAN -> if (isFa) "گاه‌شماری بین‌المللی میلادی" else "International Gregorian calendar"
                        CalendarType.LUNAR_HIJRI -> if (isFa) "گاه‌شماری قمری اسلامی و رویدادهای مذهبی" else "Islamic Lunar calendar and religious events"
                    }

                    SelectableOptionRow(
                        title = title,
                        subtitle = desc,
                        isSelected = isSelected,
                        onClick = { userSettings.setCalendarType(cType) },
                        testTag = "calendar_option_${cType.name.lowercase()}"
                    )
                    if (cType != CalendarType.values().last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // ----------------------------------------------------
        // 4. CITY & ASTRONOMICAL TIMES (بخش شهر و موقعیت مکانی)
        // ----------------------------------------------------
        item {
            SettingsCategoryCard(
                icon = Icons.Default.LocationCity,
                iconTint = HolidayPurple,
                iconContainerColor = HolidayPurple.copy(alpha = 0.15f),
                title = if (isFa) "شهر و موقعیت مکانی اوقات نجومی" else "City & Astronomical Times",
                subtitle = if (isFa) "جهت محاسبه دقیق طلوع، غروب، ظهر و اوقات خورشیدی" else "For precise sunrise, sunset, and solar times",
                cardTag = "settings_card_location"
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isFa) "شهر فعلی: ${selectedCity.namePersian}" else "Current City: ${selectedCity.nameEnglish}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isFa) "عرض: ${selectedCity.latitude}° • طول: ${selectedCity.longitude}°" else "Lat: ${selectedCity.latitude}° • Lon: ${selectedCity.longitude}°",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showCityDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("change_city_button")
                        ) {
                            Text(text = if (isFa) "تغییر شهر" else "Change City")
                        }
                    }
                }
            }
        }

        // ----------------------------------------------------
        // 5. CALENDAR DISPLAY PREFERENCES (بخش تنظیمات نمایش تقویم)
        // ----------------------------------------------------
        item {
            SettingsCategoryCard(
                icon = Icons.Default.Tune,
                iconTint = MaterialTheme.colorScheme.primary,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                title = if (isFa) "تنظیمات نمایش تقویم" else "Calendar Display Preferences",
                subtitle = if (isFa) "شخصی‌سازی اطلاعات و جزئیات روزها در جدول" else "Customize details shown in calendar cells",
                cardTag = "settings_card_display"
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { userSettings.setShowSecondaryDates(!showSecondaryDates) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isFa) "نمایش تاریخ‌های قمری و میلادی زیر هر روز" else "Show Lunar & Gregorian Dates in Day Cells",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isFa) "نمایش ارقام کوچک معادل قمری و میلادی در زیر شماره روزها (به‌صورت پیش‌فرض خاموش)" else "Display small equivalent lunar and gregorian dates below each day number",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = showSecondaryDates,
                            onCheckedChange = { userSettings.setShowSecondaryDates(it) },
                            modifier = Modifier.testTag("toggle_secondary_dates")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { userSettings.setShowSeasonalRain(!showSeasonalRain) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isFa) "باران انیمیشنی ایموجی‌های فصلی" else "Seasonal Emoji Rain Animation",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isFa) "بارش ملایم در سربرگ (بهار: 🌸💐🌺، تابستان: 🌼🌻☀️، پاییز: 🍁🍂، زمستان: ❄️☃️)" else "Gentle seasonal rain in top header (Spring: 🌸💐🌺, Summer: 🌼🌻☀️, Autumn: 🍁🍂, Winter: ❄️☃️)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = showSeasonalRain,
                            onCheckedChange = { userSettings.setShowSeasonalRain(it) },
                            modifier = Modifier.testTag("toggle_seasonal_rain")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pinned Daily Notification Setting (right next to seasonal animation)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { toggleNotification() }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isFa) "نوتیفیکیشن پین‌شده تاریخ در نوار اعلان" else "Pinned Date Notification",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isFa) "نمایش ثابت و پین‌شده تاریخ و روز جاری در نوار اعلان گوشی (پیش‌فرض خاموش)" else "Pinned notification showing today's date in status bar (Default Off)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = showPinnedNotification,
                            onCheckedChange = { toggleNotification() },
                            modifier = Modifier.testTag("toggle_pinned_notification")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { userSettings.setUseEnglishDayNumbers(!useEnglishDayNumbers) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isFa) "اعداد تقویم با ارقام انگلیسی (پیش‌فرض)" else "Calendar Numbers in English Digits (Default)",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isFa) "نمایش تمامی ارقام تقویم، روزها، سال، ساعت‌ها و آمار با ارقام انگلیسی (1, 2, 3...)" else "Display all calendar numbers, days, years, times and stats using English digits (1, 2, 3...)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = useEnglishDayNumbers,
                            onCheckedChange = { userSettings.setUseEnglishDayNumbers(it) },
                            modifier = Modifier.testTag("toggle_english_day_numbers")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAppearanceDialog = true }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(holidayColorLong),
                                modifier = Modifier.size(28.dp)
                            ) {}
                            Column {
                                Text(
                                    text = if (isFa) "ویرایش ظاهر و رنگ تعطیلات رسمی" else "Customize Holiday Color & Appearance",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isFa) "انتخاب از میان ۱۶ رنگ متنوع یا طیف رنگین‌کمانی دلخواه" else "Choose from 16 preset colors or custom rainbow slider",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = Color(holidayColorLong)
                        )
                    }
                }
            }
        }

        // ----------------------------------------------------
        // 6. ABOUT & INFO (بخش درباره برنامه و قابلیت‌ها)
        // ----------------------------------------------------
        item {
            SettingsCategoryCard(
                icon = Icons.Default.Info,
                iconTint = MaterialTheme.colorScheme.secondary,
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                title = if (isFa) "درباره تقویم جامع PUNY4" else "About PUNY4 Calendar",
                subtitle = if (isFa) "اطلاعات برنامه و قابلیت‌های تقویم" else "App information and calendar features",
                cardTag = "settings_card_about"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black,
                            modifier = Modifier.size(64.dp),
                            shadowElevation = 4.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.calendar_astro_icon),
                                contentDescription = "PUNY4 Calendar Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PUNY4 Calendar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "v 1.0.0",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isFa) "تقویم جامع، نجوم و اوقات خورشیدی" else "Comprehensive Calendar & Astronomy",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = if (isFa) {
                            "یک تقویم جامع با طراحی مدرن متریال دیزاین ۳ با پشتیبانی همزمان از سه تقویم خورشیدی (جلالی)، میلادی و قمری، همراه با محاسبات دقیق نجومی، فازهای ماه، بروج فلکی، ایام قمر در عقرب، پیشگویی‌های ماهانه و اوقات شرعی روزانه."
                        } else {
                            "A comprehensive calendar built with modern Material Design 3 supporting Solar Hijri (Jalali), Gregorian, and Lunar Hijri calendars simultaneously, featuring astronomical lunar phases, zodiac signs, Qamar Dar Aqrab status, monthly predictions, and daily solar prayer times."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Features highlight
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FeatureBullet(
                            text = if (isFa) "پشتیبانی کامل از هر دو زبان فارسی 🇮🇷 و انگلیسی 🇺🇸" else "Full support for Persian 🇮🇷 and English 🇺🇸 languages"
                        )
                        FeatureBullet(
                            text = if (isFa) "محاسبه دقیق وضعیت قمر در عقرب (برج و صورت فلکی)" else "Precise Qamar Dar Aqrab astronomical tracking"
                        )
                        FeatureBullet(
                            text = if (isFa) "بخش پیشگویی‌های ماهانه سنتی و طالع کواکب" else "Traditional monthly predictions and planetary signs"
                        )
                        FeatureBullet(
                            text = if (isFa) "ابزار تبدیل تاریخ دقیق سه‌گانه بین تقویم‌ها" else "Accurate 3-way date conversion between calendars"
                        )
                    }
                }
            }
        }
    }

    // ----------------------------------------------------
    // CITY SELECTION DIALOG (گزینه‌ای و مرتب)
    // ----------------------------------------------------
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = {
                Text(
                    text = if (isFa) "انتخاب شهر جهت اوقات خورشیدی" else "Select City for Solar Times",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(AstronomicalCalculator.CITIES.size) { index ->
                        val city = AstronomicalCalculator.CITIES[index]
                        val isChosen = (city.id == selectedCity.id)
                        val cityName = if (isFa) city.namePersian else city.nameEnglish

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isChosen) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                                .clickable {
                                    userSettings.setCity(city)
                                    showCityDialog = false
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = cityName,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isChosen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${city.latitude}°N, ${city.longitude}°E",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            RadioButton(
                                selected = isChosen,
                                onClick = {
                                    userSettings.setCity(city)
                                    showCityDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text(if (isFa) "بستن" else "Close")
                }
            }
        )
    }

    if (showAppearanceDialog) {
        AppearanceDialog(
            userSettings = userSettings,
            isFa = isFa,
            onDismiss = { showAppearanceDialog = false }
        )
    }
}

@Composable
private fun SettingsCategoryCard(
    icon: ImageVector,
    iconTint: Color,
    iconContainerColor: Color,
    title: String,
    subtitle: String,
    cardTag: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(cardTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconContainerColor,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun SelectableOptionRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun FeatureBullet(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
