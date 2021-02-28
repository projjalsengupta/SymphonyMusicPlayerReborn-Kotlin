package com.symphony.bitmaputils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection


object BitmapUtils {
    private const val TAG = "bitmaputils"
    fun decodeResource(
        res: Resources?,
        id: Int,
        reqWidth: Int,
        reqHeight: Int,
        config: Bitmap.Config?
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.inPreferredConfig = config
            BitmapFactory.decodeResource(res, id, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inDensity = options.outWidth
            options.inTargetDensity = reqWidth * options.inSampleSize
            options.inMutable = true
            options.inJustDecodeBounds = false
            BitmapFactory.decodeResource(res, id, options)
        } catch (e: Exception) {
            Log.v(BitmapUtils.TAG, "error while decoding resource")
            null
        }
    }

    fun decodeUrl(link: String?): ByteArray? {
        return try {
            val url = URL(link)
            val connection = url.openConnection() as HttpsURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapUtils.getBytes(input)
        } catch (e: Exception) {
            null
        }
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        var bytesResult: ByteArray
        ByteArrayOutputStream().use { byteBuffer ->
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len: Int
            while (inputStream.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            bytesResult = byteBuffer.toByteArray()
        }
        return bytesResult
    }

    fun decodeByteArray(
        data: ByteArray?,
        length: Int,
        reqWidth: Int,
        reqHeight: Int,
        config: Bitmap.Config?
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.inPreferredConfig = config
            BitmapFactory.decodeByteArray(data, 0, length, options)
            options.inSampleSize = BitmapUtils.calculateInSampleSize(options, reqWidth, reqHeight)
            options.inDensity = options.outWidth
            options.inTargetDensity = reqWidth * options.inSampleSize
            options.inMutable = true
            options.inJustDecodeBounds = false
            BitmapFactory.decodeByteArray(data, 0, length, options)
        } catch (e: Exception) {
            Log.v(BitmapUtils.TAG, "error while decoding byte array")
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize > reqHeight
                && halfWidth / inSampleSize > reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun getByteDataFromFile(context: Context?, uri: Uri?): ByteArray? {
        return try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri)
            val data = mediaMetadataRetriever.embeddedPicture
            mediaMetadataRetriever.release()
            data
        } catch (e: Exception) {
            null
        }
    }

    fun getByteDataFromUri(context: Context?, uri: Uri?): ByteArray? {
        return try {
            if (context != null && uri != null) {
                context.contentResolver.openInputStream(uri).use { inputStream ->
                    inputStream?.let { inputStream1 -> getBytes(inputStream1) }
                }
            } else {
                null
            }
        } catch (ignored: Exception) {
            null
        }
    }

    fun drawableToBitmap(drawable: Drawable?, height: Int, width: Int): Bitmap? {
        if (drawable == null) {
            return null
        }
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    fun resizeBitmap(image: Bitmap?, maxWidth: Int, maxHeight: Int): Bitmap? {
        var finalImage: Bitmap = image ?: return null
        return try {
            if (!finalImage.isRecycled) {
                return if (maxHeight > 0 && maxWidth > 0) {
                    val width = finalImage.width
                    val height = finalImage.height
                    val ratioBitmap = width.toFloat() / height.toFloat()
                    val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
                    var finalWidth = maxWidth
                    var finalHeight = maxHeight
                    if (ratioMax > 1) {
                        finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
                    } else {
                        finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
                    }
                    finalImage =
                        Bitmap.createScaledBitmap(finalImage, finalWidth, finalHeight, true)
                    finalImage
                } else {
                    finalImage
                }
            }
            null
        } catch (ignored: Exception) {
            null
        }
    }
}
