package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.screens.DetailActivity
import com.udacity.screens.MainActivity

const val CHANNEL_ID = "channelId"
private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 1901

fun NotificationManager.sendNotification(message: String, applicationContext: Context) {
    val pendingIntent = appPendingIntent(applicationContext)
    val detailsPendingIntent: PendingIntent? =
        detailsActivityPendingIntent(
            applicationContext,
            message,
            applicationContext.getString(R.string.downloadingNotification)
        )

    // Build the notification
    val builder =
        notificationBuilder(applicationContext, message, pendingIntent, detailsPendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}

private fun detailsActivityPendingIntent(
    applicationContext: Context,
    message: String,
    status: String
): PendingIntent? {
    val detailsIntent = Intent(applicationContext, DetailActivity::class.java)
    detailsIntent.putExtra(DetailActivity.INTENT_EXTRA_FILE_NAME, message)
    detailsIntent.putExtra(DetailActivity.INTENT_EXTRA_STATUS, status)
    return TaskStackBuilder.create(applicationContext).run {
        // Add the intent, which inflates the back stack
        addNextIntentWithParentStack(detailsIntent)
        // Get the PendingIntent containing the entire back stack
        getPendingIntent(REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

private fun appPendingIntent(applicationContext: Context): PendingIntent? {
    val intent = Intent(applicationContext, MainActivity::class.java)
    return PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

private fun notificationBuilder(
    applicationContext: Context,
    message: String,
    pendingIntent: PendingIntent?,
    detailsPendingIntent: PendingIntent?
): NotificationCompat.Builder {
    return NotificationCompat.Builder(
        applicationContext,
        CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(message)
        .setContentIntent(pendingIntent).setAutoCancel(true).addAction(
            R.drawable.ic_download,
            applicationContext.getString(R.string.notification_button),
            detailsPendingIntent
        ).setPriority(NotificationCompat.PRIORITY_HIGH)
}

fun NotificationManager.updateStatus(applicationContext: Context, message: String, status: String) {
    val pendingIntent = appPendingIntent(applicationContext)
    val detailsPendingIntent: PendingIntent? =
        detailsActivityPendingIntent(applicationContext, message, status)

    // Build the notification
    val builder =
        notificationBuilder(applicationContext, message, pendingIntent, detailsPendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}