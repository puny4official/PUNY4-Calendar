package com.example.calendar.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.calendar.model.AppLanguage
import com.example.calendar.model.CityLocation

/**
 * Manages the persistent, pinned daily calendar notification in the Android status bar.
 * Displays today's solar date, gregorian date, lunar date, and events.
 */
object DailyNotificationHelper {

    const val CHANNEL_ID = "puny4_daily_calendar_channel"
    const val NOTIFICATION_ID = 1001

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

        val title = if (isFa) {
            val dayStr = DigitFormatter.toSystemDigits(j.day, renderFaDigits)
            val yearStr = DigitFormatter.toSystemDigits(j.year, renderFaDigits)
            "$weekdayName $dayStr $jalaliMonthName $yearStr"
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
            .setSmallIcon(R.drawable.ic_calendar_notification)
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
