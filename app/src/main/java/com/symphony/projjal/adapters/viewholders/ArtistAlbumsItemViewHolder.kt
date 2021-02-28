package com.symphony.projjal.adapters.viewholders

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Album
import com.symphony.mediastorequery.model.Artist
import com.symphony.projjal.adapters.HorizontalAlbumsAdapter
import com.symphony.projjal.databinding.RecyclerviewItemHorizontalRecyclerviewAlbumItemBinding

class ArtistAlbumsItemViewHolder(
    val binding: RecyclerviewItemHorizontalRecyclerviewAlbumItemBinding,
    private val clickListener: (Album) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(artist: Artist) = with(itemView) {
        val albums = artist.albums
        binding.recyclerView.adapter =
            HorizontalAlbumsAdapter(items = albums, clickListener = { clickListener(it) })
        binding.recyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
    }
}