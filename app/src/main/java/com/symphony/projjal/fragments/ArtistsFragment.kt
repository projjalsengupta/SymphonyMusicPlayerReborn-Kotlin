package com.symphony.projjal.fragments

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.symphony.mediastorequery.MediaStoreQuery
import com.symphony.mediastorequery.model.Artist
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.R
import com.symphony.projjal.adapters.ArtistsAdapter
import com.symphony.projjal.fragments.LibraryItemFragment.Constants.FRAGMENT_ARTISTS
import com.symphony.projjal.utils.FragmentUtils
import com.symphony.projjal.utils.FragmentUtils.addFragment
import com.symphony.projjal.utils.PreferenceUtils.artistGridSize
import com.symphony.projjal.utils.PreferenceUtils.artistSortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistsFragment : LibraryItemFragment() {
    private var adapter: ArtistsAdapter? = null

    private fun setRecyclerViewAdapter(artists: MutableList<Artist>) {
        adapter = ArtistsAdapter(
            context = activity,
            items = artists,
            clickListener = { it1, it2 ->
                addFragment(
                    activity = activity as AppCompatActivity,
                    id = R.id.fragmentContainer,
                    newFragment = ArtistContentFragment.newInstance(it1),
                    fragmentName = "artist ${it1.id}",
                    sharedImageView = FragmentUtils.SharedImageView(it2)
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
                    FRAGMENT_ARTISTS
                ) { adapter?.notifyDataSetChanged() }
            },
            sortClicked = {
                onSortClicked(it, FRAGMENT_ARTISTS) {
                    load()
                }
            },
            gridClicked = { onGridClicked(it, FRAGMENT_ARTISTS) }
        )
        val layoutManager = GridLayoutManager(context, artistGridSize)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) {
                    artistGridSize
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
            val artists = MediaStoreQuery(context).desc(artistSortOrder == 2).getArtists()
            withContext(Main) {
                setRecyclerViewAdapter(artists)
                super.load()
            }
        }
    }

    companion object {
        fun newInstance(): ArtistsFragment {
            return ArtistsFragment()
        }
    }
}