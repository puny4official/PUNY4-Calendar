package com.example.calendar.core

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

/** Home-screen widget: big Jalali day number with the month name underneath (Iran time). */
class DayWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { manager.updateAppWidget(it, build(context)) }
        DailyNotificationHelper.scheduleNextDayChange(context)
    }

    override fun onEnabled(context: Context) {
        DailyNotificationHelper.scheduleNextDayChange(context)
    }

    companion object {
        fun build(context: Context): RemoteViews {
            val g = CalendarManager.getTodayGregorian()
            val j = JalaliCalendar.jdnToJalali(JalaliCalendar.gregorianToJdn(g.year, g.month, g.day))
            val views = RemoteViews(context.packageName, R.layout.widget_day)
            views.setTextViewText(R.id.widget_day, j.day.toString())
            views.setTextViewText(R.id.widget_month, JalaliCalendar.MONTH_NAMES_PERSIAN[j.month - 1])
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            views.setOnClickPendingIntent(
                R.id.widget_root,
                PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            )
            return views
        }

        /** Refreshes all placed widgets; returns true if at least one exists. */
        fun updateAll(context: Context): Boolean {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, DayWidgetProvider::class.java))
            ids.forEach { mgr.updateAppWidget(it, build(context)) }
            return ids.isNotEmpty()
        }
    }
}
