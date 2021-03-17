package com.symphony.projjal.glide.album

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.symphony.bitmaputils.BitmapUtils.getByteDataFromDrawable
import com.symphony.bitmaputils.BitmapUtils.getByteDataFromUri
import com.symphony.mediastorequery.model.Album
import com.symphony.projjal.glide.ErrorDrawable.getAlbumPlaceHolderDrawable
import java.nio.ByteBuffer

class AlbumDataFetcher internal constructor(val model: Album, context: Context?) :
    DataFetcher<ByteBuffer> {
    private var context: Context?
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        var data = getByteDataFromUri(context, model.albumArtUri)
        if (data == null) {
            data = getByteDataFromDrawable(context, getAlbumPlaceHolderDrawable(context))
        }
        if (data != null) {
            callback.onDataReady(ByteBuffer.wrap(data))
        } else {
            callback.onDataReady(null)
        }
    }

    override fun cleanup() {
        context = null
    }

    override fun cancel() {
        context = null
    }

    override fun getDataClass(): Class<ByteBuffer> {
        return ByteBuffer::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }

    init {
        this.context = context
    }
}
