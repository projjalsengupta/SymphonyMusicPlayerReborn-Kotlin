package com.symphony.projjal.adapters.diffutilcallback

import androidx.recyclerview.widget.DiffUtil
import com.symphony.mediastorequery.model.Song

class SongDiffUtilCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}