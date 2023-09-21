package com.xilonet.signa.model.android

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

// Se encarga del back-end de la reproducción de videos. También se encarga de limpiar memoria.
class ExoPlayerManager(private val ctxt: Context) {

    // Holds the last ExoPlayer in order to release it when we create another one
    private var lastPlayer: ExoPlayer? = null

    // Creates an ExoPlayer to show the video passed in the path "videoPath"
    fun getExoPlayer(videoPath: String): ExoPlayer {
        lastPlayer?.release()
        val videoUri = Uri.parse("asset:///$videoPath")
        val exoPlayer = ExoPlayer.Builder(ctxt).build().apply {
                            val dataSourceFactory = DefaultDataSource.Factory(ctxt)
                            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(MediaItem.fromUri(videoUri))
                            setMediaSource(source)
                            prepare()
                        }

        exoPlayer.apply{
            volume = 0f
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }

        lastPlayer = exoPlayer
        return exoPlayer
    }
}