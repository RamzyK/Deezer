package media.notification_control

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat as NotifCompat
import androidx.core.app.NotificationManagerCompat
import com.example.deezer.R
import media.service.NotificationActionService
import network.model.tracklist.Song


class NotificationBarController {
    companion object{
        const  val CHANNEL_ID = "chanel"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_PLAY = "chanel_play"
        const val ACTION_NEXT = "chanel_next"
    }

    private lateinit var notification: Notification

    fun createNotification(context: Context, song: Song, defaultPlayPauseIcon: Int){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = NotificationManagerCompat.from(context)
            val albumIcon = BitmapFactory.decodeResource(context.resources, R.drawable.default_cover_art)
            val  mediaSessionCompat = MediaSessionCompat(context, "tag")

            lateinit var pendingIntentPrevious: PendingIntent
            lateinit var pendingIntentPlay: PendingIntent
            lateinit var pendingIntentNext: PendingIntent


            val intentPrevious = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PREVIOUS)
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationIconPrevious = R.drawable.ic_previous_notification_bar


            val intentPlay = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PLAY)
            pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)


            val intentNext = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_NEXT)
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                intentNext, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationIconNext: Int = R.drawable.ic_next_notification_bar



            notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.default_cover_art)
                .setContentTitle(song.title)
                .setContentText(song.artist.name)
                .setLargeIcon(albumIcon)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(notificationIconPrevious, "Previous", pendingIntentPrevious)
                .addAction(defaultPlayPauseIcon, "Play", pendingIntentPlay)
                .addAction(notificationIconNext, "Next", pendingIntentNext)
                .setStyle(NotifCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSessionCompat.sessionToken))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            notificationManager.notify(1, notification)

        }
    }
}