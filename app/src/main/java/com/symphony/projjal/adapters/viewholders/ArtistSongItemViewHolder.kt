package com.symphony.projjal.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.databinding.RecyclerviewItemArtistSongBinding

class ArtistSongItemViewHolder(val binding: RecyclerviewItemArtistSongBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(song: Song, color: Int) = with(itemView) {
        binding.text1.text = song.title
        binding.text1.setTextColor(color)
        binding.text2.text = song.durationText
        binding.text2.setTextColor(color)
        binding.menu.setColorFilter(color)
    }
}