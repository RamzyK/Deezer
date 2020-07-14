package view.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.deezer.R
import media.DeezerMediaPlayer
import network.model.albums.Albums
import network.model.tracklist.Song
import utils.OnSongClicked
import view.adapter.DeezerDetailAlbumAdapter
import viewmodels.AlbumDetailViewModel
import widgets.CustomStickBottomBar


class AlbumDetailActivity : AppCompatActivity(), OnSongClicked {
    private lateinit var currentPageTrackList: List<Song>

    private val deezerMediaPlayer = DeezerMediaPlayer
    private lateinit var albums: Albums
    private var songIdToPlay: String = ""
    private var context = this

    // Header album info views
    private lateinit var artistNameTv: TextView
    private lateinit var albumTitleTv: TextView
    private lateinit var albumCoverIv: ImageView
    private lateinit var albumIsExplicitTv: TextView
    private lateinit var albumReleaseDateTv: TextView

    // Stick music reader bar views
    private lateinit var bottomBar: CustomStickBottomBar
    private lateinit var bottomBarCoverIv: ImageView
    private lateinit var bottomBarSongNameTv: TextView
    private lateinit var bottomBarArtistNameTv: TextView
    private lateinit var bottomBarPlayPauseIv: ImageView
    private lateinit var bottomBarPreviousSongIv: ImageView
    private lateinit var bottomBarNextSongIv: ImageView

    private lateinit var songsViewModel: AlbumDetailViewModel
    private lateinit var deezerDetailAlbumAdapter: DeezerDetailAlbumAdapter

