package com.symphony.projjal.adapters.diffutilcallback

import androidx.recyclerview.widget.DiffUtil
import com.symphony.mediastorequery.model.Artist

class ArtistDiffUtilCallback : DiffUtil.ItemCallback<Artist>() {
    override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem == newItem
    }
}