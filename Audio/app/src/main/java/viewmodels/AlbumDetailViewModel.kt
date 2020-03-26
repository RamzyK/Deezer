package viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import network.client.DeezerClient
import network.model.albums.Albums
import network.model.tracklist.TrackListSongs

class AlbumDetailViewModel : ViewModel() {
    lateinit var context : AppCompatActivity
    var trackListSongs : MutableLiveData<TrackListSongs> = MutableLiveData()
    val deezerService = DeezerClient

    fun getTrackList(album: Albums) {
        deezerService.deezerAlbumsTrackList.observe(context, Observer {
            trackListSongs.value = it
        })
        if(album.alternative != null){
            deezerService.getAllSongsFromAlternative(album.alternative)
        }else{
            deezerService.getAllSongs(album)
        }
    }

    fun getAllTracks() : MutableLiveData<TrackListSongs>{
        return trackListSongs
    }
}