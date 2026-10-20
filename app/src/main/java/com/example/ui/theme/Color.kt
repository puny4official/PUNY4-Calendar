package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Cosmic Dark Theme Palette

// Primary — Neon Purple
val PrimaryDark = Color(0xFF8B5CFF)
val OnPrimaryDark = Color(0xFFFFFFFF)
val PrimaryContainerDark = Color(0xFF35146E)
val OnPrimaryContainerDark = Color(0xFFE9DDFF)

val PrimaryLight = Color(0xFF7B4DFF)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE9DDFF)
val OnPrimaryContainerLight = Color(0xFF24104A)

// Secondary — Electric Blue
val SecondaryDark = Color(0xFF4D8DFF)
val OnSecondaryDark = Color(0xFFFFFFFF)
val SecondaryContainerDark = Color(0xFF18356B)
val OnSecondaryContainerDark = Color(0xFFDCE8FF)

val SecondaryLight = Color(0xFF356FE0)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFDCE8FF)
val OnSecondaryContainerLight = Color(0xFF102653)

// Tertiary — Cosmic Violet
val TertiaryDark = Color(0xFFB58CFF)
val OnTertiaryDark = Color(0xFF241044)
val TertiaryContainerDark = Color(0xFF4A287A)
val OnTertiaryContainerDark = Color(0xFFEBDDFF)

val TertiaryLight = Color(0xFF7045C5)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFEBDDFF)
val OnTertiaryContainerLight = Color(0xFF27134A)

// Background & Surface — Cosmic Black
val BackgroundDark = Color(0xFF000000)
val SurfaceDark = Color(0xFF000000)
val SurfaceVariantDark = Color(0xFF121212)
val OnBackgroundDark = Color(0xFFF8F5FF)
val OnSurfaceDark = Color(0xFFF8F5FF)
val OnSurfaceVariantDark = Color(0xFFB5B5B5)

// Light theme
val BackgroundLight = Color(0xFFFFFFFF)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF2F2F2)
val OnBackgroundLight = Color(0xFF17121F)
val OnSurfaceLight = Color(0xFF17121F)
val OnSurfaceVariantLight = Color(0xFF5F5F5F)

// PUNY4 Cosmic Colors
val CelestialBlue = Color(0xFF4D8DFF)

/** The user-chosen accent / holiday colour (default purple). Everything "purple" in the UI follows it. */
val LocalAccentColor = androidx.compose.runtime.compositionLocalOf { Color(0xFF8B5CFF) }

val HolidayPurple: Color
    @androidx.compose.runtime.Composable @androidx.compose.runtime.ReadOnlyComposable get() = LocalAccentColor.current
val HolidayPurpleLight: Color
    @androidx.compose.runtime.Composable @androidx.compose.runtime.ReadOnlyComposable
    get() = androidx.compose.ui.graphics.lerp(LocalAccentColor.current, Color.White, 0.22f)
val HolidayPurpleContainer: Color
    @androidx.compose.runtime.Composable @androidx.compose.runtime.ReadOnlyComposable
    get() = androidx.compose.ui.graphics.lerp(LocalAccentColor.current, Color.Black, 0.68f)
val OnHolidayPurpleContainer: Color
    @androidx.compose.runtime.Composable @androidx.compose.runtime.ReadOnlyComposable
    get() = androidx.compose.ui.graphics.lerp(LocalAccentColor.current, Color.White, 0.86f)

val HolidayPink = Color(0xFFEC4899)
val HolidayPinkLight = Color(0xFFF472B6)

val HolidayRed = Color(0xFFEF4444)
val HolidayRedContainer = Color(0xFF4A1118)

val AstroGold = Color(0xFFF5B942)
val MoonPaleYellow = Color(0xFFF9E29C)
val MoonSilver = MoonPaleYellow

val ScorpioAlert = Color(0xFFDC2626)
val SuccessGreen = Color(0xFF10B981)
