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
    // Holds a list of ExoPlayer instances
    private val players = mutableListOf<ExoPlayer>()

    // Creates a new ExoPlayer for the video located at "videoPath"
    fun getExoPlayer(videoPath: String): ExoPlayer {
        val videoUri = Uri.parse("asset:///$videoPath")
        val exoPlayer = ExoPlayer.Builder(ctxt).build().apply {
            val dataSourceFactory = DefaultDataSource.Factory(ctxt)
            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUri))
            setMediaSource(source)
            prepare()
        }

        exoPlayer.apply {
            volume = 1f // Set volume to 1 (full volume)
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }

        players.add(exoPlayer)
        return exoPlayer
    }

    // Releases all ExoPlayer instances
    fun releaseAllPlayers() {
        players.forEach { it.release() }
        players.clear()
    }
}
