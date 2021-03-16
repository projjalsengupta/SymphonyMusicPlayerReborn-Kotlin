package com.symphony.colorutils

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewAnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import java.lang.Exception
import java.util.*
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt

object ColorUtils {
    fun getColor(context: Context?, resId: Int): Int {
        if (context == null) {
            return Color.TRANSPARENT
        }
        return ContextCompat.getColor(context, resId)
    }

    fun adjustAlpha(color: Int, factor: Float): Int {
        return Color.argb((color.alpha * factor).roundToInt(), color.red, color.green, color.blue)
    }

    private fun compareColors(firstColor: Int, secondColor: Int): Double {
        return abs(getColorDarknessValue(firstColor) - getColorDarknessValue(secondColor))
    }

    fun contrastColor(color: Int): Int {
        return if (getColorDarknessValue(color) > 0.5) {
            Color.BLACK
        } else {
            Color.WHITE
        }
    }

    private fun getColorDarknessValue(color: Int): Double {
        return (color.red * 0.299 + color.green * 0.587 + color.blue * 0.114) / 256
    }

    private fun darkenColor(color: Int, fraction: Double): Int {
        return (color - color * fraction).coerceAtLeast(0.0).toInt()
    }

    private fun lightenColor(color: Int, fraction: Double): Int {
        return (color + color * fraction).coerceAtMost(255.0).toInt()
    }

    fun getDominantColors(bitmap: Bitmap): IntArray {
        if (bitmap.isRecycled) {
            return intArrayOf(Color.BLACK, Color.WHITE)
        }
        val palette: Palette =
            Palette.from(bitmap).generate()

        var backgroundColor = Color.BLACK
        when {
            palette.vibrantSwatch != null -> {
                backgroundColor = palette.vibrantSwatch?.rgb ?: Color.BLACK
            }
            palette.mutedSwatch != null -> {
                backgroundColor = palette.mutedSwatch?.rgb ?: Color.BLACK
            }
            palette.darkVibrantSwatch != null -> {
                backgroundColor = palette.darkVibrantSwatch?.rgb ?: Color.BLACK
            }
            palette.darkMutedSwatch != null -> {
                backgroundColor = palette.darkMutedSwatch?.rgb ?: Color.BLACK
            }
            palette.lightVibrantSwatch != null -> {
                backgroundColor = palette.lightVibrantSwatch?.rgb ?: Color.BLACK
            }
            palette.lightMutedSwatch != null -> {
                backgroundColor = palette.lightMutedSwatch?.rgb ?: Color.BLACK
            }
            palette.swatches.isNotEmpty() -> {
                backgroundColor =
                    Collections.max(palette.swatches, SwatchComparator.instance).rgb
            }
        }

        val foregroundColor = adjustAlpha(contrastColor(backgroundColor), 0.75f)

        return intArrayOf(backgroundColor, foregroundColor)
    }

    private class SwatchComparator : Comparator<Swatch?> {
        override fun compare(ob1: Swatch?, ob2: Swatch?): Int {
            return (ob1?.population ?: 0) - (ob2?.population ?: 0)
        }

        companion object {
            private var sInstance: SwatchComparator? = null
            val instance: SwatchComparator?
                get() {
                    if (sInstance == null) {
                        sInstance = SwatchComparator()
                    }
                    return sInstance
                }
        }
    }

    fun getLuminance(color: Int): Int {
        return 77 * (color shr 16 and 255) + 150 * (color shr 8 and 255) + 29 * (color and 255) shr 8
    }

    private fun getAdjustedForegroundColor(backgroundColor: Int, foregroundColor: Int): Int {
        var textColor = foregroundColor
        var comparison = compareColors(backgroundColor, textColor)
        val backgroundContrastColor: Int = contrastColor(backgroundColor)
        var counter = 10
        while (comparison < 0.5 && counter > 0) {
            textColor = if (backgroundContrastColor == Color.BLACK) {
                darken(textColor, 1 - comparison)
            } else {
                lighten(textColor, 1 - comparison)
            }
            comparison = compareColors(backgroundColor, textColor)
            counter--
        }
        return textColor
    }

    private fun lighten(color: Int, fraction: Double): Int {
        return Color.argb(
            color.alpha,
            lightenColor(color.red, fraction),
            lightenColor(color.green, fraction),
            lightenColor(color.blue, fraction)
        )
    }

    private fun darken(color: Int, fraction: Double): Int {
        return Color.argb(
            color.alpha,
            darkenColor(color.red, fraction),
            darkenColor(color.green, fraction),
            darkenColor(color.blue, fraction)
        )
    }

    fun getColoredString(string: String, color: Int): SpannableString {
        val spannableString = SpannableString(string)
        spannableString.setSpan(ForegroundColorSpan(color), 0, string.length, 0)
        return spannableString
    }

    fun animateBackgroundColorChange(
        fromColor: Int,
        toColor: Int,
        view: View,
        duration: Long = 500
    ): Animator? {
        return try {
            val backgroundColorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                fromColor,
                toColor
            )
            backgroundColorAnimation.duration = duration
            backgroundColorAnimation.addUpdateListener { animator ->
                view.setBackgroundColor(animator.animatedValue as Int)
            }
            backgroundColorAnimation.start()
            backgroundColorAnimation
        } catch (ignored: Exception) {
            view.setBackgroundColor(toColor)
            null
        }
    }

    fun animateBackgroundColorChangeWithCircularReveal(
        fromColor: Int,
        toColor: Int,
        centerView: View,
        view1: View,
        view2: View,
        duration: Long = 500
    ): Animator? {
        return try {
            view1.visibility = View.VISIBLE
            view1.setBackgroundColor(fromColor)
            view2.visibility = View.INVISIBLE
            view2.setBackgroundColor(toColor)

            val location = IntArray(2)
            centerView.getLocationOnScreen(location)
            val cx = location[0] + centerView.width / 2
            val cy = location[1] + centerView.height / 2
            val height = view1.height
            val width = view1.width
            val radiusList = DoubleArray(4)
            radiusList[0] = hypot(cx.toDouble(), cy.toDouble())
            radiusList[1] = hypot((width - cx).toDouble(), cy.toDouble())
            radiusList[2] = hypot(cx.toDouble(), (height - cy).toDouble())
            radiusList[3] = hypot((width - cx).toDouble(), (height - cy).toDouble())
            val radius = radiusList.maxOrNull()?.toFloat() ?: 0f

            val circularRevealAnimation =
                ViewAnimationUtils.createCircularReveal(view2, cx, cy, 0f, radius)

            view2.visibility = View.VISIBLE

            circularRevealAnimation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    view1.setBackgroundColor(toColor)
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationRepeat(animation: Animator?) {}

            })
            circularRevealAnimation.duration = duration
            circularRevealAnimation.start()
            circularRevealAnimation
        } catch (ignored: Exception) {
            view1.setBackgroundColor(toColor)
            view2.setBackgroundColor(toColor)
            null
        }
    }
}
