package com.example.fess.firebasenotificationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context

import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log


import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    val TAG = "FCM_Service"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: " + remoteMessage!!.from)
        Log.d(TAG, "Notification Message Body: " + remoteMessage.notification!!.body!!)
        remoteMessage.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }
        sendNotification(remoteMessage)
    }


    private fun sendNotification(remoteMessage: RemoteMessage) {

        val click_Action = remoteMessage.notification!!.clickAction
        val message = remoteMessage.data["message"]
        val dataFrom = remoteMessage.data["from_user_id"]


        val messageTitle = remoteMessage.notification!!.title
        val messageBody = remoteMessage.notification!!.body
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val mBuilder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setContentTitle(getString(R.string.fcm_message))
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_fire_emoji)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        val intent = Intent(click_Action)
        intent.putExtra("message", message)
        intent.putExtra("from_user_id", dataFrom)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pendingIntent)


        val mNotificationID = System.currentTimeMillis().toInt()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(mNotificationID, mBuilder.build())

    }
}
