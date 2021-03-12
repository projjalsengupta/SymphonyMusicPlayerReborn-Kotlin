package com.symphony.projjal.customviews.visualizer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.symphony.projjal.customviews.visualizer.renderer.Renderer

class VisualizerView : View {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    )

    private var mBytes: ByteArray = ByteArray(0)
    private var mFFTBytes: ByteArray = ByteArray(0)
    private val mRect = Rect()
    private var mRenderers: MutableSet<Renderer> = mutableSetOf()
    private val mFlashPaint = Paint()
    private val mFadePaint = Paint()
    private fun init() {
        mBytes = ByteArray(0)
        mFFTBytes = ByteArray(0)
        mFlashPaint.color = Color.argb(122, 255, 255, 255)
        mFadePaint.color = Color.argb(
            238,
            255,
            255,
            255
        ) // Adjust alpha to change how quickly the image fades
        mFadePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        mRenderers = HashSet<Renderer>()
    }

    fun addRenderer(renderer: Renderer) {
        mRenderers.add(renderer)
    }

    fun clearRenderers() {
        mRenderers.clear()
    }

    fun updateVisualizer(bytes: ByteArray) {
        mBytes = bytes
        invalidate()
    }

    fun updateVisualizerFFT(bytes: ByteArray) {
        mFFTBytes = bytes
        invalidate()
    }

    var mFlash = false

    fun flash() {
        mFlash = true
        invalidate()
    }

    var mCanvasBitmap: Bitmap? = null
    var mCanvas: Canvas? = null

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mRect[0, 0, width] = height
        if (mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        if (mCanvas == null) {
            mCanvas = Canvas(mCanvasBitmap!!)
        }

        val audioData = AudioData(mBytes)
        for (r in mRenderers) {
            r.render(mCanvas!!, audioData, mRect)
        }
        val fftData = FFTData(mFFTBytes)
        for (r in mRenderers) {
            r.render(mCanvas!!, fftData, mRect)
        }
        mCanvas!!.drawPaint(mFadePaint)
        if (mFlash) {
            mFlash = false
            mCanvas!!.drawPaint(mFlashPaint)
        }
        canvas.drawBitmap(mCanvasBitmap!!, Matrix(), null)
    }

    companion object {
        private const val TAG = "VisualizerView"
    }

    init {
        init()
    }
}