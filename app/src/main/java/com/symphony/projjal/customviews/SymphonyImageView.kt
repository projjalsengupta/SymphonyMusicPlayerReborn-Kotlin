package com.symphony.projjal.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.doOnPreDraw
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.symphony.projjal.R
import com.symphony.projjal.utils.ConversionUtils.dpToPx
import kotlin.math.max

open class SymphonyImageView : ShapeableImageView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init(attrs)
    }

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    fun circle() {
        doOnPreDraw {
            val radius = max(width, height) / 2
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius.toFloat())
                .build()
        }
    }

    fun rounded() {
        doOnPreDraw {
            val radius = dpToPx(8)
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius.toFloat())
                .build()
        }
    }

    fun rectangle() {
        doOnPreDraw {
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, 0f)
                .build()
        }
    }

    private var square = false
        set(value) {
            field = value
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (square) {
            setMeasuredDimension(measuredWidth, measuredWidth)
        } else {
            setMeasuredDimension(measuredWidth, measuredHeight)
        }
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val symphonyImageViewAttributes =
            context.obtainStyledAttributes(attrs, R.styleable.SymphonyImageView, 0, 0)
        try {
            square =
                symphonyImageViewAttributes.getBoolean(R.styleable.SymphonyImageView_square, false)
        } finally {
            symphonyImageViewAttributes.recycle()
        }
    }
}