package media

import android.media.MediaPlayer
import network.model.tracklist.Song

object DeezerMediaPlayer {
    private var mediaPlayer = MediaPlayer()
    private var currentSong: Song? = null
    private var currentAlbumCover: String? = null
    private var currentSongPosInTrackList = 0
    private var currentTrackList: List<Song>? = null

    fun playSong(song:Song){
        if(!mediaPlayer.isPlaying){
            mediaPlayer.reset()
        }
        mediaPlayer.setDataSource(song.preview)
        mediaPlayer.prepare()
        startSong()
    }

    fun resetMedaiPlayer(){
        stopSong()
        mediaPlayer.reset()
    }

    fun setUpMediaPlayer(){
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.5f, 0.5f)
    }

    private fun startSong(){
        mediaPlayer.start()
    }

    private fun stopSong(){
        mediaPlayer.stop()
    }

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
}