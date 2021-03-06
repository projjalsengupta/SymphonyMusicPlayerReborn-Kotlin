package com.symphony.projjal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.symphony.mediastorequery.model.Album
import com.symphony.projjal.adapters.viewholders.LibraryItemViewHolder
import com.symphony.projjal.customviews.SymphonyImageView
import com.symphony.projjal.databinding.RecyclerviewItemHorizontalRecyclerviewBinding
import com.symphony.projjal.utils.PreferenceUtils.albumGridSize
import com.symphony.projjal.utils.PreferenceUtils.albumImageStyle
import com.symphony.projjal.utils.PreferenceUtils.albumLayoutStyle

class HorizontalAlbumsAdapter(
    private var items: MutableList<Album>,
    private val clickListener: (Album, SymphonyImageView) -> Unit
) :
    RecyclerView.Adapter<LibraryItemViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LibraryItemViewHolder, position: Int) {
        val album = items[position]
        holder.key = album.id.toString()
        holder.bind(
            primaryText = album.title,
            secondaryText = if (album.year.toString() == "0") "-" else album.year.toString(),
            item = album,
            gridSize = albumGridSize,
            layoutStyle = albumLayoutStyle,
            imageStyle = albumImageStyle,
            selected = false
        )
        holder.clickView.setOnClickListener { clickListener(album, holder.image) }
        holder.image.transitionName = "album${album.id}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryItemViewHolder {
        val binding = RecyclerviewItemHorizontalRecyclerviewBinding.inflate(
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

    override fun onViewRecycled(holder: LibraryItemViewHolder) {
        holder.target?.let {
            holder.itemView.context?.let { it1 -> Glide.with(it1).clear(it) }
        }
        super.onViewRecycled(holder)
    }
}