package rehs.app.mensa;

import java.util.Vector;

import com.androidhive.XMLParser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Mensa extends Activity {
	public static Vector<Meal> meals;
	static final String DATA_FEED = "http://majestix.uni-duesseldorf.info/essen/mensaplan.xml";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(DATA_FEED); // getting XML
		if(xml.length() > 0){
			new DataCollector(xml);
						
			ListView listView = (ListView) findViewById(R.id.list);
			
			MealListArrayAdapter adapter = new MealListArrayAdapter(this, meals);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent newActivity = new Intent(Mensa.this,Details.class);   
					newActivity.putExtra("id", position);
	                startActivity(newActivity);
				}
			});
		}
		else{
			View view = getLayoutInflater().inflate(R.layout.error, null);
			TextView title =(TextView) view.findViewById(R.id.error);
			title.setText("Keine Daten verfügbar");
			setContentView(view);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

}
