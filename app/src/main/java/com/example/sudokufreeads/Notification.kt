package com.example.sudokufreeads

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import com.example.sudokufreeads.R

const val notificationid=1
const val channelid="channel1"
const val titleExtra="New sudoku game"
const val messageExtra="Don't forget to exercise your brain with a new sudoku game!"

class Notification: BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent?) {

        var newIntent=Intent(context, MainActivity::class.java)

        val notifyPendingIntent= PendingIntent.getActivity(
            context, 0, newIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification= NotificationCompat.Builder(context, channelid)
            .setSmallIcon(R.drawable.number7)
            .setContentIntent(notifyPendingIntent)
            .setContentTitle(titleExtra)
            .setColor(Color.parseColor("#487EBD"))
            .setContentText(messageExtra)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationid, notification)


    }
}