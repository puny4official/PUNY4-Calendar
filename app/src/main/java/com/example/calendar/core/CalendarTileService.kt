package com.example.calendar.core

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.MainActivity

/** Quick Settings tile that shows today's Jalali day number and opens the app on tap. */
class CalendarTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        refresh()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        refresh()
    }

    private fun refresh() {
        val tile = qsTile ?: return
        val g = CalendarManager.getTodayGregorian()
        val jdn = JalaliCalendar.gregorianToJdn(g.year, g.month, g.day)
        val j = JalaliCalendar.jdnToJalali(jdn)
        tile.icon = Icon.createWithBitmap(DailyNotificationHelper.createDayNumberBitmap(j.day.toString()))
        tile.label = "${j.day} ${JalaliCalendar.MONTH_NAMES_PERSIAN[j.month - 1]}"
        if (Build.VERSION.SDK_INT >= 29) tile.subtitle = "${j.year}"
        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
    }

    override fun onClick() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        if (Build.VERSION.SDK_INT >= 34) {
            startActivityAndCollapse(
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            )
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
