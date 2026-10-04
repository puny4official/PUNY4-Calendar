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
import com.example.calendar.model.AppLanguage
import com.example.calendar.model.CityLocation

/**
 * Manages the persistent, pinned daily calendar notification in the Android status bar.
 * Displays today's solar date, gregorian date, lunar date, and events.
 * The small icon in the status bar dynamically shows today's day number extra large, bold,
 * and without any surrounding circle, pinned persistently at the top.
 */
object DailyNotificationHelper {

    const val CHANNEL_ID = "puny4_daily_calendar_channel_v3"
    const val NOTIFICATION_ID = 1001

    /**
     * Generates a freestanding, extra-large, extra-bold day number bitmap for the status bar icon.
     * Completely eliminates any outer circular ring as requested ("اون رو درشت کن عددش رو و از دایره خارج کن").
     */
    private fun createDayNumberBitmap(dayNumberStr: String): Bitmap {
        val size = 128
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val center = size / 2f

        // Draw day number text extra bold and significantly enlarged, completely filling the icon area
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            // Extra large font size now that the bounding circle is eliminated
            textSize = if (dayNumberStr.length >= 2) 104f else 122f
        }

        // Allow text to occupy the full icon canvas safely
        val maxInnerWidth = size * 0.96f
        var textWidth = textPaint.measureText(dayNumberStr)
        while (textWidth > maxInnerWidth && textPaint.textSize > 32f) {
            textPaint.textSize -= 2f
            textWidth = textPaint.measureText(dayNumberStr)
        }

        val fontMetrics = textPaint.fontMetrics
        // Precise mathematical vertical centering
        val y = center - (fontMetrics.ascent + fontMetrics.descent) / 2f
        canvas.drawText(dayNumberStr, center, y, textPaint)

        return bitmap
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "گاه‌شمار و تقویم امروز"
            val channelDesc = "نمایش پین‌شده و دائمی تاریخ روز در نوار اعلان"
            // Use IMPORTANCE_DEFAULT with sound/vibration disabled so it stays pinned and visible
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDesc
                setShowBadge(false)
                setSound(null, null)
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
            .setOngoing(true) // Pinned and persistent (cannot be swiped away)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX) // High priority so it stays visible even when other notifications arrive
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true) // Silent so it does not disturb or make notification sounds
            .setSortKey("00_pinned_calendar")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun cancelDailyNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
