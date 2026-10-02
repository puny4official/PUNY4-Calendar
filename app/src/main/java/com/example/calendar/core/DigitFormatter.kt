package com.example.calendar.core

/**
 * Digit and number formatter that converts English numerals to native Persian numerals (۰-۹)
 * when in Persian language mode, ensuring that Android renders all calendar numbers using the
 * user device's active system font (برگرفته از فونت پیش‌فرض و انتخابی خود گوشی).
 */
object DigitFormatter {
    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toSystemDigits(input: String, toPersian: Boolean = false): String {
        if (!toPersian) return input
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

    fun toSystemDigits(number: Int, toPersian: Boolean = false): String {
        return toSystemDigits(number.toString(), toPersian)
    }

    fun toSystemDigits(number: Long, toPersian: Boolean = false): String {
        return toSystemDigits(number.toString(), toPersian)
    }

    fun toSystemDigits(number: Double, toPersian: Boolean = false): String {
        val str = if (number % 1.0 == 0.0) {
            number.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.1f", number)
        }
        return toSystemDigits(str, toPersian)
    }

    fun toSystemDigits(number: Float, toPersian: Boolean = false): String {
        return toSystemDigits(number.toDouble(), toPersian)
    }

    fun toSystemDigits(number: Number, toPersian: Boolean = false): String {
        return toSystemDigits(number.toString(), toPersian)
    }
}
