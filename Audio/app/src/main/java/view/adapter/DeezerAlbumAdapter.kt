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

class DeezerAlbumAdapter(clickListener: OnItemClicked) : RecyclerView.Adapter<DeezerAlbumAdapter.AlbumViewHolder>() {
    var albumList : List<Albums>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private lateinit var layoutInflater : LayoutInflater
    private var listener = clickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        val albumView = layoutInflater.inflate(R.layout.cell_album, parent, false)
        return AlbumViewHolder(albumView)
    }

    override fun getItemCount(): Int {
        if(albumList == null){
            return 0
        }
        return albumList!!.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albumList!![position], listener)
    }



    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val albumCover = itemView.findViewById<ImageView>(R.id.album_cover_iv)
        private val albumName = itemView.findViewById<TextView>(R.id.album_name_tv)

        fun bind(albums: Albums, listener: OnItemClicked){
            val url = albums.cover
            albumName.text = albums.title
            Glide.with(itemView)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.default_cover_art)
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(albumCover)
            itemView.setOnClickListener {
                listener.albumClicked(albums)
            }
        }
    }

}