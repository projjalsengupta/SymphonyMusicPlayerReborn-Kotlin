package com.symphony.projjal.activities

import androidx.appcompat.app.AppCompatActivity
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.SymphonyApplication
import com.symphony.projjal.exoplayer.SymphonyExoPlayer
import com.symphony.projjal.services.MusicService

open class BaseActivity : AppCompatActivity(), SymphonyExoPlayer.EventListener {
    protected var musicService: MusicService? = null

    override fun onResume() {
        super.onResume()
        SymphonyApplication.applicationInstance.getNonNullMusicService {
            musicService = it
            musicService?.addEventListener(this@BaseActivity, true)
        }
    }

    override fun onPause() {
        super.onPause()
        musicService?.removeEventListener(this@BaseActivity)
        musicService = null
    }

    override fun onPlayingQueueChanged(queue: MutableList<Song>) {
    }

    override fun onShuffleChanged(shuffle: Boolean) {
    }

    override fun onRepeatChanged(repeat: Int) {
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
    }

    override fun onSongChanged(position: Int, song: Song?) {
    }

    override fun onPlaybackPositionChanged(playbackPosition: Int, duration: Int) {
    }
}