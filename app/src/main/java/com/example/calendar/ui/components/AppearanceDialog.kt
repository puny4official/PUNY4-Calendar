package com.example.calendar.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calendar.data.UserSettings
import com.example.ui.theme.ThemeMode

data class PresetHolidayColor(
    val namePersian: String,
    val nameEnglish: String,
    val colorLong: Long
)

val PRESET_HOLIDAY_COLORS = listOf(
    PresetHolidayColor("بنفش رویایی", "Dreamy Purple", 0xFF8B5CF6L),
    PresetHolidayColor("یاقوتی درباری", "Royal Ruby", 0xFFE11D48L),
    PresetHolidayColor("زرشکی اصیل", "Crimson Red", 0xFFBE123CL),
    PresetHolidayColor("قرمز شعله‌ای", "Flame Red", 0xFFEF4444L),
    PresetHolidayColor("نارنجی مرجانی", "Coral Orange", 0xFFF97316L),
    PresetHolidayColor("کهربایی طلایی", "Amber Gold", 0xFFD97706L),
    PresetHolidayColor("یشمی ایرانی", "Persian Jade", 0xFF059669L),
    PresetHolidayColor("زمردی باطراوت", "Emerald Green", 0xFF10B981L),
    PresetHolidayColor("سبز پسته‌ای", "Teal Green", 0xFF14B8A6L),
    PresetHolidayColor("فیروزه‌ای اصیل", "Persian Turquoise", 0xFF06B6D4L),
    PresetHolidayColor("آبی آسمانی", "Sky Blue", 0xFF0EA5E9L),
    PresetHolidayColor("لاجوردی ایرانی", "Persian Azure", 0xFF2563EBL),
    PresetHolidayColor("آبی کاربنی", "Cobalt Navy", 0xFF1D4ED8L),
    PresetHolidayColor("نیلی شب", "Midnight Indigo", 0xFF6366F1L),
    PresetHolidayColor("سرخابی فانتزی", "Fuchsia Pink", 0xFFD946EFL),
    PresetHolidayColor("شرابی کلاسیک", "Plum Wine", 0xFF881337L)
)

