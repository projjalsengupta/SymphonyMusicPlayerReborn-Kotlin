package com.symphony.projjal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.symphony.mediastorequery.model.Album
import com.symphony.projjal.R
import com.symphony.projjal.adapters.viewholders.LibraryHeaderItemViewHolder
import com.symphony.projjal.adapters.viewholders.LibraryItemViewHolder
import com.symphony.projjal.databinding.RecyclerviewItemLibraryHeaderBinding
import com.symphony.projjal.databinding.RecyclerviewItemLibraryLargeBinding
import com.symphony.projjal.databinding.RecyclerviewItemLibrarySmallBinding
import com.symphony.projjal.utils.PreferenceUtils.albumGridSize
import com.symphony.projjal.utils.PreferenceUtils.albumImageStyle
import com.symphony.projjal.utils.PreferenceUtils.albumLayoutStyle
import me.zhanghai.android.fastscroll.PopupTextProvider

class AlbumsAdapter(
    private val context: Context?,
    private var items: MutableList<Album>,
    private val clickListener: (Album) -> Unit,
    private val selectionChanged: (MutableList<Album>) -> Unit,
    private val styleClicked: (View) -> Unit,
    private val sortClicked: (View) -> Unit,
    private val gridClicked: (View) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), PopupTextProvider {

    val selectedItems: MutableList<Album> = mutableListOf()

    object Constants {
        const val TYPE_HEADER = 1
        const val TYPE_ALBUM = 2
    }

    fun clearSelection() {
        val tempSelectedItems = selectedItems.toMutableList()
        selectedItems.clear()
        for (album in tempSelectedItems) {
            notifyItemChanged(items.indexOf(album) + 1)
        }
    }

    override fun getItemCount(): Int {
        return 1 + items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) Constants.TYPE_HEADER else Constants.TYPE_ALBUM
    }

    override fun getItemId(position: Int): Long = position.toLong()

    private fun addOrRemoveItemFromSelection(album: Album) {
        if (selectedItems.contains(album)) {
            selectedItems.remove(album)
        } else {
            selectedItems.add(album)
        }
        notifyItemChanged(items.indexOf(album) + 1)
        selectionChanged(selectedItems)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LibraryItemViewHolder -> {
                val album = items[position - 1]
                holder.key = album.id.toString()
                holder.bind(
                    primaryText = album.title,
                    secondaryText = album.artist,
                    item = album.albumArtUri,
                    gridSize = albumGridSize,
                    layoutStyle = albumLayoutStyle,
                    imageStyle = albumImageStyle,
                    selected = selectedItems.contains(album)
                )
                holder.clickView.setOnClickListener {
                    if (selectedItems.size > 0) {
                        addOrRemoveItemFromSelection(album)
                    } else {
                        clickListener(album)
                    }
                }
                holder.clickView.setOnLongClickListener {
                    addOrRemoveItemFromSelection(album)
                    true
                }
            }
            is LibraryHeaderItemViewHolder -> {
                holder.bind(
                    "${items.size} ${context?.getString(R.string.albums)}",
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
                if (albumGridSize == 1) {
                    val binding = RecyclerviewItemLibrarySmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return LibraryItemViewHolder(
                        root = binding.root,
                        containerView = binding.containerView,
                        text1 = binding.text1,
                        text2 = binding.text2,
                        image = binding.image,
                        clickView = binding.clickView,
                        type = LibraryItemViewHolder.Constants.TYPE_ALBUM,
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
                        containerView = binding.containerView,
                        text1 = binding.text1,
                        text2 = binding.text2,
                        image = binding.image,
                        clickView = binding.clickView,
                        type = LibraryItemViewHolder.Constants.TYPE_ALBUM,
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
            items[position - 1].title.isNotEmpty() -> items[position - 1].title.substring(
                0,
                1
            )
            else -> ""
        }
    }
}