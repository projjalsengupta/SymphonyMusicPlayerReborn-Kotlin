package com.symphony.mediastorequery

import android.net.Uri
import android.provider.MediaStore

object Constants {
    val artWorkUri: Uri = Uri.parse("content://media/external/audio/albumart")

    val SORT_SONGS_BY_TITLE = MediaStore.Audio.Media.TITLE
    val SORT_SONGS_BY_ALBUM_TITLE = MediaStore.Audio.Media.ALBUM
    val SORT_SONGS_BY_ARTIST = MediaStore.Audio.Media.ARTIST
    val SORT_SONGS_BY_YEAR = MediaStore.Audio.Media.YEAR

    val SORT_ALBUMS_BY_TITLE = "title"
    val SORT_ALBUMS_BY_ARTIST = "artist"
    val SORT_ALBUMS_BY_YEAR = "year"
}