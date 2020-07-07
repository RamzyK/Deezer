package view.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deezer.R
import java.io.IOException
import java.util.*


class BluetoothConfigActivity : AppCompatActivity() {
    private var socket: BluetoothSocket? = null
    private var mReceiver: BroadcastReceiver? =  null

    private var sendDataBtn: Button? = null
    private var socketState: TextView? = null
    private var isCoToSocket: Boolean  = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_config)

        sendDataBtn = findViewById(R.id.snd)
        socketState = findViewById(R.id.socket_state)

        setListeners()
        registerBtBroadcast()
        connectToSocketBt()
    }

    private fun registerBtBroadcast(){
        mReceiver = receiveBluetoothState()
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        this.registerReceiver(mReceiver, filter)
    }

    private fun receiveBluetoothState(): BroadcastReceiver{
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> {
                            Log.d("co", "Bluetooth off")
                            isCoToSocket = false
                            socket?.close()
                            connectToSocketBt()
                        }
                        BluetoothAdapter.STATE_ON -> {
                            Log.d("co", "Bluetooth on")
                        }
                    }
                }
                when(action){
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        Log.d("deco", "Connected to device")
                        isCoToSocket = true
                        connectToSocketBt()
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        Log.d("deco", "Disconnected from device")
                        isCoToSocket = false
                        socket?.close()
                        manageSocketState(isCoToSocket)
                    }
                }
            }
        }
    }


    private fun connectToSocketBt(){
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if(btAdapter != null){
            if (btAdapter.isEnabled) {
                val deviceMacAddress = "B8:27:EB:A1:9D:A3"
                if (deviceMacAddress !== "?") {
                    val device = btAdapter.getRemoteDevice(deviceMacAddress)
                    val serialUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // bluetooth serial port service
                    try {
                        socket = device.createRfcommSocketToServiceRecord(serialUuid)
                        socket = device.javaClass.getMethod("createInsecureRfcommSocket",
                            Int::class.javaPrimitiveType
                        ).invoke(device, 1) as BluetoothSocket
                    } catch (e: Exception) {
                        Log.e("", "Error creating socket")
                    }
                    try {
                        socket!!.connect()
                        isCoToSocket = true
                        Log.e("", "Connected")
                    } catch (e: IOException) {
                        Log.e("", e.message!!)
                        isCoToSocket = false
                    }
                } else {
                    Log.e("", "BT device not selected")
                }
            }
            manageSocketState(isCoToSocket)
        }else{
            Toast.makeText(this, "Bluetooth indisponible sur le device", Toast.LENGTH_LONG).show()
        }

    }

    private fun manageSocketState(state: Boolean){
        if(state){
            socketState?.text = getString(R.string.connected_indicator)
        }else{
            socketState?.text = getString(R.string.not_connected_indicator)
        }
    }

    private fun sendData(){
        if(isCoToSocket){
            try {
                socket!!.outputStream.write("OUHO".toByteArray())
            } catch (e: Exception) {
                Toast.makeText(this, "Désactiver puis réactiver le Bluetooth",  Toast.LENGTH_LONG).show()
                isCoToSocket = false
                socket?.close()
                manageSocketState(isCoToSocket)
                Log.e("TAG", e.message!!)
            }
        }
    }

    private fun setListeners(){
        sendDataBtn?.setOnClickListener {
            sendTextOverAVRCP()
            if(this.isCoToSocket){
                sendData()
            }else{
                Toast.makeText(this, "Désactiver puis réactiver le Bluetooth",  Toast.LENGTH_LONG).show()
            }
        }
    }

    fun sendTextOverAVRCP(){
    var mediaSession = MediaSession(this, "Deezer")
        val state = PlaybackState.Builder()
            .setActions(
                PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PLAY_PAUSE or
                        PlaybackState.ACTION_PLAY_FROM_MEDIA_ID or PlaybackState.ACTION_PAUSE or
                        PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS
            )
            .setState(PlaybackState.STATE_PLAYING, 1, 1f, SystemClock.elapsedRealtime())
            .build()
        //set the metadata to send, this is the text that will be displayed
        //if the strings are too long they might be cut off
        //you need to experiment with the receiving device to know max length
        val metadata = MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, "")
            .putString(MediaMetadata.METADATA_KEY_ARTIST, "")
            .putString(MediaMetadata.METADATA_KEY_GENRE, "1")
            .build()

        mediaSession.setActive(true)
        mediaSession.setMetadata(metadata)
        mediaSession.setPlaybackState(state)
    }



    override fun onDestroy() {
        super.onDestroy()

        socket?.close()
        unregisterReceiver(mReceiver)
    }
}