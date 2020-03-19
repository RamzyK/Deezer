package viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import network.client.DeezerClient
import network.model.albums.Albums
import network.model.tracklist.TrackListSongs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeezerAlbumDetailViewModel : ViewModel() {
    lateinit var context : AppCompatActivity
    var trackListSongs : MutableLiveData<TrackListSongs> = MutableLiveData()
    val deezerService = DeezerClient

    fun getTrackList(album: Albums) {
       deezerService.deezerAlbumsTrackList.observe(context, Observer {
           trackListSongs.value = it
       })
        deezerService.getAllSongs(album)
    }

    fun getAllTracks() : MutableLiveData<TrackListSongs>{
        return trackListSongs
    }
}