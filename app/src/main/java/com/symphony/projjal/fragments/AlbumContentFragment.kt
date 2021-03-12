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
import com.symphony.mediastorequery.MediaStoreQuery
import com.symphony.mediastorequery.model.Album
import com.symphony.mediastorequery.model.Artist
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.R
import com.symphony.projjal.SymphonyGlideExtension.albumPlaceholder
import com.symphony.projjal.adapters.AlbumContentAdapter
import com.symphony.projjal.databinding.FragmentAlbumContentBinding
import com.symphony.projjal.glide.palette.PaletteBitmap
import com.symphony.projjal.utils.ViewUtils.topFitsSystemWindows
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumContentFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentAlbumContentBinding? = null
    private val binding get() = _binding!!

    private var album: Album? = null
    private var artist: Artist? = null

    private var animated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumContentBinding.inflate(inflater, container, false)
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
            if (album != null && artist != null) {
                val adapter = AlbumContentAdapter(
                    activity as AppCompatActivity,
                    album!!,
                    artist!!,
                    clickListener = { songs: MutableList<Song>, position: Int ->
                        musicService?.playList(songs, position)
                    },
                    textColor = textColor
                )
                binding.recyclerView.adapter = adapter
            }
        }
    }

    private fun setAlbumDetails() {
        binding.albumName.text = album?.title
        binding.artistName.text = album?.artist
        val albumDetailsText = "${album?.songCount} Songs - ${album?.durationText}"
        binding.albumDetails.text = albumDetailsText
        binding.albumName.isSelected = true
        binding.artistName.isSelected = true
        binding.albumDetails.isSelected = true
    }

    private fun load() {
        if (album == null) {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.root.visibility = View.INVISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val singleArtistList =
                album?.artistId?.let { MediaStoreQuery(context).getArtistById(it) }
            if (singleArtistList != null && singleArtistList.size > 0) {
                artist = singleArtistList[0]
            }
            withContext(Dispatchers.Main) {
                setAlbumDetails()
                GlideApp.with(binding.image.context)
                    .`as`(PaletteBitmap::class.java)
                    .load(album?.albumArtUri)
                    .override(binding.image.width, binding.image.height)
                    .albumPlaceholder(binding.image.context)
                    .into(object : ImageViewTarget<PaletteBitmap?>(binding.image) {
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
                                    binding.image.setImageBitmap(it.bitmap)
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun showLayout(backgroundColor: Int, foregroundColor: Int) {
        binding.backgroundView.setBackgroundColor(backgroundColor)
        binding.collapsingToolbarLayout.setContentScrimColor(backgroundColor)
        binding.collapsingToolbarLayout.setStatusBarScrimColor(backgroundColor)

        setToolbarIconsColor(foregroundColor)
        binding.albumName.setTextColor(foregroundColor)
        binding.artistName.setTextColor(foregroundColor)
        binding.albumDetails.setTextColor(foregroundColor)
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
        fun newInstance(album: Album): AlbumContentFragment {
            val albumContentFragment = AlbumContentFragment()
            albumContentFragment.album = album
            return albumContentFragment
        }
    }

    override fun onClick(v: View?) {
        val album = album ?: return
        when (v?.id) {
            binding.play.id -> musicService?.playList(album.songs)
            binding.shuffleAll.id -> musicService?.shuffleList(album.songs)
        }
    }
}