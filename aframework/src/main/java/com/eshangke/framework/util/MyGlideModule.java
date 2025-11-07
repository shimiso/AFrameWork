package com.eshangke.framework.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

/**
 * 设置
 */
public class MyGlideModule implements GlideModule {
    //1、创建
    //2、在ids.xml下添加ID
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 默认为不透明背景，转换为透明背景
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
//      默认缓存是在机身内存中，缓存的东西无法看到，出于对内存大小的需要，可以设置为外存
//      重新设置缓存大小
//      builder.setDiskCache(
//                new InternalCacheDiskCacheFactory(context, yourSizeInBytes));
//      重新设置缓存目录于程序目录内
//      builder.setDiskCache(
//                new InternalCacheDiskCacheFactory(context, cacheDirectoryName, yourSizeInBytes));
//      重新设置缓存目录于SD卡中，使用默认SD卡内存大小
        builder.setDiskCache(
                new ExternalCacheDiskCacheFactory(context, new SharePreferenceUtil(context).getBitmapCachePath(), DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE));
        //调整使用的运行内存
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        //得到默认可以使用的运行内存大小
        int defaultMemoryCacheSize = 2 * calculator.getMemoryCacheSize();
        //得到默认可以使用的位图池大小
        int defaultBitmapPoolSize = 2 * calculator.getBitmapPoolSize();
//      Glide.get(context).setMemoryCategory(MemoryCategory.HIGH);
        //设置运行内存池大小
        builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize));
        //设置默认位图池大小
        builder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }


}