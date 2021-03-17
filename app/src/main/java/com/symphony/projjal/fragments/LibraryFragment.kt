package com.symphony.projjal.fragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.attached.isActive
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.symphony.colorutils.ColorUtils.adjustAlpha
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.adapters.LibraryPagerAdapter
import com.symphony.projjal.databinding.FragmentLibraryBinding
import com.symphony.projjal.singletons.Cab
import com.symphony.projjal.utils.ViewUtils
import com.symphony.themeengine.ThemeEngine
import kotlin.math.abs


class LibraryFragment : BaseFragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: LibraryPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        setUpToolbar()
        loadViewPager()
        return binding.root
    }

    private var previousPosition = 0

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (previousPosition == position) {
                return
            }
            if (Cab.cab?.isActive() == true) {
                Cab.cab?.destroy()
            }
            previousPosition = position
        }
    }

    override fun onResume() {
        super.onResume()
        binding.libraryViewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onPause() {
        super.onPause()
        binding.libraryViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    private fun loadViewPager() {
        val activity = activity ?: return

        pagerAdapter = LibraryPagerAdapter(activity)
        binding.libraryViewPager.adapter = pagerAdapter

        binding.libraryViewPager.offscreenPageLimit = 4

        val tabTitles = arrayOf(
            getString(R.string.songs),
            getString(R.string.albums),
            getString(R.string.artists),
            getString(R.string.playlists)
        )

        TabLayoutMediator(binding.tabLayout, binding.libraryViewPager) { tab, position ->
            tab.text = tabTitles[position]
            binding.libraryViewPager.setCurrentItem(tab.position, true)
        }.attach()

        val themeEngine = ThemeEngine(activity)
        binding.tabLayout.setTabTextColors(
            adjustAlpha(themeEngine.textColorPrimary, 0.33f),
            themeEngine.textColorPrimary
        )

        binding.libraryViewPager.post {
            startPostponedEnterTransition()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpToolbar() {
        val activity = activity
        if (activity != null) {
            ViewUtils.topFitsSystemWindows(
                binding.toolbarContainer,
                context,
                resources.configuration.orientation
            )
            binding.title.setTextColor(ThemeEngine(activity).textColorPrimary)
            GlideApp.with(this@LibraryFragment)
                .load(R.drawable.ic_logo)
                .override(binding.logo.width, binding.logo.height)
                .into(binding.logo)
            binding.title.text = getString(R.string.app_name)
            binding.title.setTextColor(ThemeEngine(activity).textColorPrimary)
            binding.search.setColorFilter(ThemeEngine(activity).textColorPrimary)
            binding.toolbarContainer.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.toolbar.alpha = 1 -
                        (abs(verticalOffset).toFloat() / binding.toolbar.height.toFloat())
            })
        }
    }

    companion object {
        fun newInstance(): LibraryFragment {
            return LibraryFragment()
        }
    }


}