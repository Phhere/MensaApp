package rehs.app.mensa;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.androidhive.Utils;

public class ImageLoader {
	private final WebLoad loader;
	private final ImageView view;

	public ImageLoader(String url, ImageView ViewPlace, int noImage) {
		String key = Utils.md5(url);
		this.loader = new WebLoad(url, key, 1000 * 60 * 60 * 24 * 7);
		this.view = ViewPlace;
		if (this.loader.hasCache()) {
			Log.d("Cache","Reloading image from FS");
			InputStream stream = this.loader.getStream();
			if (stream != null) {
				Bitmap b = BitmapFactory.decodeStream(stream);
				this.view.setImageBitmap(b);
				try {
					stream.close();
				} catch (IOException e) {
					this.view.setImageResource(noImage);
				}
			}
		} else {
			this.view.setImageResource(noImage);
			Thread t = new Thread() {
				@Override
				public void run() {
					((Activity) view.getContext())
							.runOnUiThread(new Runnable() {
								public void run() {
									Log.d("Download","Downloading Image");
									loader.download();
									InputStream stream = loader.getStream();
									if (stream != null) {
										view.setImageBitmap(BitmapFactory
												.decodeStream(stream));
										try {
											stream.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							});
				}
			};
			t.setPriority(Thread.NORM_PRIORITY-1);
			t.start();
			
		}
	}
}
