package com.symphony.projjal.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.symphony.projjal.fragments.AlbumsFragment
import com.symphony.projjal.fragments.ArtistsFragment
import com.symphony.projjal.fragments.PlaylistsFragment
import com.symphony.projjal.fragments.SongsFragment

class LibraryPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SongsFragment.newInstance()
            1 -> AlbumsFragment.newInstance()
            2 -> ArtistsFragment.newInstance()
            3 -> PlaylistsFragment.newInstance()
            else -> Fragment()
        }
    }
}