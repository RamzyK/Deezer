package view.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezer.R
import network.model.albums.Albums
import utils.OnItemClicked
import view.adapter.DeezerAlbumAdapter
import viewmodels.DeezerAlbumViewModel


class AlbumListActivity : AppCompatActivity() {
    lateinit var albumsRecyclerView: RecyclerView
    lateinit var albumViewModel: DeezerAlbumViewModel
    lateinit var albumAdapter : DeezerAlbumAdapter

    val context =  this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_list)

        setUp()
        setViewModel()
        setObservers()
        albumViewModel.getAlbums()
    }

    private fun setUp(){
        albumAdapter = DeezerAlbumAdapter(clickListener = object : OnItemClicked {
            override fun albumClicked(album: Albums) {
                launchNextScreen(context, album)
            }
        })
        albumsRecyclerView = findViewById(R.id.home_activity_albums_grid_view)
        albumsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        albumsRecyclerView.adapter = albumAdapter
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

    fun launchNextScreen(context: Context, album: Albums) {
        val goToAlbumTrackList = Intent(context, AlbumDetail::class.java)
        goToAlbumTrackList.putExtra("album_detail", album)
        startActivity(goToAlbumTrackList)
    }

}
