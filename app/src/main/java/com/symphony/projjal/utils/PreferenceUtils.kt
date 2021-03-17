package com.symphony.projjal.utils

import io.paperdb.Paper

object PreferenceUtils {
    private var _songGridSize: Int = -1
    var songGridSize: Int
        get() {
            if (_songGridSize == -1) {
                _songGridSize = Paper.book().read("songGridSize", 1)
            }
            return _songGridSize
        }
        set(songGridSize) {
            _songGridSize = songGridSize
            Paper.book().write("songGridSize", songGridSize)
        }

    private var _songLayoutStyle: Int = -1
    var songLayoutStyle: Int
        get() {
            if (_songLayoutStyle == -1) {
                _songLayoutStyle = Paper.book().read("songLayoutStyle", 1)
            }
            return _songLayoutStyle
        }
        set(songLayoutStyle) {
            _songLayoutStyle = songLayoutStyle
            Paper.book().write("songLayoutStyle", songLayoutStyle)
        }

    private var _songImageStyle: Int = -1
    var songImageStyle: Int
        get() {
            if (_songImageStyle == -1) {
                _songImageStyle = Paper.book().read("songImageStyle", 1)
            }
            return _songImageStyle
        }
        set(songImageStyle) {
            _songImageStyle = songImageStyle
            Paper.book().write("songImageStyle", songImageStyle)
        }

    private var _songSortBy: Int = -1
    var songSortBy: Int
        get() {
            if (_songSortBy == -1) {
                _songSortBy = Paper.book().read("songSortBy", 1)
            }
            return _songSortBy
        }
        set(songSortBy) {
            _songSortBy = songSortBy
            Paper.book().write("songSortBy", songSortBy)
        }

    private var _songSortOrder: Int = -1
    var songSortOrder: Int
        get() {
            if (_songSortOrder == -1) {
                _songSortOrder = Paper.book().read("songSortOrder", 1)
            }
            return _songSortOrder
        }
        set(songSortOrder) {
            _songSortOrder = songSortOrder
            Paper.book().write("songSortOrder", songSortOrder)
        }

    private var _albumGridSize: Int = -1
    var albumGridSize: Int
        get() {
            if (_albumGridSize == -1) {
                _albumGridSize = Paper.book().read("albumGridSize", 3)
            }
            return _albumGridSize
        }
        set(albumGridSize) {
            _albumGridSize = albumGridSize
            Paper.book().write("albumGridSize", albumGridSize)
        }

    private var _albumLayoutStyle: Int = -1
    var albumLayoutStyle: Int
        get() {
            if (_albumLayoutStyle == -1) {
                _albumLayoutStyle = Paper.book().read("albumLayoutStyle", 3)
            }
            return _albumLayoutStyle
        }
        set(albumLayoutStyle) {
            _albumLayoutStyle = albumLayoutStyle
            Paper.book().write("albumLayoutStyle", albumLayoutStyle)
        }

    private var _albumImageStyle: Int = -1
    var albumImageStyle: Int
        get() {
            if (_albumImageStyle == -1) {
                _albumImageStyle = Paper.book().read("albumImageStyle", 1)
            }
            return _albumImageStyle
        }
        set(albumImageStyle) {
            _albumImageStyle = albumImageStyle
            Paper.book().write("albumImageStyle", albumImageStyle)
        }

    private var _albumSortBy: Int = -1
    var albumSortBy: Int
        get() {
            if (_albumSortBy == -1) {
                _albumSortBy = Paper.book().read("albumSortBy", 1)
            }
            return _albumSortBy
        }
        set(albumSortBy) {
            _albumSortBy = albumSortBy
            Paper.book().write("albumSortBy", albumSortBy)
        }

    private var _albumSortOrder: Int = -1
    var albumSortOrder: Int
        get() {
            if (_albumSortOrder == -1) {
                _albumSortOrder = Paper.book().read("albumSortOrder", 1)
            }
            return _albumSortOrder
        }
        set(albumSortOrder) {
            _albumSortOrder = albumSortOrder
            Paper.book().write("albumSortOrder", albumSortOrder)
        }

    private var _artistGridSize: Int = -1
    var artistGridSize: Int
        get() {
            if (_artistGridSize == -1) {
                _artistGridSize = Paper.book().read("artistGridSize", 3)
            }
            return _artistGridSize
        }
        set(artistGridSize) {
            _artistGridSize = artistGridSize
            Paper.book().write("artistGridSize", artistGridSize)
        }

    private var _artistLayoutStyle: Int = -1
    var artistLayoutStyle: Int
        get() {
            if (_artistLayoutStyle == -1) {
                _artistLayoutStyle = Paper.book().read("artistLayoutStyle", 3)
            }
            return _artistLayoutStyle
        }
        set(artistLayoutStyle) {
            _artistLayoutStyle = artistLayoutStyle
            Paper.book().write("artistLayoutStyle", artistLayoutStyle)
        }

    private var _artistImageStyle: Int = -1
    var artistImageStyle: Int
        get() {
            if (_artistImageStyle == -1) {
                _artistImageStyle = Paper.book().read("artistImageStyle", 1)
            }
            return _artistImageStyle
        }
        set(artistImageStyle) {
            _artistImageStyle = artistImageStyle
            Paper.book().write("artistImageStyle", artistImageStyle)
        }

    private var _artistSortBy: Int = -1
    var artistSortBy: Int
        get() {
            if (_artistSortBy == -1) {
                _artistSortBy = Paper.book().read("artistSortBy", 1)
            }
            return _artistSortBy
        }
        set(artistSortBy) {
            _artistSortBy = artistSortBy
            Paper.book().write("artistSortBy", artistSortBy)
        }

    private var _artistSortOrder: Int = -1
    var artistSortOrder: Int
        get() {
            if (_artistSortOrder == -1) {
                _artistSortOrder = Paper.book().read("artistSortOrder", 1)
            }
            return _artistSortOrder
        }
        set(artistSortOrder) {
            _artistSortOrder = artistSortOrder
            Paper.book().write("artistSortOrder", artistSortOrder)
        }

    private var _nowPlayingColorChangingAnimationStyle: Int = -1
    var nowPlayingColorChangingAnimationStyle: Int
        get() {
            if (_nowPlayingColorChangingAnimationStyle == -1) {
                _nowPlayingColorChangingAnimationStyle =
                    Paper.book().read("nowPlayingColorChangingAnimationStyle", 2)
            }
            return _nowPlayingColorChangingAnimationStyle
        }
        set(nowPlayingColorChangingAnimationStyle) {
            _nowPlayingColorChangingAnimationStyle = nowPlayingColorChangingAnimationStyle
            Paper.book().write(
                "nowPlayingColorChangingAnimationStyle",
                nowPlayingColorChangingAnimationStyle
            )
        }
}
