package com.symphony.projjal.glide.song

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.signature.ObjectKey
import com.symphony.mediastorequery.model.Song
import java.nio.ByteBuffer

class SongModelLoader internal constructor(private val context: Context) :
    ModelLoader<Song, ByteBuffer> {
    override fun buildLoadData(
        model: Song,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<ByteBuffer> {
        return LoadData(
            ObjectKey(model), SongDataFetcher(
                model,
                context
            )
        )
    }

    override fun handles(model: Song): Boolean {
        return true
    }
}