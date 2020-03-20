package widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.deezer.R

class CustomStickBottomBar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var coverIV: ImageView
    private var artistNameTV: TextView
    private var songNameTV: TextView
    private var playPauseIv: ImageView

    private var attributes : TypedArray


    init {
        inflate(context, R.layout.custom_bottom_sticky_bar, this)
        coverIV = findViewById(R.id.bottom_sticky_bar_cover_iv)
        artistNameTV = findViewById(R.id.bottom_sticky_bar_artist_tv)
        songNameTV = findViewById(R.id.bottom_sticky_bar_song_tv)
        playPauseIv = findViewById(R.id.bottom_sticky_bar_player_btn)

        attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomStickBottomBar)
        setViews()
        attributes.recycle()
    }

    private fun setViews() {
        coverIV.setImageDrawable(attributes.getDrawable(R.styleable.CustomStickBottomBar_bottom_bar_cover))
        artistNameTV.text = attributes.getString(R.styleable.CustomStickBottomBar_bottom_bar_artist)
        songNameTV.text = attributes.getString(R.styleable.CustomStickBottomBar_bottom_bar_album)

    }
}