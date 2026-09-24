package com.manu156.levelup.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.manu156.levelup.R

class SessionForegroundService : Service() {

    companion object {
        const val ACTION_START = "com.manu156.levelup.START_SESSION"
        const val ACTION_STOP = "com.manu156.levelup.STOP_SESSION"
        const val EXTRA_TASK_TITLE = "task_title"
        const val EXTRA_START_TIME = "start_time"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "session_timer_channel"
    }

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val title = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Focus Session"
                val startTime = intent.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis())
                val notification = buildNotification(title, startTime)
                val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                } else {
                    0
                }
                ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, serviceType)
            }
            ACTION_STOP -> {
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun buildNotification(title: String, startTime: Long): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("Focus session in progress")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setUsesChronometer(true)
            .setWhen(startTime)
            .setShowWhen(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Session Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
