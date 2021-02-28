package com.symphony.projjal.glide.artist

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.signature.ObjectKey
import com.symphony.mediastorequery.model.Artist
import java.nio.ByteBuffer

class ArtistModelLoader internal constructor(private val context: Context) :
    ModelLoader<Artist, ByteBuffer> {
    override fun buildLoadData(
        model: Artist,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<ByteBuffer> {
        return LoadData(ObjectKey(model), ArtistDataFetcher(model, context))
    }

    override fun handles(model: Artist): Boolean {
        return true
    }
}