package com.symphony.projjal.customviews.visualizer.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.symphony.projjal.customviews.visualizer.AudioData
import com.symphony.projjal.customviews.visualizer.FFTData
import kotlin.math.floor
import kotlin.math.sin

class CircleBarRenderer
    (
    private val mPaint: Paint,
    private val mDivisions: Int,
    private val mCycleColor: Boolean = false
) :
    Renderer() {
    override fun onRender(
        canvas: Canvas,
        data: AudioData,
        rect: Rect
    ) {
    }

    override fun onRender(
        canvas: Canvas,
        data: FFTData,
        rect: Rect
    ) {
        if (mCycleColor) {
            cycleColor()
        }
        for (i in 0 until data.bytes.size / mDivisions) {
            // Calculate dbValue
            val rfk: Byte = data.bytes[mDivisions * i]
            val ifk: Byte = data.bytes[mDivisions * i + 1]
            val magnitude = (rfk * rfk + ifk * ifk).toFloat()
            val dbValue =
                75 * Math.log10(magnitude.toDouble()).toFloat()
            val cartPoint = floatArrayOf(
                (i * mDivisions).toFloat() / (data.bytes.size - 1),
                rect.height() / 2 - dbValue / 4
            )
            val polarPoint = toPolar(cartPoint, rect)
            mFFTPoints!![i * 4] = polarPoint[0]
            mFFTPoints!![i * 4 + 1] = polarPoint[1]
            val cartPoint2 = floatArrayOf(
                (i * mDivisions).toFloat() / (data.bytes.size - 1),
                rect.height() / 2 + dbValue
            )
            val polarPoint2 = toPolar(cartPoint2, rect)
            mFFTPoints!![i * 4 + 2] = polarPoint2[0]
            mFFTPoints!![i * 4 + 3] = polarPoint2[1]
        }
        canvas.drawLines(mFFTPoints!!, mPaint)

        // Controls the pulsing rate
        modulation += 0.13f
        angleModulation += 0.28f
    }

    var modulation = 0f
    var modulationStrength = 0.4f // 0-1
    var angleModulation = 0f
    var aggresive = 0.4f
    private fun toPolar(
        cartesian: FloatArray,
        rect: Rect
    ): FloatArray {
        val cX = rect.width() / 2.toDouble()
        val cY = rect.height() / 2.toDouble()
        val angle = cartesian[0] * 2 * Math.PI
        val radius =
            (rect.width() / 2 * (1 - aggresive) + aggresive * cartesian[1] / 2) * (1 - modulationStrength + modulationStrength * (1 + Math.sin(
                modulation.toDouble()
            )) / 2)
        return floatArrayOf(
            (cX + radius * Math.sin(angle + angleModulation)).toFloat(),
            (cY + radius * Math.cos(angle + angleModulation)).toFloat()
        )
    }

    private var colorCounter = 0f
    private fun cycleColor() {
        val r =
            floor(128 * (sin(colorCounter.toDouble()) + 1)).toInt()
        val g =
            floor(128 * (sin(colorCounter + 2.toDouble()) + 1)).toInt()
        val b =
            floor(128 * (sin(colorCounter + 4.toDouble()) + 1)).toInt()
        mPaint.color = Color.argb(128, r, g, b)
        colorCounter += 0.03f
    }
}
