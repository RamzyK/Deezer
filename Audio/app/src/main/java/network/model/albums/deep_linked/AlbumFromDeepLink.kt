package network.model.albums.deep_linked

import network.model.Artist
import java.io.Serializable

data class AlbumFromDeepLink(
    val artist: Artist,
    val available: Boolean,
    val cover: String,
    val cover_big: String,
    val cover_medium: String,
    val cover_small: String,
    val cover_xl: String,
    val duration: Int,
    val explicit_content_cover: Int,
    val explicit_content_lyrics: Int,
    val explicit_lyrics: Boolean,
    val fans: Int,
    val genre_id: Int,
    val id: Int,
    val label: String,
    val link: String,
    val nb_tracks: Int,
    val rating: Int,
    val record_type: String,
    val release_date: String,
    val share: String,
    val title: String,
    val tracklist: String,
    val type: String,
    val upc: String
):Serializable