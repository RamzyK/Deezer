package media.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionService : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0!!.sendBroadcast(
            Intent("DEEZER_PLAYER")
                .putExtra("actioname", p1!!.action)
        )
    }
}