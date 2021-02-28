package com.symphony.projjal

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.symphony.mediastorequery.model.Artist
import com.symphony.mediastorequery.model.Song
import com.symphony.projjal.glide.artist.ArtistLoaderFactory
import com.symphony.projjal.glide.palette.PaletteBitmap
import com.symphony.projjal.glide.palette.PaletteBitmapTranscoder
import com.symphony.projjal.glide.song.SongLoaderFactory
import java.nio.ByteBuffer

@GlideModule
class SymphonyGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.prepend(Song::class.java, ByteBuffer::class.java, SongLoaderFactory(context))
        registry.prepend(Artist::class.java, ByteBuffer::class.java, ArtistLoaderFactory(context))
        registry.register(
            Bitmap::class.java,
            PaletteBitmap::class.java,
            PaletteBitmapTranscoder(glide.bitmapPool)
        )
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val diskCacheSizeBytes = 1024L * 1024L * 2048L
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes))
        val bitmapPoolSizeBytes = 1024 * 1024 * 10
        builder.setBitmapPool(LruBitmapPool(bitmapPoolSizeBytes.toLong()))
        builder.setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .disallowHardwareConfig()
        )
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}