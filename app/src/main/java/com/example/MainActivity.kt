package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.unit.sp
import com.example.calendar.core.CalendarManager
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.data.UserSettings
import com.example.calendar.model.AppLanguage
import com.example.calendar.ui.components.AppGuideDialog
import com.example.calendar.ui.components.AppearanceDialog
import com.example.calendar.ui.components.SeasonalRainOverlay
import com.example.calendar.ui.screens.AstronomyScreen
import com.example.calendar.ui.screens.CalendarScreen
import com.example.calendar.ui.screens.DateConverterScreen
import com.example.calendar.ui.screens.SettingsScreen
import com.example.ui.theme.AstroGold
import com.example.ui.theme.CelestialBlue
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class MainTab(val titlePersian: String, val titleEnglish: String, val icon: ImageVector) {
    CALENDAR("تقویم", "Calendar", Icons.Default.CalendarMonth),
    ASTRONOMY("نجوم و آسمان", "Astronomy & Sky", Icons.Default.AutoAwesome),
    CONVERTER("تبدیل تاریخ", "Date Converter", Icons.Default.SyncAlt),
    SETTINGS("تنظیمات", "Settings", Icons.Default.Settings);

    fun title(lang: AppLanguage): String = if (lang == AppLanguage.PERSIAN) titlePersian else titleEnglish
}

class MainActivity : ComponentActivity() {

    private lateinit var userSettings: UserSettings

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userSettings = UserSettings(applicationContext)

