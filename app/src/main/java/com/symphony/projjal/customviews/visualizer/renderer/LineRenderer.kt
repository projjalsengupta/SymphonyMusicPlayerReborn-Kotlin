package com.symphony.projjal.customviews.visualizer.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.symphony.projjal.customviews.visualizer.AudioData
import com.symphony.projjal.customviews.visualizer.FFTData
import kotlin.math.abs
import kotlin.math.floor

class LineRenderer(
    private val mPaint: Paint,
    private val mFlashPaint: Paint,
    private val mCycleColor: Boolean = false
) : Renderer() {
    private var amplitude = 0f
    override fun onRender(
        canvas: Canvas,
        data: AudioData,
        rect: Rect
    ) {
        if (mCycleColor) {
            cycleColor()
        }

        if (mPoints == null) {
            return
        }
        for (i in 0 until data.bytes.size - 1) {
            mPoints!![i * 4] = (rect.width() * i / (data.bytes.size - 1)).toFloat()
            mPoints!![i * 4 + 1] = (rect.height() / 2
                    + (data.bytes.get(i) + 128).toByte() * (rect.height() / 3) / 128).toFloat()
            mPoints!![i * 4 + 2] = (rect.width() * (i + 1) / (data.bytes.size - 1)).toFloat()
            mPoints!![i * 4 + 3] = (rect.height() / 2
                    + (data.bytes.get(i + 1) + 128).toByte() * (rect.height() / 3) / 128).toFloat()
        }

        var accumulator = 0f
        for (i in 0 until data.bytes.size - 1) {
            accumulator += abs(data.bytes[i].toFloat())
        }
        val amp: Float = accumulator / (128 * data.bytes.size)
        if (amp > amplitude) {
            amplitude = amp
            canvas.drawLines(mPoints!!, mFlashPaint)
        } else {
            amplitude *= 0.99f
            canvas.drawLines(mPoints!!, mPaint)
        }
    }

    override fun onRender(
        canvas: Canvas,
        data: FFTData,
        rect: Rect
    ) {
    }

    private var colorCounter = 0f
    private fun cycleColor() {
        val r =
            floor(128 * (Math.sin(colorCounter.toDouble()) + 3)).toInt()
        val g =
            floor(128 * (Math.sin(colorCounter + 1.toDouble()) + 1)).toInt()
        val b =
            floor(128 * (Math.sin(colorCounter + 7.toDouble()) + 1)).toInt()
        mPaint.color = Color.argb(128, r, g, b)
        colorCounter += 0.03f
    }
}
