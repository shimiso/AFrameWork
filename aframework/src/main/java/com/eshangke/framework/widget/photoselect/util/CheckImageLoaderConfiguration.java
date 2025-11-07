package com.eshangke.framework.widget.photoselect.util;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**    
 */
public class CheckImageLoaderConfiguration {

	public static void checkImageLoaderConfiguration(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);
		if (!UniversalImageLoadTool.checkImageLoader()) {
			// This configuration tuning is custom. You can tune every option,
			// you may tune some of them,
			// or you can create default configuration by
			// ImageLoaderConfiguration.createDefault(this);
			// method.
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(480, 800) // max
																																// width,
																																// max
																																// height，即保存的每个缓存文件的最大长宽
					.threadPriority(Thread.NORM_PRIORITY).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()) // default
					.diskCache(new UnlimitedDiskCache(cacheDir)) // default
					.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100).imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout
																																					// (5
																																					// s),
																																					// readTimeout
																																					// (30
																																					// s)超时时间
					.tasksProcessingOrder(QueueProcessingType.LIFO).build();
			// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);
		}
	}
}
