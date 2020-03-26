package network.model.albums

import network.model.Artist
import java.io.Serializable

 class Albums(
    val artist: Artist,
    val available: Boolean = false,
    val alternative: Alternative? = null,
    val cover: String,
    val cover_big: String = "",
    val cover_medium: String = "",
    val cover_small: String = "",
    val cover_xl: String = "",
    val explicit_lyrics: Boolean = false,
    val id: Int = 0,
    val link: String = "",
    val nb_tracks: Int = 0,
    val record_type: String = "",
    val release_date: String = "",
    val time_add: Int = 0,
    val title: String,
    val tracklist: String,
    val type: String = ""
): Serializable