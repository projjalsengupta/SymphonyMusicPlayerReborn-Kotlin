package com.symphony.mediastorequery.model

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.symphony.mediastorequery.Constants.artWorkUri

data class Song(
    val id: Long,
    val album: String,
    val albumId: Long,
    val artist: String,
    val artistId: Long,
    val dateAdded: Int,
    val duration: Int,
    val title: String,
    val track: Int,
    val year: Int
) {
    val fileUri: Uri
        get() {
            return ContentUris
                .withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
        }

    val albumArtUri: Uri
        get() {
            return ContentUris
                .withAppendedId(
                    artWorkUri,
                    albumId
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