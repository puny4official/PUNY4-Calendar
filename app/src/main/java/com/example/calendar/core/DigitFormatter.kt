package com.example.calendar.core

/**
 * Digit and number formatter that converts English numerals to native Persian numerals (۰-۹)
 * when in Persian language mode, ensuring that Android renders all calendar numbers using the
 * user device's active system font (برگرفته از فونت پیش‌فرض و انتخابی خود گوشی).
 */
object DigitFormatter {
    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toSystemDigits(input: String, isFa: Boolean = true): String {
        if (!isFa) return input
        val sb = StringBuilder(input.length)
        for (c in input) {
            if (c in '0'..'9') {
                sb.append(PERSIAN_DIGITS[c - '0'])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun toSystemDigits(number: Int, isFa: Boolean = true): String {
        return toSystemDigits(number.toString(), isFa)
    }

    fun toSystemDigits(number: Long, isFa: Boolean = true): String {
        return toSystemDigits(number.toString(), isFa)
    }

    fun toSystemDigits(number: Double, isFa: Boolean = true): String {
        val str = if (number % 1.0 == 0.0) {
            number.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.1f", number)
        }
        return toSystemDigits(str, isFa)
    }

    fun toSystemDigits(number: Float, isFa: Boolean = true): String {
        return toSystemDigits(number.toDouble(), isFa)
    }

    fun toSystemDigits(number: Number, isFa: Boolean = true): String {
        return toSystemDigits(number.toString(), isFa)
    }
}
