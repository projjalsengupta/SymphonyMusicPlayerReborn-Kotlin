package com.symphony.projjal.fragments

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.core.view.get
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.attached.isActive
import com.afollestad.materialcab.createCab
import com.symphony.projjal.*
import com.symphony.projjal.databinding.FragmentLibraryItemBinding
import com.symphony.projjal.fragments.LibraryItemFragment.Constants.FRAGMENT_ALBUMS
import com.symphony.projjal.fragments.LibraryItemFragment.Constants.FRAGMENT_ARTISTS
import com.symphony.projjal.fragments.LibraryItemFragment.Constants.FRAGMENT_SONGS
import com.symphony.projjal.singletons.Cab
import com.symphony.projjal.utils.PreferenceUtils.albumGridSize
import com.symphony.projjal.utils.PreferenceUtils.albumImageStyle
import com.symphony.projjal.utils.PreferenceUtils.albumLayoutStyle
import com.symphony.projjal.utils.PreferenceUtils.albumSortBy
import com.symphony.projjal.utils.PreferenceUtils.albumSortOrder
import com.symphony.projjal.utils.PreferenceUtils.artistGridSize
import com.symphony.projjal.utils.PreferenceUtils.artistImageStyle
import com.symphony.projjal.utils.PreferenceUtils.artistLayoutStyle
import com.symphony.projjal.utils.PreferenceUtils.artistSortBy
import com.symphony.projjal.utils.PreferenceUtils.artistSortOrder
import com.symphony.projjal.utils.PreferenceUtils.songGridSize
import com.symphony.projjal.utils.PreferenceUtils.songImageStyle
import com.symphony.projjal.utils.PreferenceUtils.songLayoutStyle
import com.symphony.projjal.utils.PreferenceUtils.songSortBy
import com.symphony.projjal.utils.PreferenceUtils.songSortOrder
import com.symphony.themeengine.ThemeEngine
import me.zhanghai.android.fastscroll.FastScrollerBuilder

abstract class LibraryItemFragment : BaseFragment() {
    private var _binding: FragmentLibraryItemBinding? = null
    protected val binding get() = _binding!!

    private var animated: Boolean = false

