package com.symphony.projjal.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.bumptech.glide.request.target.ImageViewTarget
import com.symphony.mediastorequery.model.Artist
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.SymphonyGlideExtension.artistPlaceholder
import com.symphony.projjal.SymphonyGlideExtension.large
import com.symphony.projjal.adapters.ArtistContentAdapter
import com.symphony.projjal.databinding.FragmentArtistContentBinding
import com.symphony.projjal.glide.palette.PaletteBitmap
import com.symphony.projjal.utils.ViewUtils.fitsSystemWindows
import com.symphony.projjal.utils.ViewUtils.topFitsSystemWindows

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
        load()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUpPadding()
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

    private fun setUpPadding() {
        topFitsSystemWindows(
            view = binding.backgroundView,
            context = activity,
            orientation = resources.configuration.orientation
        )
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

    private fun setAlbumDetails() {
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
        binding.root.visibility = View.INVISIBLE
        setAlbumDetails()
        GlideApp.with(binding.image.context)
            .`as`(PaletteBitmap::class.java)
            .load(artist)
            .large()
            .artistPlaceholder(binding.image.context)
            .into(object : ImageViewTarget<PaletteBitmap?>(binding.image.image) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    val activity = activity
                    if (activity != null) {
                        showLayout(
                            ContextCompat.getColor(activity, R.color.grey_400),
                            ContextCompat.getColor(activity, R.color.black)
                        )
                    }
                }

                override fun setResource(resource: PaletteBitmap?) {
                    resource?.let {
                        showLayout(it.backgroundColor, it.foregroundColor)
                        if (!resource.bitmap.isRecycled) {
                            binding.image.image.setImageBitmap(it.bitmap)
                        }
                    }
                }
            })
    }

    private fun showLayout(backgroundColor: Int, foregroundColor: Int) {
        binding.backgroundView.setBackgroundColor(backgroundColor)
        binding.collapsingToolbarLayout.setContentScrimColor(backgroundColor)
        binding.collapsingToolbarLayout.setStatusBarScrimColor(backgroundColor)

        setToolbarIconsColor(foregroundColor)
        binding.artistName.setTextColor(foregroundColor)
        binding.artistDetails.setTextColor(foregroundColor)
        binding.gradientView.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.TRANSPARENT, backgroundColor)
        )

        binding.play.background.setTint(foregroundColor)
        binding.play.setTextColor(backgroundColor)
        binding.play.icon.setTint(backgroundColor)
        binding.play.rippleColor = ColorStateList.valueOf(backgroundColor)
        binding.shuffleAll.strokeColor = ColorStateList.valueOf(foregroundColor)
        binding.shuffleAll.setTextColor(foregroundColor)
        binding.shuffleAll.icon.setTint(foregroundColor)
        binding.shuffleAll.rippleColor = ColorStateList.valueOf(foregroundColor)

        setRecyclerViewAdapter(foregroundColor)
        animateRecyclerView()
        binding.root.visibility = View.VISIBLE
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