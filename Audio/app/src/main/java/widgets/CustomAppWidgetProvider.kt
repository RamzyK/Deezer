package widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.example.deezer.R
import media.DeezerMediaPlayer

class CustomAppWidgetProvider : AppWidgetProvider() {

    private val ACTION_PREVIOUS = "deezer.action.PREVIOUS_MUSIC"
    private val ACTION_NEXT = "deezer.action.NEXT_MUSIC"
    private val ACTION_PLAY_PAUSE = "deezer.action.PLAY_PAUSE_MUSIC"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds.forEach { appWidgetId ->
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.custom_widget_view
            ).apply {
                val prevIntent = Intent(context, CustomAppWidgetProvider::class.java)
                prevIntent.action = ACTION_PREVIOUS
                val prevPending = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                setOnClickPendingIntent(R.id.custom_widget_btn_prev, prevPending)

                val nextIntent = Intent(context, CustomAppWidgetProvider::class.java)
                nextIntent.action = ACTION_NEXT
                val nextPending = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                setOnClickPendingIntent(R.id.custom_widget_btn_next, nextPending)

                val playPauseIntent = Intent(context, CustomAppWidgetProvider::class.java)
                playPauseIntent.action = ACTION_PLAY_PAUSE
                val playPausePending = PendingIntent.getBroadcast(context, 2, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                setOnClickPendingIntent(R.id.custom_widget_btn_play, playPausePending)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        when(intent.action) {
            ACTION_PLAY_PAUSE ->
                if (DeezerMediaPlayer.getMediaPlayer().isPlaying) {
                    DeezerMediaPlayer.pauseSong()
                } else {
                    DeezerMediaPlayer.playSongAgain()
                }
            ACTION_NEXT -> DeezerMediaPlayer.nextSong()
            ACTION_PREVIOUS -> DeezerMediaPlayer.previousSong()
        }

        super.onReceive(context, intent)
    }

    fun update() {
        val context = DeezerMediaPlayer.context
        val currentSong = DeezerMediaPlayer.getCurrentSong()
        val iconPlay = context.resources.getIdentifier("ic_play_white_bottom_bar", "drawable", context.packageName)
        val iconPause = context.resources.getIdentifier("ic_white_pause_48", "drawable", context.packageName)

        if (currentSong != null) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val remoteViews = RemoteViews(context.packageName, R.layout.custom_widget_view)
            val thisWidget = ComponentName(context, CustomAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            val awt = AppWidgetTarget(context.applicationContext, R.id.custom_widget_iv, remoteViews, *appWidgetIds)

            Glide.with(context.applicationContext)
                .asBitmap()
                .load(DeezerMediaPlayer.getCurrentSmallCover())
                .placeholder(R.drawable.default_cover_art)
                .error(R.drawable.default_cover_art)
                .into(awt)

            remoteViews.setTextViewText(R.id.custom_widget_tv, currentSong.title_short)
            if(DeezerMediaPlayer.getMediaPlayer().isPlaying) {
                remoteViews.setInt(R.id.custom_widget_btn_play, "setBackgroundResource", iconPause)
            } else {
                remoteViews.setInt(R.id.custom_widget_btn_play, "setBackgroundResource", iconPlay)
            }

            appWidgetManager.updateAppWidget(thisWidget, remoteViews)
        }
    }
}