        setContent {
            val themeMode by userSettings.themeMode.collectAsState()
            val appLanguage by userSettings.appLanguage.collectAsState()
            val fontScalePercent by userSettings.fontScalePercent.collectAsState()
            val isFa = (appLanguage == AppLanguage.PERSIAN)
            val layoutDirection = if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr

            val currentDensity = LocalDensity.current
            val customDensity = remember(currentDensity, fontScalePercent) {
                Density(
                    density = currentDensity.density,
                    fontScale = currentDensity.fontScale * (fontScalePercent / 100f)
                )
            }

            MyApplicationTheme(themeMode = themeMode) {
                // Respect layout direction and custom percentage font scale without moving any UI components
                CompositionLocalProvider(
                    LocalLayoutDirection provides layoutDirection,
                    LocalDensity provides customDensity
                ) {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val coroutineScope = rememberCoroutineScope()
                    var selectedTab by remember { mutableStateOf(MainTab.CALENDAR) }
                    var showGuideDialog by remember { mutableStateOf(false) }
                    var showAppearanceDialog by remember { mutableStateOf(false) }

                    // Current season for app header rain effect
                    val todayG = remember { CalendarManager.getTodayGregorian() }
                    val todayJalali = remember { JalaliCalendar.gregorianToJalali(todayG.year, todayG.month, todayG.day) }
                    val currentSeason = remember(todayJalali.month) { JalaliCalendar.getSeasonPersian(todayJalali.month) }
                    val showSeasonalRain by userSettings.showSeasonalRain.collectAsState()

                    // Handle back press: close drawer if open, otherwise return to calendar
                    BackHandler(enabled = drawerState.isOpen || selectedTab != MainTab.CALENDAR) {
                        if (drawerState.isOpen) {
                            coroutineScope.launch { drawerState.close() }
                        } else {
                            selectedTab = MainTab.CALENDAR
                        }
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier
                                    .widthIn(max = 320.dp)
                                    .fillMaxHeight()
                                    .testTag("hamburger_drawer_sheet"),
                                drawerContainerColor = MaterialTheme.colorScheme.surface
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 16.dp, horizontal = 12.dp)
                                ) {
                                    // ---------------------------------------------
                                    // DRAWER HEADER (PUNY4 Calendar Branding)
                                    // ---------------------------------------------
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        CelestialBlue,
                                                        MaterialTheme.colorScheme.primary
                                                    )
                                                )
                                            )
                                            .padding(18.dp)
                                    ) {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color.Black,
                                                    modifier = Modifier.size(48.dp),
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
                                                    Text(
                                                        text = "PUNY4 Calendar",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                    Text(
                                                        text = if (isFa) "تقویم جامع و نجوم" else "Comprehensive Calendar & Astronomy",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // ---------------------------------------------
                                    // MAIN NAVIGATION ITEMS
                                    // ---------------------------------------------
                                    NavigationDrawerItem(
                                        icon = {
                                            Icon(
                                                imageVector = MainTab.CALENDAR.icon,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = MainTab.CALENDAR.title(appLanguage),
                                                fontWeight = if (selectedTab == MainTab.CALENDAR) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        selected = selectedTab == MainTab.CALENDAR,
                                        onClick = {
                                            selectedTab = MainTab.CALENDAR
                                            coroutineScope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier
                                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                                            .testTag("drawer_item_calendar")
                                    )

                                    NavigationDrawerItem(
                                        icon = {
                                            Icon(
                                                imageVector = MainTab.ASTRONOMY.icon,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = MainTab.ASTRONOMY.title(appLanguage),
                                                fontWeight = if (selectedTab == MainTab.ASTRONOMY) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        selected = selectedTab == MainTab.ASTRONOMY,
                                        onClick = {
                                            selectedTab = MainTab.ASTRONOMY
                                            coroutineScope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier
                                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                                            .testTag("drawer_item_astronomy")
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    )

                                    // Section title for Tools & Preferences
                                    Text(
                                        text = if (isFa) "ابزارها و تنظیمات" else "Tools & Settings",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                    )

                                    NavigationDrawerItem(
                                        icon = {
                                            Icon(
                                                imageVector = MainTab.CONVERTER.icon,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = MainTab.CONVERTER.title(appLanguage),
                                                fontWeight = if (selectedTab == MainTab.CONVERTER) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        selected = selectedTab == MainTab.CONVERTER,
                                        onClick = {
                                            selectedTab = MainTab.CONVERTER
                                            coroutineScope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier
                                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                                            .testTag("drawer_item_converter")
                                    )

                                    NavigationDrawerItem(
                                        icon = {
                                            Icon(
                                                imageVector = MainTab.SETTINGS.icon,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = MainTab.SETTINGS.title(appLanguage),
                                                fontWeight = if (selectedTab == MainTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        selected = selectedTab == MainTab.SETTINGS,
                                        onClick = {
                                            selectedTab = MainTab.SETTINGS
                                            coroutineScope.launch { drawerState.close() }
                                        },
                                        modifier = Modifier
                                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                                            .testTag("drawer_item_settings")
                                    )

                                    NavigationDrawerItem(
                                        icon = {
                                            val currentHolColorLong by userSettings.holidayColorLong.collectAsState()
                                            Icon(
                                                imageVector = Icons.Default.Palette,
                                                contentDescription = null,
                                                tint = Color(currentHolColorLong)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = if (isFa) "ویرایش ظاهر" else "Appearance",
                                                fontWeight = FontWeight.Normal
                                            )
                                        },
                                        selected = false,
                                        onClick = {
                                            coroutineScope.launch { drawerState.close() }
                                            showAppearanceDialog = true
                                        },
                                        modifier = Modifier
                                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                                            .testTag("drawer_item_appearance")
                                    )

                                    NavigationDrawerItem(
                                        icon = {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFF9333EA),
                                                border = BorderStroke(1.2.dp, Color(0xFFE879F9)),
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "!",
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 13.sp,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = if (isFa) "راهنمای برنامه" else "App Guide",
                                                fontWeight = FontWeight.Normal
                                            )
                                        },
                                        selected = false,
                                        onClick = {
                                            coroutineScope.launch { drawerState.close() }
                                            showGuideDialog = true
                                        },
                                        modifier = Modifier
                                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                                            .testTag("drawer_item_guide")
                                    )

                                    // Push version to the bottom of the drawer
                                    Spacer(modifier = Modifier.weight(1f))

                                    // ---------------------------------------------
                                    // DRAWER FOOTER (APP VERSION)
                                    // ---------------------------------------------
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp)
                                            .testTag("drawer_version_footer")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color.Black,
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Image(
                                                        painter = painterResource(id = R.drawable.calendar_astro_icon),
                                                        contentDescription = "PUNY4",
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "PUNY4 Calendar",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = if (isFa) "نسخه ۱.۰.۰" else "Version 1.0.0",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "v1.0.0",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("main_scaffold"),
                            topBar = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clipToBounds()
                                ) {
                                    // Seasonal falling emoji rain in top header where app name is written
                                    if (showSeasonalRain && selectedTab == MainTab.CALENDAR) {
                                        SeasonalRainOverlay(
                                            season = currentSeason,
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clipToBounds(),
                                            particleCount = 14
                                        )
                                    }

                                    TopAppBar(
                                        title = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color.Black,
                                                    modifier = Modifier.size(32.dp),
                                                    shadowElevation = 2.dp
                                                ) {
                                                    Image(
                                                        painter = painterResource(id = R.drawable.calendar_astro_icon),
                                                        contentDescription = "PUNY4 Logo",
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                                Text(
                                                    text = when (selectedTab) {
                                                        MainTab.CALENDAR -> "PUNY4 Calendar"
                                                        MainTab.ASTRONOMY -> if (isFa) "اطلاعات نجومی و رصد" else "Astronomy & Sky"
                                                        MainTab.CONVERTER -> if (isFa) "تبدیل تاریخ تقویم‌ها" else "Calendar Converter"
                                                        MainTab.SETTINGS -> if (isFa) "تنظیمات برنامه" else "App Settings"
                                                    },
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.titleLarge
                                                )
                                            }
                                        },
                                        navigationIcon = {
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                                    }
                                                },
                                                modifier = Modifier.testTag("hamburger_menu_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu,
                                                    contentDescription = if (isFa) "منوی برنامه" else "Menu"
                                                )
                                            }
                                        },
                                        actions = {
                                            if (selectedTab == MainTab.CALENDAR) {
                                                IconButton(
                                                    onClick = { showGuideDialog = true },
                                                    modifier = Modifier.testTag("top_bar_guide_button")
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color(0xFF9333EA),
                                                        border = BorderStroke(1.5.dp, Color(0xFFE879F9)),
                                                        shadowElevation = 3.dp,
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = "!",
                                                                fontWeight = FontWeight.Black,
                                                                fontSize = 15.sp,
                                                                color = Color.White
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                IconButton(
                                                    onClick = { selectedTab = MainTab.CALENDAR },
                                                    modifier = Modifier.testTag("return_to_calendar_button")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CalendarMonth,
                                                        contentDescription = if (isFa) "بازگشت به تقویم" else "Back to Calendar",
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        },
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                            // Note: bottomBar is intentionally completely omitted as requested:
                            // "و در پایین هیچ بخشی گزینه ای نباشه"
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (selectedTab) {
                                    MainTab.CALENDAR -> CalendarScreen(
                                        userSettings = userSettings,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    MainTab.ASTRONOMY -> AstronomyScreen(
                                        userSettings = userSettings,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    MainTab.CONVERTER -> DateConverterScreen(
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    MainTab.SETTINGS -> SettingsScreen(
                                        userSettings = userSettings,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }

                    if (showGuideDialog) {
                        AppGuideDialog(
                            isFa = isFa,
                            onDismiss = { showGuideDialog = false }
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
            }
        }
    }
}
