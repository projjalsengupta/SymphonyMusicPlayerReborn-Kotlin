package com.symphony.projjal.glide

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.symphony.projjal.R

object ErrorDrawable {
    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private var songPlaceHolderDrawable: Drawable? = null

    fun getSongPlaceHolderDrawable(context: Context?): Drawable? {
        if (songPlaceHolderDrawable == null && context != null) {
            val background = ColorDrawable(ContextCompat.getColor(context, R.color.grey_900))
            val foreground = ContextCompat.getDrawable(context, R.drawable.ic_song)
            foreground!!.setTint(ContextCompat.getColor(context, R.color.grey_700))

            val layers = arrayOf(
                background,
                foreground
            )

            val padding = dpToPx(16)

            val errorDrawable = LayerDrawable(layers)
            errorDrawable.setLayerInset(1, padding, padding, padding, padding)

            songPlaceHolderDrawable = errorDrawable
        }
        return songPlaceHolderDrawable
    }

    private var artistPlaceHolderDrawable: Drawable? = null

    fun getArtistPlaceHolderDrawable(context: Context?): Drawable? {
        if (artistPlaceHolderDrawable == null && context != null) {
            val background = ColorDrawable(ContextCompat.getColor(context, R.color.grey_900))
            val foreground = ContextCompat.getDrawable(context, R.drawable.ic_artist)
            foreground!!.setTint(ContextCompat.getColor(context, R.color.grey_700))

            val layers = arrayOf(
                background,
                foreground
            )

            val padding = dpToPx(16)

            val errorDrawable = LayerDrawable(layers)
            errorDrawable.setLayerInset(1, padding, padding, padding, padding)

            artistPlaceHolderDrawable = errorDrawable
        }
        return artistPlaceHolderDrawable
    }

    private var albumPlaceHolderDrawable: Drawable? = null

    fun getAlbumPlaceHolderDrawable(context: Context?): Drawable? {
        if (albumPlaceHolderDrawable == null && context != null) {
            val background = ColorDrawable(ContextCompat.getColor(context, R.color.grey_900))
            val foreground = ContextCompat.getDrawable(context, R.drawable.ic_album)
            foreground!!.setTint(ContextCompat.getColor(context, R.color.grey_700))

            val layers = arrayOf(
                background,
                foreground
            )

            val padding = dpToPx(16)

            val errorDrawable = LayerDrawable(layers)
            errorDrawable.setLayerInset(1, padding, padding, padding, padding)

            albumPlaceHolderDrawable = errorDrawable
        }
        return albumPlaceHolderDrawable
    }
}