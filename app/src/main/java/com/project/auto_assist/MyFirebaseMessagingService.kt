package com.project.auto_assist


import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//
//const val CHANNEL_ID = "notification_channel"
//const val CHANNEL_NAME = "com.project.i212582_i210607"
//class FirebaseMessageReceiver : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        if(remoteMessage.notification == null) return
//        super.onMessageReceived(remoteMessage)
//        val title = remoteMessage.notification?.title
//        val message = remoteMessage.notification?.body
//        generateNotification(title!!, message!!)
//    }
//
//    fun getRemoteView(title: String, message: String): RemoteViews {
//        val remoteView = RemoteViews("com.project.i212582_i210607", R.layout.notification)
//        remoteView.setTextViewText(R.id.title, title)
//        remoteView.setTextViewText(R.id.Message, message)
//        remoteView.setImageViewResource(R.id.imageView, R.drawable.ic_launcher_foreground)
//        return remoteView
//    }
//
//    fun generateNotification(title: String, message: String) {
//
//        val intent = Intent(this, Home::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//
//        var builder:NotificationCompat.Builder = NotificationCompat
//            .Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setAutoCancel(true)
//            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//
//        builder = builder.setContent(getRemoteView(title, message))
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0, builder.build())
//
//    }
//}

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extract notification data
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val data = remoteMessage.data

        // Handle notification data (e.g., show notification, perform actions)
        if (title != null && body != null) {
            showNotification(title, body)
        } else {
            // Handle data-only payloads (optional)
            if (data.isNotEmpty()) {
                // Access data key-value pairs
                val someDataValue = data["key"] // Replace "key" with the actual key from your notification
                // Perform actions based on the data
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private const val CHANNEL_ID = "fcm_channel"
        private const val NOTIFICATION_ID = 1
    }
}

