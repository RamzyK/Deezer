package utils

import network.model.albums.Albums
import network.model.tracklist.Song

interface OnItemClicked {
    fun albumClicked(album: Albums)
}