package widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.deezer.R

class TrackListTopInfoView (context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var coverIV: ImageView
    private var artistNameTV: TextView
    private var albumNameTV: TextView
    private var isAlbumExplicit = false
    private var explicitTV: TextView
    private var albumReleaseDateYv: TextView
    private var attributes : TypedArray


    init {
        inflate(context, R.layout.custom_track_list_header_view, this)
        coverIV = findViewById(R.id.album_cover_iv)
        artistNameTV = findViewById(R.id.artist_name_tv)
        albumNameTV = findViewById(R.id.album_name_tv)
        albumReleaseDateYv = findViewById(R.id.album_release_date_tv)
        explicitTV = findViewById(R.id.is_explicit_tv)

        attributes = context.obtainStyledAttributes(attrs, R.styleable.TrackListTopInfoView)
        setViews()
        attributes.recycle()
    }

    private fun setViews() {
        isAlbumExplicit = attributes.getBoolean(R.styleable.TrackListTopInfoView_explicit, false)

        coverIV.setImageDrawable(attributes.getDrawable(R.styleable.TrackListTopInfoView_cover))
        artistNameTV.text = attributes.getString(R.styleable.TrackListTopInfoView_artist)
        albumNameTV.text = attributes.getString(R.styleable.TrackListTopInfoView_album)
        albumReleaseDateYv.text =  attributes.getString(R.styleable.TrackListTopInfoView_release_date)
        if (isAlbumExplicit) {
           explicitTV.visibility = View.VISIBLE
        }else{
            explicitTV.visibility = View.INVISIBLE
        }


    }

}