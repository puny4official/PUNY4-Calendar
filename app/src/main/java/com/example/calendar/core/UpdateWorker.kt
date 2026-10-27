package com.example.calendar.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/** Periodically checks GitHub Releases and notifies the user once per new version. */
class UpdateWorker(private val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val current = runCatching {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
        }.getOrNull() ?: return Result.success()
        val res = UpdateChecker.check(current) ?: return Result.retry()
        if (!res.hasUpdate) return Result.success()

        val prefs = ctx.getSharedPreferences("update_prefs", Context.MODE_PRIVATE)
        if (prefs.getString("last_notified", null) == res.latest) return Result.success()

        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "بروزرسانی برنامه", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val open = PendingIntent.getActivity(
            ctx, 3001, Intent(Intent.ACTION_VIEW, Uri.parse(res.downloadUrl)),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("بروزرسانی جدید PUNY4 Calendar")
            .setContentText("نسخه‌ی ${res.latest} آماده‌ی دانلود است. برای دریافت لمس کنید.")
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        runCatching { nm.notify(NOTIF_ID, n) }
        prefs.edit().putString("last_notified", res.latest).apply()
        return Result.success()
    }

    companion object {
        private const val CHANNEL = "puny4_app_update"
        private const val NOTIF_ID = 1101

        fun schedule(context: Context) {
            val req = PeriodicWorkRequestBuilder<UpdateWorker>(15, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context.applicationContext)
                .enqueueUniquePeriodicWork("puny4_update_check", ExistingPeriodicWorkPolicy.UPDATE, req)
        }
    }
}
