package com.symphony.mediastorequery.model

import android.content.ContentUris
import android.net.Uri
import com.symphony.mediastorequery.Constants.artWorkUri

data class Album(val id: Long, val songs: MutableList<Song>) {
    val title: String get() = if (songs.size > 0) songs[0].album else ""
    val artist: String get() = if (songs.size > 0) songs[0].artist else ""
    val artistId: Long get() = if (songs.size > 0) songs[0].artistId else -1
    val year: Int get() = if (songs.size > 0) songs[0].year else -1
    val songCount: Int get() = songs.size
    val duration: Int
        get() {
            var totalDuration = 0
            for (song in songs) {
                totalDuration += song.duration
            }
            return totalDuration
        }
    val albumArtUri: Uri
        get() {
            return ContentUris
                .withAppendedId(
                    artWorkUri,
                    id
                )
        }

    val durationText: String
        get() {
            val secs: Int = duration / 1000
            return if (secs / 3600 > 1) {
                String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60)
            } else {
                String.format("%02d:%02d", secs / 60, secs % 60)
            }
        }
}