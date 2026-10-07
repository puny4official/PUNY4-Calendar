package com.example.calendar.data

import android.content.Context
import android.content.SharedPreferences
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.model.AppLanguage
import com.example.calendar.model.CalendarType
import com.example.calendar.model.CityLocation
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSettings(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_calendar_prefs", Context.MODE_PRIVATE)

    private val _appLanguage = MutableStateFlow(loadAppLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _calendarType = MutableStateFlow(loadCalendarType())
    val calendarType: StateFlow<CalendarType> = _calendarType.asStateFlow()

    private val _selectedCity = MutableStateFlow(loadCity())
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _showSecondaryDates = MutableStateFlow(prefs.getBoolean("show_sec_dates_v2", false))
    val showSecondaryDates: StateFlow<Boolean> = _showSecondaryDates.asStateFlow()

    private val _showSeasonalRain = MutableStateFlow(prefs.getBoolean("show_seasonal_rain", false))
    val showSeasonalRain: StateFlow<Boolean> = _showSeasonalRain.asStateFlow()

    private val _showPinnedNotification = MutableStateFlow(prefs.getBoolean("show_pinned_notification", false))
    val showPinnedNotification: StateFlow<Boolean> = _showPinnedNotification.asStateFlow()

    private val _fontScalePercent = MutableStateFlow(prefs.getInt("font_scale_percent", 100))
    val fontScalePercent: StateFlow<Int> = _fontScalePercent.asStateFlow()

    private val _dayNumberScalePercent = MutableStateFlow(prefs.getInt("day_number_scale_percent", 100))
    val dayNumberScalePercent: StateFlow<Int> = _dayNumberScalePercent.asStateFlow()

    private val _useEnglishDayNumbers = MutableStateFlow(prefs.getBoolean("use_english_day_numbers", true))
    val useEnglishDayNumbers: StateFlow<Boolean> = _useEnglishDayNumbers.asStateFlow()

    private val _holidayColorLong = MutableStateFlow(prefs.getLong("holiday_color_long", 0xFF8B5CF6L))
    val holidayColorLong: StateFlow<Long> = _holidayColorLong.asStateFlow()

    private fun loadAppLanguage(): AppLanguage {
        val name = prefs.getString("app_language", AppLanguage.PERSIAN.name) ?: AppLanguage.PERSIAN.name
        return try {
            AppLanguage.valueOf(name)
        } catch (_: Exception) {
            AppLanguage.PERSIAN
        }
    }

    fun setAppLanguage(lang: AppLanguage) {
        prefs.edit().putString("app_language", lang.name).apply()
        _appLanguage.value = lang
    }

    private fun loadThemeMode(): ThemeMode {
        val name = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    private fun loadCalendarType(): CalendarType {
        val name = prefs.getString("calendar_type", CalendarType.SOLAR_HIJRI.name) ?: CalendarType.SOLAR_HIJRI.name
        return try {
            CalendarType.valueOf(name)
        } catch (_: Exception) {
            CalendarType.SOLAR_HIJRI
        }
    }

    fun setCalendarType(type: CalendarType) {
        prefs.edit().putString("calendar_type", type.name).apply()
        _calendarType.value = type
    }

    private fun loadCity(): CityLocation {
        val cityId = prefs.getString("city_id", "tehran") ?: "tehran"
        return AstronomicalCalculator.CITIES.find { it.id == cityId } ?: AstronomicalCalculator.CITIES[0]
    }

    fun setCity(city: CityLocation) {
        prefs.edit().putString("city_id", city.id).apply()
        _selectedCity.value = city
    }

    fun setShowSecondaryDates(show: Boolean) {
        prefs.edit().putBoolean("show_sec_dates_v2", show).apply()
        _showSecondaryDates.value = show
    }

    fun setShowSeasonalRain(show: Boolean) {
        prefs.edit().putBoolean("show_seasonal_rain", show).apply()
        _showSeasonalRain.value = show
    }

    fun setShowPinnedNotification(show: Boolean) {
        prefs.edit().putBoolean("show_pinned_notification", show).apply()
        _showPinnedNotification.value = show
    }

    fun setFontScalePercent(percent: Int) {
        val clamped = percent.coerceIn(70, 160)
        prefs.edit().putInt("font_scale_percent", clamped).apply()
        _fontScalePercent.value = clamped
    }

    fun setDayNumberScalePercent(percent: Int) {
        val clamped = percent.coerceIn(80, 160)
        prefs.edit().putInt("day_number_scale_percent", clamped).apply()
        _dayNumberScalePercent.value = clamped
    }

    fun setUseEnglishDayNumbers(enabled: Boolean) {
        prefs.edit().putBoolean("use_english_day_numbers", enabled).apply()
        _useEnglishDayNumbers.value = enabled
    }

    fun setHolidayColor(colorLong: Long) {
        prefs.edit().putLong("holiday_color_long", colorLong).apply()
        _holidayColorLong.value = colorLong
    }

    fun getNote(jdn: Long): String {
        return prefs.getString("note_$jdn", "") ?: ""
    }

    fun saveNote(jdn: Long, note: String) {
        if (note.isBlank()) {
            prefs.edit().remove("note_$jdn").apply()
        } else {
            prefs.edit().putString("note_$jdn", note.trim()).apply()
        }
    }
}
