package com.jackemate.appberdi.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.jackemate.appberdi.entities.Content

@GlideModule
class GladeModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val diskCacheSizeBytes = 1024 * 1024 * 250 // 250 MB
        builder.setDiskCache(
            InternalCacheDiskCacheFactory(
                context,
                Content.TYPE_IMG,
                diskCacheSizeBytes.toLong()
            )
        )
    }
}