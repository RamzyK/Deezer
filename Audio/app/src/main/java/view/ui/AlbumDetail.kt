package view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deezer.R
import network.model.albums.Albums
import network.model.tracklist.Song
import utils.OnSongClicked
import view.adapter.DetailAlbumAdapter
import viewmodels.DeezerAlbumDetailViewModel

class AlbumDetail : AppCompatActivity() {
    lateinit var albums: Albums
    lateinit var artistNameTv: TextView
    lateinit var albumTitleTv: TextView
    lateinit var albumCoverIv: ImageView
    lateinit var albumIsExpliciteTv: TextView
    lateinit var albumReleaseDateTv: TextView

    lateinit var trackListSongsListRv: RecyclerView
    lateinit var songsViewModel: DeezerAlbumDetailViewModel
    lateinit var detailAlbumAdapter: DetailAlbumAdapter

    var context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_album)

        linkData()
        setUpViews()
        setUpListSongsRv()
        setViewModel()
        setObservers()
        songsViewModel.getTrackList(albums)
    }

    private fun linkData(){
        albums = intent.getSerializableExtra("album_detail") as Albums
        artistNameTv = findViewById(R.id.artist_name_tv)
        albumTitleTv = findViewById(R.id.album_name_tv)
        albumCoverIv = findViewById(R.id.album_cover_iv)
        albumIsExpliciteTv = findViewById(R.id.is_explicit_tv)
        albumReleaseDateTv = findViewById(R.id.album_release_date_tv)
        trackListSongsListRv = findViewById(R.id.track_list_songs_rv)
    }

    private fun setUpViews(){
        artistNameTv.text = albums.artist.name
        albumTitleTv.text = albums.title
        Glide.with(this)
            .load(albums.cover)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(albumCoverIv)
        if(albums.explicit_lyrics){
            albumIsExpliciteTv.visibility = View.VISIBLE
        }else{
            albumIsExpliciteTv.visibility = View.INVISIBLE
        }
        albumReleaseDateTv.text = albums.release_date

    }

    private fun setUpListSongsRv(){
        detailAlbumAdapter = DetailAlbumAdapter(object : OnSongClicked {
            override fun trackListSongClicked(song: Song) {
                // Play song
                Toast.makeText(context, "Tu pourras bientôt t'enjailler gui ;)", Toast.LENGTH_LONG).show()
            }
        })
        trackListSongsListRv.layoutManager = LinearLayoutManager(this)
        trackListSongsListRv.adapter = detailAlbumAdapter
    }

    private fun setViewModel(){
        songsViewModel = ViewModelProviders.of(this).get(DeezerAlbumDetailViewModel::class.java)
        songsViewModel.context = this
    }

    private fun setObservers(){
        songsViewModel.getAllTracks().observe(this, Observer {
            if(it!= null){
                detailAlbumAdapter.songsList = it.data
            }else{
                Toast.makeText(context, "Une erreur est survenue !", Toast.LENGTH_LONG).show()
            }
        })
    }
}
