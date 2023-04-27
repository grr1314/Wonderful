package com.lc.nativelib

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build

internal object Notifications {

    @Suppress("LongParameterList")
    fun showNotification(
        context: Context,
        contentTitle: CharSequence,
        contentText: CharSequence,
        pendingIntent: PendingIntent?,
        notificationId: Int,
        type: NotificationType
    ) {
        if (!Utils.canShowNotification(context)) {
            return
        }
        val builder = Notification.Builder(context)
            .setContentText(contentText)
            .setContentTitle(contentTitle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notification =
            buildNotification(context, builder, type)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun buildNotification(
        context: Context,
        builder: Notification.Builder,
        type: NotificationType
    ): Notification {
        builder.setSmallIcon(R.drawable.leak_canary_leak)
            .setWhen(System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notificationChannel: NotificationChannel? =
                notificationManager.getNotificationChannel(type.name)
            if (notificationChannel == null) {
                val channelName = context.getString(type.nameResId)
                notificationChannel =
                    NotificationChannel(type.name, channelName, type.importance)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            builder.setChannelId(type.name)
        }

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            @Suppress("DEPRECATION")
            builder.notification
        } else {
            builder.build()
        }
    }

}