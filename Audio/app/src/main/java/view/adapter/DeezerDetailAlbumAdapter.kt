package view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezer.R
import network.model.tracklist.Song
import utils.OnSongClicked

class DeezerDetailAlbumAdapter(clickListener: OnSongClicked) : RecyclerView.Adapter<DeezerDetailAlbumAdapter.TrackListSongViewHolder>() {
    var songsList : List<Song>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private lateinit var layoutInflater : LayoutInflater
    private var listener = clickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListSongViewHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        val songView = layoutInflater.inflate(R.layout.cell_track_list_songs, parent, false)
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
        private val songPositionTv = itemView.findViewById<TextView>(R.id.song_pos_tv)
        private val songNameTv = itemView.findViewById<TextView>(R.id.song_name_tv)
        private val songArtistNameTv = itemView.findViewById<TextView>(R.id.artist_name_on_song_tv)
        //rivate val favSongIv = itemView.findViewById<ImageView>(R.id.fav_image_view)

        fun bind(song: Song, pos: Int, listener: OnSongClicked){
            songPositionTv.text = (pos + 1).toString()
            songNameTv.text = song.title_short
            songArtistNameTv.text = song.artist.name

            itemView.setOnClickListener{
                listener.trackListSongClicked(song, pos)
            }
        }
    }
}