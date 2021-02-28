package com.symphony.projjal.glide.palette

import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.util.Util

class PaletteBitmapResource internal constructor(
    private val paletteBitmap: PaletteBitmap,
    private val bitmapPool: BitmapPool
) :
    Resource<PaletteBitmap> {
    override fun getResourceClass(): Class<PaletteBitmap> {
        return PaletteBitmap::class.java
    }

    override fun get(): PaletteBitmap {
        return paletteBitmap
    }

    override fun getSize(): Int {
        return Util.getBitmapByteSize(paletteBitmap.bitmap)
    }

    override fun recycle() {
        bitmapPool.put(paletteBitmap.bitmap)
    }
}