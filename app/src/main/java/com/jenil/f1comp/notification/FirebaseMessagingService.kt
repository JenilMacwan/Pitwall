package com.jenil.f1comp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jenil.f1comp.MainActivity
import com.jenil.f1comp.R
import androidx.core.net.toUri

class PitwallMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            val data = message.data
            val title = data["title"] ?: "Pitwall Update"
            val body = data["body"] ?: ""
            val type = data["type"]

            showNotification(title, body, type)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New FCM Token: $token")
    }

    private fun showNotification(title: String, body: String, type: String? = null) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val newChannelId = "f1_alerts_v3"
        val largeIconBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_app_icon)
        val soundUri =
            (android.content.ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.f1_car_2).toUri()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = android.app.NotificationChannel(
                newChannelId,
                "Race & Session Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "F1 updates, news, and session alerts"
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Context label based on the message type
        val categoryLabel = when (type) {
            "breaking_news" -> "📰 Breaking News"
            "live_event" -> "🏁 Race Control"
            "standings" -> "🏆 Championship Standings"
            else -> "Pitwall Update"
        }

        val notification = NotificationCompat.Builder(this, newChannelId)
            .setSmallIcon(R.drawable.ic_app_icon) // Guaranteed transparent vector
            .setLargeIcon(largeIconBitmap)
            .setColor(ContextCompat.getColor(this, R.color.f1_red))
            .setContentTitle(title)
            .setContentText(body)
            // BigTextStyle enables the user to expand the notification to read full text
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setSummaryText(categoryLabel)
                    .bigText(if (body.isNotEmpty()) "$title\n\n$body" else title)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}