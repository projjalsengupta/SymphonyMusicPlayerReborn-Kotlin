package com.symphony.projjal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.databinding.FragmentNowPlayingCurrentSongBinding

class NowPlayingCurrentSongFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentNowPlayingCurrentSongBinding? = null
    private val binding get() = _binding!!

    var slidingUpPanelState: SlidingUpPanelLayout.PanelState? =
        SlidingUpPanelLayout.PanelState.COLLAPSED
        set(value) {
            field = value
            if (value == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                binding.playPause.visibility = View.GONE
                binding.playNext.visibility = View.GONE
                binding.image.visibility = View.GONE
            } else if (value == SlidingUpPanelLayout.PanelState.EXPANDED) {
                binding.menu.visibility = View.GONE
                binding.favorite.visibility = View.GONE
            } else {
                binding.playPause.visibility = View.VISIBLE
                binding.playNext.visibility = View.VISIBLE
                binding.image.visibility = View.VISIBLE
                binding.menu.visibility = View.VISIBLE
                binding.favorite.visibility = View.VISIBLE
            }
        }

    var slidingUpPanelSlideOffset: Float = 0f
        set(value) {
            field = value
            setAlpha(binding.playPause, 1 - value)
            setAlpha(binding.playNext, 1 - value)
            setAlpha(binding.image, 1 - value)
            setAlpha(binding.menu, value)
            setAlpha(binding.favorite, value)
        }

    private fun setAlpha(view: View, slideOffset: Float) {
        view.alpha = 1 - slideOffset
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowPlayingCurrentSongBinding.inflate(inflater, container, false)
        setOnClickListeners()
        return binding.root
    }

    private fun setOnClickListeners() {
        binding.playPause.setOnClickListener(this)
        binding.playNext.setOnClickListener(this)
        binding.menu.setOnClickListener(this)
    }

    override fun onSongChanged(position: Int, song: Song?) {
        changeText(song)
        GlideApp.with(binding.image.context)
            .load(song)
            .override(binding.image.width, binding.image.height)
            .into(binding.image)
    }

    private fun changeText(song: Song?) {
        binding.title.text = song?.title
        val albumAndArtistText = "${song?.artist} • ${song?.album}"
        binding.albumAndArtist.text = albumAndArtistText

        binding.title.isSelected = true
        binding.albumAndArtist.isSelected = true
    }

    private fun setPlayPause(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playPause.setImageResource(R.drawable.ic_play)
        }
    }

    companion object {
        fun newInstance(): NowPlayingCurrentSongFragment {
            return NowPlayingCurrentSongFragment()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.playPause -> {
                musicService?.changePlayPause()
            }
            R.id.playNext -> {
                musicService?.playNext()
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        setPlayPause(isPlaying)
    }
}