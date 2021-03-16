package com.symphony.projjal.activities

import android.animation.Animator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.attached.isActive
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.symphony.colorutils.ColorUtils
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.SymphonyApplication.Companion.applicationInstance
import com.symphony.projjal.databinding.ActivityMainBinding
import com.symphony.projjal.fragments.LibraryFragment
import com.symphony.projjal.fragments.NowPlayingFragment
import com.symphony.projjal.fragments.NowPlayingSmallControllerFragment
import com.symphony.projjal.glide.palette.PaletteBitmap
import com.symphony.projjal.singletons.Cab.cab
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
        setUpFragmentManagerBackstackListener()
        val context = applicationContext
        if (context != null) {
            previousBackgroundColor = ThemeEngine(context).backgroundColor
            currentSongBackgroundColor = previousBackgroundColor
            currentSongForegroundColor = ThemeEngine(context).textColorPrimary
        }
    }

    private fun setUpFragmentManagerBackstackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setSmallControllerAndNavigationBarColors(
                    currentSongBackgroundColor,
                    currentSongForegroundColor
                )
            }
        }
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
        setAlpha(binding.smallControlsContainer, 1f)
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
        binding.smallControlsContainer.doOnPreDraw {
            binding.slidingUpPanel.panelHeight = getNavigationBarHeight(
                context = this@MainActivity,
                orientation = resources.configuration.orientation
            ) + if (songCount == 0) 0 else binding.smallControlsContainer.height
        }
    }

    fun setAntiDragView(view: View) {
        binding.slidingUpPanel.setAntiDragView(view)
    }

    private fun setUpSlidingPanel() {
        binding.slidingUpPanel.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                setAlpha(binding.smallControlsContainer, slideOffset)
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

    private var previousBackgroundColor = Color.BLACK

    private var animator: Animator? = null

    private fun animateNavigationBackground(backgroundColor: Int) {
        animator?.cancel()
        animator = ColorUtils.animateBackgroundColorChange(
            previousBackgroundColor,
            backgroundColor,
            binding.slidingContentContainer
        )
        previousBackgroundColor = backgroundColor
    }

    fun setSmallControllerAndNavigationBarColors(backgroundColor: Int, foregroundColor: Int) {
        animateNavigationBackground(backgroundColor)
        nowPlayingSmallControllerFragment.setColors(
            backgroundColor,
            foregroundColor
        )
    }

    private var currentSongBackgroundColor: Int = 0
    private var currentSongForegroundColor: Int = 0

    override fun onSongChanged(position: Int, song: Song?) {
        GlideApp.with(applicationContext)
            .`as`(PaletteBitmap::class.java)
            .load(song)
            .override(500, 500)
            .into(object : CustomTarget<PaletteBitmap?>() {
                override fun onResourceReady(
                    resource: PaletteBitmap,
                    transition: Transition<in PaletteBitmap?>?
                ) {
                    currentSongBackgroundColor = resource.backgroundColor
                    currentSongForegroundColor = resource.foregroundColor
                    if (supportFragmentManager.backStackEntryCount == 0) {
                        setSmallControllerAndNavigationBarColors(
                            resource.backgroundColor,
                            resource.foregroundColor
                        )
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    currentSongBackgroundColor = ContextCompat.getColor(
                        applicationContext,
                        R.color.grey_400
                    )
                    currentSongForegroundColor = Color.BLACK
                    if (supportFragmentManager.backStackEntryCount == 0) {
                        setSmallControllerAndNavigationBarColors(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.grey_400
                            ),
                            Color.BLACK
                        )
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}