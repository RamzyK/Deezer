package view.adapter

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezer.R
import network.model.albums.Albums
import network.model.tracklist.Song
import utils.OnItemClicked
import utils.OnSongClicked

class DetailAlbumAdapter(clickListener: OnSongClicked) : RecyclerView.Adapter<DetailAlbumAdapter.TrackListSongViewHolder>() {
    var songsList : List<Song>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private lateinit var layoutInflater : LayoutInflater
    private var listener = clickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListSongViewHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        val songView = layoutInflater.inflate(R.layout.track_list_songs_cell, parent, false)
        return TrackListSongViewHolder(songView)
    }

    override fun getItemCount(): Int {
        if(songsList == null){
            return 0
        }
        return songsList!!.size
    }

    override fun onBindViewHolder(holder: TrackListSongViewHolder, position: Int) {
        holder.bind(songsList!![position], position, listener)
    }


    class TrackListSongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songPositionTv = itemView.findViewById<TextView>(R.id.song_pos_tv)
        val songNameTv = itemView.findViewById<TextView>(R.id.song_name_tv)
        val songArtistNameTv = itemView.findViewById<TextView>(R.id.artist_name_on_song_tv)
        val songDurationTv = itemView.findViewById<TextView>(R.id.song_duration_tv)
        val favSongIv = itemView.findViewById<ImageView>(R.id.fav_image_view)

        fun bind(song: Song, pos: Int, listener: OnSongClicked){
            songPositionTv.text = (pos + 1).toString()
            songNameTv.text = song.title
            songArtistNameTv.text = song.artist.name
            songDurationTv.text = song.duration.toString()

            itemView.setOnClickListener{
                listener.trackListSongClicked(song)
            }
        }
    }
}