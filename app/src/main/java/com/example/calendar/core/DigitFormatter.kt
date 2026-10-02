package com.example.calendar.core

/**
 * Digit and number formatter that converts English numerals to native Persian numerals (۰-۹)
 * when in Persian language mode, ensuring that Android renders all calendar numbers using the
 * user device's active system font (برگرفته از فونت پیش‌فرض و انتخابی خود گوشی).
 */
object DigitFormatter {
    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    // Pre-cached digit strings for 0..100: completely eliminates GC allocations during grid & list scroll
    private val PERSIAN_CACHED_STRINGS = Array(101) { num ->
        val s = num.toString()
        val sb = StringBuilder(s.length)
        for (c in s) {
            sb.append(PERSIAN_DIGITS[c - '0'])
        }
        sb.toString()
    }
    private val ENGLISH_CACHED_STRINGS = Array(101) { it.toString() }

    fun toSystemDigits(input: String, toPersian: Boolean = false): String {
        if (!toPersian) return input
        // Check if input is a simple small integer
        if (input.length <= 2 && input.all { it in '0'..'9' }) {
            val num = input.toIntOrNull()
            if (num != null && num in 0..100) {
                return PERSIAN_CACHED_STRINGS[num]
            }
        }
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
        if (number in 0..100) {
            return if (toPersian) PERSIAN_CACHED_STRINGS[number] else ENGLISH_CACHED_STRINGS[number]
        }
        return toSystemDigits(number.toString(), toPersian)
    }

    fun toSystemDigits(number: Long, toPersian: Boolean = false): String {
        if (number in 0L..100L) {
            return if (toPersian) PERSIAN_CACHED_STRINGS[number.toInt()] else ENGLISH_CACHED_STRINGS[number.toInt()]
        }
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
