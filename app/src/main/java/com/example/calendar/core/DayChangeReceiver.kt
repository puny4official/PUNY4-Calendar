package com.example.calendar.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Refreshes the pinned notification at Iran midnight, and after reboot, app update,
 * or a manual time / time-zone change (which would invalidate the scheduled alarm).
 */
class DayChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_RESTORE) {
            // Notification was removed (cleaner / cache clearing): bring it back 5 seconds later.
            DailyNotificationHelper.scheduleKeepAlive(context, 5_000L)
            return
        }
        DailyNotificationHelper.refreshFromSettings(context)
        // Keep the home-screen widget in sync and make sure the next midnight alarm stays scheduled.
        if (DayWidgetProvider.updateAll(context)) {
            DailyNotificationHelper.scheduleNextDayChange(context)
        }
    }

    companion object {
        const val ACTION_DAY_CHANGED = "com.puny4.calendar.ACTION_DAY_CHANGED"
        const val ACTION_RESTORE = "com.puny4.calendar.ACTION_RESTORE"
        const val ACTION_KEEP_ALIVE = "com.puny4.calendar.ACTION_KEEP_ALIVE"
    }
}
