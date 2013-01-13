package rehs.app.mensa;

//import com.androidhive.ImageLoader;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class Details extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Infos holen welches Essen angezeigt werden soll
		Intent intent = getIntent();
		Meal m = DataCollector.meals.get(intent.getIntExtra("id", 0));
		
		View view = getLayoutInflater().inflate(R.layout.activity_details, null);
		
		TextView title =(TextView) view.findViewById(R.id.title);
		TextView beilage =(TextView) view.findViewById(R.id.beilage);
		ImageView imageView = (ImageView) view.findViewById(R.id.image);
		TextView zusatz =(TextView) view.findViewById(R.id.zusatz);
		
		title.setText(m.title);
		beilage.setText(m.beilage);
		
		if (m.image != "") {
			/*int loader = R.drawable.no_image;
			String image_url = m.image;
			ImageLoader imgLoader = new ImageLoader(this.getBaseContext());
			imgLoader.DisplayImage(image_url, loader, imageView);*/
			new ImageLoader(m.image,imageView,R.drawable.no_image);
		} else {
			imageView.setImageResource(R.drawable.no_image);
		}
		
		if(m.zusatz.isEmpty() == false){
			zusatz.setText(m.zusatz);
		}
		else{
			zusatz.setText("");
			zusatz.setVisibility(0);
		}
		
		setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
