package com.jenil.f1comp.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jenil.f1comp.MainActivity
import com.jenil.f1comp.R
import java.net.HttpURLConnection
import java.net.URL

class PitwallMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            val data = message.data
            val title = data["title"] ?: "Pitwall Update"
            val body = data["body"] ?: ""
            val type = data["type"]

            showNotification(title, body, type, data)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New FCM Token: $token")
    }

    private fun showNotification(
        title: String,
        body: String,
        type: String? = null,
        data: Map<String, String> = emptyMap()
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val newChannelId = "f1_alerts_v3"
        val largeIconBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_app_icon)
        val soundUri =
            (android.content.ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.f1_car_2).toUri()

        // 1. Handle Click Action: Browser for breaking news, In-App for others
        val articleUrl = data["url"]
        val pendingIntent = if (type == "breaking_news" && !articleUrl.isNullOrEmpty()) {
            val browserIntent = Intent(Intent.ACTION_VIEW, articleUrl.toUri()).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                browserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            val deepLinkUri = when (type) {
                "standings" -> "f1comp://standings".toUri()
                else -> "f1comp://home".toUri()
            }
            val appIntent = Intent(Intent.ACTION_VIEW, deepLinkUri, this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("fcm_click_tracking", type)
            }
            PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // 2. Notification Channel Setup
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

        // 3. Category Label & Image Download
        val categoryLabel = when (type) {
            "breaking_news" -> "📰 Breaking News"
            "live_event" -> "🏁 Race Control"
            "standings" -> "🏆 Championship Standings"
            else -> "Pitwall Update"
        }

        val articleImageUrl = data["image_url"] ?: data["image"]
        val downloadedBitmap = if (type == "breaking_news") getBitmapFromUrl(articleImageUrl) else null

        // 4. Style Selection: BigPictureStyle (if image exists) or BigTextStyle
        val notificationStyle: NotificationCompat.Style = if (downloadedBitmap != null) {
            NotificationCompat.BigPictureStyle()
                .bigPicture(downloadedBitmap)
                .bigLargeIcon(null as Bitmap?) // Removes thumbnail on expand so the full picture shines
                .setSummaryText(categoryLabel)
        } else {
            NotificationCompat.BigTextStyle()
                .setSummaryText(categoryLabel)
                .bigText(if (body.isNotEmpty()) "$title\n\n$body" else title)
        }

        // 5. Build and Show
        val notification = NotificationCompat.Builder(this, newChannelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setLargeIcon(largeIconBitmap)
            .setColor(ContextCompat.getColor(this, R.color.f1_red))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(notificationStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun getBitmapFromUrl(urlString: String?): Bitmap? {
        if (urlString.isNullOrEmpty()) return null
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connectTimeout = 4000
            connection.readTimeout = 4000
            connection.connect()
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("FCM", "Error downloading notification image: ${e.message}")
            null
        }
    }
}