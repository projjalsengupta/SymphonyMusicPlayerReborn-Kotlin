package com.symphony.projjal.glide.song

import android.content.Context
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.symphony.mediastorequery.model.Song
import java.nio.ByteBuffer

class SongLoaderFactory(private val context: Context) :
    ModelLoaderFactory<Song, ByteBuffer> {
    override fun build(unused: MultiModelLoaderFactory): ModelLoader<Song, ByteBuffer> {
        return SongModelLoader(context)
    }

    override fun teardown() {}
}
