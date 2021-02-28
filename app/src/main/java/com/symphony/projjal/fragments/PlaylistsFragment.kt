package com.symphony.projjal.fragments

import androidx.recyclerview.widget.LinearLayoutManager
import com.symphony.mediastorequery.MediaStoreQuery
import com.symphony.mediastorequery.model.Playlist
import com.symphony.projjal.adapters.PlaylistsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistsFragment : LibraryItemFragment() {
    private var adapter: PlaylistsAdapter? = null

    private fun setRecyclerViewAdapter(playlists: MutableList<Playlist>) {
        adapter = PlaylistsAdapter(context = activity, items = playlists)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun load() {
        CoroutineScope(Dispatchers.IO).launch {
            val playlists = MediaStoreQuery(context).getPlaylists()
            withContext(Main) {
                setRecyclerViewAdapter(playlists)
                super.load()
            }
        }
    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}