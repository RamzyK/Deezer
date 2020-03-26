package view.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.deezer.R
import kotlinx.android.synthetic.main.cell_track_list_songs.*
import media.DeezerMediaPlayer
import utils.OnMusicIsPlaying

class MusicNavigationActivity : AppCompatActivity(), OnMusicIsPlaying {

    private lateinit var albumCoverIv: ImageView
    private lateinit var songNameTv: TextView
    private lateinit var artistNameTv: TextView

    private lateinit var seekBar: SeekBar
    private lateinit var timeSpentTv: TextView
    private lateinit var timeLeftTv: TextView

    private lateinit var shuffleIv: ImageView
    private lateinit var previousSongIv: ImageView
    private lateinit var pausePlaySongIv: ImageView
    private lateinit var nextSongIv: ImageView
    private lateinit var repeatMusicIv: ImageView

    private val deezerMediaPlayer = DeezerMediaPlayer
    private var repeatingAlbum = false
    private var repeatingSong = false
    private var repeatingIsEnabled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_navigation)

        deezerMediaPlayer.musicPlayingUpdater = this
        setViews()
        setUpData()
        setListeners()

    }

    private fun setViews(){
        albumCoverIv = findViewById(R.id.music_nav_album_cover_iv)
        songNameTv = findViewById(R.id.music_nav_song_name_tv)
        artistNameTv = findViewById(R.id.music_nav_artst_name_tv)

        seekBar = findViewById(R.id.music_time_lapse_seek_bar)
        timeSpentTv = findViewById(R.id.time_spent_tv)
        timeLeftTv = findViewById(R.id.time_left_tv)

        shuffleIv = findViewById(R.id.shuffle_nav_music_iv)
        previousSongIv = findViewById(R.id.previous_nav_music_iv)
        pausePlaySongIv = findViewById(R.id.play_music_nav_iv)
        nextSongIv = findViewById(R.id.next_music_nav_iv)
        repeatMusicIv = findViewById(R.id.repeat_music_nav_iv)
    }


    private fun setUpData(){
        seekBar.max = 30

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            seekBar.setProgress(deezerMediaPlayer.timeSpent, true)
        }else{
            seekBar.progress = deezerMediaPlayer.timeSpent
        }
        if(deezerMediaPlayer.getMediaPlayer().isPlaying){
            pausePlaySongIv.setImageResource(R.drawable.ic_pause_notification_bar)
        }else{
            pausePlaySongIv.setImageResource(R.drawable.ic_play_notification_bar)
        }
        loadSongData()
    }

    private fun loadSongData(){
        val currentAlbumCover = deezerMediaPlayer.getCurrentCover()
        Glide.with(albumCoverIv)
            .load(currentAlbumCover)
            .centerCrop()
            .placeholder(R.drawable.default_cover_art)
            .error(R.drawable.default_cover_art)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(albumCoverIv)
        songNameTv.text = deezerMediaPlayer.getCurrentSong()!!.title_short
        artistNameTv.text = deezerMediaPlayer.getCurrentSong()!!.artist.name
        if(deezerMediaPlayer.getMediaPlayer().isPlaying){
            pausePlaySongIv.setImageResource(R.drawable.ic_pause_notification_bar)
        }else{
            pausePlaySongIv.setImageResource(R.drawable.ic_play_notification_bar)
        }
    }

    private fun setListeners(){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                    deezerMediaPlayer.getMediaPlayer().seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        previousSongIv.setOnClickListener{
            deezerMediaPlayer.previousSong()
            loadSongData()
        }

        nextSongIv.setOnClickListener{
            deezerMediaPlayer.nextSong()
            loadSongData()
        }

        pausePlaySongIv.setOnClickListener{
            if(deezerMediaPlayer.getMediaPlayer().isPlaying){
                deezerMediaPlayer.pauseSong()
                pausePlaySongIv.setImageResource(R.drawable.ic_play_notification_bar)
            }else{
                deezerMediaPlayer.playSongAgain()
                pausePlaySongIv.setImageResource(R.drawable.ic_pause_notification_bar)
            }
        }

        shuffleIv.setOnClickListener{

        }

        repeatMusicIv.setOnClickListener{
            if(!repeatingIsEnabled){
                repeatingIsEnabled = !repeatingIsEnabled
                repeatingAlbum = !repeatingAlbum
                // Repetition de tout l'album
                repeatMusicIv.setImageResource(R.drawable.ic_repeat_selected)
                deezerMediaPlayer.loopOnAlbum()
                deezerMediaPlayer.loopOnSong(false)
            }else{
                // Repetion deja active
                if(!repeatingSong){
                    // Repetition du son desactivée alors on l'active
                    repeatingSong = !repeatingSong
                    repeatMusicIv.setImageResource(R.drawable.ic_repeat_one_time_selected)
                    deezerMediaPlayer.loopOnSong(true)
                }else{
                    repeatMusicIv.setImageResource(R.drawable.ic_repeat_unselected)
                    repeatingIsEnabled = !repeatingIsEnabled
                    repeatingAlbum = !repeatingAlbum
                    repeatingSong = !repeatingSong
                    deezerMediaPlayer.disableLoop()
                    deezerMediaPlayer.loopOnSong(false)
                }
            }
        }
    }


    override fun updateMusicReader(musicTimePlayed: Int, musicTimeLeft: Int) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            seekBar.setProgress(musicTimePlayed, true)
        }else{
            seekBar.progress = musicTimePlayed
        }
        if(musicTimePlayed < 10) timeSpentTv.text = "0:0$musicTimePlayed"  else timeSpentTv.text = "0:$musicTimePlayed"
        if(musicTimeLeft < 10) timeLeftTv.text =  "0:0$musicTimeLeft" else timeLeftTv.text =  "0:$musicTimeLeft"
    }

    override fun loadData() {
        if(!isDestroyed)
            loadSongData()
    }


}
