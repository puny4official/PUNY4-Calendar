package com.example.calendar.core

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
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

    private val handler = Handler(Looper.getMainLooper())
    private var ticking = false

    // Re-posts the notification every second with a fresh timestamp so it keeps ranking first
    // (new notifications can't push it out of the status bar). Paused while the screen is off.
    private val tick = object : Runnable {
        override fun run() {
            val n = pending
            if (n != null) {
                try {
                    n.`when` = System.currentTimeMillis() + 3153600000000L
                    (getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager)
                        .notify(DailyNotificationHelper.NOTIFICATION_ID, n)
                } catch (_: Exception) { }
            }
            if (ticking) handler.postDelayed(this, 1000L)
        }
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> startTicking()
                Intent.ACTION_SCREEN_OFF -> stopTicking()
            }
        }
    }

    private fun startTicking() {
        if (ticking) return
        ticking = true
        handler.postDelayed(tick, 1000L)
    }

    private fun stopTicking() {
        ticking = false
        handler.removeCallbacks(tick)
    }

    override fun onCreate() {
        super.onCreate()
        try {
            registerReceiver(screenReceiver, IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON); addAction(Intent.ACTION_SCREEN_OFF)
            })
        } catch (_: Exception) { }
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (pm.isInteractive) startTicking()
    }

    override fun onDestroy() {
        stopTicking()
        try { unregisterReceiver(screenReceiver) } catch (_: Exception) { }
        super.onDestroy()
    }

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