    private lateinit var trackListSongsListRv: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_album)

        bindViews()
        deezerMediaPlayer.setUpMediaPlayer()
        setUpViews()
        setUpBottomStickBar()
        setViewModel()
        setObservers()
        setListeners()
        songsViewModel.getTrackList(albums)
    }

    override fun onRestart() {
        super.onRestart()
        setUpBottomStickBar()
    }

    private fun bindViews() {
        intent?.let {
            albums = it.getSerializableExtra("album_detail") as Albums
            it.getStringExtra("songId")?.let { id ->
                songIdToPlay = id
            }
        }
        artistNameTv = findViewById(R.id.artist_name_tv)
        albumTitleTv = findViewById(R.id.album_name_tv)
        albumCoverIv = findViewById(R.id.album_cover_iv)
        albumIsExplicitTv = findViewById(R.id.is_explicit_tv)
        albumReleaseDateTv = findViewById(R.id.album_release_date_tv)
        trackListSongsListRv = findViewById(R.id.track_list_songs_rv)

        bottomBar = findViewById(R.id.detail_album_bottom_media_bar)
        bottomBarCoverIv = findViewById(R.id.bottom_sticky_bar_cover_iv)
        bottomBarSongNameTv = findViewById(R.id.bottom_sticky_bar_song_tv)
        bottomBarArtistNameTv = findViewById(R.id.bottom_sticky_bar_artist_tv)
        bottomBarPlayPauseIv = findViewById(R.id.bottom_sticky_bar_player_btn)
        bottomBarPreviousSongIv = findViewById(R.id.bottom_sticky_bar_previous_btn)
        bottomBarNextSongIv = findViewById(R.id.bottom_sticky_bar_next_btn)
    }

    private fun setUpViews() {
        artistNameTv.text = albums.artist.name
        albumTitleTv.text = albums.title
        Glide.with(this)
            .load(albums.cover_big)
            .centerCrop()
            .placeholder(R.drawable.default_cover_art)
            .error(R.drawable.default_cover_art)
            .into(albumCoverIv)
        if (albums.explicit_lyrics) {
            albumIsExplicitTv.visibility = View.VISIBLE
        } else {
            albumIsExplicitTv.visibility = View.INVISIBLE
        }
        albumReleaseDateTv.text = albums.release_date
        setUpListSongsRv()
    }

    private fun setUpListSongsRv() {
        this.deezerDetailAlbumAdapter = DeezerDetailAlbumAdapter(this)
        trackListSongsListRv.layoutManager = LinearLayoutManager(this)
        trackListSongsListRv.adapter = deezerDetailAlbumAdapter
    }

    private fun setUpBottomStickBar() {
        val currentSong = DeezerMediaPlayer.getCurrentSong()
        if (currentSong != null) {
            Glide.with(bottomBarCoverIv)
                .load(DeezerMediaPlayer.getCurrentSmallCover())
                .centerCrop()
                .placeholder(R.drawable.default_cover_art)
                .error(R.drawable.default_cover_art)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(bottomBarCoverIv)
            bottomBar.visibility = View.VISIBLE
            bottomBarSongNameTv.text = deezerMediaPlayer.getCurrentSong()!!.title_short
            bottomBarArtistNameTv.text = deezerMediaPlayer.getCurrentSong()!!.artist.name
            if (deezerMediaPlayer.getMediaPlayer().isPlaying) {
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_white_pause_48)
            } else {
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_play_white_bottom_bar)
            }
        } else {
            bottomBar.visibility = View.INVISIBLE
        }
    }

    private fun setViewModel() {
        songsViewModel = ViewModelProviders.of(this).get(AlbumDetailViewModel::class.java)
        songsViewModel.context = this
    }

    private fun setObservers() {
        songsViewModel.getAllTracks().observe(this, Observer {
            if (it != null) {
                currentPageTrackList = it.data
                deezerDetailAlbumAdapter.songsList = it.data

                if (songIdToPlay != "") { // Deep link start song
                    it.data.map { s ->
                        if (s.id == songIdToPlay.toInt()) {
                            val song = it.data[s.track_position - 1]
                            trackListSongClicked(song, s.track_position - 1)
                        }
                    }
                }
            } else {
                Toast.makeText(context, getString(R.string.all_tracks_get_error), Toast.LENGTH_LONG)
                    .show()
            }
        })

        deezerMediaPlayer.isEndOfSongObservable.observe(this, Observer {
            if (!this.isDestroyed) {
                setUpBottomStickBar()
            }
        })
    }

    private fun setListeners() {
        bottomBarPlayPauseIv.setOnClickListener {
            if (deezerMediaPlayer.getMediaPlayer().isPlaying) {
                deezerMediaPlayer.pauseSong()
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_play_white_bottom_bar)
            } else {
                deezerMediaPlayer.playSongAgain()
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_white_pause_48)
            }
        }

        bottomBarPreviousSongIv.setOnClickListener {
            deezerMediaPlayer.previousSong()
            setUpBottomStickBar()
        }

        bottomBarNextSongIv.setOnClickListener {
            deezerMediaPlayer.nextSong()
            setUpBottomStickBar()
        }

        bottomBar.setOnClickListener {
            showMusicNavigationController()
        }

    }

    private fun showMusicNavigationController() {
        val intent = Intent(context, MusicNavigationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }

    override fun trackListSongClicked(song: Song, pos: Int) {
        // Play song
        deezerMediaPlayer.setCurrentCover(albums.cover_big)
        deezerMediaPlayer.setCurrentSmallCover(albums.cover_small)
        deezerMediaPlayer.setCurrentSong(song)
        deezerMediaPlayer.setCurrentSongPos(pos)
        deezerMediaPlayer.setTrackList(currentPageTrackList) // Devient la liste globale à l'écoute
        if (deezerMediaPlayer.getMediaPlayer().isPlaying) {
            deezerMediaPlayer.resetMedaiPlayer()
        }
        deezerMediaPlayer.playSong(song)
        setUpBottomStickBar()
    }

    override fun trackLIstSongLongClicker(song: Song, pos: Int) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = getString(R.string.share_message_header)
            val songId = String.format(getString(R.string.song_link), albums.id, song.id)
            shareMessage = shareMessage + getString(R.string.deep_link_host) + songId
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

            startActivity(
                Intent.createChooser(
                    shareIntent,
                    String.format(getString(R.string.share_message_content), song.title_short)
                )
            )
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

}