private fun hsvToColorLong(hue: Float, saturation: Float, value: Float): Long {
    val hsv = floatArrayOf(hue.coerceIn(0f, 360f), saturation.coerceIn(0f, 1f), value.coerceIn(0f, 1f))
    val argb = android.graphics.Color.HSVToColor(hsv)
    return argb.toLong() and 0xFFFFFFFFL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceDialog(
    userSettings: UserSettings,
    isFa: Boolean,
    onDismiss: () -> Unit
) {
    val themeMode by userSettings.themeMode.collectAsState()
    val currentHolidayColorLong by userSettings.holidayColorLong.collectAsState()
    val activeColor = remember(currentHolidayColorLong) { Color(currentHolidayColorLong) }
    val fontScalePercent by userSettings.fontScalePercent.collectAsState()
    val dayNumberScalePercent by userSettings.dayNumberScalePercent.collectAsState()

    var selectedHue by remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(activeColor.toArgb(), hsv)
        mutableFloatStateOf(hsv[0])
    }
    var selectedSaturation by remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(activeColor.toArgb(), hsv)
        mutableFloatStateOf(if (hsv[1] < 0.1f) 0.85f else hsv[1])
    }

    val animatedColor by animateColorAsState(
        targetValue = activeColor,
        label = "previewColor"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("appearance_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = animatedColor.copy(alpha = 0.20f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = animatedColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isFa) "ویرایش ظاهر و تم تقویم" else "Appearance & Calendar Theme",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isFa) "شخصی‌سازی حالت پوسته و رنگ تعطیلات" else "Customize theme mode & holiday style",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isFa) "بستن" else "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. THEME MODE SELECTION (حالت روشن / تاریک امولد / سیستم)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("appearance_theme_mode_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isFa) "حالت پوسته برنامه (روشن / تاریک AMOLED):" else "App Theme Mode (Light / AMOLED Black):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val themeOptions = listOf(
                                    Triple(ThemeMode.LIGHT, Icons.Default.LightMode, if (isFa) "روشن" else "Light"),
                                    Triple(ThemeMode.DARK, Icons.Default.DarkMode, if (isFa) "تاریک (امولد)" else "Dark (AMOLED)"),
                                    Triple(ThemeMode.SYSTEM, Icons.Default.SettingsBrightness, if (isFa) "سیستم" else "System")
                                )

                                themeOptions.forEach { (mode, icon, label) ->
                                    val isSelected = themeMode == mode
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { userSettings.setThemeMode(mode) }
                                            .testTag("theme_btn_${mode.name.lowercase()}"),
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) animatedColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            width = if (isSelected) 1.8.dp else 1.dp,
                                            color = if (isSelected) animatedColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) animatedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                softWrap = false,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. LIVE PREVIEW CARD
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isFa) "پیش‌نمایش زنده در تقویم:" else "Live Calendar Preview:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Sample calendar day cell with chosen color
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.5.dp, animatedColor.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                            .background(animatedColor.copy(alpha = 0.50f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = if (isFa) "۲۲" else "22",
                                                fontFamily = FontFamily.Default,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (isFa) "جمعه" else "Friday",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White.copy(alpha = 0.90f)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isFa) "خانه روز تعطیل" else "Holiday Cell",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Sample badge card
                                Column(
                                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = animatedColor.copy(alpha = 0.50f),
                                        border = BorderStroke(1.2.dp, animatedColor.copy(alpha = 0.70f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White)
                                            )
                                            Text(
                                                text = if (isFa) "تعطیل رسمی کشور" else "Official National Holiday",
                                                color = Color.White,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(
                                        text = if (isFa) "کادر شیشه‌ای و نوشته سفید هماهنگ با انتخاب شما" else "Glassy container with pure white text",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. PALETTE PRESETS (انواع رنگ‌ها)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isFa) "پالت رنگ‌های آماده برای تعطیلات رسمی:" else "Preset Holiday Colors:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(
                            onClick = {
                                userSettings.setHolidayColor(0xFF8B5CF6L)
                                selectedHue = 265f
                                selectedSaturation = 0.62f
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFa) "بنفش پیش‌فرض" else "Default Purple",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grid of 16 colors (4 columns)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PRESET_HOLIDAY_COLORS.chunked(4).forEach { rowColors ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowColors.forEach { item ->
                                    val isSelected = (currentHolidayColorLong == item.colorLong)
                                    val itemColor = Color(item.colorLong)

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(64.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                userSettings.setHolidayColor(item.colorLong)
                                                val hsv = FloatArray(3)
                                                android.graphics.Color.colorToHSV(itemColor.toArgb(), hsv)
                                                selectedHue = hsv[0]
                                                selectedSaturation = hsv[1]
                                            }
                                            .testTag("color_preset_${item.colorLong.toString(16)}"),
                                        shape = RoundedCornerShape(12.dp),
                                        color = itemColor.copy(alpha = if (isSelected) 0.85f else 0.45f),
                                        border = BorderStroke(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) Color.White else itemColor.copy(alpha = 0.8f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clip(CircleShape)
                                                        .background(itemColor)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (isFa) item.namePersian else item.nameEnglish,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = Color.White,
                                                maxLines = 1,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. CUSTOM COLOR SLIDER (طیف دلخواه)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Colorize,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isFa) "انتخاب رنگ دلخواه و نامحدود" else "Custom Color Slider",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = animatedColor,
                                    modifier = Modifier.size(width = 44.dp, height = 24.dp)
                                ) {}
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Rainbow gradient track for Hue
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                Color.Red,
                                                Color.Yellow,
                                                Color.Green,
                                                Color.Cyan,
                                                Color.Blue,
                                                Color.Magenta,
                                                Color.Red
                                            )
                                        )
                                    )
                            )

                            Slider(
                                value = selectedHue,
                                onValueChange = { hue ->
                                    selectedHue = hue
                                    val newColorLong = hsvToColorLong(hue, selectedSaturation, 0.90f)
                                    userSettings.setHolidayColor(newColorLong)
                                },
                                valueRange = 0f..360f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("hue_slider")
                            )

                            Text(
                                text = if (isFa) "با حرکت دادن اسلایدر بالا می‌توانید دقیقاً هر رنگی که می‌پسندید را اعمال نمایید." else "Slide across the rainbow to select any custom hue.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. DAY NUMBERS FONT SIZE (افزایش سایز اعداد فقط و فقط اعداد روز)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("appearance_day_numbers_size_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isFa) "سایز اعداد روزهای تقویم (فقط اعداد روز):" else "Calendar Day Numbers Size (Day Numbers Only):",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isFa) "بزرگ کردن اعداد بدون تغییر یا به هم خوردن ظاهر تقویم" else "Enlarge day numbers cleanly without distorting layout",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = animatedColor.copy(alpha = 0.20f)
                                ) {
                                    Text(
                                        text = if (isFa) "$dayNumberScalePercent٪" else "$dayNumberScalePercent%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = animatedColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Stepper Row with Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalIconButton(
                                    onClick = { userSettings.setDayNumberScalePercent(dayNumberScalePercent - 5) },
                                    enabled = dayNumberScalePercent > 80,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "کاهش سایز اعداد")
                                }

                                Slider(
                                    value = dayNumberScalePercent.toFloat(),
                                    onValueChange = { userSettings.setDayNumberScalePercent(it.toInt()) },
                                    valueRange = 80f..160f,
                                    steps = 15,
                                    modifier = Modifier.weight(1f)
                                )

                                FilledTonalIconButton(
                                    onClick = { userSettings.setDayNumberScalePercent(dayNumberScalePercent + 5) },
                                    enabled = dayNumberScalePercent < 160,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "افزایش سایز اعداد")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Day Numbers Quick Presets
                            val dayPresets = listOf(
                                90 to if (isFa) "۹۰٪ کوچک" else "90%",
                                100 to if (isFa) "۱۰۰٪ پیش‌فرض" else "100%",
                                115 to if (isFa) "۱۱۵٪ متوسط" else "115%",
                                130 to if (isFa) "۱۳۰٪ بزرگ" else "130%",
                                145 to if (isFa) "۱۴۵٪ خیلی بزرگ" else "145%"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                dayPresets.forEach { (percent, label) ->
                                    val isSelected = (dayNumberScalePercent == percent)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) animatedColor else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) animatedColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { userSettings.setDayNumberScalePercent(percent) }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 10.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. TEXT FONT SCALE (فقط برای متن‌ها)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("appearance_text_font_scale_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isFa) "بزرگی متن‌ها و نوشته‌ها (فقط متن):" else "Text Font Size (Texts Only):",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isFa) "تنظیم اندازه نوشته‌ها و توضیحات بدون تغییر در اعداد روز" else "Scale text descriptions and labels only",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                                ) {
                                    Text(
                                        text = if (isFa) "$fontScalePercent٪" else "$fontScalePercent%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Stepper Row with Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalIconButton(
                                    onClick = { userSettings.setFontScalePercent(fontScalePercent - 5) },
                                    enabled = fontScalePercent > 70,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "کاهش اندازه متن")
                                }

                                Slider(
                                    value = fontScalePercent.toFloat(),
                                    onValueChange = { userSettings.setFontScalePercent(it.toInt()) },
                                    valueRange = 70f..160f,
                                    steps = 17,
                                    modifier = Modifier.weight(1f)
                                )

                                FilledTonalIconButton(
                                    onClick = { userSettings.setFontScalePercent(fontScalePercent + 5) },
                                    enabled = fontScalePercent < 160,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "افزایش اندازه متن")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Text Presets
                            val textPresets = listOf(
                                85 to if (isFa) "۸۵٪ کوچک" else "85%",
                                100 to if (isFa) "۱۰۰٪ پیش‌فرض" else "100%",
                                115 to if (isFa) "۱۱۵٪ متوسط" else "115%",
                                130 to if (isFa) "۱۳۰٪ بزرگ" else "130%",
                                145 to if (isFa) "۱۴۵٪ خیلی بزرگ" else "145%"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                textPresets.forEach { (percent, label) ->
                                    val isSelected = (fontScalePercent == percent)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { userSettings.setFontScalePercent(percent) }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 10.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("appearance_done_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animatedColor
                    )
                ) {
                    Text(
                        text = if (isFa) "تایید و اعمال تغییرات" else "Done & Apply",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
