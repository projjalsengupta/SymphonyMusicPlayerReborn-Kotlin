package com.symphony.projjal.exoplayer

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.exoplayer2.Player

class ExoVisualizer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Player.EventListener {

    private var currentWaveform: FloatArray? = null

    private val bandView = FFTBandView(context, attrs)

    init {
        addView(bandView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    fun newBytes(sampleRateHz: Int, channelCount: Int, fft: FloatArray) {
        currentWaveform = fft
        bandView.onFFT(fft)
    }
}
