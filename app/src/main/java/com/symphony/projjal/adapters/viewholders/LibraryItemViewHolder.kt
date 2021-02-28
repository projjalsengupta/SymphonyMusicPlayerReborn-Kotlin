package com.symphony.projjal.adapters.viewholders

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import com.symphony.colorutils.ColorUtils.contrastColor
import com.symphony.colorutils.ColorUtils.getColor
import com.symphony.projjal.*
import com.symphony.projjal.SymphonyGlideExtension.albumPlaceholder
import com.symphony.projjal.SymphonyGlideExtension.artistPlaceholder
import com.symphony.projjal.SymphonyGlideExtension.medium
import com.symphony.projjal.SymphonyGlideExtension.small
import com.symphony.projjal.SymphonyGlideExtension.songPlaceholder
import com.symphony.projjal.adapters.viewholders.LibraryItemViewHolder.Constants.TYPE_ALBUM
import com.symphony.projjal.adapters.viewholders.LibraryItemViewHolder.Constants.TYPE_ARTIST
import com.symphony.projjal.adapters.viewholders.LibraryItemViewHolder.Constants.TYPE_HORIZONTAL_ALBUM
import com.symphony.projjal.adapters.viewholders.LibraryItemViewHolder.Constants.TYPE_SONG
import com.symphony.projjal.customviews.SymphonyImageView
import com.symphony.projjal.glide.palette.PaletteBitmap
import com.symphony.themeengine.ThemeEngine

class LibraryItemViewHolder(
    val root: View,
    val selectionBackground: View,
    val text1: TextView,
    val text2: TextView,
    val image: SymphonyImageView,
    val cardView: MaterialCardView,
    val clickView: View,
    val menu: ImageButton,
    val type: Int
) : RecyclerView.ViewHolder(root) {

    object Constants {
        const val TYPE_SONG = 1
        const val TYPE_ALBUM = 2
        const val TYPE_ARTIST = 3
        const val TYPE_HORIZONTAL_ALBUM = 4
    }

    var target: Target<PaletteBitmap?>? = null

    private var fallbackBackgroundColor: Int = Color.BLACK
    private var fallbackForegroundColor: Int = Color.WHITE
    private var colorControlHighLight: Int = 0

    var key: String? = null

    init {
        itemView.context?.let {
            val themeEngine = ThemeEngine(it)
            fallbackBackgroundColor = themeEngine.backgroundColor
            fallbackForegroundColor = themeEngine.textColorPrimary
            colorControlHighLight = themeEngine.colorControlHighLight
        }
    }

    fun bind(
        primaryText: String,
        secondaryText: String,
        item: Any?,
        gridSize: Int,
        layoutStyle: Int,
        imageStyle: Int,
        selected: Boolean
    ) = with(itemView) {
        resetLayout(gridSize, layoutStyle)

        if (selected) {
            selectionBackground.setBackgroundColor(colorControlHighLight)
        } else {
            selectionBackground.setBackgroundColor(Color.TRANSPARENT)
        }

        text1.text = primaryText
        text2.text = secondaryText

        var loader = GlideApp.with(itemView.context)
            .`as`(PaletteBitmap::class.java)
            .load(item)

        when (type) {
            TYPE_SONG -> {
                loader = loader.songPlaceholder(itemView.context)
            }
            TYPE_ALBUM -> {
                loader = loader.albumPlaceholder(itemView.context)
            }
            TYPE_HORIZONTAL_ALBUM -> {
                loader = loader.albumPlaceholder(itemView.context)
            }
            TYPE_ARTIST -> {
                loader = loader.artistPlaceholder(itemView.context)
            }
        }

        if (gridSize == 1) {
            loader = loader.small()
            when (imageStyle) {
                IMAGE_STYLE_SQUARE -> {
                    image.rectangle()
                }
                IMAGE_STYLE_CIRCLE -> {
                    image.circle()
                }
                IMAGE_STYLE_ROUNDED_CORNERS -> {
                    image.rounded()
                }
            }
        } else {
            loader = loader.medium()
            when (layoutStyle) {
                LAYOUT_STYLE_CIRCLE -> {
                    image.circle()
                }
                LAYOUT_STYLE_PLAIN -> {
                    image.rounded()
                }
                else -> {
                    image.rectangle()
                }
            }
        }

        target = loader.into(object : ImageViewTarget<PaletteBitmap?>(image.image) {
            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)

                if (gridSize > 1) {
                    setLayoutStyle(
                        backgroundColor = ContextCompat.getColor(context, R.color.grey_400),
                        foregroundColor = ContextCompat.getColor(context, R.color.black),
                        layoutStyle = layoutStyle
                    )
                }

                image.startAnimation(
                    AnimationUtils.loadAnimation(
                        itemView.context,
                        R.anim.fade_in_image
                    )
                )
            }

            override fun setResource(resource: PaletteBitmap?) {
                if (resource != null && !resource.bitmap.isRecycled) {
                    image.image.setImageBitmap(resource.bitmap)
                    image.startAnimation(
                        AnimationUtils.loadAnimation(
                            itemView.context,
                            R.anim.fade_in_image
                        )
                    )
                    if (gridSize > 1) {
                        setLayoutStyle(
                            backgroundColor = resource.backgroundColor,
                            foregroundColor = resource.foregroundColor,
                            layoutStyle = layoutStyle
                        )
                    }
                }
            }
        })
    }

    fun setLayoutStyle(
        backgroundColor: Int,
        foregroundColor: Int,
        layoutStyle: Int
    ) = with(itemView) {
        cardView.strokeWidth = 0
        when (layoutStyle) {
            LAYOUT_STYLE_CARD -> {
                if (contrastColor(fallbackBackgroundColor) == Color.WHITE) {
                    cardView.setCardBackgroundColor(getColor(context, R.color.grey_800))
                } else {
                    cardView.setCardBackgroundColor(getColor(context, R.color.grey_100))
                }
            }
            LAYOUT_STYLE_OUTLINE -> {
                cardView.strokeColor = backgroundColor
                cardView.strokeWidth = oneDpToPx()

                menu.setColorFilter(backgroundColor)
            }
            LAYOUT_STYLE_COLORED -> {
                cardView.setCardBackgroundColor(backgroundColor)

                text1.setTextColor(foregroundColor)
                text2.setTextColor(foregroundColor)

                menu.setColorFilter(foregroundColor)
            }
        }
    }

    private fun resetLayout(gridSize: Int, layoutStyle: Int) = with(itemView) {
        if (gridSize == 1) {
            menu.visibility = View.VISIBLE
            return
        }

        if (type == TYPE_HORIZONTAL_ALBUM && layoutStyle == LAYOUT_STYLE_CIRCLE || layoutStyle == LAYOUT_STYLE_PLAIN) {
            cardView.setCardBackgroundColor(Color.TRANSPARENT)
        } else {
            cardView.setCardBackgroundColor(fallbackBackgroundColor)
        }

        text1.setTextColor(fallbackForegroundColor)
        text2.setTextColor(fallbackForegroundColor)
        menu.setColorFilter(fallbackForegroundColor)

        if (gridSize > 1 && layoutStyle == LAYOUT_STYLE_CIRCLE) {
            text1.gravity = Gravity.CENTER
            text2.gravity = Gravity.CENTER
        } else {
            text1.gravity = Gravity.START
            text2.gravity = Gravity.START
        }

        menu.visibility = View.GONE
    }

    private fun oneDpToPx(): Int {
        return Resources.getSystem().displayMetrics.density.toInt()
    }
}