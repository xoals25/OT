package com.example.ot.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ot.Activity.MainActivity
import com.example.ot.R


class WebrtcService : Service() {
    val TAG = "WebrtcService"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    fun startForegroundService() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val builder: NotificationCompat.Builder
        val CHANNEL_ID = "snwodeer_service_channel"
        builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("asd")
                .setContentTitle("asdd")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.heart) //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            val channel = NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(channel)
        } else {
            builder.setSmallIcon(R.mipmap.heart) // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        }
        startForeground(1, builder.build())

        sendMessage("start")
    }
    private fun sendMessage(message: String) {
        val intent = Intent("MoveServiceFilter")
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

}