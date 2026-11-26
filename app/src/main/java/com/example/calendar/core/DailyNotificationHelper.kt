package com.example.calendar.core

import android.app.AlarmManager
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

    const val CHANNEL_ID = "puny4_daily_calendar_channel_v4"
    const val NOTIFICATION_ID = 1001

    /**
     * Generates a freestanding, extra-large, extra-bold day number bitmap for the status bar icon.
     * Completely eliminates any outer circular ring as requested ("اون رو درشت کن عددش رو و از دایره خارج کن").
     */
    fun createDayNumberBitmap(context: Context, dayNumberStr: String): Bitmap =
        IconDigitFont.render(context, dayNumberStr, 128, 128, aspect = 0.46f, padFrac = 0.0f, gapFrac = 0.07f)

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "گاه‌شمار و تقویم امروز"
            val channelDesc = "نمایش پین‌شده و دائمی تاریخ روز در نوار اعلان"
            // Use IMPORTANCE_DEFAULT with sound/vibration disabled so it stays pinned and visible
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDesc
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
                enableLights(false)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            runCatching { notificationManager.deleteNotificationChannel("puny4_daily_calendar_channel_v3") }
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
        val dayIconBitmap = createDayNumberBitmap(context, dayNumberStr)
        val dayIconCompat = IconCompat.createWithBitmap(dayIconBitmap)

        val primaryType = runCatching {
            com.example.calendar.model.CalendarType.valueOf(
                context.applicationContext.getSharedPreferences("app_calendar_prefs", Context.MODE_PRIVATE)
                    .getString("calendar_type", null) ?: "SOLAR_HIJRI"
            )
        }.getOrDefault(com.example.calendar.model.CalendarType.SOLAR_HIJRI)

        val gregorianMonthName = if (isFa) {
            CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN[g.month - 1].substringBefore(" ")
        } else {
            java.time.Month.of(g.month).name.lowercase().replaceFirstChar { it.uppercase() }
        }
        val islamicMonthName = IslamicCalendar.MONTH_NAMES_ARABIC[i.month - 1]

        fun num(n: Int) = if (isFa) DigitFormatter.toSystemDigits(n, renderFaDigits) else n.toString()
        fun dateText(type: com.example.calendar.model.CalendarType): String = when (type) {
            com.example.calendar.model.CalendarType.SOLAR_HIJRI -> "${num(j.day)} $jalaliMonthName ${num(j.year)}"
            com.example.calendar.model.CalendarType.IMPERIAL ->
                "${num(j.day)} ${com.example.calendar.model.ANCIENT_MONTH_NAMES[j.month - 1]} ${num(j.year + com.example.calendar.model.IMPERIAL_YEAR_OFFSET)}"
            com.example.calendar.model.CalendarType.GREGORIAN -> "${num(g.day)} $gregorianMonthName ${num(g.year)}"
            com.example.calendar.model.CalendarType.LUNAR_HIJRI -> "${num(i.day)} $islamicMonthName ${num(i.year)}"
        }

        // The chosen calendar leads the title; the others follow below it (Solar Hijri first, then the rest)
        val shownWeekday = if (primaryType == com.example.calendar.model.CalendarType.IMPERIAL)
            com.example.calendar.model.ancientWeekdayLabel(weekdayName, JalaliCalendar.WEEKDAY_NAMES_PERSIAN) else weekdayName
        val title = if (isFa) "$shownWeekday ${dateText(primaryType)}" else "${dayInfo.dayOfWeekEnglish}, ${dateText(primaryType)}"
        val secDateStr = listOf(
            com.example.calendar.model.CalendarType.SOLAR_HIJRI,
            com.example.calendar.model.CalendarType.IMPERIAL,
            com.example.calendar.model.CalendarType.GREGORIAN,
            com.example.calendar.model.CalendarType.LUNAR_HIJRI
        )
            .filter { it != primaryType }
            .joinToString("  •  ") { dateText(it) }

        val holidayEvent = dayInfo.events.firstOrNull { it.isHoliday }?.title
        val eventTitle = holidayEvent ?: dayInfo.events.firstOrNull()?.title
        val holidayOrEvent = if (!eventTitle.isNullOrBlank()) {
            "مناسبت: $eventTitle"
        } else {
            if (isFa) "فصل: ${dayInfo.seasonPersian} ${dayInfo.seasonEmoji}" else "Season: ${dayInfo.seasonPersian} ${dayInfo.seasonEmoji}"
        }

        val bigTextBase = "$title\n$secDateStr\n"
        val bigText = android.text.SpannableStringBuilder(bigTextBase).apply {
            val start = length
            append(holidayOrEvent)
            if (holidayEvent != null) {
                val colorLong = context.applicationContext
                    .getSharedPreferences("app_calendar_prefs", Context.MODE_PRIVATE)
                    .getLong("holiday_color_long", 0xFF8B5CF6L)
                setSpan(
                    android.text.style.ForegroundColorSpan(colorLong.toInt()),
                    start, length, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(dayIconCompat)
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
            .setOnlyAlertOnce(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setShowWhen(false)
            .setWhen(System.currentTimeMillis() + 3153600000000L) // far-future timestamp: many ROMs sort same-priority notifications by time, so this keeps it first
            .setDeleteIntent(
                PendingIntent.getBroadcast(
                    context, 2003,
                    Intent(context, DayChangeReceiver::class.java).setAction(DayChangeReceiver.ACTION_RESTORE),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        PinnedCalendarService.show(context.applicationContext, builder.build())

        // Make sure the notification flips to the new day at Iran midnight, even if the app is closed.
        scheduleNextDayChange(context)
        scheduleKeepAlive(context)
    }

    fun cancelDailyNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        PinnedCalendarService.stop(context.applicationContext)
        notificationManager.cancel(NOTIFICATION_ID)
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(keepAlivePendingIntent(context))
        dayChangePendingIntent(context)?.let {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(it)
        }
    }

    /** Re-posts the notification using the saved settings (used by the midnight alarm and boot/time changes). */
    fun refreshFromSettings(context: Context) {
        val appContext = context.applicationContext
        val settings = com.example.calendar.data.UserSettings(appContext)
        if (settings.showPinnedNotification.value) {
            showPinnedDailyNotification(
                context = appContext,
                appLanguage = settings.appLanguage.value,
                useEnglishDigits = settings.useEnglishDayNumbers.value,
                city = settings.selectedCity.value
            )
        } else {
            cancelDailyNotification(appContext)
        }
    }

    private fun dayChangePendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, DayChangeReceiver::class.java).setAction(DayChangeReceiver.ACTION_DAY_CHANGED)
        return PendingIntent.getBroadcast(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Sets a one-shot alarm for the next midnight in Iran time (+2s so the date has surely rolled over). */
    private fun keepAlivePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DayChangeReceiver::class.java).setAction(DayChangeReceiver.ACTION_KEEP_ALIVE)
        return PendingIntent.getBroadcast(
            context, 2002, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Keeps the pinned notification alive: if a cleaner app / cache cleaning kills the app, this alarm
     * brings the notification back shortly afterwards (and keeps doing so every 15 minutes).
     */
    fun scheduleKeepAlive(context: Context, delayMs: Long = 5 * 60 * 1000L) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = keepAlivePendingIntent(context)
        val at = System.currentTimeMillis() + delayMs
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }
    }

    fun scheduleNextDayChange(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = dayChangePendingIntent(context) ?: return
        val triggerAt = CalendarManager.nextIranMidnightMillis() + 1000L
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } catch (_: SecurityException) {
            // Exact alarms not permitted on this device: fall back to an inexact one (can be a few minutes late).
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }
}
