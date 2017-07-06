package com.eshangke.framework.widget.photoselect.bean;

import java.util.WeakHashMap;

import android.graphics.Bitmap;

public class ImageCache extends WeakHashMap<String, Bitmap> {

	public boolean isCached(String url) {
		return containsKey(url) && get(url) != null;
	}

}