package viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import network.client.DeezerClient
import network.model.albums.Albums
import network.model.albums.DeezerAlbums

class AlbumViewModel: ViewModel() {
    lateinit var context : AppCompatActivity
    var albumList : MutableLiveData<DeezerAlbums> = MutableLiveData()
    var albumFromLink : MutableLiveData<Albums> = MutableLiveData()
    val deezerService = DeezerClient

    fun getAlbums() {
        deezerService.deezerAlbums.observe(context, Observer {
            albumList.value = it
        })
        deezerService.getAllAlbums()
    }

    fun getTrackListFromExternalLink(path: String){
        deezerService.albumFromLink.observe(context, Observer {
            albumFromLink.value =
                Albums(tracklist = it.tracklist,
                artist = it.artist, cover = it.cover, title = it.title)
        })
        deezerService.getAllSongsFromLink(path)
    }

}