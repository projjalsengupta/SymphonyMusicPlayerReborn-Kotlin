package com.symphony.projjal.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.symphony.projjal.databinding.RecyclerviewItemHeadingBinding

class HeadingItemViewHolder(val binding: RecyclerviewItemHeadingBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(heading: String, color: Int) = with(itemView) {
        binding.heading.text = heading
        binding.heading.setTextColor(color)
    }
}