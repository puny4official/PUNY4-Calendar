package com.example.calendar.core

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat

/**
 * Foreground service that keeps the calendar notification permanently pinned
 * (first position, next to the clock, not swipeable) until the user turns it off.
 */
class PinnedCalendarService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    /** Swiped away from recents / killed by a cleaner: come back quickly. */
    override fun onTaskRemoved(rootIntent: Intent?) {
        DailyNotificationHelper.scheduleKeepAlive(applicationContext, 1500L)
        super.onTaskRemoved(rootIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val n = pending
        if (n == null) {
            DailyNotificationHelper.refreshFromSettings(applicationContext)
            if (pending == null) { stopSelf(); return START_NOT_STICKY }
            return START_STICKY
        }
        try {
            if (Build.VERSION.SDK_INT >= 34) {
                ServiceCompat.startForeground(
                    this, DailyNotificationHelper.NOTIFICATION_ID, n,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(DailyNotificationHelper.NOTIFICATION_ID, n)
            }
        } catch (_: Exception) {
            stopSelf()
        }
        return START_STICKY
    }

    companion object {
        @Volatile var pending: Notification? = null
        @Volatile var running = false

        fun show(context: Context, n: Notification) {
            pending = n
            try {
                ContextCompat.startForegroundService(context, Intent(context, PinnedCalendarService::class.java))
            } catch (_: Exception) {
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager)
                    .notify(DailyNotificationHelper.NOTIFICATION_ID, n)
            }
        }

        fun stop(context: Context) {
            pending = null
            context.stopService(Intent(context, PinnedCalendarService::class.java))
        }
    }
}
