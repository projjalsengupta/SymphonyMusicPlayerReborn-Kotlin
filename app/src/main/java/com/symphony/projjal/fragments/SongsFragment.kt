package com.symphony.projjal.fragments

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.symphony.mediastorequery.Constants.SORT_SONGS_BY_ALBUM_TITLE
import com.symphony.mediastorequery.Constants.SORT_SONGS_BY_ARTIST
import com.symphony.mediastorequery.Constants.SORT_SONGS_BY_TITLE
import com.symphony.mediastorequery.Constants.SORT_SONGS_BY_YEAR
import com.symphony.mediastorequery.MediaStoreQuery
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.R
import com.symphony.projjal.adapters.SongsAdapter
import com.symphony.projjal.fragments.LibraryItemFragment.Constants.FRAGMENT_SONGS
import com.symphony.projjal.utils.PreferenceUtils.songGridSize
import com.symphony.projjal.utils.PreferenceUtils.songSortBy
import com.symphony.projjal.utils.PreferenceUtils.songSortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongsFragment : LibraryItemFragment() {
    private var adapter: SongsAdapter? = null

    private fun setRecyclerViewAdapter(songs: MutableList<Song>) {
        adapter = SongsAdapter(
            context = activity,
            items = songs,
            selectionChanged = {
                invalidateCab(it.size, {
                    adapter?.clearSelection()
                }, {
                    val adapter = adapter
                    if (adapter != null) {
                        when (it) {
                            R.id.play -> musicService?.playList(adapter.selectedItems)
                        }
                    }
                    true
                })
            },
            styleClicked =
            {
                onStyleClicked(
                    it,
                    FRAGMENT_SONGS
                ) { adapter?.notifyDataSetChanged() }
            },
            clickListener =
            { items: MutableList<Song>, position: Int ->
                musicService?.playList(items, position)
            },
            sortClicked =
            {
                onSortClicked(it, FRAGMENT_SONGS) {
                    load()
                }
            },
            gridClicked =
            { onGridClicked(it, FRAGMENT_SONGS) },
            shuffleAllClicked =
            {
                musicService?.shuffleList(it)
            }
        )
        val layoutManager = GridLayoutManager(context, songGridSize)
        layoutManager.spanSizeLookup =
            object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) {
                        songGridSize
                    } else {
                        1
                    }
                }
            }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    override fun load() {
        CoroutineScope(Dispatchers.IO).launch {
            var sortBy = SORT_SONGS_BY_TITLE
            when (songSortBy) {
                1 -> sortBy = SORT_SONGS_BY_TITLE
                2 -> sortBy = SORT_SONGS_BY_ALBUM_TITLE
                3 -> sortBy = SORT_SONGS_BY_ARTIST
                4 -> sortBy = SORT_SONGS_BY_YEAR
            }
            val mediaStoreQuery = MediaStoreQuery(context).desc(songSortOrder == 2)
            val songs = mediaStoreQuery.getSongs(sortBy = sortBy)
            withContext(Main) {
                setRecyclerViewAdapter(songs)
                super.load()
            }
        }
    }

    override fun onDestroyView() {
        invalidateCab(0, {}, { true })
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): SongsFragment {
            return SongsFragment()
        }
    }
}