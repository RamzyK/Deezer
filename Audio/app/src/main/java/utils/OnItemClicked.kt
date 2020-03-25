package utils

import network.model.albums.Albums

interface OnItemClicked {
    fun albumClicked(album: Albums, type: Int)
}