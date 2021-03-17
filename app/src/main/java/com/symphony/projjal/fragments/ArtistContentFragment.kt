package com.symphony.projjal.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.symphony.mediastorequery.model.Artist
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.activities.MainActivity
import com.symphony.projjal.adapters.ArtistContentAdapter
import com.symphony.projjal.databinding.FragmentArtistContentBinding
import com.symphony.projjal.glide.BlurTransformation
import com.symphony.projjal.glide.palette.PaletteBitmap
import com.symphony.projjal.utils.ViewUtils

class ArtistContentFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentArtistContentBinding? = null
    private val binding get() = _binding!!

    private var artist: Artist? = null

    private var animated: Boolean = false

    private var artistContentAdapter: ArtistContentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistContentBinding.inflate(inflater, container, false)
        setUpToolbar()
        setUpOnClickListeners()
        setUpFragmentManagerBackstackListener()
        setUpTransition()
        setUpPadding()
        return binding.root
    }


    private fun setUpPadding() {
        ViewUtils.topFitsSystemWindows(
            view = binding.appBarLayout,
            context = context,
            orientation = resources.configuration.orientation
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        load()
    }

    private fun setUpTransition() {
        val transition =
            TransitionInflater.from(context).inflateTransition(R.transition.image_transition)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    private fun setUpFragmentManagerBackstackListener() {
        activity?.supportFragmentManager?.addOnBackStackChangedListener {
            if (activity?.supportFragmentManager?.backStackEntryCount ?: 0 > 0 && (backgroundColor != 0 || foregroundColor != 0)) {
                if (!isHidden) {
                    setSmallControllerAndNavigationBarColors(backgroundColor, foregroundColor)
                }
            }
        }
    }

    private fun setUpOnClickListeners() {
        binding.play.setOnClickListener(this)
        binding.shuffleAll.setOnClickListener(this)
    }

    private fun setUpToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun animateRecyclerView() {
        if (!animated) {
            val animation = AnimationUtils.loadLayoutAnimation(
                activity, R.anim.layout_animation_fall_down
            )
            binding.recyclerView.layoutAnimation = animation
            animated = true
        }
    }

    private fun setRecyclerViewAdapter(textColor: Int) {
        val activity = activity
        if (activity != null) {
            if (artist != null) {
                artistContentAdapter = ArtistContentAdapter(
                    activity as AppCompatActivity,
                    artist!!,
                    clickListener = { songs: MutableList<Song>, position: Int ->
                        musicService?.playList(songs, position)
                    },
                    textColor = textColor
                )
                binding.recyclerView.adapter = artistContentAdapter
            }
        }
    }

    private fun setArtistDetails() {
        binding.artistName.text = artist?.name
        val albumDetailsText =
            "${artist?.songCount} Songs - ${artist?.albumCount} Albums - ${artist?.durationText}"
        binding.artistDetails.text = albumDetailsText
        binding.artistName.isSelected = true
        binding.artistDetails.isSelected = true
    }

    private fun load() {
        if (artist == null) {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.image.transitionName = "artist${artist?.id}"
        setArtistDetails()
        GlideApp.with(binding.appBarLayout.context)
            .load(artist)
            .override(50, 50)
            .transform(BlurTransformation(binding.appBarLayout.context))
            .into(object : CustomTarget<Drawable?>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    binding.appBarLayout.background = resource
                }
            })

        GlideApp.with(binding.image.context)
            .`as`(PaletteBitmap::class.java)
            .load(artist)
            .override(binding.image.width, binding.image.height)
            .into(object : ImageViewTarget<PaletteBitmap?>(binding.image) {
                override fun setResource(resource: PaletteBitmap?) {
                    resource?.let {
                        showLayout(it.backgroundColor, it.foregroundColor)
                        if (!resource.bitmap.isRecycled) {
                            binding.image.setImageBitmap(it.bitmap)
                        }
                    }
                }
            })
    }

    private var backgroundColor: Int = 0
    private var foregroundColor: Int = 0

    private fun showLayout(backgroundColor: Int, foregroundColor: Int) {
        binding.backgroundView.setBackgroundColor(backgroundColor)
        binding.collapsingToolbarLayout.setContentScrimColor(backgroundColor)
        binding.collapsingToolbarLayout.setStatusBarScrimColor(backgroundColor)

        setToolbarIconsColor(foregroundColor)
        binding.artistName.setTextColor(foregroundColor)
        binding.artistDetails.setTextColor(foregroundColor)
        binding.gradientBackgroundView.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.TRANSPARENT, backgroundColor)
        )

        binding.play.setTextColor(foregroundColor)
        binding.play.rippleColor = ColorStateList.valueOf(foregroundColor)
        binding.addToQueue.setTextColor(foregroundColor)
        binding.addToQueue.rippleColor = ColorStateList.valueOf(foregroundColor)
        binding.shuffleAll.imageTintList = ColorStateList.valueOf(backgroundColor)
        binding.shuffleAll.backgroundTintList = ColorStateList.valueOf(foregroundColor)

        setRecyclerViewAdapter(foregroundColor)
        animateRecyclerView()

        setSmallControllerAndNavigationBarColors(backgroundColor, foregroundColor)

        this.backgroundColor = backgroundColor
        this.foregroundColor = foregroundColor

        startPostponedEnterTransition()
    }

    private fun setSmallControllerAndNavigationBarColors(
        backgroundColor: Int,
        foregroundColor: Int
    ) {
        (activity as MainActivity?)?.setSmallControllerAndNavigationBarColors(
            backgroundColor,
            foregroundColor
        )
    }

    private fun setToolbarIconsColor(color: Int) {
        binding.toolbar.navigationIcon?.setTint(color)
        val menu = binding.toolbar.menu
        if (menu != null) {
            for (i: MenuItem in menu.children) {
                i.icon.setTint(color)
            }
        }
    }

    companion object {
        fun newInstance(artist: Artist): ArtistContentFragment {
            val artistContentFragment = ArtistContentFragment()
            artistContentFragment.artist = artist
            return artistContentFragment
        }
    }

    override fun onClick(v: View?) {
        val artist = artist ?: return
        when (v?.id) {
            binding.play.id -> musicService?.playList(artist.songs)
            binding.shuffleAll.id -> musicService?.shuffleList(artist.songs)
        }
    }
}