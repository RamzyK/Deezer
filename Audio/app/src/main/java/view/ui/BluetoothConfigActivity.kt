package view.ui

import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deezer.R


class BluetoothConfigActivity : AppCompatActivity() {

    private var sendDataBtn: Button? = null

    private var topSpeaker: ImageView? = null
    private var leftSpeaker: ImageView? = null
    private var rightSpeaker: ImageView? = null

    private var randomSpeakerIsOn = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_config)

        sendDataBtn = findViewById(R.id.snd)
        topSpeaker = findViewById(R.id.top_speaker_img)
        leftSpeaker = findViewById(R.id.left_speaker_img)
        rightSpeaker = findViewById(R.id.right_speaker_img)

        setListeners()
        sendConfigDone("1")
        setSpeakerOn()
    }

    private fun setListeners() {
        sendDataBtn?.setOnClickListener {
            Toast.makeText(this, getString(R.string.config_sent_message), Toast.LENGTH_LONG).show()
            sendConfigDone("2")
            this.onBackPressed()
        }
    }

    private fun sendConfigDone(code: String) {
        val mediaSession = MediaSession(this, getString(R.string.app_name))
        val state = PlaybackState.Builder()
            .setActions(
                PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PLAY_PAUSE or
                        PlaybackState.ACTION_PLAY_FROM_MEDIA_ID or PlaybackState.ACTION_PAUSE or
                        PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS
            )
            .setState(PlaybackState.STATE_PLAYING, 1, 1f, SystemClock.elapsedRealtime())
            .build()

        val metadata = MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, "")
            .putString(MediaMetadata.METADATA_KEY_ARTIST, "")
            .putString(MediaMetadata.METADATA_KEY_GENRE, code)
            .build()

        mediaSession.isActive = true
        mediaSession.setMetadata(metadata)
        mediaSession.setPlaybackState(state)
        Toast.makeText(this, getString(R.string.config_sent_message), Toast.LENGTH_LONG).show()

    }

    private fun setSpeakerOn() {
        randomSpeakerIsOn = (0..2).random()

        when (randomSpeakerIsOn) {
            0 -> topSpeaker?.setImageResource(R.drawable.top_activated)
            1 -> leftSpeaker?.setImageResource(R.drawable.left_activated)
            2 -> rightSpeaker?.setImageResource(R.drawable.right_activated)
        }
    }

}