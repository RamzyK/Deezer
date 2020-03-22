package media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.example.deezer.R
import media.notification_control.NotificationBarController
import network.model.tracklist.Song
import utils.OnNotificationControllerTouched

object DeezerMediaPlayer : OnNotificationControllerTouched, BroadcastReceiver(){
    private var mediaPlayer = MediaPlayer()
    private var currentSong: Song? = null
    private var currentAlbumCover: String? = null
    private var currentSongPosInTrackList = 0
    private var currentTrackList: List<Song>? = null
    lateinit var context: Context
    var isEndOfSongObservable: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var notificationManager: NotificationManager


    fun playSong(song:Song){
        mediaPlayer.setOnCompletionListener{
            this.nextSong()
            isEndOfSongObservable.value = true
        }
        NotificationBarController().createNotification(context, currentAlbumCover!!, song, R.drawable.ic_pause_notification_bar)
        if(!mediaPlayer.isPlaying){
            mediaPlayer.reset()
        }
        mediaPlayer.setDataSource(song.preview)
        mediaPlayer.prepare()
        startSong()
    }

    fun nextSong(){
        resetMedaiPlayer()
        currentSongPosInTrackList = (currentSongPosInTrackList + 1) % currentTrackList!!.size
        currentSong = currentTrackList!![(currentSongPosInTrackList)]
        playSong(currentTrackList!![currentSongPosInTrackList])
    }

    fun previousSong(){
        resetMedaiPlayer()
        currentSongPosInTrackList = if(currentSongPosInTrackList - 1 < 0){
            currentTrackList!!.size - 1
        }else{
            (currentSongPosInTrackList - 1) % currentTrackList!!.size
        }
        currentSong = currentTrackList!![currentSongPosInTrackList]
        playSong(currentTrackList!![currentSongPosInTrackList])
    }



    fun resetMedaiPlayer(){
        stopSong()
        mediaPlayer.reset()
    }

    fun setUpMediaPlayer(){
        mediaPlayer.isLooping = false
        mediaPlayer.setVolume(0.5f, 0.5f)
    }

    fun playSongAgain(){
        mediaPlayer.start()
        NotificationBarController().createNotification(context, currentAlbumCover!!, currentSong!!, R.drawable.ic_pause_notification_bar)
    }

    fun pauseSong(){
        mediaPlayer.pause()
        NotificationBarController().createNotification(context, currentAlbumCover!!, currentSong!!, R.drawable.ic_play_notification_bar)
    }


    private fun startSong(){
        mediaPlayer.start()
    }

    private fun stopSong(){
        mediaPlayer.stop()
    }

    // Channel notification
    fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificatiionChannel = NotificationChannel(NotificationBarController.CHANNEL_ID,
                "Deezer_Player", NotificationManager.IMPORTANCE_LOW)

            notificationManager = context.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(notificatiionChannel)

        }

    }

    fun cancelAll(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll()

        }
    }


    // Getters and Setters
    fun getMediaPlayer(): MediaPlayer{
        return mediaPlayer
    }

    fun getCurrentSong(): Song?{
        return currentSong
    }

    fun setCurrentSong(s: Song){
        currentSong = s
    }

    fun getCurrentCover(): String?{
        return currentAlbumCover
    }

    fun setCurrentCover(s: String){
        currentAlbumCover = s
    }

    fun setTrackList(trackList: List<Song>){
        currentTrackList = trackList
    }

    fun setCurrentSongPos(position: Int){
        currentSongPosInTrackList = position
    }

    override fun onSongPrevious() {
        previousSong()
        isEndOfSongObservable.value = true
    }

    override fun onSongPlay() {
        mediaPlayer.start()
        NotificationBarController().createNotification(context, currentAlbumCover!!, currentSong!!, R.drawable.ic_pause_notification_bar)
        isEndOfSongObservable.value = true
    }

    override fun onSongPause() {
        mediaPlayer.pause()
        NotificationBarController().createNotification(context, currentAlbumCover!!, currentSong!!, R.drawable.ic_play_notification_bar)
        isEndOfSongObservable.value = true
    }

    override fun onSongNext() {
        nextSong()
        isEndOfSongObservable.value = true
    }

    override fun onReceive(p0: Context?, p1: Intent?) {

        when (p1!!.extras!!.getString("actioname")) {
            NotificationBarController.ACTION_PREVIOUS -> onSongPrevious()
            NotificationBarController.ACTION_NEXT -> onSongNext()
            NotificationBarController.ACTION_PLAY -> {
                if (this.mediaPlayer.isPlaying) {
                    onSongPause()
                } else {
                    onSongPlay()
                }
            }
        }
    }
}