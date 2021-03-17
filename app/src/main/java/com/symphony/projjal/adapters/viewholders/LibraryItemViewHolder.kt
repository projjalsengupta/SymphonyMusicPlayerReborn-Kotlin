package com.symphony.projjal.adapters.viewholders

import android.animation.Animator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.symphony.colorutils.ColorUtils
import com.symphony.colorutils.ColorUtils.adjustAlpha
import com.symphony.colorutils.ColorUtils.contrastColor
import com.symphony.colorutils.ColorUtils.getColor
import com.symphony.projjal.*
import com.symphony.projjal.adapters.viewholders.LibraryItemViewHolder.Constants.TYPE_HORIZONTAL_ALBUM
import com.symphony.projjal.customviews.SymphonyImageView
import com.symphony.projjal.glide.palette.PaletteBitmap
import com.symphony.themeengine.ThemeEngine

class LibraryItemViewHolder(
    val root: View,
    val containerView: View,
    val text1: TextView,
    val text2: TextView,
    val image: SymphonyImageView,
    val clickView: View,
    val menu: ImageButton,
    val type: Int
) : RecyclerView.ViewHolder(root) {

    private var selectableItemBackgroundId: Int = 0

    init {
        val outValue = TypedValue()
        itemView.context.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )
        selectableItemBackgroundId = outValue.resourceId
    }

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
            if (contrastColor(themeEngine.backgroundColor) == Color.BLACK) {
                fallbackBackgroundColor = getColor(it, R.color.grey_400)
            } else {
                fallbackBackgroundColor = getColor(it, R.color.grey_900)
            }
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
            clickView.setBackgroundColor(colorControlHighLight)
        } else {
            clickView.setBackgroundResource(selectableItemBackgroundId)
        }

        text1.text = primaryText
        text2.text = secondaryText

        var loader = GlideApp.with(itemView.context)
            .`as`(PaletteBitmap::class.java)
            .load(item)

        if (gridSize == 1) {
            //loader = loader.small()
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
            //loader = loader.medium()
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

        target = loader
            .override(image.width, image.height)
            .into(object : ImageViewTarget<PaletteBitmap?>(image) {
                override fun setResource(resource: PaletteBitmap?) {
                    if (resource != null && !resource.bitmap.isRecycled) {
                        image.setImageBitmap(resource.bitmap)
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
        when (layoutStyle) {
            LAYOUT_STYLE_CARD -> {
                animateBackgroundColor(containerView, fallbackBackgroundColor)
            }
            LAYOUT_STYLE_COLORED -> {
                animateBackgroundColor(containerView, backgroundColor)

                text1.setTextColor(foregroundColor)
                text2.setTextColor(foregroundColor)

                menu.setColorFilter(foregroundColor)
            }
            else -> {
                animateBackgroundColor(containerView, Color.TRANSPARENT)
            }
        }
    }

    private fun resetLayout(gridSize: Int, layoutStyle: Int) = with(itemView) {
        if (gridSize == 1) {
            menu.visibility = View.VISIBLE
            return
        }

        animator?.cancel()

        if (type == TYPE_HORIZONTAL_ALBUM && layoutStyle == LAYOUT_STYLE_CIRCLE || layoutStyle == LAYOUT_STYLE_PLAIN) {
            containerView.setBackgroundColor(Color.TRANSPARENT)
        } else {
            containerView.setBackgroundColor(fallbackBackgroundColor)
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

    private var animator: Animator? = null

    private fun animateBackgroundColor(view: View, color: Int) {
        animator?.cancel()
        animator = ColorUtils.animateBackgroundColorChange(
            adjustAlpha(color, 0.25f),
            color,
            view,
            250
        )
    }
}