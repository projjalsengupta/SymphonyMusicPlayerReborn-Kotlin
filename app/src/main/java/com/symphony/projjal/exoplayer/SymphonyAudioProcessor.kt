package com.symphony.projjal.exoplayer

import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.BaseAudioProcessor
import com.google.android.exoplayer2.util.Assertions
import java.nio.ByteBuffer

class SymphonyAudioProcessor(audioBufferSink: AudioBufferSink?) :
    BaseAudioProcessor() {
    private val audioBufferSink: AudioBufferSink = Assertions.checkNotNull(audioBufferSink)
    public override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val remaining = inputBuffer.remaining()
        if (remaining != 0) {
            val audioBuffer = audioBufferSink.handleBuffer(inputBuffer)
            replaceOutputBuffer(remaining).put(audioBuffer).flip()
            //replaceOutputBuffer(remaining).put(inputBuffer).flip()
        }
    }

    override fun onFlush() {
        flushSinkIfActive()
    }

    override fun onQueueEndOfStream() {
        flushSinkIfActive()
    }

    override fun onReset() {
        flushSinkIfActive()
    }

    private fun flushSinkIfActive() {
        if (this.isActive) {
            audioBufferSink.flush(
                inputAudioFormat.sampleRate,
                inputAudioFormat.channelCount,
                inputAudioFormat.encoding
            )
        }
    }

    interface AudioBufferSink {
        fun flush(sampleRateHz: Int, channelCount: Int, encoding: Int)
        fun handleBuffer(buffer: ByteBuffer): ByteBuffer
    }
}