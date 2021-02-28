package com.symphony.projjal.adapters.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.symphony.projjal.databinding.RecyclerviewItemLibraryHeaderBinding

class LibraryHeaderItemViewHolder(val binding: RecyclerviewItemLibraryHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        countString: String,
        shuffleAllClicked: (View) -> Unit = {},
        styleClicked: (View) -> Unit,
        sortClicked: (View) -> Unit,
        gridClicked: (View) -> Unit,
        shuffleAllVisible: Boolean = true
    ) = with(itemView) {
        binding.text.text = countString
        binding.shuffleAll.visibility = if (shuffleAllVisible) View.VISIBLE else View.GONE
        binding.style.setOnClickListener { styleClicked(it) }
        binding.grid.setOnClickListener { gridClicked(it) }
        binding.sort.setOnClickListener { sortClicked(it) }
        binding.shuffleAll.setOnClickListener { shuffleAllClicked(it) }
    }
}