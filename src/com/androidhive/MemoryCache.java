package com.androidhive;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class MemoryCache {
	private final Map<String, SoftReference<Bitmap>> cache = Collections
			.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());

	public Bitmap get(String id) {
		if (!this.cache.containsKey(id)) {
			return null;
		}
		SoftReference<Bitmap> ref = this.cache.get(id);
		return ref.get();
	}

	public void put(String id, Bitmap bitmap) {
		this.cache.put(id, new SoftReference<Bitmap>(bitmap));
	}

	public void clear() {
		this.cache.clear();
	}
}
