package com.symphony.projjal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.R
import com.symphony.projjal.adapters.NowPlayingQueueAdapter
import com.symphony.projjal.databinding.FragmentNowPlayingQueueBinding
import com.symphony.projjal.utils.ConversionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NowPlayingQueueFragment : BaseFragment() {
    private val nowPlayingQueueFragmentJob = Job()
    private val nowPlayingQueueFragmentScope =
        CoroutineScope(Dispatchers.Main + nowPlayingQueueFragmentJob)

    var slidingUpPanelState: SlidingUpPanelLayout.PanelState? =
        SlidingUpPanelLayout.PanelState.COLLAPSED
        set(value) {
            if (field == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                scrollTo(musicService?.positionInPlayingOrder ?: 0)
            }
            field = value
        }
    private val currentQueue: MutableList<Song?> = mutableListOf()

    private var _binding: FragmentNowPlayingQueueBinding? = null
    private val binding get() = _binding!!

    private var nowPlayingQueueAdapter: NowPlayingQueueAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowPlayingQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun scrollTo(position: Int) {
        if (position == -1) {
            return
        }
        binding.nowPlayingQueue.post {
            (binding.nowPlayingQueue.layoutManager as LinearLayoutManager?)?.scrollToPositionWithOffset(
                if (position >= currentQueue.size) position else position + 1,
                0
            )
            nowPlayingQueueAdapter?.setPositionInPlayingOrder(position)
        }
    }

    override fun onPlayingQueueChanged(queue: MutableList<Song?>) {
        currentQueue.clear()
        currentQueue.addAll(queue)
        if (nowPlayingQueueAdapter == null) {
            nowPlayingQueueAdapter = NowPlayingQueueAdapter(queue, {
                musicService?.playSongInPlayingOrderAt(it)
            }, { view, song ->

            })
            binding.nowPlayingQueue.adapter = nowPlayingQueueAdapter
        } else {
            nowPlayingQueueAdapter?.setQueue(queue)
        }
    }

    private fun changeUpNextText(position: Int) {
        nowPlayingQueueFragmentScope.launch(Dispatchers.IO) {
            var duration = 0

            for (i in position + 1 until currentQueue.size) {
                duration += currentQueue[i]?.duration ?: 0
            }

            val upNextString =
                String.format(
                    getString(R.string.up_next),
                    ConversionUtils.milisToTimeString(duration)
                )
            nowPlayingQueueFragmentScope.launch {
                binding.upNext.text = upNextString
            }
        }
    }

    fun setUpNextTextColor(color: Int) {
        try {
            binding.upNext.setTextColor(color)
        } catch (ignored: Exception) {
        }
    }

    override fun onSongChanged(position: Int, song: Song?) {
        changeUpNextText(position)
        if (slidingUpPanelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            nowPlayingQueueAdapter?.setPositionInPlayingOrder(position)
        }
    }

    companion object {
        fun newInstance(): NowPlayingQueueFragment {
            return NowPlayingQueueFragment()
        }
    }
}