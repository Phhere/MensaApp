package com.androidhive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rehs.app.mensa.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private final Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	private final Context context;

	public ImageLoader(Context context) {
		this.fileCache = new FileCache(context);
		this.context = context;
		this.executorService = Executors.newFixedThreadPool(5);
	}

	int stub_id = R.drawable.ic_launcher;

	public void DisplayImage(String url, int loader, ImageView imageView) {
		this.stub_id = loader;
		this.imageViews.put(imageView, url);
		Bitmap bitmap = this.memoryCache.get(url);
		File f = this.fileCache.getFile(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else if (f.exists()) {
			Bitmap b = BitmapFactory.decodeFile(f.getPath());
			imageView.setImageBitmap(b);
		} else {
			imageView.setImageResource(loader);
			this.queuePhoto(url, imageView);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		this.executorService.submit(new PhotosLoader(p));
	}

	public Bitmap getBitmap(String url) {
		File f = this.fileCache.getFile(url);

		// from SD cache
		Bitmap b = BitmapFactory.decodeFile(f.getPath());
		if (b != null) {
			return b;
		}

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(300);
			conn.setReadTimeout(300);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			if (f.length() == 0) {
				f.delete();
				bitmap = BitmapFactory.decodeResource(
						this.context.getResources(), R.drawable.no_image);
			} else {
				bitmap = BitmapFactory.decodeFile(f.getPath());
			}
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	/*
	 * private Bitmap decodeFile(File f){ try { //decode image size
	 * BitmapFactory.Options o = new BitmapFactory.Options();
	 * o.inJustDecodeBounds = true; BitmapFactory.decodeStream(new
	 * FileInputStream(f),null,o);
	 * 
	 * //Find the correct scale value. It should be the power of 2. final int
	 * REQUIRED_SIZE=70; int width_tmp=o.outWidth, height_tmp=o.outHeight; int
	 * scale=1; while(true){ if(width_tmp/2<REQUIRED_SIZE ||
	 * height_tmp/2<REQUIRED_SIZE) break; width_tmp/=2; height_tmp/=2; scale*=2;
	 * }
	 * 
	 * //decode with inSampleSize BitmapFactory.Options o2 = new
	 * BitmapFactory.Options(); o2.inSampleSize=scale; return
	 * BitmapFactory.decodeStream(new FileInputStream(f), null, o2); } catch
	 * (FileNotFoundException e) {} return null; }
	 */

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			this.url = u;
			this.imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (ImageLoader.this.imageViewReused(this.photoToLoad)) {
				return;
			}
			Bitmap bmp = ImageLoader.this.getBitmap(this.photoToLoad.url);
			ImageLoader.this.memoryCache.put(this.photoToLoad.url, bmp);
			if (ImageLoader.this.imageViewReused(this.photoToLoad)) {
				return;
			}
			BitmapDisplayer bd = new BitmapDisplayer(bmp, this.photoToLoad);
			Activity a = (Activity) this.photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = this.imageViews.get(photoToLoad.imageView);
		if ((tag == null) || !tag.equals(photoToLoad.url)) {
			return true;
		}
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			this.bitmap = b;
			this.photoToLoad = p;
		}

		@Override
		public void run() {
			if (ImageLoader.this.imageViewReused(this.photoToLoad)) {
				return;
			}
			if (this.bitmap != null) {
				this.photoToLoad.imageView.setImageBitmap(this.bitmap);
			} else {
				this.photoToLoad.imageView
						.setImageResource(ImageLoader.this.stub_id);
			}
		}
	}

	public void clearCache() {
		this.memoryCache.clear();
	}

}
