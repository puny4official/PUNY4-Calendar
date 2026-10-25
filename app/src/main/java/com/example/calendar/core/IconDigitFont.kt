package com.example.calendar.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

/** Font choices for the day number drawn in the widget, notification small icon and quick-settings tile. */
enum class IconDigitFont(val id: String, val fa: String, val en: String, val asset: String? = null) {
    DIGITAL("digital", "دیجیتالی", "Digital"),
    POPPINS("poppins", "پاپینز", "Poppins", "fonts/poppins_bold.ttf"),
    LIGHT("light", "باریک", "Light", "fonts/poppins_light.ttf"),
    ITALIC("italic", "مورب", "Italic", "fonts/poppins_italic.ttf"),
    GEOMETRIC("geometric", "هندسی", "Geometric", "fonts/adventor.otf"),
    BLACK("black", "ضخیم", "Black", "fonts/inter_black.otf");

    companion object {
        fun fromId(id: String?): IconDigitFont = values().firstOrNull { it.id == id } ?: DIGITAL
        fun current(context: Context): IconDigitFont = fromId(
            context.applicationContext.getSharedPreferences("app_calendar_prefs", Context.MODE_PRIVATE)
                .getString("icon_digit_font", null)
        )

        /** Renders [text] centred in a [w]x[h] white bitmap using the user's chosen font. */
        fun render(
            context: Context, text: String, w: Int, h: Int,
            aspect: Float = 0.58f, padFrac: Float = 0.04f, gapFrac: Float = 0.10f
        ): Bitmap {
            val font = current(context)
            if (font == DIGITAL) return DigitalDigits.render(text, w, h, aspect = aspect, padFrac = padFrac, gapFrac = gapFrac)
            val tf = runCatching { Typeface.createFromAsset(context.applicationContext.assets, font.asset!!) }
                .getOrDefault(Typeface.DEFAULT_BOLD)
            val latin = text.map { c ->
                when (c) {
                    in '۰'..'۹' -> '0' + (c - '۰')
                    in '٠'..'٩' -> '0' + (c - '٠')
                    else -> c
                }
            }.joinToString("")
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; typeface = tf; textSize = h * 1f }
            val bounds = android.graphics.Rect()
            paint.getTextBounds(latin, 0, latin.length, bounds)
            val scale = minOf(w * 0.96f / bounds.width().coerceAtLeast(1), h * 0.92f / bounds.height().coerceAtLeast(1))
            paint.textSize = h * scale
            paint.getTextBounds(latin, 0, latin.length, bounds)
            canvas.drawText(latin, w / 2f - bounds.exactCenterX(), h / 2f - bounds.exactCenterY(), paint)
            return bmp
        }
    }
}
