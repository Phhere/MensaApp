package rehs.app.mensa;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class Mensa extends Activity {
	public static Vector<Meal> meals;
	public static Context context;
	public static DataCollector data;
	static final String DATA_FEED = "http://majestix.uni-duesseldorf.info/essen/mensaplan.xml";
	public static MealListArrayAdapter adapter;
	public static ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Mensa.context = this.getBaseContext();
		WebLoad loader = new WebLoad(Mensa.DATA_FEED, "mensaplan.xml",
				1000 * 60 * 30);
		String xml = loader.getString();

		if (xml == null) {
			View view = this.getLayoutInflater().inflate(R.layout.error, null);
			TextView title = (TextView) view.findViewById(R.id.error);
			title.setText("Keine Daten verfügbar");
			this.setContentView(view);
		} else {
			this.setContentView(R.layout.activity_start);
			Mensa.data = new DataCollector(xml);

			Mensa.listView = (ListView) this.findViewById(R.id.list);

			Mensa.adapter = new MealListArrayAdapter(this);
			Mensa.listView.setAdapter(Mensa.adapter);
			Mensa.listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent newActivity = new Intent(Mensa.this, Details.class);
					newActivity.putExtra("id", position);
					Mensa.this.startActivity(newActivity);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent newActivity = new Intent(Mensa.this, SettingsActivity.class);
			this.startActivity(newActivity);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static void reload() {
		Mensa.adapter.notifyDataSetChanged();
		// Mensa.listView.invalidateViews();
	}

}
