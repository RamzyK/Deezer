package network.model

import java.io.Serializable

data class Artist(
    val id: Int,
    val name: String,
    val picture: String,
    val picture_big: String,
    val picture_medium: String,
    val picture_small: String,
    val picture_xl: String,
    val tracklist: String,
    val type: String
) : Serializable