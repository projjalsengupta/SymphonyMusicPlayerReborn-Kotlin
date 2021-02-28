package com.symphony.projjal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.SymphonyGlideExtension.small
import com.symphony.projjal.SymphonyGlideExtension.songPlaceholder
import com.symphony.projjal.databinding.FragmentNowPlayingSmallControllerBinding

class NowPlayingSmallControllerFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentNowPlayingSmallControllerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowPlayingSmallControllerBinding.inflate(inflater, container, false)
        setOnClickListeners()
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.playPause.id -> musicService?.changePlayPause()
            binding.playNext.id -> musicService?.playNext()
        }
    }

    private fun setOnClickListeners() {
        binding.playPause.setOnClickListener(this@NowPlayingSmallControllerFragment)
        binding.playNext.setOnClickListener(this@NowPlayingSmallControllerFragment)
    }

    private fun updateSongInfo(song: Song?) {
        if (song == null) {
            return
        }
        binding.text1.text = song.title
        binding.text2.text = song.artist
        GlideApp.with(binding.image.context)
            .load(song)
            .small()
            .songPlaceholder(context)
            .into(binding.image.image)
    }

    private fun setPlayPause(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun setProgress(progress: Int, max: Int) {
        binding.progressBar.progress = progress
        binding.progressBar.max = max
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        setPlayPause(isPlaying)
    }

    override fun onPlaybackPositionChanged(playbackPosition: Int, duration: Int) {
        setProgress(playbackPosition, duration)
    }

    override fun onSongChanged(position: Int, song: Song?) {
        updateSongInfo(song)
    }

    companion object {
        fun newInstance(): NowPlayingSmallControllerFragment {
            return NowPlayingSmallControllerFragment()
        }
    }
}