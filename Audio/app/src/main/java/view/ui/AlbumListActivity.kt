package view.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.deezer.R
import media.DeezerMediaPlayer
import network.model.albums.Albums
import utils.OnItemClicked
import view.adapter.DeezerAlbumAdapter
import viewmodels.DeezerAlbumViewModel
import widgets.CustomStickBottomBar


class AlbumListActivity : AppCompatActivity(), OnItemClicked {
    private val deezerMediaPlayer = DeezerMediaPlayer
    private val context =  this

    // Stick music reader bar views
    private lateinit var bottomBar: CustomStickBottomBar
    private lateinit var bottomBarCoverIv: ImageView
    private lateinit var bottomBarSongNameTv: TextView
    private lateinit var bottomBarArtistNameTv: TextView
    private lateinit var bottomBarPlayPauseIv: ImageView
    private lateinit var bottomBarPreviousSongIv: ImageView
    private lateinit var bottomBarNextSongIv: ImageView

    private lateinit var albumViewModel: DeezerAlbumViewModel
    private lateinit var albumAdapter : DeezerAlbumAdapter

    private lateinit var albumsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)

        bindViews()
        setUpBottomBar()
        setViewModel()
        setObservers()
        setListeners()
        albumViewModel.getAlbums()
    }

    override fun onRestart() {
        super.onRestart()
        setUpBottomBar()
    }

    private fun bindViews(){
        albumsRecyclerView = findViewById(R.id.home_activity_albums_grid_view)
        bottomBar = findViewById(R.id.bottom_media_bar)
        bottomBarCoverIv = findViewById(R.id.bottom_sticky_bar_cover_iv)
        bottomBarSongNameTv = findViewById(R.id.bottom_sticky_bar_song_tv)
        bottomBarArtistNameTv = findViewById(R.id.bottom_sticky_bar_artist_tv)
        bottomBarPlayPauseIv = findViewById(R.id.bottom_sticky_bar_player_btn)
        bottomBarPreviousSongIv = findViewById(R.id.bottom_sticky_bar_previous_btn)
        bottomBarNextSongIv = findViewById(R.id.bottom_sticky_bar_next_btn)
        setUpRv()
    }

    private fun setUpRv(){
        albumAdapter = DeezerAlbumAdapter(this)
        albumsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        albumsRecyclerView.adapter = albumAdapter
    }

    private fun setUpBottomBar(){
        val currentSong = DeezerMediaPlayer.getCurrentSong()
        if(currentSong != null){
            Glide.with(bottomBarCoverIv)
                .load(DeezerMediaPlayer.getCurrentCover())
                .centerCrop()
                .placeholder(R.drawable.default_cover_art)
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(bottomBarCoverIv)
            bottomBar.visibility = View.VISIBLE
            bottomBarSongNameTv.text = deezerMediaPlayer.getCurrentSong()!!.title_short
            bottomBarArtistNameTv.text = deezerMediaPlayer.getCurrentSong()!!.artist.name
            if(deezerMediaPlayer.getMediaPlayer().isPlaying){
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_white_pause_48)
            }else{
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_white_start_music_48)
            }
        }else{
            bottomBar.visibility = View.INVISIBLE
        }
    }

    private fun setViewModel(){
        albumViewModel = ViewModelProviders.of(this).get(DeezerAlbumViewModel::class.java)
        albumViewModel.context = this
    }

    private fun setObservers(){
        albumViewModel.getAlbumsList().observe(this, Observer {
            albumAdapter.albumList =  it.data
        })
    }

    private fun setListeners(){
        bottomBarPlayPauseIv.setOnClickListener{
            if(deezerMediaPlayer.getMediaPlayer().isPlaying){
                deezerMediaPlayer.getMediaPlayer().pause()
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_white_start_music_48)
            }else{
                deezerMediaPlayer.getMediaPlayer().start()
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_white_pause_48)
            }
        }

        bottomBarPreviousSongIv.setOnClickListener{
            deezerMediaPlayer.previousSong()
            setUpBottomBar()
        }

        bottomBarNextSongIv.setOnClickListener{
            deezerMediaPlayer.nextSong()
            setUpBottomBar()
        }
    }

    private fun launchNextScreen(context: Context, album: Albums) {
        val goToAlbumTrackList = Intent(context, AlbumDetail::class.java)
        goToAlbumTrackList.putExtra("album_detail", album)
        startActivity(goToAlbumTrackList)
    }

    override fun albumClicked(album: Albums) {
        launchNextScreen(context, album)
    }

}
