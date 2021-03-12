package com.symphony.projjal.exoplayer

import java.nio.ByteBuffer

class SymphonyAudioBufferSink : SymphonyAudioProcessor.AudioBufferSink {
    private var sampleRateHz = 0
    private var channelCount = 0
    private var encoding = 0

    override fun flush(sampleRateHz: Int, channelCount: Int, encoding: Int) {
        this.sampleRateHz = sampleRateHz
        this.channelCount = channelCount
        this.encoding = encoding
    }

    override fun handleBuffer(buffer: ByteBuffer): ByteBuffer {
        val byteArray = ByteArray(buffer.remaining())
        buffer.get(byteArray)

        return ByteBuffer.wrap(byteArray)
    }
}