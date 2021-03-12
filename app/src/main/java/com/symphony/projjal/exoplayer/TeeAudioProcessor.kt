package com.symphony.projjal.exoplayer

import com.google.android.exoplayer2.C.PcmEncoding
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.BaseAudioProcessor
import com.google.android.exoplayer2.util.Assertions
import java.nio.ByteBuffer

open class TeeAudioProcessor(audioBufferSink: AudioBufferSink?) : BaseAudioProcessor() {
    /** A sink for audio buffers handled by the audio processor.  */
    interface AudioBufferSink {
        /** Called when the audio processor is flushed with a format of subsequent input.  */
        fun flush(
            sampleRateHz: Int,
            channelCount: Int,
            @PcmEncoding encoding: Int
        )

        /**
         * Called when data is written to the audio processor.
         *
         * @param buffer A read-only buffer containing input which the audio processor will handle.
         */
        fun handleBuffer(buffer: ByteBuffer?)
    }

    private val audioBufferSink: AudioBufferSink = Assertions.checkNotNull(audioBufferSink)
    public override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        // This processor is always active (if passed to the sink) and outputs its input.
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val remaining = inputBuffer.remaining()
        if (remaining == 0) {
            return
        }
        audioBufferSink.handleBuffer(inputBuffer.asReadOnlyBuffer())
        replaceOutputBuffer(remaining).put(inputBuffer).flip()
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
        if (isActive) {
            audioBufferSink.flush(
                inputAudioFormat.sampleRate,
                inputAudioFormat.channelCount,
                inputAudioFormat.encoding
            )
        }
    }
}
