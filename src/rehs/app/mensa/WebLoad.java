package rehs.app.mensa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.androidhive.Utils;

public class WebLoad {
	private File cacheDir;
	private String url;
	private int ttl;
	private String cache;

	public WebLoad(String url, String cache, int ttl) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			this.cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(), this
							.getClass().getPackage().getName()
							+ "/cache");
		} else {
			this.cacheDir = Mensa.context.getCacheDir();
		}
		if (this.cacheDir.exists() == false) {
			this.cacheDir.mkdirs();
		}

		this.url = url;
		this.cache = cache;
		this.ttl = ttl;
	}

	public boolean hasCache() {
		File cached = new File(this.cacheDir, cache);
		return (cached.exists() && cached.lastModified() > System
				.currentTimeMillis() - this.ttl);
	}

	public boolean download() {
		File cached = new File(this.cacheDir, cache);
		if (this.hasCache()) {
			return true;
		} else {
			try {
				URL imageUrl = new URL(this.url);
				HttpURLConnection conn = (HttpURLConnection) imageUrl
						.openConnection();
				conn.setConnectTimeout(300);
				conn.setReadTimeout(300);
				conn.setInstanceFollowRedirects(true);
				InputStream is = conn.getInputStream();
				OutputStream os = new FileOutputStream(cached);
				Utils.CopyStream(is, os);
				os.close();
				is.close();
				return true;
			} catch (Exception ex) {
				try {
					cached.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		}
	}

	public InputStream getStream(){
		File cached = new File(this.cacheDir, cache);
		if (this.hasCache()) {
			try {
				return new FileInputStream(cached);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	public String getString() {
		this.download();
		InputStream is = this.getStream();
		if(is == null) return null;
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
