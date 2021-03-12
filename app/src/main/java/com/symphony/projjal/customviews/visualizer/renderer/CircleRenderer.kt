package com.symphony.projjal.customviews.visualizer.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.symphony.projjal.customviews.visualizer.AudioData
import com.symphony.projjal.customviews.visualizer.FFTData
import kotlin.math.floor

class CircleRenderer(
    private val mPaint: Paint,
    private val mCycleColor: Boolean = false
) :
    Renderer() {
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
            val cartPoint = floatArrayOf(
                i.toFloat() / (data.bytes.size - 1),
                rect.height() / 2 + (data.bytes.get(i) + 128) as Byte * (rect.height() / 2) / 128
                    .toFloat()
            )
            val polarPoint = toPolar(cartPoint, rect)
            mPoints!![i * 4] = polarPoint[0]
            mPoints!![i * 4 + 1] = polarPoint[1]
            val cartPoint2 = floatArrayOf(
                (i + 1) as Float / (data.bytes.size - 1),
                rect.height() / 2 + (data.bytes.get(i + 1) + 128) as Byte * (rect.height() / 2) / 128
                    .toFloat()
            )
            val polarPoint2 = toPolar(cartPoint2, rect)
            mPoints!![i * 4 + 2] = polarPoint2[0]
            mPoints!![i * 4 + 3] = polarPoint2[1]
        }
        canvas.drawLines(mPoints!!, mPaint)

        // Controls the pulsing rate
        modulation += 0.04f
    }

    override fun onRender(
        canvas: Canvas,
        data: FFTData,
        rect: Rect
    ) {
        // Do nothing, we only display audio data
    }

    var modulation = 0f
    var aggresive = 0.33f
    private fun toPolar(
        cartesian: FloatArray,
        rect: Rect
    ): FloatArray {
        val cX = rect.width() / 2.toDouble()
        val cY = rect.height() / 2.toDouble()
        val angle = cartesian[0] * 2 * Math.PI
        val radius =
            (rect.width() / 2 * (1 - aggresive) + aggresive * cartesian[1] / 2) * (1.2 + Math.sin(
                modulation.toDouble()
            )) / 2.2
        return floatArrayOf(
            (cX + radius * Math.sin(angle)).toFloat(),
            (cY + radius * Math.cos(angle)).toFloat()
        )
    }

    private var colorCounter = 0f
    private fun cycleColor() {
        val r =
            floor(128 * (Math.sin(colorCounter.toDouble()) + 1)).toInt()
        val g =
            floor(128 * (Math.sin(colorCounter + 2.toDouble()) + 1)).toInt()
        val b =
            floor(128 * (Math.sin(colorCounter + 4.toDouble()) + 1)).toInt()
        mPaint.color = Color.argb(128, r, g, b)
        colorCounter += 0.03f
    }
    /**
     * Renders the audio data onto a pulsing circle
     * @param canvas
     * @param mPaint - Paint to draw lines with
     * @param mCycleColor - If true the color will change on each frame
     */
}
