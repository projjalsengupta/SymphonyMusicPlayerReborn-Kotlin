package com.symphony.projjal.glide.palette

import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.symphony.colorutils.ColorUtils.getDominantColors

class PaletteBitmapTranscoder(private val bitmapPool: BitmapPool) :
    ResourceTranscoder<Bitmap?, PaletteBitmap> {
    override fun transcode(
        toTranscode: Resource<Bitmap?>,
        options: Options
    ): Resource<PaletteBitmap>? {
        val bitmap = toTranscode.get()
        val colors: IntArray = getDominantColors(bitmap)
        val result = PaletteBitmap(bitmap, colors[0], colors[1])
        return PaletteBitmapResource(result, bitmapPool)
    }
}