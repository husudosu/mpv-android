package `is`.xyz.streaming

import android.net.Uri

 class APIMediaPlaybackService {
    val uriBuilder: Uri.Builder = Uri.Builder()

    fun generateMediaUrl(host: String, port: Int, fileName: String): Uri {
        return uriBuilder
            .encodedPath("http://$host:$port/master.m3u8")
            .appendQueryParameter("file_name", fileName)
            .appendQueryParameter("video_map", "0")
            .appendQueryParameter("audio_map", "0")
            .build()
    }

}