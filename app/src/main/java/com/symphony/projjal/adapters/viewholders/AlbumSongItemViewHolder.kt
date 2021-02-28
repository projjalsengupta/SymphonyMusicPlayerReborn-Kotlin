package com.symphony.projjal.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.databinding.RecyclerviewItemAlbumSongBinding

class AlbumSongItemViewHolder(val binding: RecyclerviewItemAlbumSongBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(song: Song, color: Int) = with(itemView) {
        binding.trackNumber.text = "${if (song.track != 0) song.track % 1000 else "-"}"
        binding.trackNumber.setTextColor(color)
        binding.text1.text = song.title
        binding.text1.setTextColor(color)
        binding.text2.text = song.durationText
        binding.text2.setTextColor(color)
        binding.menu.setColorFilter(color)
    }
}