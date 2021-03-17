package com.symphony.projjal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.adapters.viewholders.NowPlayingViewPagerItemViewHolder
import com.symphony.projjal.databinding.NowPlayingImageViewBinding

class NowPlayingViewPagerAdapter(private val queue: MutableList<Song>) :
    RecyclerView.Adapter<NowPlayingViewPagerItemViewHolder>() {

    fun setQueue(queue: MutableList<Song>) {
        this.queue.clear()
        this.queue.addAll(queue)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NowPlayingViewPagerItemViewHolder {
        val binding = NowPlayingImageViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NowPlayingViewPagerItemViewHolder(binding = binding)
    }

    override fun getItemCount(): Int = queue.size

    override fun onBindViewHolder(holder: NowPlayingViewPagerItemViewHolder, position: Int) {
        val song = queue[position]
        holder.bind(song)
    }
}