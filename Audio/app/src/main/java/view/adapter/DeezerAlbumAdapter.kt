package view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.deezer.R
import network.model.albums.Albums
import utils.OnItemClicked

class DeezerAlbumAdapter(clickListener: OnItemClicked) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ALBUM = 0
    private val SINGLE = 1

    var albumList : List<Albums>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private lateinit var layoutInflater : LayoutInflater
    private var listener = clickListener


    override fun getItemViewType(position: Int): Int {
        if(albumList != null){
            when(albumList!![position].record_type){
                "album" -> return ALBUM
                "single" -> return SINGLE
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         when (viewType) {
            ALBUM -> {
                layoutInflater = LayoutInflater.from(parent.context)
                val albumView = layoutInflater.inflate(R.layout.cell_album, parent, false)
                return AlbumViewHolder(albumView)
            }
            SINGLE -> {
                layoutInflater = LayoutInflater.from(parent.context)
                val singleView = layoutInflater.inflate(R.layout.cell_single, parent, false)
                return SingleViewHolder(singleView)
            }
            else -> {
                layoutInflater = LayoutInflater.from(parent.context)
                val albumView = layoutInflater.inflate(R.layout.cell_album, parent, false)
                return AlbumViewHolder(albumView)
            }
        }
    }

    override fun getItemCount(): Int {
        if(albumList == null){
            return 0
        }
        return albumList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(albumList!![position].record_type){
            "album" -> {
                (holder as AlbumViewHolder).bind(album = albumList!![position], listener = listener)
            }
            "single" -> {
                (holder as SingleViewHolder).bind(single = albumList!![position], listener = listener)
            }
        }
    }



    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val albumCover = itemView.findViewById<ImageView>(R.id.album_cover_iv)
        private val albumName = itemView.findViewById<TextView>(R.id.album_name_tv)
        private val albumArtistName = itemView.findViewById<TextView>(R.id.album_artist_name_tv)

        fun bind(album: Albums, listener: OnItemClicked){
            val url = album.cover
            albumName.text = album.title
            albumArtistName.text = album.artist.name
            Glide.with(itemView)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.default_cover_art)
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(albumCover)
            itemView.setOnClickListener {
                listener.albumClicked(album, 0)
            }
        }
    }

    class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val albumCover = itemView.findViewById<ImageView>(R.id.single_album_cover_iv)
        private val albumName = itemView.findViewById<TextView>(R.id.single_album_name_tv)
        private val albumArtistName = itemView.findViewById<TextView>(R.id.single_artist_name_tv)

        fun bind(single: Albums, listener: OnItemClicked){
            val url = single.cover
            albumName.text = single.title
            albumArtistName.text = single.artist.name
            Glide.with(itemView)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.default_cover_art)
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(albumCover)
            itemView.setOnClickListener {
                listener.albumClicked(single, 1)
            }
        }
    }

}