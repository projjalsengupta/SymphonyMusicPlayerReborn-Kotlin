package com.symphony.projjal.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import com.google.android.material.card.MaterialCardView
import com.symphony.projjal.R
import kotlin.math.max

open class SymphonyImageView : MaterialCardView {
    private var _image: ImageView? = null
    val image: ImageView get() = _image!!

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inflate(
            context,
            R.layout.symphony_image_view,
            this
        )
        cardElevation = 0f
        elevation = 0f
        setCardBackgroundColor(Color.TRANSPARENT)
        rectangle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        _image = findViewById(R.id.symphony_image_view)
    }


    fun circle() {
        this.doOnPreDraw {
            this.radius = (max(width, height) / 2).toFloat()
        }
    }

    fun rounded() {
        this.doOnPreDraw {
            this.radius = 24f
        }
    }

    fun rectangle() {
        this.doOnPreDraw {
            this.radius = 0f
        }
    }
}
