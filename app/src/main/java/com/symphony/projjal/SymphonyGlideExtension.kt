package com.symphony.projjal

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.RequestOptions


object SymphonyGlideExtension {
    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private var albumPlaceHolderDrawable: Drawable? = null

    fun <T> GlideRequest<T>.albumPlaceholder(context: Context?): GlideRequest<T> {
        if (context == null) {
            return apply(RequestOptions())
        }
        if (albumPlaceHolderDrawable == null) {
            val background = ColorDrawable(ContextCompat.getColor(context, R.color.grey_400))
            val foreground = ContextCompat.getDrawable(context, R.drawable.ic_album)

            foreground!!.setTint(Color.BLACK)

            val layers = arrayOf(
                background,
                foreground
            )

            val padding = dpToPx(8)

            val errorDrawable = LayerDrawable(layers)
            errorDrawable.setLayerInset(1, padding, padding, padding, padding)
            albumPlaceHolderDrawable = errorDrawable
        }
        val options = RequestOptions()
            .error(albumPlaceHolderDrawable)
        return apply(options)
    }

    private var songPlaceHolderDrawable: Drawable? = null

    fun getSongPlaceHolderDrawable(context: Context?): Drawable? {
        if (songPlaceHolderDrawable == null && context != null) {
            val background = ColorDrawable(ContextCompat.getColor(context, R.color.grey_400))
            val foreground = ContextCompat.getDrawable(context, R.drawable.ic_song)
            foreground!!.setTint(Color.BLACK)

            val layers = arrayOf(
                background,
                foreground
            )

            val padding = dpToPx(8)

            val errorDrawable = LayerDrawable(layers)
            errorDrawable.setLayerInset(1, padding, padding, padding, padding)

            songPlaceHolderDrawable = errorDrawable
        }
        return songPlaceHolderDrawable
    }

    fun <T> GlideRequest<T>.songPlaceholder(context: Context?): GlideRequest<T> {
        if (context == null) {
            return apply(RequestOptions())
        }
        if (songPlaceHolderDrawable == null) {
            songPlaceHolderDrawable = getSongPlaceHolderDrawable(context)
        }
        val options = RequestOptions()
            .error(songPlaceHolderDrawable)
        return apply(options)
    }

    private var artistPlaceHolderDrawable: Drawable? = null

    fun <T> GlideRequest<T>.artistPlaceholder(context: Context?): GlideRequest<T> {
        if (context == null) {
            return apply(RequestOptions())
        }
        if (artistPlaceHolderDrawable == null) {
            val background = ColorDrawable(ContextCompat.getColor(context, R.color.grey_400))
            val foreground = ContextCompat.getDrawable(context, R.drawable.ic_artist)
            foreground!!.setTint(Color.BLACK)

            val layers = arrayOf(
                background,
                foreground
            )

            val padding = dpToPx(8)

            val errorDrawable = LayerDrawable(layers)
            errorDrawable.setLayerInset(1, padding, padding, padding, padding)

            artistPlaceHolderDrawable = errorDrawable
        }

        val options = RequestOptions()
            .error(artistPlaceHolderDrawable)
        return apply(options)
    }
}