package rehs.app.mensa;

import java.util.Vector;

import com.androidhive.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MealListArrayAdapter extends BaseAdapter {
	private final Vector<Meal> values;
	private LayoutInflater inflater;
	private Context context;
	private String thumbnail = "http://majestix.uni-duesseldorf.info/essen/proxy.php?image=";

	public MealListArrayAdapter(Context context, Vector<Meal> values) {
		super();
		this.values = values;
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = inflater.inflate(R.layout.item, parent, false);
		}
		Meal m = this.values.get(position);

		TextView textView = (TextView) view.findViewById(R.id.title);
		ImageView imageView = (ImageView) view.findViewById(R.id.thumb);
		TextView price = (TextView) view.findViewById(R.id.price);
		price.setText(m.price + " �");
		textView.setText(m.title);
		if (m.image != "") {
			int loader = R.drawable.no_image;
			String image_url = this.thumbnail + m.image;
			ImageLoader imgLoader = new ImageLoader(this.context);
			imgLoader.DisplayImage(image_url, loader, imageView);
		} else {
			imageView.setImageResource(R.drawable.no_image);
		}

		return view;
	}

	@Override
	public int getCount() {
		return this.values.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.values.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}