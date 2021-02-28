package com.symphony.projjal.glide.artist

import android.content.Context
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.symphony.mediastorequery.model.Artist
import java.nio.ByteBuffer

class ArtistLoaderFactory(private val context: Context) :
    ModelLoaderFactory<Artist, ByteBuffer> {
    override fun build(unused: MultiModelLoaderFactory): ModelLoader<Artist, ByteBuffer> {
        return ArtistModelLoader(context)
    }

    override fun teardown() {}
}
