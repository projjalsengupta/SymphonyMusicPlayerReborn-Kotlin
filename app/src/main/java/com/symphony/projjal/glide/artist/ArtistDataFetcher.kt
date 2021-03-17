package com.symphony.projjal.glide.artist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.ThumbnailUtils
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.symphony.bitmaputils.BitmapUtils
import com.symphony.bitmaputils.BitmapUtils.convertToByteArray
import com.symphony.bitmaputils.BitmapUtils.getByteDataFromUri
import com.symphony.mediastorequery.model.Artist
import com.symphony.projjal.glide.ErrorDrawable
import java.nio.ByteBuffer
import kotlin.math.*

class ArtistDataFetcher internal constructor(val model: Artist, val context: Context?) :
    DataFetcher<ByteBuffer> {

    private val MAX_ROW_WIDTH = context?.resources?.displayMetrics?.widthPixels ?: 1000

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        var data: ByteArray? = createMosaicImage(model)
        if (data == null) {
            data = BitmapUtils.getByteDataFromDrawable(
                context,
                ErrorDrawable.getArtistPlaceHolderDrawable(context)
            )
        }
        if (data != null) {
            callback.onDataReady(ByteBuffer.wrap(data))
        } else {
            callback.onDataReady(null)
        }
    }

    override fun cleanup() {}
    override fun cancel() {}
    override fun getDataClass(): Class<ByteBuffer> {
        return ByteBuffer::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    private fun createMosaicImage(artist: Artist): ByteArray? {
        var index = 0
        val bitmapList: MutableList<Bitmap> = mutableListOf()
        while (index < artist.albumCount && bitmapList.size < 9) {
            val bitmap =
                getBitmapFromByteArray(
                    getByteDataFromUri(
                        context,
                        artist.albums[index].albumArtUri
                    )
                )
            if (bitmap != null) {
                bitmapList.add(bitmap)
            }
            index++
        }
        return mergeBitmap(bitmapList)?.convertToByteArray()
    }

    private fun mergeBitmap(bitmapList: MutableList<Bitmap>): Bitmap? {
        if (bitmapList.isEmpty()) return null
        if (bitmapList.size < 4) {
            return bitmapList[0]
        }

        val numberOfImages = floor(sqrt(bitmapList.size.toDouble())).pow(2.toDouble()).toInt()
        val numberOfRows = sqrt(numberOfImages.toDouble()).toInt()

        var size = 0
        for (index in 0 until numberOfImages) {
            val dimension: Int = bitmapList[index].width.coerceAtMost(bitmapList[index].height)
            bitmapList[index] =
                ThumbnailUtils.extractThumbnail(bitmapList[index], dimension, dimension)
            size = max(size, bitmapList[index].width)
        }

        size = min(size, MAX_ROW_WIDTH / numberOfRows)

        for (index in 0 until numberOfImages) {
            bitmapList[index] = Bitmap.createScaledBitmap(bitmapList[index], size, size, false)
        }

        val comboBitmap: Bitmap
        val height: Int = size * numberOfRows
        val width: Int = size * numberOfRows
        comboBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val comboImage = Canvas(comboBitmap)
        for (index in 0 until numberOfImages) {
            comboImage.drawBitmap(
                bitmapList[index],
                (size * (index % numberOfRows)).toFloat(),
                (size * (index / numberOfRows)).toFloat(),
                null
            )
        }
        return comboBitmap
    }

    private fun getBitmapFromByteArray(byteArray: ByteArray?): Bitmap? {
        if (byteArray == null) {
            return null
        }
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (ignored: Exception) {
            null
        }
    }
}
