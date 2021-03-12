package com.symphony.projjal.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.Timeline
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.exoplayer.SymphonyExoPlayer

class MusicService : Service() {
    private var player: SymphonyExoPlayer? = null

    private val binder = MusicServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    fun playSongInPlayingOrderAt(position: Int) {
        player?.setPositionInPlayingOrder(position)
        player?.play()
    }

    fun playList(songs: MutableList<Song>, position: Int = 0) {
        player?.playList(songs, position)
    }

    fun shuffleList(songs: MutableList<Song>) {
        player?.shuffleList(songs)
    }

    fun addEventListener(
        eventListener: SymphonyExoPlayer.EventListener,
        invokeCallbacks: Boolean = false
    ) {
        player?.addListener(eventListener, invokeCallbacks)
    }

    fun addVisualizerListener(
        visualizerListener: SymphonyExoPlayer.VisualizerListener
    ) {
        player?.addVisualizerListener(visualizerListener)
    }

    fun removeEventListener(eventListener: SymphonyExoPlayer.EventListener) {
        player?.removeListener(eventListener)
    }

    fun removeVisualizerListener(visualizerListener: SymphonyExoPlayer.VisualizerListener) {
        player?.removeVisualizerListener(visualizerListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    fun playNext() {
        player?.playNext()
    }

    fun playPrevious(forcePrevious: Boolean = false) {
        player?.playPrevious(forcePrevious)
    }

    fun seekTo(position: Long) {
        player?.playbackPosition = position
    }

    val isPlaying: Boolean get() = player?.isPlaying ?: false

    val shuffle: Boolean get() = player?.shuffle ?: false

    val repeat: Int get() = player?.repeat ?: REPEAT_MODE_OFF

    val playbackPosition: Long get() = player?.playbackPosition ?: 0

    val duration: Long get() = player?.duration ?: 0

    val currentSong: Song? get() = player?.currentSong

    val currentTimeline: Timeline? get() = player?.currentTimeline

    val queueInPlayingOrder: MutableList<Song?>
        get() = player?.getQueueInPlayingOrder() ?: mutableListOf()

    val currentWindowIndex: Int get() = player?.currentWindowIndex ?: 0

    val totalSongCount: Int get() = player?.size ?: 0

    val isQueueEmpty: Boolean get() = player?.isQueueEmpty ?: true

    val audioSessionId: Int get() = player?.audioSessionId ?: 0

    val positionInPlayingOrder: Int
        get() {
            return player?.getPositionInPlayingOrder(currentWindowIndex, shuffle) ?: 0
        }

    fun changeRepeat() {
        player?.changeRepeat()
    }

    fun changeShuffle() {
        player?.changeShuffle()
    }

    fun changePlayPause() {
        player?.changePlayPause()
    }

    fun setPositionInPlayingOrder(position: Int) {
        player?.setPositionInPlayingOrder(position)
    }

    override fun onCreate() {
        super.onCreate()
        player = SymphonyExoPlayer(applicationContext, this@MusicService)
    }
}
