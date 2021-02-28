package com.symphony.projjal.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.databinding.RecyclerviewItemNowPlayingQueueBinding

class NowPlayingQueueItemViewHolder(val binding: RecyclerviewItemNowPlayingQueueBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(song: Song?, position: Int, positionInPlayingOrder: Int) = with(itemView) {
        binding.position.text = "${position - positionInPlayingOrder}"
        binding.text1.text = song?.title
        binding.text2.text = song?.durationText
        if (position <= positionInPlayingOrder) {
            binding.clickView.alpha = 0.5f
        } else {
            binding.clickView.alpha = 1f
        }
    }
}