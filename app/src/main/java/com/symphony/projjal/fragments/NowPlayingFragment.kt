package com.symphony.projjal.fragments

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2.*
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.symphony.colorutils.ColorUtils.animateBackgroundColorChange
import com.symphony.colorutils.ColorUtils.animateBackgroundColorChangeWithCircularReveal
import com.symphony.colorutils.ColorUtils.contrastColor
import com.symphony.colorutils.ColorUtils.getColor
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.SymphonyGlideExtension.large
import com.symphony.projjal.activities.MainActivity
import com.symphony.projjal.adapters.NowPlayingBottomDetailsAdapter
import com.symphony.projjal.adapters.NowPlayingViewPagerAdapter
import com.symphony.projjal.databinding.FragmentNowPlayingBinding
import com.symphony.projjal.glide.palette.PaletteBitmap
import com.symphony.projjal.utils.ConversionUtils.milisToTimeString
import com.symphony.projjal.utils.PreferenceUtils.nowPlayingColorChangingAnimationStyle
import com.symphony.projjal.utils.ViewUtils
import com.symphony.themeengine.ThemeEngine

class NowPlayingFragment : BaseFragment(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private lateinit var nowPlayingBottomDetailsAdapter: NowPlayingBottomDetailsAdapter

    private val nowPlayingCurrentSongFragment: NowPlayingCurrentSongFragment =
        NowPlayingCurrentSongFragment.newInstance()

    private var previousBackgroundColor: Int = 0

    private var fallbackBackgroundColor: Int = 0
    private var fallbackForegroundColor: Int = 0

    private var themeBackgroundColor: Int = 0
    private var themeForegroundColor: Int = 0

    private var _binding: FragmentNowPlayingBinding? = null
    private val binding get() = _binding!!

    private var nowPlayingViewPagerAdapter: NowPlayingViewPagerAdapter? = null

    private var previousState = ViewPager.SCROLL_STATE_IDLE
    private var isUserChanging = false
    private val pageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (isUserChanging) {
                musicService?.playSongInPlayingOrderAt(position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (previousState == SCROLL_STATE_DRAGGING
                && state == SCROLL_STATE_SETTLING
            ) {
                isUserChanging = true
            } else if (previousState == SCROLL_STATE_SETTLING
                && state == SCROLL_STATE_IDLE
            ) {
                isUserChanging = false
            }
            previousState = state
        }
    }

    var mainActivity: MainActivity? = null

    override fun onResume() {
        super.onResume()
        binding.nowPlayingViewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onPause() {
        super.onPause()
        binding.nowPlayingViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        getInitialColors()
        setUpPadding()
        setOnClickListenersAndSeekBarChangeListener()
        setMainActivityAntiDragView()
        setUpSlidingPanel()
        setUpCurrentSongFragment()
        loadBottomDetailsViewPager()
        return binding.root
    }

    private fun setUpCurrentSongFragment() {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(
            binding.nowPlayingCurrentSongFragmentContainer.id,
            nowPlayingCurrentSongFragment
        )
        fragmentTransaction?.commit()
    }

    fun closeSlidingUpPanelLayout(): Boolean {
        if (binding.slidingUpPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            binding.slidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            return true
        }
        return false
    }

    private fun setUpSlidingPanel() {
        val context = context
        if (context != null) {
            binding.colorView1.doOnLayout {
                val availablePanelHeight: Int =
                    binding.slidingUpPanel.height - binding.backgroundView.height - ViewUtils.getNavigationBarHeight(
                        context,
                        resources.configuration.orientation
                    ) - ViewUtils.getStatusBarHeight(
                        context,
                        resources.configuration.orientation
                    )
                val requiredPanelHeight: Int =
                    binding.nowPlayingCurrentSongFragmentContainer.height + binding.tabLayout.height
                if (availablePanelHeight < requiredPanelHeight) {
                    binding.viewPagerContainer.layoutParams.height =
                        binding.viewPagerContainer.height - (requiredPanelHeight - availablePanelHeight)
                    binding.viewPagerContainer.forceSquare = false
                }
                binding.slidingUpPanel.panelHeight = requiredPanelHeight
            }
        }
        binding.slidingUpPanel.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                nowPlayingCurrentSongFragment.slidingUpPanelSlideOffset = slideOffset
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                nowPlayingCurrentSongFragment.slidingUpPanelState = newState
                nowPlayingBottomDetailsAdapter.changeSlidingUpPanelState(newState)
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    scrollTo(musicService?.positionInPlayingOrder ?: 0)
                    binding.tabLayout.setTabTextColors(
                        getColor(context, R.color.grey_400),
                        getColor(context, R.color.grey_400)
                    )
                    val drawable = binding.tabLayout.tabSelectedIndicator
                    drawable.alpha = 0
                    binding.tabLayout.setSelectedTabIndicator(drawable)
                } else {
                    binding.tabLayout.setTabTextColors(
                        getColor(context, R.color.grey_400),
                        getColor(context, R.color.white)
                    )
                    val drawable = binding.tabLayout.tabSelectedIndicator
                    drawable.alpha = 255
                    binding.tabLayout.setSelectedTabIndicator(drawable)
                }
            }
        })
        binding.slidingUpPanel.setAntiDragView(binding.bottomDetailsViewPager)
    }

    private fun setMainActivityAntiDragView() {
        (activity as MainActivity?)?.setAntiDragView(binding.bottomDetailsContainer)
    }

    private fun getInitialColors() {
        val activity = activity
        if (activity != null) {
            val themeEngine = ThemeEngine(activity)
            previousBackgroundColor = themeEngine.backgroundColor
            fallbackBackgroundColor = ContextCompat.getColor(activity, R.color.grey_400)
            fallbackForegroundColor = ContextCompat.getColor(activity, R.color.black)
            themeBackgroundColor = themeEngine.backgroundColor
            themeForegroundColor = themeEngine.textColorPrimary
        }
    }

    private fun setUpPadding() {
        ViewUtils.fitsSystemWindows(
            view = binding.slidingUpPanel,
            context = activity,
            orientation = resources.configuration.orientation
        )
    }

    private fun setOnClickListenersAndSeekBarChangeListener() {
        binding.shuffle.setOnClickListener(this)
        binding.playPrevious.setOnClickListener(this)
        binding.playPause.setOnClickListener(this)
        binding.playNext.setOnClickListener(this)
        binding.repeat.setOnClickListener(this)
        binding.backgroundView.setOnClickListener(this)
        binding.seekBar.setOnSeekBarChangeListener(this)
        binding.seekBar.setOnSeekBarChangeListener(this)
        binding.bottomDetailsContainer.setOnClickListener(this)
    }

    private fun loadBottomDetailsViewPager() {
        val activity = activity ?: return

        nowPlayingBottomDetailsAdapter = NowPlayingBottomDetailsAdapter(activity)
        binding.bottomDetailsViewPager.adapter = nowPlayingBottomDetailsAdapter

        binding.bottomDetailsViewPager.isUserInputEnabled = false

        val tabTitles = arrayOf(
            getString(R.string.up_next_title),
            getString(R.string.lyrics_title)
        )

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (mainActivity?.slidingPanelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    try {
                        binding.slidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                    } catch (ignored: Exception) {
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (mainActivity?.slidingPanelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    try {
                        binding.slidingUpPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                    } catch (ignored: Exception) {
                    }
                }
            }

        })

        TabLayoutMediator(binding.tabLayout, binding.bottomDetailsViewPager) { tab, position ->
            tab.text = tabTitles[position]
            binding.bottomDetailsViewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.playPause -> {
                musicService?.changePlayPause()
            }
            R.id.playNext -> {
                musicService?.playNext()
            }
            R.id.playPrevious -> {
                musicService?.playPrevious()
            }
            R.id.shuffle -> {
                musicService?.changeShuffle()
            }
            R.id.repeat -> {
                musicService?.changeRepeat()
            }
        }
    }

    private fun setProgress(progress: Int, max: Int) {
        binding.seekBar.progress = progress
        binding.seekBar.max = max
        binding.duration.text = milisToTimeString(max)
        binding.currentPlaybackPosition.text = milisToTimeString(progress)
    }

    private fun setPlayPause(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun setShuffle(shuffle: Boolean) {
        if (shuffle) {
            binding.shuffle.alpha = 1f
        } else {
            binding.shuffle.alpha = 0.5f
        }
        binding.shuffle.setColorFilter(contrastColor(previousBackgroundColor))
    }

    private fun setRepeat(repeat: Int) {
        when (repeat) {
            REPEAT_MODE_OFF -> {
                binding.repeat.setImageResource(R.drawable.ic_repeat)
                binding.repeat.alpha = 0.5f
            }
            REPEAT_MODE_ALL -> {
                binding.repeat.setImageResource(R.drawable.ic_repeat)
                binding.repeat.alpha = 1f
            }
            REPEAT_MODE_ONE -> {
                binding.repeat.setImageResource(R.drawable.ic_repeat_one)
                binding.repeat.alpha = 1f
            }
        }
        binding.repeat.setColorFilter(contrastColor(previousBackgroundColor))
    }

    private fun scrollTo(position: Int) {
        if (position == -1) {
            return
        }
        binding.nowPlayingViewPager.post {
            binding.nowPlayingViewPager.setCurrentItem(
                position,
                if (mainActivity != null) mainActivity?.slidingPanelState == SlidingUpPanelLayout.PanelState.EXPANDED else true
            )
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            musicService?.seekTo(progress.toLong())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onPlayingQueueChanged(queue: MutableList<Song?>) {
        if (nowPlayingViewPagerAdapter == null) {
            nowPlayingViewPagerAdapter = NowPlayingViewPagerAdapter(queue)
            binding.nowPlayingViewPager.apply {
                adapter = nowPlayingViewPagerAdapter
                orientation = ORIENTATION_HORIZONTAL
                (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }
        } else {
            nowPlayingViewPagerAdapter?.setQueue(queue)
        }
    }

    override fun onShuffleChanged(shuffle: Boolean) {
        setShuffle(shuffle)
    }

    override fun onRepeatChanged(repeat: Int) {
        setRepeat(repeat)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        setPlayPause(isPlaying)
    }

    override fun onSongChanged(position: Int, song: Song?) {
        scrollTo(position)
        changeColors(song)
    }

    override fun onPlaybackPositionChanged(playbackPosition: Int, duration: Int) {
        setProgress(playbackPosition, duration)
    }

    private fun changeColors(song: Song?) {
        val activity = activity
        if (activity != null) {
            GlideApp.with(activity)
                .`as`(PaletteBitmap::class.java)
                .load(song)
                .large()
                .into(object : CustomTarget<PaletteBitmap?>() {
                    override fun onResourceReady(
                        resource: PaletteBitmap,
                        transition: Transition<in PaletteBitmap?>?
                    ) {
                        animateColors(resource.backgroundColor, resource.foregroundColor)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        animateColors(fallbackBackgroundColor, fallbackForegroundColor)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    private fun animateColors(backgroundColor: Int, foregroundColor: Int) {
        if (nowPlayingColorChangingAnimationStyle == 1) {
            animateBackgroundColorChange(
                previousBackgroundColor,
                backgroundColor,
                binding.colorView1
            )
        } else {
            animateBackgroundColorChangeWithCircularReveal(
                previousBackgroundColor,
                backgroundColor,
                binding.playPause,
                binding.colorView1,
                binding.colorView2
            )
        }

        binding.playPause.rippleColor = backgroundColor
        binding.shuffle.setColorFilter(foregroundColor)
        binding.playPrevious.setColorFilter(foregroundColor)
        binding.playNext.setColorFilter(foregroundColor)
        binding.repeat.setColorFilter(foregroundColor)

        binding.tabLayout.tabRippleColor = ColorStateList.valueOf(backgroundColor)

        nowPlayingBottomDetailsAdapter.changeColor(backgroundColor)

        previousBackgroundColor = backgroundColor
    }

    override fun onDestroy() {
        mainActivity = null
        super.onDestroy()
    }

    companion object {
        fun newInstance(): NowPlayingFragment {
            return NowPlayingFragment()
        }
    }
}