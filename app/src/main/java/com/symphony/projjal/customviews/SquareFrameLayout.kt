package com.symphony.projjal.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class SquareFrameLayout : FrameLayout {
    var forceSquare = true
        set(value) {
            field = forceSquare
            invalidate()
        }

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (forceSquare) {
            setMeasuredDimension(measuredWidth, measuredWidth)
        } else {
            setMeasuredDimension(measuredWidth, measuredHeight)
        }

    }
}
