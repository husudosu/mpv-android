package `is`.xyz.streaming

import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences

class Settings(context: Context) {
    var mediaServerHost: String = "127.0.0.1"
    var mediaServerPort: Int = 8080

    init {
        val prefs = getDefaultSharedPreferences(context)

        this.mediaServerHost = prefs.getString("media_server_host", "127.0.0.1").toString()
        this.mediaServerPort = prefs.getInt("media_server_port", 8080)
    }
}