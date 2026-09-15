package com.manu156.levelup.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
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

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var notificationManager: NotificationManager
    private var timerRunnable: Runnable? = null
    private var currentTitle: String = ""

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
                currentTitle = title
                startForeground(NOTIFICATION_ID, buildNotification(title, startTime))
                startTimer(startTime)
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(startTime: Long) {
        timerRunnable = object : Runnable {
            override fun run() {
                notificationManager.notify(NOTIFICATION_ID, buildNotification(currentTitle, startTime))
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable!!)
    }

    private fun buildNotification(title: String, startTime: Long): Notification {
        val elapsedMs = System.currentTimeMillis() - startTime
        val totalSeconds = elapsedMs / 1000
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        val timeStr = buildString {
            if (h > 0) append("$h h ")
            append(String.format("%02d:%02d", m, s))
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("Active: $timeStr")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
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

    override fun onDestroy() {
        super.onDestroy()
        timerRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
