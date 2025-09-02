package com.heejae.tenniverse.util.service

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.heejae.tenniverse.R
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.persentation.home.HomeActivity
import com.heejae.tenniverse.persentation.home.rentdetail.RentDetailActivity
import com.heejae.tenniverse.util.PUT_EXTRA_FROM_FCM_RENT_UID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    //
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data

        DEBUG(this@FCMService.name, "data ${message.data} rentId: ${data["rentId"]}")

        val intent = Intent(this, RentDetailActivity::class.java).apply {
            putExtra(PUT_EXTRA_FROM_FCM_RENT_UID, data["rentId"])
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            Activity.RESULT_OK,
            intent,
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.channel_id)
        val channelName = getString(R.string.channel_name)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setAutoCancel(true)
            .setContentText(data["body"])
            .setContentTitle(data["title"])
            .setSmallIcon(R.drawable.ic_tenniverse_logo)
            .setContentIntent(pendingIntent)

        // create notification channel

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}