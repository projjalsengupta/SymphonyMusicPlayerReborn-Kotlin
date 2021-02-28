package com.symphony.mediastorequery.model

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.google.gson.Gson

data class Playlist(
    val name: String,
    val id: Long,
    val songs: MutableList<Song>
) {
    val songCount: Int get() = songs.size
    val totalDuration: Int
        get() {
            var totalDuration = 0
            for (song in songs) {
                totalDuration += song.duration
            }
            return totalDuration
        }
}