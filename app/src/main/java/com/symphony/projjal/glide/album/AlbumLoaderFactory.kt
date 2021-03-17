package com.symphony.projjal.glide.album

import android.content.Context
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.symphony.mediastorequery.model.Album
import java.nio.ByteBuffer

class AlbumLoaderFactory(private val context: Context) :
    ModelLoaderFactory<Album, ByteBuffer> {
    override fun build(unused: MultiModelLoaderFactory): ModelLoader<Album, ByteBuffer> {
        return AlbumModelLoader(context)
    }

    override fun teardown() {}
}
