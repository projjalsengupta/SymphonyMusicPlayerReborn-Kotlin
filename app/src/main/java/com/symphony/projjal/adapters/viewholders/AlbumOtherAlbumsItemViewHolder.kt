package com.symphony.projjal.adapters.viewholders

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Album
import com.symphony.mediastorequery.model.Artist
import com.symphony.projjal.adapters.HorizontalAlbumsAdapter
import com.symphony.projjal.customviews.SymphonyImageView
import com.symphony.projjal.databinding.RecyclerviewItemHorizontalRecyclerviewAlbumItemBinding

class AlbumOtherAlbumsItemViewHolder(
    val binding: RecyclerviewItemHorizontalRecyclerviewAlbumItemBinding,
    private val clickListener: (Album, SymphonyImageView) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(album: Album, artist: Artist) = with(itemView) {
        val albums = artist.albums
        albums.removeAll { it.id == album.id }
        binding.recyclerView.adapter =
            HorizontalAlbumsAdapter(items = albums, clickListener = { it1, it2 ->
                clickListener(it1, it2)
            })
        binding.recyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
    }
}