package com.androidhive;

import java.io.File;

import rehs.app.mensa.Helper;
import android.content.Context;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			this.cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"TempImages");
		} else {
			this.cacheDir = context.getCacheDir();
		}
		if (!this.cacheDir.exists()) {
			this.cacheDir.mkdirs();
		}
	}

	public File getFile(String url) {
		String filename = Helper.md5(url);
		File f = new File(this.cacheDir, filename);
		return f;

	}
}