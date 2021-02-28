package com.symphony.projjal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.symphony.mediastorequery.model.Artist
import com.symphony.projjal.R
import com.symphony.projjal.adapters.viewholders.LibraryHeaderItemViewHolder
import com.symphony.projjal.adapters.viewholders.LibraryItemViewHolder
import com.symphony.projjal.databinding.RecyclerviewItemLibraryHeaderBinding
import com.symphony.projjal.databinding.RecyclerviewItemLibraryLargeBinding
import com.symphony.projjal.databinding.RecyclerviewItemLibrarySmallBinding
import com.symphony.projjal.utils.PreferenceUtils.artistGridSize
import com.symphony.projjal.utils.PreferenceUtils.artistImageStyle
import com.symphony.projjal.utils.PreferenceUtils.artistLayoutStyle
import me.zhanghai.android.fastscroll.PopupTextProvider

class ArtistsAdapter(
    private val context: Context?,
    private var items: MutableList<Artist>,
    private val clickListener: (Artist) -> Unit,
    private val selectionChanged: (MutableList<Artist>) -> Unit,
    private val styleClicked: (View) -> Unit,
    private val sortClicked: (View) -> Unit,
    private val gridClicked: (View) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), PopupTextProvider {

    val selectedItems: MutableList<Artist> = mutableListOf()

    object Constants {
        const val TYPE_HEADER = 1
        const val TYPE_ARTIST = 2
    }

    fun clearSelection() {
        val tempSelectedItems = selectedItems.toMutableList()
        selectedItems.clear()
        for (artist in tempSelectedItems) {
            notifyItemChanged(items.indexOf(artist) + 1)
        }
    }

    override fun getItemCount(): Int {
        return 1 + items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) Constants.TYPE_HEADER else Constants.TYPE_ARTIST
    }

    override fun getItemId(position: Int): Long = position.toLong()

    private fun addOrRemoveItemFromSelection(artist: Artist) {
        if (selectedItems.contains(artist)) {
            selectedItems.remove(artist)
        } else {
            selectedItems.add(artist)
        }
        notifyItemChanged(items.indexOf(artist) + 1)
        selectionChanged(selectedItems)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LibraryItemViewHolder -> {
                val artist = items[position - 1]
                holder.key = artist.id.toString()
                holder.bind(
                    primaryText = artist.name,
                    secondaryText = "${artist.albumCount} ${context?.getString(R.string.albums)} - ${artist.songCount} ${
                        context?.getString(
                            R.string.songs
                        )
                    }",
                    item = artist,
                    gridSize = artistGridSize,
                    layoutStyle = artistLayoutStyle,
                    imageStyle = artistImageStyle,
                    selected = selectedItems.contains(artist)
                )
                holder.clickView.setOnClickListener {
                    if (selectedItems.size > 0) {
                        addOrRemoveItemFromSelection(artist)
                    } else {
                        clickListener(artist)
                    }
                }
                holder.clickView.setOnLongClickListener {
                    addOrRemoveItemFromSelection(artist)
                    true
                }
            }
            is LibraryHeaderItemViewHolder -> {
                holder.bind(
                    "${items.size} ${context?.getString(R.string.artists)}",
                    styleClicked = {
                        styleClicked(it)
                    },
                    gridClicked = {
                        gridClicked(it)
                    },
                    sortClicked = {
                        sortClicked(it)
                    },
                    shuffleAllVisible = false
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            Constants.TYPE_HEADER -> {
                val binding = RecyclerviewItemLibraryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return LibraryHeaderItemViewHolder(binding)
            }
            else -> {
                if (artistGridSize == 1) {
                    val binding = RecyclerviewItemLibrarySmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return LibraryItemViewHolder(
                        root = binding.root,
                        selectionBackground = binding.selectionBackground,
                        text1 = binding.text1,
                        text2 = binding.text2,
                        image = binding.image,
                        cardView = binding.cardView,
                        clickView = binding.clickView,
                        type = LibraryItemViewHolder.Constants.TYPE_ARTIST,
                        menu = binding.menu
                    )
                } else {
                    val binding = RecyclerviewItemLibraryLargeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return LibraryItemViewHolder(
                        root = binding.root,
                        selectionBackground = binding.selectionBackground,
                        text1 = binding.text1,
                        text2 = binding.text2,
                        image = binding.image,
                        cardView = binding.cardView,
                        clickView = binding.clickView,
                        type = LibraryItemViewHolder.Constants.TYPE_ARTIST,
                        menu = binding.menu
                    )
                }
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is LibraryItemViewHolder -> {
                holder.target?.let {
                    context?.let { it1 -> Glide.with(it1).clear(it) }
                }
            }
        }
        super.onViewRecycled(holder)
    }

    override fun getPopupText(position: Int): String {
        return when {
            position == 0 -> "#"
            items[position - 1].name.isNotEmpty() -> items[position - 1].name.substring(
                0,
                1
            )
            else -> ""
        }
    }
}