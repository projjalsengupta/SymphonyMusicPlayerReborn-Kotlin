package com.symphony.projjal.fragments

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.symphony.colorutils.ColorUtils
import com.symphony.colorutils.ColorUtils.adjustAlpha
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.databinding.FragmentNowPlayingSmallControllerBinding
import com.symphony.themeengine.ThemeEngine

class NowPlayingSmallControllerFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentNowPlayingSmallControllerBinding? = null
    private val binding get() = _binding!!

    private var previousBackgroundColor = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowPlayingSmallControllerBinding.inflate(inflater, container, false)
        setOnClickListeners()
        val context = context
        if (context != null) {
            previousBackgroundColor = ThemeEngine(context).backgroundColor
        }
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.playPause.id -> musicService?.changePlayPause()
        }
    }

    private fun setOnClickListeners() {
        binding.playPause.setOnClickListener(this@NowPlayingSmallControllerFragment)
    }

    private fun updateSongInfo(song: Song?) {
        if (song == null) {
            return
        }
        binding.text1.text = song.title
        binding.text2.text = song.artist
        GlideApp.with(binding.image.context)
            .load(song)
            .override(binding.image.width, binding.image.height)
            .into(binding.image)
    }

    private var animator: Animator? = null

    fun setColors(backgroundColor: Int, foregroundColor: Int, gradient: Boolean = false) {
        animator?.cancel()
        animator = ColorUtils.animateBackgroundColorChange(
            previousBackgroundColor,
            backgroundColor,
            binding.backgroundView,
            gradient = gradient
        )
        previousBackgroundColor = backgroundColor
        binding.text1.setTextColor(foregroundColor)
        binding.text2.setTextColor(foregroundColor)
        binding.playPause.setColorFilter(foregroundColor)
        binding.progressBar.setIndicatorColor(foregroundColor)
        binding.progressBar.trackColor = adjustAlpha(foregroundColor, 0.25f)
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