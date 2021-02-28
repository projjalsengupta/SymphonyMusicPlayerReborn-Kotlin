package com.symphony.projjal.fragments

import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.R
import com.symphony.projjal.databinding.FragmentNowPlayingLyricsBinding
import com.symphony.projjal.utils.LyricsUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class NowPlayingLyricsFragment : BaseFragment() {
    private val nowPlayingLyricsFragmentJob = Job()
    private val nowPlayingLyricsFragmentScope =
        CoroutineScope(Dispatchers.Main + nowPlayingLyricsFragmentJob)

    private var _binding: FragmentNowPlayingLyricsBinding? = null
    private val binding get() = _binding!!

    private var currentLoadedSong: Song? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowPlayingLyricsBinding.inflate(inflater, container, false)
        setTextViewMovementMethod()
        return binding.root
    }

    private fun setTextViewMovementMethod() {
        binding.lyrics.movementMethod = ScrollingMovementMethod()
    }

    private fun setLyrics(song: Song?) {
        binding.lyrics.scrollTo(0, 0)
        binding.lyrics.text = ""
        nowPlayingLyricsFragmentScope.launch(Dispatchers.IO) {
            val lyrics: String? = try {
                LyricsUtil.findLyrics(context, song?.fileUri)
            } catch (ignored: Exception) {
                null
            }
            currentLoadedSong = song
            nowPlayingLyricsFragmentScope.launch {
                if (lyrics == null) {
                    binding.lyrics.text = getString(R.string.no_lyrics_found)
                } else {
                    binding.lyrics.text = lyrics
                }
            }
        }
    }

    override fun onSongChanged(position: Int, song: Song?) {
        if (currentLoadedSong != song) {
            setLyrics(song)
        }
    }

    companion object {
        fun newInstance(): NowPlayingLyricsFragment {
            return NowPlayingLyricsFragment()
        }
    }
}