package com.symphony.projjal.utils

import android.content.Context
import android.net.Uri
import java.io.IOException

object LyricsUtil {
    private val LYRICS_HEADER = byteArrayOf(0x55, 0x53, 0x4c, 0x54)

    @Throws(IOException::class)
    fun findLyrics(context: Context?, uri: Uri?): String? {
        if (context == null || uri == null) {
            return null
        }
        return findLyrics(fullyReadUriToBytes(context, uri))
    }

    @Throws(IOException::class)
    fun findLyrics(audioBytes: ByteArray?): String? {
        if (audioBytes == null) {
            return null
        }
        val lyrics: String? = null
        if (audioBytes.size < 22) return lyrics
        try {
            if (convertByteToChar(audioBytes[0]) == 'I' && convertByteToChar(
                    audioBytes[1]
                ) == 'D' && convertByteToChar(audioBytes[2]) == '3'
            ) {
                val lyricsHeaderIndex =
                    BoyerMooreSearch.find(audioBytes, LYRICS_HEADER)
                if (lyricsHeaderIndex != -1) {
                    val length: Int = getLyricsLength(audioBytes, lyricsHeaderIndex)
                    val offset = lyricsHeaderIndex + 15
                    return convertBytesToLyrics(audioBytes, offset, length)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (lyrics?.trim() == "") return null
        return lyrics
    }

    private fun getLyricsLength(audioBytes: ByteArray, lyricsHeaderIndex: Int): Int {
        val frameLength: Int =
            convertByteToChar(audioBytes[lyricsHeaderIndex + 4]).toInt() shl 24 or
                    (convertByteToChar(audioBytes[lyricsHeaderIndex + 5]).toInt() shl 16) or
                    (convertByteToChar(audioBytes[lyricsHeaderIndex + 6]).toInt() shl 8) or
                    convertByteToChar(audioBytes[lyricsHeaderIndex + 7]).toInt()
        return frameLength - 5
    }

    private fun convertByteToChar(b: Byte): Char {
        return (b.toInt() and 0xff).toChar()
    }

    private fun convertBytesToLyrics(audioBytes: ByteArray, offset: Int, length: Int): String {
        val builder = StringBuilder(length)
        var c: Char
        for (i in offset until offset + length) {
            c = convertByteToChar(audioBytes[i])
            if (c != '\u0000') builder.append(c)
        }
        return builder.toString()
    }

    @Throws(IOException::class)
    private fun fullyReadUriToBytes(context: Context, uri: Uri): ByteArray? {
        return context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }
    }

    private object BoyerMooreSearch {
        private const val SIZE = 256
        private val lastOccurrence = IntArray(SIZE)
        private fun buildIndex(pattern: ByteArray) {
            val length = pattern.size
            for (i in 0 until SIZE) lastOccurrence[i] = -1
            for (i in 0 until length) lastOccurrence[convertByteToChar(
                pattern[i]
            ).toInt()] = i
        }

        private fun findLast(b: Byte): Int {
            return lastOccurrence[convertByteToChar(b).toInt()]
        }

        fun find(content: ByteArray?, pattern: ByteArray?): Int {
            if (content == null || content.isEmpty()) return -1
            if (pattern == null || pattern.isEmpty()) return -1

            if (content.size < pattern.size)
                return -1

            buildIndex(pattern)

            var start = pattern.size - 1
            val end = content.size
            var position: Int
            var j: Int

            while (start < end) {
                position = start
                j = pattern.size - 1
                while (j >= 0) {
                    if (pattern[j] != content[position]) {
                        start += if (findLast(content[position]) != -1) {
                            if (j - findLast(content[position]) > 0)
                                j - findLast(content[position]) else 1
                        } else {
                            j + 1
                        }
                        break
                    }
                    if (j == 0) {
                        return position
                    }
                    position--
                    j--
                }
            }
            return -1
        }
    }
}