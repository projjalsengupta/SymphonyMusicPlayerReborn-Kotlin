package com.symphony.projjal.fragments

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialcab.attached.destroy
import com.symphony.mediastorequery.Constants.SORT_ALBUMS_BY_ARTIST
import com.symphony.mediastorequery.Constants.SORT_ALBUMS_BY_TITLE
import com.symphony.mediastorequery.Constants.SORT_ALBUMS_BY_YEAR
import com.symphony.mediastorequery.MediaStoreQuery
import com.symphony.mediastorequery.model.Album
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.R
import com.symphony.projjal.adapters.AlbumsAdapter
import com.symphony.projjal.fragments.LibraryItemFragment.Constants.FRAGMENT_ALBUMS
import com.symphony.projjal.singletons.Cab
import com.symphony.projjal.utils.FragmentUtils.addFragment
import com.symphony.projjal.utils.PreferenceUtils.albumGridSize
import com.symphony.projjal.utils.PreferenceUtils.albumSortBy
import com.symphony.projjal.utils.PreferenceUtils.albumSortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumsFragment : LibraryItemFragment() {
    private var adapter: AlbumsAdapter? = null

    private fun setRecyclerViewAdapter(albums: MutableList<Album>) {
        adapter = AlbumsAdapter(
            context = activity,
            items = albums,
            clickListener = {
                addFragment(
                    activity = activity as AppCompatActivity,
                    id = R.id.fragmentContainer,
                    newFragment = AlbumContentFragment.newInstance(it),
                    fragmentName = "album ${it.id}"
                )
            },
            selectionChanged = {
                invalidateCab(it.size, {
                    adapter?.clearSelection()
                }, {
                    val adapter = adapter
                    if (adapter != null) {
                        val selectedSongs = mutableListOf<Song>()
                        adapter.selectedItems.forEach {
                            selectedSongs.addAll(it.songs)
                        }
                        when (it) {
                            R.id.play -> musicService?.playList(selectedSongs)
                        }
                    }
                    true
                })
            },
            styleClicked = {
                onStyleClicked(
                    it,
                    FRAGMENT_ALBUMS
                ) { adapter?.notifyDataSetChanged() }
            },
            sortClicked = {
                onSortClicked(it, FRAGMENT_ALBUMS) {
                    load()
                }
            },
            gridClicked = { onGridClicked(it, FRAGMENT_ALBUMS) }
        )
        val layoutManager = GridLayoutManager(context, albumGridSize)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) {
                    albumGridSize
                } else {
                    1
                }
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        invalidateCab(0, {}, { true })
        super.onDestroyView()
    }

    override fun load() {
        CoroutineScope(Dispatchers.IO).launch {
            var sortBy = SORT_ALBUMS_BY_TITLE
            when (albumSortBy) {
                1 -> sortBy = SORT_ALBUMS_BY_TITLE
                2 -> sortBy = SORT_ALBUMS_BY_ARTIST
                3 -> sortBy = SORT_ALBUMS_BY_YEAR
            }
            val mediaStoreQuery = MediaStoreQuery(context).desc(albumSortOrder == 2)
            val albums = mediaStoreQuery.getAlbums(sortBy = sortBy)
            withContext(Main) {
                setRecyclerViewAdapter(albums)
                super.load()
            }
        }
    }

    companion object {
        fun newInstance(): AlbumsFragment {
            return AlbumsFragment()
        }
    }
}