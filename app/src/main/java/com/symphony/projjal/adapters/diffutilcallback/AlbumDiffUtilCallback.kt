package com.symphony.projjal.adapters.diffutilcallback

import androidx.recyclerview.widget.DiffUtil
import com.symphony.mediastorequery.model.Album

class AlbumDiffUtilCallback : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem
    }
}