package com.symphony.projjal.adapters

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.adapters.viewholders.NowPlayingQueueItemViewHolder
import com.symphony.projjal.databinding.RecyclerviewItemNowPlayingQueueBinding

class NowPlayingQueueAdapter(
    private val queue: MutableList<Song>,
    private val clickListener: (Int) -> Unit,
    private val menuClicked: (View, Song?) -> Unit
) :
    RecyclerView.Adapter<NowPlayingQueueItemViewHolder>() {

    private var positionInPlayingOrder = 0

    private val viewHolderMap: MutableMap<NowPlayingQueueItemViewHolder, Int> = mutableMapOf()

    override fun getItemCount(): Int {
        return queue.size
    }

    fun setQueue(queue: MutableList<Song>) {
        this.queue.clear()
        this.queue.addAll(queue)
        this.viewHolderMap.clear()
        notifyDataSetChanged()
    }

    fun setPositionInPlayingOrder(
        positionInPlayingOrder: Int
    ) {
        this.positionInPlayingOrder = positionInPlayingOrder
        for ((key, value) in viewHolderMap) {
            key.bind(queue[value], value, positionInPlayingOrder)
        }
    }

    override fun onBindViewHolder(holder: NowPlayingQueueItemViewHolder, position: Int) {
        val song = queue[position]
        holder.bind(song, position, positionInPlayingOrder)
        holder.binding.clickView.setOnClickListener {
            clickListener(position)
        }
        holder.binding.menu.setOnClickListener {
            menuClicked(it, song)
        }
        viewHolderMap[holder] = position
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NowPlayingQueueItemViewHolder {
        val binding = RecyclerviewItemNowPlayingQueueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NowPlayingQueueItemViewHolder(binding)
    }
}