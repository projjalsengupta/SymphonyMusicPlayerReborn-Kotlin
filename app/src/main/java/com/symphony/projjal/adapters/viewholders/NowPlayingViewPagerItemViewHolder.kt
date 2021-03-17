package com.symphony.projjal.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.GlideApp
import com.symphony.projjal.databinding.NowPlayingImageViewBinding

class NowPlayingViewPagerItemViewHolder(val binding: NowPlayingImageViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(song: Song?) = with(itemView) {
        if (song == null) {
            return@with
        }
        GlideApp.with(itemView)
            .load(song)
            .override(binding.nowPlayingImage.width, binding.nowPlayingImage.height)
            .into(binding.nowPlayingImage)
        binding.nowPlayingImage.rectangle()
    }
}