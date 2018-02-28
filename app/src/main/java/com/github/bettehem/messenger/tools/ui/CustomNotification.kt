package com.github.bettehem.messenger.tools.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.github.bettehem.androidtools.notification.CustomNotification
import com.github.bettehem.messenger.MainActivity
import com.github.bettehem.messenger.R

fun notification(context: Context, title: String, message: String, isSecretMessage: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "messenger_notification"
        val channelName = "Messenger Notifications"
        val channelDescription = "Shows notifications when you get a message or request etc."
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = channelDescription

        //set notification light
        channel.enableLights(true)
        channel.lightColor = Color.MAGENTA
        channel.enableVibration(true)

        //create channel
        notificationManager.createNotificationChannel(channel)


        //create notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationBuilder.setContentTitle("New Message from " + title.split("enger - ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
        notificationBuilder.setContentText(message)
        notificationBuilder.setChannelId(channelId)
        notificationBuilder.setContentIntent(PendingIntent.getActivity(context, context.hashCode(), Intent(context, MainActivity::class.java), PendingIntent.FLAG_ONE_SHOT))

        val notification = notificationBuilder.build()

        notificationManager.notify(channel.id.hashCode(), notification)

    } else {
        //TODO: implement user icons
        //TODO: add settings check if has notifications disabled
        if (isSecretMessage) {
            //TODO: Remove hard-coded string
            CustomNotification.make(context, R.mipmap.ic_launcher, "New Message from " + title.split("enger - ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1], message, Intent(context, MainActivity::class.java), false, true).show()
        } else {
            CustomNotification.make(context, R.mipmap.ic_launcher, "New Message from " + title.split("enger - ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1], message, Intent(context, MainActivity::class.java), false, true).show()
        }
    }
}