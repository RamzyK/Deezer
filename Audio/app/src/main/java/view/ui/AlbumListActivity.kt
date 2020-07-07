package view.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.deezer.R
import media.DeezerMediaPlayer
import media.service.OnClearFromRecentService
import network.model.albums.Albums
import utils.OnItemClicked
import view.adapter.DeezerAlbumAdapter
import viewmodels.AlbumViewModel
import widgets.CustomStickBottomBar
import widgets.CustomToast


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

    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var albumAdapter : DeezerAlbumAdapter

    private lateinit var albumsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)

        deezerMediaPlayer.context = this
        bindViews()
        setUpRv()
        setUpExternalData()
        setUpBottomStickBar()
        setViewModel()
        setObservers()
        setListeners()
        setExternalIntentReceptor()
        albumViewModel.getAlbums()
    }

    override fun onRestart() {
        super.onRestart()
        setUpBottomStickBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.custom_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action -> {
                val configIntent =  Intent(this, BluetoothConfigActivity::class.java)
                startActivity(configIntent)
            }
            else -> {
            }
        }
        return true
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
    }

    private fun setUpRv(){
        albumAdapter = DeezerAlbumAdapter(this)
        albumsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        albumsRecyclerView.adapter = albumAdapter
    }

    // Notification & Deep links
    private fun setUpExternalData(){
        deezerMediaPlayer.createChannel()

        registerReceiver(deezerMediaPlayer, IntentFilter("DEEZER_PLAYER"))
        startService(Intent(baseContext, OnClearFromRecentService::class.java))
    }

    private fun setUpBottomStickBar(){
        val currentSong = DeezerMediaPlayer.getCurrentSong()
        if(currentSong != null){
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
            if(deezerMediaPlayer.getMediaPlayer().isPlaying){
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_white_pause_48)
            }else{
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_play_white_bottom_bar)
            }
        }else{
            bottomBar.visibility = View.INVISIBLE
        }
    }

    private fun setViewModel(){
        albumViewModel = ViewModelProviders.of(this).get(AlbumViewModel::class.java)
        albumViewModel.context = this
    }

    private fun setObservers(){
        albumViewModel.albumList.observe(this, Observer {
            albumAdapter.albumList =  it.data
        })

        deezerMediaPlayer.isEndOfSongObservable.observe(this, Observer {
            if(!this.isDestroyed){
                setUpBottomStickBar()
            }
        })
    }

    private fun setListeners(){
        bottomBarPlayPauseIv.setOnClickListener{
            if(deezerMediaPlayer.getMediaPlayer().isPlaying){
                deezerMediaPlayer.pauseSong()
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_play_white_bottom_bar)
            }else{
                deezerMediaPlayer.playSongAgain()
                bottomBarPlayPauseIv.setImageResource(R.drawable.ic_white_pause_48)
            }
        }

        bottomBarPreviousSongIv.setOnClickListener{
            deezerMediaPlayer.previousSong()
            setUpBottomStickBar()
        }

        bottomBarNextSongIv.setOnClickListener{
            deezerMediaPlayer.nextSong()
            setUpBottomStickBar()
        }

        bottomBar.setOnClickListener{
            showMusicNavigationController()
        }
    }

    private fun launchNextScreen(context: Context, album: Albums) {
        val goToAlbumTrackList = Intent(context, AlbumDetailActivity::class.java)
        goToAlbumTrackList.putExtra("album_detail", album)
        startActivity(goToAlbumTrackList)
    }

    private fun launchDetailAlbumFromLink(album: Albums, songToplay: String){
        val intent = Intent(this, AlbumDetailActivity::class.java)
        intent.putExtra("album_detail", album)
        intent.putExtra("songId", songToplay)
        startActivity(intent)
    }

    private fun showMusicNavigationController(){
        startActivity(Intent(context, MusicNavigationActivity::class.java))
    }

    override fun albumClicked(album: Albums, type: Int) {
        if(album.available){
            launchNextScreen(context, album)
        }else{
            CustomToast(this, "Album momentanément indisponible").showCustomToast(type)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearNotificication()
    }

    private fun clearNotificication(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            deezerMediaPlayer.cancelAll()
        }

        unregisterReceiver(deezerMediaPlayer)
    }

    private fun setExternalIntentReceptor(){
        intent.data?.let { it ->
            val params = it.pathSegments
            val albumId = params[0]
            val songId = params[1]

            albumViewModel.getTrackListFromExternalLink(albumId)
            albumViewModel.albumFromLink.observe(this, Observer {
                launchDetailAlbumFromLink(it, songId)
            })
        }
    }

}
