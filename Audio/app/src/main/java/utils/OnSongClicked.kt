package utils

import network.model.tracklist.Song

interface OnSongClicked {
    fun trackListSongClicked(song: Song, pos: Int)
}