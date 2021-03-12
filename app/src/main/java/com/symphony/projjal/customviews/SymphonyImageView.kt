package com.symphony.projjal.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.symphony.projjal.R


open class SymphonyImageView : AppCompatImageView {
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
        background = ContextCompat.getDrawable(context, R.drawable.shape_circle)
        clipToOutline = true
    }

    fun rounded() {
        background = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corners)
        clipToOutline = true
    }

    fun rectangle() {
        background = ContextCompat.getDrawable(context, R.drawable.shape_rectangle)
        clipToOutline = true
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