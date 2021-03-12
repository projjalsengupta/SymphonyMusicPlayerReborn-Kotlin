package com.symphony.projjal.activities

import android.os.Bundle
import android.view.View
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.attached.isActive
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.SymphonyApplication.Companion.applicationInstance
import com.symphony.projjal.databinding.ActivityMainBinding
import com.symphony.projjal.fragments.LibraryFragment
import com.symphony.projjal.fragments.NowPlayingFragment
import com.symphony.projjal.fragments.NowPlayingSmallControllerFragment
import com.symphony.projjal.singletons.Cab.cab
import com.symphony.projjal.utils.ConversionUtils.dpToPx
import com.symphony.projjal.utils.ViewUtils.getNavigationBarHeight
import com.symphony.themeengine.ThemeEngine

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var nowPlayingSmallControllerFragment = NowPlayingSmallControllerFragment.newInstance()
    private var libraryFragment = LibraryFragment.newInstance()
    private var nowPlayingFragment = NowPlayingFragment.newInstance()

    val slidingPanelState: SlidingUpPanelLayout.PanelState get() = binding.slidingUpPanel.panelState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(ThemeEngine(this).theme)
        setContentView(binding.root)
        setUpLibrary()
        setUpSlidingPanel()
        setUpSmallController()
        setUpNowPlaying()
    }

    private fun setUpLibrary() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(
            binding.fragmentContainer.id,
            libraryFragment
        )
        fragmentTransaction.commit()
    }

    private fun setUpSmallController() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(
            binding.smallControlsContainer.id,
            nowPlayingSmallControllerFragment
        )
        fragmentTransaction.commit()
    }

    private fun setUpNowPlaying() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(
            binding.nowPlayingContainer.id,
            nowPlayingFragment
        )
        fragmentTransaction.commit()
        nowPlayingFragment.mainActivity = this@MainActivity
    }

    private fun setAlpha(view: View, slideOffset: Float) {
        view.alpha = 1 - slideOffset
    }

    private fun updateSlidingPanelHeight(songCount: Int) {
        binding.slidingUpPanel.panelHeight = getNavigationBarHeight(
            context = this@MainActivity,
            orientation = resources.configuration.orientation
        ) + dpToPx(if (songCount == 0) 0 else 51)
    }

    fun setAntiDragView(view: View) {
        binding.slidingUpPanel.setAntiDragView(view)
    }

    private fun setUpSlidingPanel() {
        binding.slidingUpPanel.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                if (musicService?.totalSongCount?.equals(0) == true) {
                    setAlpha(binding.smallControlsContainer, 1 - slideOffset)
                } else {
                    setAlpha(binding.smallControlsContainer, slideOffset)
                }
                setAlpha(binding.nowPlayingContainer, 1 - slideOffset)
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                when (newState) {
                    SlidingUpPanelLayout.PanelState.EXPANDED -> {
                        binding.smallControlsContainer.visibility = View.GONE
                        binding.nowPlayingContainer.visibility = View.VISIBLE
                    }
                    SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                        binding.smallControlsContainer.visibility = View.VISIBLE
                        binding.nowPlayingContainer.visibility = View.GONE
                    }
                    else -> {
                        binding.smallControlsContainer.visibility = View.VISIBLE
                        binding.nowPlayingContainer.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        if (!nowPlayingFragment.closeSlidingUpPanelLayout()) {
            when {
                binding.slidingUpPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED -> {
                    binding.slidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                }
                cab?.isActive() == true -> {
                    cab?.destroy()
                }
                else -> {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        applicationInstance.getNonNullMusicService {
            it.removeEventListener(this@MainActivity)
        }
    }

    override fun onPlayingQueueChanged(queue: MutableList<Song?>) {
        updateSlidingPanelHeight(queue.size)
    }
}