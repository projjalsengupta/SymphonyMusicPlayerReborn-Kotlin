package com.symphony.projjal.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.symphony.projjal.fragments.NowPlayingLyricsFragment
import com.symphony.projjal.fragments.NowPlayingQueueFragment

class NowPlayingBottomDetailsAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val nowPlayingQueueFragment = NowPlayingQueueFragment.newInstance()
    private val nowPlayingLyricsFragment = NowPlayingLyricsFragment.newInstance()

    override fun getItemCount(): Int {
        return 2
    }

    fun changeColor(color: Int) {
        nowPlayingQueueFragment.setUpNextTextColor(color)
    }

    fun changeSlidingUpPanelState(panelState: SlidingUpPanelLayout.PanelState?) {
        nowPlayingQueueFragment.slidingUpPanelState = panelState
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> nowPlayingQueueFragment
            1 -> nowPlayingLyricsFragment
            else -> Fragment()
        }
    }
}