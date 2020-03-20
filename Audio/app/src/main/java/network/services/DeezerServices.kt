package network.services

import network.model.albums.DeezerAlbums
import network.model.tracklist.TrackListSongs
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface DeezerServices {

    @GET("user/2529/albums")
    fun getAllAlbums(): Call<DeezerAlbums>

    @GET
    fun getTrackList(@Url tracks: String): Call<TrackListSongs>

}