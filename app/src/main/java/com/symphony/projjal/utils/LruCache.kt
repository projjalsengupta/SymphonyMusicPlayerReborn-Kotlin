package com.symphony.projjal.utils

import android.graphics.Bitmap
import android.util.LruCache


interface ImageCache {
    fun getBitmap(key: String?): Bitmap?
    fun putBitmap(key: String?, bitmap: Bitmap?)
}

class LruCache @JvmOverloads constructor(maxSize: Int = DEFAULT_CACHE_SIZE) :
    LruCache<String?, Bitmap?>(maxSize), ImageCache {
    override fun getBitmap(key: String?): Bitmap? {
        return get(key)
    }

    override fun putBitmap(key: String?, bitmap: Bitmap?) {
        put(key, bitmap)
    }

    override fun sizeOf(key: String?, value: Bitmap?): Int {
        return if (value == null) 0 else value.rowBytes * value.height / 1024
    }

    companion object {
        private val DEFAULT_CACHE_SIZE = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    }
}