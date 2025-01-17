package network.client

import android.util.Log
import androidx.lifecycle.MutableLiveData
import network.model.albums.Albums
import network.model.albums.Alternative
import network.model.albums.DeezerAlbums
import network.model.albums.deep_linked.AlbumFromDeepLink
import network.model.tracklist.TrackListSongs
import network.services.DeezerServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object DeezerClient {
    private var deezerServices: DeezerServices? = getApi()
    private const val BASE_URL = "https://api.deezer.com/2.0/"
    var deezerAlbums: MutableLiveData<DeezerAlbums> = MutableLiveData()
    var deezerAlbumsTrackList: MutableLiveData<TrackListSongs> = MutableLiveData()
    var albumFromLink: MutableLiveData<AlbumFromDeepLink> = MutableLiveData()

    private fun getApi(): DeezerServices? {
        if (deezerServices == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            deezerServices = retrofit.create(DeezerServices::class.java)
        }
        return deezerServices
    }

    fun getAllAlbums() {
        getApi()?.getAllAlbums()
            ?.enqueue(object : Callback<DeezerAlbums> {
                override fun onResponse(
                    call: Call<DeezerAlbums>?,
                    response: Response<DeezerAlbums>?
                ) {
                    deezerAlbums.value = response!!.body()
                }

                override fun onFailure(call: Call<DeezerAlbums>?, t: Throwable?) {
                    Log.e("deezer client", t?.message!!)
                }

            })
    }

    fun getAllSongs(album: Albums) {
        val validEndPoint = album.tracklist.substringAfter("2.0/")
        getApi()?.getTrackList(validEndPoint)
            ?.enqueue(object : Callback<TrackListSongs> {
                override fun onResponse(
                    call: Call<TrackListSongs>?,
                    response: Response<TrackListSongs>?
                ) {
                    deezerAlbumsTrackList.value = response!!.body()
                }

                override fun onFailure(call: Call<TrackListSongs>?, t: Throwable?) {
                    Log.e("deezer client", t?.message!!)
                }

            })

    }

    fun getAllSongsFromAlternative(alternativ: Alternative) {
        val validEndPoint = alternativ.tracklist.substringAfter("2.0/")
        getApi()?.getTrackList(validEndPoint)
            ?.enqueue(object : Callback<TrackListSongs> {
                override fun onResponse(
                    call: Call<TrackListSongs>?,
                    response: Response<TrackListSongs>?
                ) {
                    deezerAlbumsTrackList.value = response!!.body()
                }

                override fun onFailure(call: Call<TrackListSongs>?, t: Throwable?) {
                    Log.e("deezer client", t?.message!!)
                }

            })
    }

    fun getAllSongsFromLink(path: String) {
        getApi()?.getTrackListFromLink(path)
            ?.enqueue(object : Callback<AlbumFromDeepLink> {
                override fun onResponse(
                    call: Call<AlbumFromDeepLink>?,
                    response: Response<AlbumFromDeepLink>?
                ) {
                    albumFromLink.value = response!!.body()
                }

                override fun onFailure(call: Call<AlbumFromDeepLink>?, t: Throwable?) {
                    Log.e("deezer client", t?.message!!)
                }

            })
    }

}