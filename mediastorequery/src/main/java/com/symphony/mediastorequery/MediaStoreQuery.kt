package com.symphony.mediastorequery

import android.content.Context
import android.provider.MediaStore
import com.symphony.mediastorequery.Constants.SORT_ALBUMS_BY_ARTIST
import com.symphony.mediastorequery.Constants.SORT_ALBUMS_BY_TITLE
import com.symphony.mediastorequery.Constants.SORT_ALBUMS_BY_YEAR
import com.symphony.mediastorequery.Constants.SORT_SONGS_BY_TITLE
import com.symphony.mediastorequery.model.Album
import com.symphony.mediastorequery.model.Artist
import com.symphony.mediastorequery.model.Playlist
import com.symphony.mediastorequery.model.Song
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class MediaStoreQuery(private val context: Context?) {
    class Options(
        var minimumDuration: Int = 0,
        var maximumDuration: Int = Int.MAX_VALUE,
        var desc: Boolean = false
    )

    private val defaultOptions: Options
    private var options: Options

    private val BASE_SELECTION =
        MediaStore.Audio.Media.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.Media.TITLE + " != ''"

    init {
        options = Options()
        defaultOptions = Options()
    }

    fun minimumDuration(duration: Int): MediaStoreQuery {
        options.minimumDuration = duration
        return this@MediaStoreQuery
    }

    fun maximumDuration(duration: Int): MediaStoreQuery {
        options.maximumDuration = duration
        return this@MediaStoreQuery
    }

    fun desc(desc: Boolean): MediaStoreQuery {
        options.desc = desc
        return this@MediaStoreQuery
    }

    fun getSongs(
        sortBy: String = SORT_SONGS_BY_TITLE,
        songId: Long = -1L,
        albumId: Long = -1L,
        artistId: Long = -1L
    ): MutableList<Song> {
        if (context == null) {
            return mutableListOf()
        }
        var selection = BASE_SELECTION
        selection += " AND " + MediaStore.Audio.Media.DURATION + " > ? AND " + MediaStore.Audio.Media.DURATION + " < ?"
        if (songId != -1L) {
            selection += " AND " + MediaStore.Audio.Media._ID + " = ?"
        }
        if (albumId != -1L) {
            selection += " AND " + MediaStore.Audio.Media.ALBUM_ID + " = ?"
        }
        if (artistId != -1L) {
            selection += " AND " + MediaStore.Audio.Media.ARTIST_ID + " = ?"
        }
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR
        )
        val songs = mutableListOf<Song>()

        val selectionArgsList = mutableListOf<String>()
        selectionArgsList += options.minimumDuration.toString()
        selectionArgsList += options.maximumDuration.toString()
        if (songId != -1L) selectionArgsList += songId.toString()
        if (albumId != -1L) selectionArgsList += albumId.toString()
        if (artistId != -1L) selectionArgsList += artistId.toString()
        val selectionArgs = selectionArgsList.toTypedArray()

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "$sortBy COLLATE NOCASE ASC"
        )?.use { queryCursor ->
            while (queryCursor.moveToNext()) {
                val song = Song(
                    id = queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    album = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    albumId = queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                    artist = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    artistId = queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
                    dateAdded = queryCursor.getInt(queryCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                    duration = queryCursor.getInt(queryCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                    title = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    track = queryCursor.getInt(queryCursor.getColumnIndex(MediaStore.Audio.Media.TRACK)),
                    year = queryCursor.getInt(queryCursor.getColumnIndex(MediaStore.Audio.Media.YEAR))
                )
                songs += song
            }
        }
        if (options.desc) {
            songs.reverse()
        }
        return songs
    }

    fun getAlbums(
        sortBy: String = SORT_ALBUMS_BY_TITLE,
        albumId: Long = -1L
    ): MutableList<Album> {
        val songs = getSongs(sortBy = SORT_SONGS_BY_TITLE, albumId = albumId)
        val albums = splitSongsIntoAlbums(songs)
        when (sortBy) {
            SORT_ALBUMS_BY_TITLE -> albums.sortBy { album -> album.title.toLowerCase(Locale.ROOT) }
            SORT_ALBUMS_BY_ARTIST -> albums.sortBy { album -> album.artist.toLowerCase(Locale.ROOT) }
            SORT_ALBUMS_BY_YEAR -> albums.sortBy { album -> album.year }
        }
        if (options.desc) {
            albums.reverse()
        }
        return albums
    }

    fun getArtists(artistId: Long = -1L): MutableList<Artist> {
        val songs = getSongs(sortBy = SORT_SONGS_BY_TITLE, artistId = artistId)
        val artists = splitSongsIntoArtists(songs)
        artists.sortBy { artist -> artist.name.toLowerCase(Locale.ROOT) }
        if (options.desc) {
            artists.reverse()
        }
        return artists
    }

    fun getArtistById(artistId: Long): MutableList<Artist> {
        val songs = getSongs(sortBy = SORT_SONGS_BY_TITLE, artistId = artistId)
        val artists = splitSongsIntoArtists(songs)
        artists.sortBy { artist -> artist.name.toLowerCase(Locale.ROOT) }
        if (options.desc) {
            artists.reverse()
        }
        return artists
    }

    private fun splitSongsIntoAlbums(songs: MutableList<Song>): MutableList<Album> {
        val albumsMap = HashMap<Long, MutableList<Song>>()
        for (song in songs) {
            if (!albumsMap.containsKey(song.albumId)) {
                albumsMap[song.albumId] = mutableListOf()
            }
            albumsMap[song.albumId]?.add(song)
        }
        val albums = mutableListOf<Album>()
        for ((key, value) in albumsMap) {
            value.sortBy { song -> song.track }
            albums += Album(key, value)
        }
        return albums
    }

    private fun splitSongsIntoArtists(songs: MutableList<Song>): MutableList<Artist> {
        val artistsMap = HashMap<Long, MutableList<Song>>()
        for (song in songs) {
            if (!artistsMap.containsKey(song.artistId)) {
                artistsMap[song.artistId] = mutableListOf()
            }
            artistsMap[song.artistId]?.add(song)
        }
        val artists = mutableListOf<Artist>()
        for ((key, value) in artistsMap) {
            value.sortBy { song -> song.title }
            val albums = splitSongsIntoAlbums(value)
            artists += Artist(key, value, albums)
        }
        return artists
    }

    fun getPlaylists(): MutableList<Playlist> {
        if (context == null) {
            return mutableListOf()
        }
        val projection = arrayOf(
            MediaStore.Audio.Playlists.NAME,
            MediaStore.Audio.Playlists._ID
        )
        val playlists = mutableListOf<Playlist>()

        context.contentResolver.query(
            MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Playlists.NAME
        )?.use { queryCursor ->
            while (queryCursor.moveToNext()) {
                val playlist = Playlist(
                    name = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)),
                    id = queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Audio.Playlists._ID)),
                    songs = getSongsFromPlaylist(
                        queryCursor.getLong(
                            queryCursor.getColumnIndex(
                                MediaStore.Audio.Playlists._ID
                            )
                        )
                    )
                )
                playlists += playlist
            }
        }
        if (options.desc) {
            playlists.reverse()
        }
        return playlists
    }

    fun getSongsFromPlaylist(id: Long): MutableList<Song> {
        if (context == null) {
            return mutableListOf()
        }
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR
        )
        val songs = mutableListOf<Song>()

        context.contentResolver.query(
            MediaStore.Audio.Playlists.Members.getContentUri("external", id),
            projection,
            null,
            null,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER
        )?.use { queryCursor ->
            while (queryCursor.moveToNext()) {
                val song = Song(
                    id = queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    album = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    albumId = queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                    artist = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    artistId = queryCursor.getLong(queryCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
                    dateAdded = queryCursor.getInt(queryCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)),
                    duration = queryCursor.getInt(queryCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                    title = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    track = queryCursor.getInt(queryCursor.getColumnIndex(MediaStore.Audio.Media.TRACK)),
                    year = queryCursor.getInt(queryCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                )
                songs += song
            }
        }
        if (options.desc) {
            songs.reverse()
        }
        return songs
    }
}