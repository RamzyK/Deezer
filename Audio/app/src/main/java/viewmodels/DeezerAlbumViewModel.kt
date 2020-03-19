package viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import network.client.DeezerClient
import network.model.albums.DeezerAlbums

class DeezerAlbumViewModel: ViewModel() {
    lateinit var context : AppCompatActivity
    var albumList : MutableLiveData<DeezerAlbums> = MutableLiveData()
    val deezerService = DeezerClient

    fun getAlbums() {
        deezerService.deezerAlbums.observe(context, Observer {
            albumList.value = it
        })
        deezerService.getAllAlbums()
    }

    fun getAlbumsList() : MutableLiveData<DeezerAlbums>{
        return albumList
    }
}