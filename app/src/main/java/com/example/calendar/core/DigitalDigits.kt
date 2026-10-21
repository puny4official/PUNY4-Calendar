package com.example.calendar.core

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

/** Renders numbers as bold seven-segment ("digital clock") digits into a bitmap. */
object DigitalDigits {
    // segments: a(top) b(top-right) c(bottom-right) d(bottom) e(bottom-left) f(top-left) g(middle)
    private val MAP = arrayOf(
        "abcdef", "bc", "abdeg", "abcdg", "bcfg", "acdfg", "acdefg", "abc", "abcdefg", "abcdfg"
    )

    fun render(text: String, widthPx: Int, heightPx: Int, color: Int = Color.WHITE, glow: Boolean = false,
               aspect: Float = 0.58f, padFrac: Float = 0.04f, gapFrac: Float = 0.10f): Bitmap {
        val bmp = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val digits = text.filter { it in '0'..'9' }.ifEmpty { "0" }
        val n = digits.length
        val pad = heightPx * padFrac
        val gapBetween = heightPx * gapFrac
        val availW = widthPx - 2 * pad - gapBetween * (n - 1)
        // digit aspect ~ 0.58 (w/h); fit to both bounds
        val dh = minOf(heightPx - 2 * pad, availW / n / aspect)
        val dw = dh * aspect
        val totalW = dw * n + gapBetween * (n - 1)
        var x0 = (widthPx - totalW) / 2f
        val y0 = (heightPx - dh) / 2f
        val t = dw * 0.14f            // segment thickness (slim)
        val g = t * 0.12f             // tiny gap between segments
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.FILL }
        val r = t / 2f

        for (ch in digits) {
            val on = MAP[ch - '0']
            val x1 = x0 + dw
            val ym = y0 + dh / 2f
            fun seg(c: Char, rect: RectF) {
                if (on.contains(c)) canvas.drawRoundRect(rect, r, r, paint)
            }
            seg('a', RectF(x0 + t * 0.55f, y0, x1 - t * 0.55f, y0 + t))
            seg('g', RectF(x0 + t * 0.55f, ym - t / 2f, x1 - t * 0.55f, ym + t / 2f))
            seg('d', RectF(x0 + t * 0.55f, y0 + dh - t, x1 - t * 0.55f, y0 + dh))
            seg('f', RectF(x0, y0 + t * 0.55f + g, x0 + t, ym - t * 0.45f - g))
            seg('b', RectF(x1 - t, y0 + t * 0.55f + g, x1, ym - t * 0.45f - g))
            seg('e', RectF(x0, ym + t * 0.45f + g, x0 + t, y0 + dh - t * 0.55f - g))
            seg('c', RectF(x1 - t, ym + t * 0.45f + g, x1, y0 + dh - t * 0.55f - g))
            x0 += dw + gapBetween
        }
        return bmp
    }
}
