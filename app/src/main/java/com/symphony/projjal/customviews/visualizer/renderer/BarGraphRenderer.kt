package com.symphony.projjal.customviews.visualizer.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.symphony.projjal.customviews.visualizer.AudioData
import com.symphony.projjal.customviews.visualizer.FFTData
import kotlin.math.log10

class BarGraphRenderer(
    private val mDivisions: Int,
    private val mPaint: Paint,
    private val mTop: Boolean
) : Renderer() {
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
        for (i in 0 until data.bytes.size / mDivisions) {
            mFFTPoints!![i * 4] = (i * 4 * mDivisions).toFloat()
            mFFTPoints!![i * 4 + 2] = (i * 4 * mDivisions).toFloat()
            val rfk: Byte = data.bytes[mDivisions * i]
            val ifk: Byte = data.bytes[mDivisions * i + 1]
            val magnitude = (rfk * rfk + ifk * ifk).toFloat()
            val dbValue = (10 * log10(magnitude.toDouble())).toInt()
            if (mTop) {
                mFFTPoints!![i * 4 + 1] = 0f
                mFFTPoints!![i * 4 + 3] = (dbValue * 2 - 10).toFloat()
            } else {
                mFFTPoints!![i * 4 + 1] = rect.height().toFloat()
                mFFTPoints!![i * 4 + 3] = (rect.height() - (dbValue * 2 - 10)).toFloat()
            }
        }
        canvas.drawLines(mFFTPoints!!, mPaint)
    }

}
