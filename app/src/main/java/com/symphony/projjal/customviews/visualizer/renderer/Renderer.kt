package com.symphony.projjal.customviews.visualizer.renderer

import android.graphics.Canvas
import android.graphics.Rect
import com.symphony.projjal.customviews.visualizer.AudioData
import com.symphony.projjal.customviews.visualizer.FFTData

abstract class Renderer {
    var mPoints: FloatArray? = null
    var mFFTPoints: FloatArray? = null

    abstract fun onRender(canvas: Canvas, data: AudioData, rect: Rect)

    abstract fun onRender(canvas: Canvas, data: FFTData, rect: Rect)

    fun render(canvas: Canvas, data: AudioData, rect: Rect) {
        if (mPoints == null || mPoints!!.size < data.bytes.size * 4) {
            mPoints = FloatArray(data.bytes.size * 4)
        }
        onRender(canvas, data, rect)
    }

    fun render(canvas: Canvas, data: FFTData, rect: Rect) {
        if (mFFTPoints == null || mFFTPoints!!.size < data.bytes.size * 4) {
            mFFTPoints = FloatArray(data.bytes.size * 4)
        }
        onRender(canvas, data, rect)
    }
}
