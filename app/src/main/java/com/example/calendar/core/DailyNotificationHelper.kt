package com.example.calendar.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.MainActivity
import com.example.R
import com.example.calendar.model.AppLanguage
import com.example.calendar.model.CityLocation

/**
 * Manages the persistent, pinned daily calendar notification in the Android status bar.
 * Displays today's solar date, gregorian date, lunar date, and events.
 * The small icon in the status bar dynamically shows today's day number.
 */
object DailyNotificationHelper {

    const val CHANNEL_ID = "puny4_daily_calendar_channel"
    const val NOTIFICATION_ID = 1001

    private fun createDayNumberBitmap(dayNumberStr: String): Bitmap {
        val size = 96
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val center = size / 2f

        // Draw clean circular ring border around the number
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 6.5f
        }
        val radius = center - (circlePaint.strokeWidth / 2f + 2f)
        canvas.drawCircle(center, center, radius, circlePaint)

        // Draw day number text centered inside the circle
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = if (dayNumberStr.length >= 2) 48f else 56f
        }

        // Auto-scale to ensure text fits with comfortable breathing room inside the circle
        val maxInnerWidth = (radius * 2f) * 0.74f
        var textWidth = textPaint.measureText(dayNumberStr)
        while (textWidth > maxInnerWidth && textPaint.textSize > 24f) {
            textPaint.textSize -= 1.5f
            textWidth = textPaint.measureText(dayNumberStr)
        }

        val fontMetrics = textPaint.fontMetrics
        val y = center - (fontMetrics.ascent + fontMetrics.descent) / 2f
        canvas.drawText(dayNumberStr, center, y, textPaint)

        return bitmap
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "گاه‌شمار و تقویم امروز"
            val channelDesc = "نمایش پین‌شده و دائمی تاریخ روز در نوار اعلان"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDesc
                setShowBadge(false)
                enableVibration(false)
                enableLights(false)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPinnedDailyNotification(
        context: Context,
        appLanguage: AppLanguage = AppLanguage.PERSIAN,
        useEnglishDigits: Boolean = true,
        city: CityLocation = AstronomicalCalculator.CITIES[0]
    ) {
        createNotificationChannel(context)

        val todayG = CalendarManager.getTodayGregorian()
        val todayJdn = JalaliCalendar.gregorianToJdn(todayG.year, todayG.month, todayG.day)
        val dayInfo = CalendarManager.getFullDayInfo(todayJdn, city)
        val isFa = (appLanguage == AppLanguage.PERSIAN)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val j = dayInfo.jalaliDate
        val g = dayInfo.gregorianDate
        val i = dayInfo.islamicDate

        val jalaliMonthName = JalaliCalendar.MONTH_NAMES_PERSIAN[j.month - 1]
        val weekdayName = dayInfo.dayOfWeekPersian
        val renderFaDigits = isFa && !useEnglishDigits

        val dayNumberStr = DigitFormatter.toSystemDigits(j.day, renderFaDigits)
        val dayIconBitmap = createDayNumberBitmap(dayNumberStr)
        val dayIconCompat = IconCompat.createWithBitmap(dayIconBitmap)

        val title = if (isFa) {
            val yearStr = DigitFormatter.toSystemDigits(j.year, renderFaDigits)
            "$weekdayName $dayNumberStr $jalaliMonthName $yearStr"
        } else {
            "${dayInfo.dayOfWeekEnglish}, ${j.day} $jalaliMonthName ${j.year}"
        }

        val gregorianMonthName = if (isFa) {
            CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN[g.month - 1].substringBefore(" ")
        } else {
            java.time.Month.of(g.month).name.lowercase().replaceFirstChar { it.uppercase() }
        }
        val islamicMonthName = IslamicCalendar.MONTH_NAMES_ARABIC[i.month - 1]

        val secDateStr = if (isFa) {
            val gDayStr = DigitFormatter.toSystemDigits(g.day, renderFaDigits)
            val gYearStr = DigitFormatter.toSystemDigits(g.year, renderFaDigits)
            val iDayStr = DigitFormatter.toSystemDigits(i.day, renderFaDigits)
            val iYearStr = DigitFormatter.toSystemDigits(i.year, renderFaDigits)
            "$gDayStr $gregorianMonthName $gYearStr  •  $iDayStr $islamicMonthName $iYearStr"
        } else {
            "${g.day} $gregorianMonthName ${g.year}  •  ${i.day} $islamicMonthName ${i.year}"
        }

        val holidayEvent = dayInfo.events.firstOrNull { it.isHoliday }?.title
        val eventTitle = holidayEvent ?: dayInfo.events.firstOrNull()?.title
        val holidayOrEvent = if (!eventTitle.isNullOrBlank()) {
            "مناسبت: $eventTitle"
        } else {
            if (isFa) "فصل: ${dayInfo.seasonPersian} ${dayInfo.seasonEmoji}" else "Season: ${dayInfo.seasonPersian} ${dayInfo.seasonEmoji}"
        }

        val bigText = "$title\n$secDateStr\n$holidayOrEvent"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(dayIconCompat)
            .setLargeIcon(dayIconBitmap)
            .setContentTitle(title)
            .setContentText(secDateStr)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Pinned in status bar
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun cancelDailyNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
