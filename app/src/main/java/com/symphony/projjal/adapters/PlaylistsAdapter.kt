package com.symphony.projjal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Playlist
import com.symphony.projjal.R
import com.symphony.projjal.databinding.RecyclerviewItemLibrarySmallBinding
import com.symphony.projjal.utils.ConversionUtils.dpToPx
import me.zhanghai.android.fastscroll.PopupTextProvider

class PlaylistsAdapter(private val context: Context?, private var items: MutableList<Playlist>) :
    RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>(), PopupTextProvider {

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val binding: RecyclerviewItemLibrarySmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            val padding = dpToPx(8)
            binding.image.updatePadding(padding, padding, padding, padding)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = items[position]
        holder.binding.text1.text = playlist.name
        val songCountText = "${playlist.songCount} Songs"
        holder.binding.text2.text = songCountText
        holder.binding.image.setImageResource(R.drawable.ic_playlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewItemLibrarySmallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getPopupText(position: Int): String {
        return if (items[position].name.isNotEmpty()) items[position].name.substring(0, 1) else ""
    }
}