package com.symphony.mediastorequery.model

data class Artist(val id: Long, val songs: MutableList<Song>, val albums: MutableList<Album>) {
    val name: String get() = if (songs.size > 0) songs[0].artist else ""
    val songCount: Int get() = songs.size
    val albumCount: Int get() = albums.size
    val duration: Int
        get() {
            var totalDuration = 0
            for (song in songs) {
                totalDuration += song.duration
            }
            return totalDuration
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