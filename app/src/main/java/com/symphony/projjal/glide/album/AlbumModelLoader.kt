package com.symphony.projjal.glide.album

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.signature.ObjectKey
import com.symphony.mediastorequery.model.Album
import com.symphony.mediastorequery.model.Song
import java.nio.ByteBuffer

class AlbumModelLoader internal constructor(private val context: Context) :
    ModelLoader<Album, ByteBuffer> {
    override fun buildLoadData(
        model: Album,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<ByteBuffer> {
        return LoadData(
            ObjectKey(model), AlbumDataFetcher(
                model,
                context
            )
        )
    }

    override fun handles(model: Album): Boolean {
        return true
    }
}