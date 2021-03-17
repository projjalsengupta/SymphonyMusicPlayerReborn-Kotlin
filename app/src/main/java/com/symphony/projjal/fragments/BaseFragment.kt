package com.symphony.projjal.fragments

import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.*
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.SymphonyApplication.Companion.applicationInstance
import com.symphony.projjal.exoplayer.SymphonyExoPlayer
import com.symphony.projjal.services.MusicService

open class BaseFragment : Fragment(), SymphonyExoPlayer.EventListener {
    protected var musicService: MusicService? = null

    override fun onResume() {
        super.onResume()
        applicationInstance.getNonNullMusicService {
            musicService = it
            musicService?.addEventListener(this@BaseFragment, true)
        }
    }

    override fun onPause() {
        super.onPause()
        musicService?.removeEventListener(this@BaseFragment)
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