    private var fastScrollerBuilder: FastScrollerBuilder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryItemBinding.inflate(inflater, container, false)
        load()
        return binding.root
    }

    private fun animateReyclerView() {
        if (!animated) {
            val animation = AnimationUtils.loadLayoutAnimation(
                activity, R.anim.layout_animation_fall_down
            )
            binding.recyclerView.layoutAnimation = animation
            animated = true
        }
    }

    private fun setUpFastScroller() {
        fastScrollerBuilder = FastScrollerBuilder(binding.recyclerView)
        fastScrollerBuilder?.useMd2Style()?.build()

    }

    open fun load() {
        animateReyclerView()
        setUpFastScroller()
        invalidateCab(0, {}, { true })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun onCabCreated(menu: Menu): Boolean {
        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
                val field = menu.javaClass.getDeclaredField("mOptionalIconsVisible")
                field.isAccessible = true
                field.setBoolean(menu, true)
            } catch (ignored: Exception) {
            }
        }
        return true
    }

    fun invalidateCab(
        count: Int,
        selectionCleared: () -> Unit,
        menuItemSelected: (Int) -> Boolean
    ) {
        if (count == 0) {
            if (Cab.cab?.isActive() == true) {
                Cab.cab?.destroy()
            }
            return
        }
        if (Cab.cab.isActive()) {
            Cab.cab?.apply {
                title(literal = "$count selected")
            }
        } else {
            val activity = activity
            if (activity != null) {
                Cab.cab = createCab(R.id.cabStub) {
                    title(literal = "$count selected")
                    titleColor(literal = ThemeEngine(activity).textColorPrimary)
                    backgroundColor(literal = ThemeEngine(activity).backgroundColor)
                    menu(R.menu.menu_library_multiselect)
                    popupTheme(ThemeEngine(activity).theme)
                    slideDown()
                    onCreate { _, menu ->
                        run {
                            onCabCreated(menu)
                            for (item in menu.children) {
                                item.icon?.setTint(ThemeEngine(activity).textColorPrimary)
                            }
                        }
                    }
                    onSelection {
                        val result = menuItemSelected(it.itemId)
                        if (Cab.cab?.isActive() == true) {
                            Cab.cab?.destroy()
                        }
                        result
                    }
                    onDestroy {
                        selectionCleared()
                        true
                    }
                }
            }
        }
    }

    object Constants {
        const val FRAGMENT_SONGS = 1
        const val FRAGMENT_ALBUMS = 2
        const val FRAGMENT_ARTISTS = 3
        const val FRAGMENT_PLAYLISTS = 4
    }

    fun onGridClicked(view: View, type: Int) {
        context?.let { ctx ->
            val popupMenu = PopupMenu(ctx, view)
            popupMenu.inflate(R.menu.menu_grid)
            var selected = -1
            when (type) {
                FRAGMENT_SONGS -> {
                    popupMenu.menu[songGridSize - 1].isChecked = true
                    selected = songGridSize
                }
                FRAGMENT_ALBUMS -> {
                    popupMenu.menu[albumGridSize - 1].isChecked = true
                    selected = albumGridSize
                }
                FRAGMENT_ARTISTS -> {
                    popupMenu.menu[artistGridSize - 1].isChecked = true
                    selected = artistGridSize
                }
            }
            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.one -> {
                        selected = 1
                    }
                    R.id.two -> {
                        selected = 2
                    }
                    R.id.three -> {
                        selected = 3
                    }
                    R.id.four -> {
                        selected = 4
                    }
                }
                when (type) {
                    FRAGMENT_SONGS -> {
                        songGridSize = selected
                    }
                    FRAGMENT_ALBUMS -> {
                        albumGridSize = selected
                    }
                    FRAGMENT_ARTISTS -> {
                        artistGridSize = selected
                    }
                }
                load()
                true
            }
            popupMenu.show()
        }
    }

    fun onStyleClicked(view: View, type: Int, onComplete: () -> Unit) {
        context?.let { ctx ->
            val popupMenu = PopupMenu(ctx, view)
            popupMenu.inflate(R.menu.menu_style)
            var selectedImageStyle = -1
            var selectedLayoutStyle = -1
            when (type) {
                FRAGMENT_SONGS -> {
                    selectedImageStyle = songImageStyle
                    selectedLayoutStyle = songLayoutStyle
                }
                FRAGMENT_ALBUMS -> {
                    selectedImageStyle = albumImageStyle
                    selectedLayoutStyle = albumLayoutStyle
                }
                FRAGMENT_ARTISTS -> {
                    selectedImageStyle = artistImageStyle
                    selectedLayoutStyle = artistLayoutStyle
                }
            }
            popupMenu.menu[0].subMenu[selectedImageStyle - 1].isChecked = true
            popupMenu.menu[1].subMenu[selectedLayoutStyle - 1].isChecked = true
            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                when (item?.itemId) {
                    R.id.imageStyleSquare -> selectedImageStyle = IMAGE_STYLE_SQUARE
                    R.id.imageStyleCircle -> selectedImageStyle = IMAGE_STYLE_CIRCLE
                    R.id.imageStyleRoundedCorners -> selectedImageStyle =
                        IMAGE_STYLE_ROUNDED_CORNERS
                    R.id.layoutStyleCard -> selectedLayoutStyle = LAYOUT_STYLE_CARD
                    R.id.layoutStylePlain -> selectedLayoutStyle = LAYOUT_STYLE_PLAIN
                    R.id.layoutStyleCircular -> selectedLayoutStyle =
                        LAYOUT_STYLE_CIRCLE
                    R.id.layoutStyleColored -> selectedLayoutStyle =
                        LAYOUT_STYLE_COLORED
                    R.id.layoutStyleOutline -> selectedLayoutStyle =
                        LAYOUT_STYLE_OUTLINE
                }
                when (type) {
                    FRAGMENT_SONGS -> {
                        songImageStyle = selectedImageStyle
                        songLayoutStyle = selectedLayoutStyle
                    }
                    FRAGMENT_ALBUMS -> {
                        albumImageStyle = selectedImageStyle
                        albumLayoutStyle = selectedLayoutStyle
                    }
                    FRAGMENT_ARTISTS -> {
                        artistImageStyle = selectedImageStyle
                        artistLayoutStyle = selectedLayoutStyle
                    }
                }
                if (item?.itemId in arrayOf(
                        R.id.imageStyleSquare,
                        R.id.imageStyleCircle,
                        R.id.imageStyleRoundedCorners,
                        R.id.layoutStyleCard,
                        R.id.layoutStylePlain,
                        R.id.layoutStyleColored,
                        R.id.layoutStyleCircular,
                        R.id.layoutStyleOutline
                    )
                ) {
                    onComplete()
                }
                true
            }
            popupMenu.show()
        }
    }

    fun onSortClicked(view: View, type: Int, onComplete: () -> Unit) {
        context?.let { ctx ->
            val popupMenu = PopupMenu(ctx, view)
            when (type) {
                FRAGMENT_SONGS -> popupMenu.inflate(R.menu.menu_sort_songs)
                FRAGMENT_ALBUMS -> popupMenu.inflate(R.menu.menu_sort_albums)
                FRAGMENT_ARTISTS -> popupMenu.inflate(R.menu.menu_sort_artists)
            }
            var selectedSortBy = -1
            var selectedSortOrder = -1
            when (type) {
                FRAGMENT_SONGS -> {
                    selectedSortBy = songSortBy
                    selectedSortOrder = songSortOrder
                }
                FRAGMENT_ALBUMS -> {
                    selectedSortBy = albumSortBy
                    selectedSortOrder = albumSortOrder
                }
                FRAGMENT_ARTISTS -> {
                    selectedSortBy = artistSortBy
                    selectedSortOrder = artistSortOrder
                }
            }
            if (type == FRAGMENT_ARTISTS) {
                popupMenu.menu[0].subMenu[selectedSortOrder - 1].isChecked = true
            } else {
                popupMenu.menu[0].subMenu[selectedSortBy - 1].isChecked = true
                popupMenu.menu[1].subMenu[selectedSortOrder - 1].isChecked = true
            }
            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                when (item?.itemId) {
                    R.id.title -> selectedSortBy = 1
                    R.id.album -> {
                        when (type) {
                            FRAGMENT_SONGS -> selectedSortBy = 2
                            FRAGMENT_ALBUMS -> selectedSortBy = 1
                        }
                    }
                    R.id.artist -> {
                        when (type) {
                            FRAGMENT_SONGS -> selectedSortBy = 3
                            FRAGMENT_ALBUMS -> selectedSortBy = 2
                        }
                    }
                    R.id.year -> {
                        when (type) {
                            FRAGMENT_SONGS -> selectedSortBy = 4
                            FRAGMENT_ALBUMS -> selectedSortBy = 3
                        }
                    }
                    R.id.ascending -> selectedSortOrder = 1
                    R.id.descending -> selectedSortOrder = 2
                }
                when (type) {
                    FRAGMENT_SONGS -> {
                        songSortBy = selectedSortBy
                        songSortOrder = selectedSortOrder
                    }
                    FRAGMENT_ALBUMS -> {
                        albumSortBy = selectedSortBy
                        albumSortOrder = selectedSortOrder
                    }
                    FRAGMENT_ARTISTS -> {
                        artistSortBy = selectedSortBy
                        artistSortOrder = selectedSortOrder
                    }
                }
                if (item?.itemId in arrayOf(
                        R.id.title,
                        R.id.album,
                        R.id.artist,
                        R.id.year,
                        R.id.ascending,
                        R.id.descending
                    )
                ) {
                    onComplete()
                }
                true
            }
            popupMenu.show()
        }
    }
}