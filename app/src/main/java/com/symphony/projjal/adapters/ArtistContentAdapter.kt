package com.symphony.projjal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.symphony.mediastorequery.model.Artist
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.R
import com.symphony.projjal.adapters.ArtistContentAdapter.Constants.TYPE_ALBUMS
import com.symphony.projjal.adapters.ArtistContentAdapter.Constants.TYPE_HEADING
import com.symphony.projjal.adapters.ArtistContentAdapter.Constants.TYPE_SONG
import com.symphony.projjal.adapters.viewholders.ArtistAlbumsItemViewHolder
import com.symphony.projjal.adapters.viewholders.ArtistSongItemViewHolder
import com.symphony.projjal.adapters.viewholders.HeadingItemViewHolder
import com.symphony.projjal.databinding.RecyclerviewItemArtistSongBinding
import com.symphony.projjal.databinding.RecyclerviewItemHeadingBinding
import com.symphony.projjal.databinding.RecyclerviewItemHorizontalRecyclerviewAlbumItemBinding
import com.symphony.projjal.fragments.AlbumContentFragment
import com.symphony.projjal.utils.FragmentUtils
import com.symphony.projjal.utils.FragmentUtils.addFragment

class ArtistContentAdapter(
    private var activity: AppCompatActivity,
    private var artist: Artist,
    private val clickListener: (MutableList<Song>, Int) -> Unit,
    private val textColor: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    object Constants {
        const val TYPE_HEADING = 1
        const val TYPE_SONG = 2
        const val TYPE_ALBUMS = 3
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == 1 + artist.songs.size) TYPE_HEADING else if (position > 0 && position < 1 + artist.songs.size) TYPE_SONG else TYPE_ALBUMS
    }

    override fun getItemCount(): Int {
        var totalItemCount = 1 + artist.songs.size
        if (artist.albumCount > 0) {
            totalItemCount += 2
        }
        return totalItemCount
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ArtistSongItemViewHolder -> {
                holder.bind(artist.songs[position - 1], textColor)
                holder.itemView.setOnClickListener {
                    clickListener(artist.songs, position - 1)
                }
            }
            is HeadingItemViewHolder -> {
                holder.bind(
                    if (position == 0) holder.itemView.context.getString(R.string.songs) else holder.itemView.context.getString(
                        R.string.albums
                    ),
                    textColor
                )
            }
            is ArtistAlbumsItemViewHolder -> {
                holder.bind(artist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_HEADING -> {
                val binding = RecyclerviewItemHeadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return HeadingItemViewHolder(binding)
            }
            TYPE_SONG -> {
                val binding = RecyclerviewItemArtistSongBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArtistSongItemViewHolder(binding)
            }
            else -> {
                val binding = RecyclerviewItemHorizontalRecyclerviewAlbumItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArtistAlbumsItemViewHolder(binding, clickListener = { it1, it2 ->
                    addFragment(
                        activity = activity,
                        id = R.id.fragmentContainer,
                        newFragment = AlbumContentFragment.newInstance(it1),
                        fragmentName = "album ${it1.id}",
                        sharedImageView = FragmentUtils.SharedImageView(it2)
                    )
                })
            }
        }
    }
}