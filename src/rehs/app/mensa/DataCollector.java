package rehs.app.mensa;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class DataCollector {
	private final Document doc;
	public static Vector<Meal> meals;

	public DataCollector(String xml) {
		Document doc = this.getDomElement(xml); // getting DOM element
		this.loadData(doc);
		this.doc = doc;
	}

	public void reloadData() {
		this.loadData(this.doc);
		Mensa.reload();
	}

	private void loadData(Document doc) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(Mensa.context);
		DataCollector.meals = new Vector<Meal>();
		NodeList nl = doc.getElementsByTagName("ROW");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			String title = e.getAttribute("TEXTL1");
			String type = e.getAttribute("SPEISE");
			String tmp = e.getAttribute(sharedPrefs.getString("sync_frequency",
					"STUDIERENDE"));
			Float price = Float.parseFloat(tmp.replace(",", "."));
			String image = e.getAttribute("PFAD");
			String date = e.getAttribute("DATUM").split("\\s+")[0];
			String zusatz = e.getAttribute("ZSNAMEN");
			String ort = e.getAttribute("ORT");
			String beilage = "";
			for (int j = 2; j < 5; j++) {
				String t = e.getAttribute("TEXTL" + j);
				if (t.isEmpty()) {
					break;
				} else {
					beilage += t + "\n";
				}
			}
			Meal m = new Meal(title, type, price, image, date, beilage, zusatz,
					ort);
			if (this.filterData(m)) {
				DataCollector.meals.add(m);
			} else {
				m = null;
			}
		}
	}

	public Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}

		return doc;
	}

	private boolean filterData(Meal m) {
		if (m.title.equals("siehe Tagesangebot")) {
			return false;
		}
		if (m.price <= 0) {
			return false;
		}

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(Mensa.context);

		String mensa = sharedPrefs.getString("mensa", "3.500");
		if (mensa.equals("-1") == false) {
			if (m.ort.equals(mensa) == false) {
				return false;
			}
		}

		String date = sharedPrefs.getString("date", "1");
		if (date.equals("-1") == false) {

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			String d;
			if (date.equals("1")) {
				Date date1 = new Date();
				d = dateFormat.format(date1);
			} else {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.DATE, 1); // number of days to add
				d = dateFormat.format(c.getTime());
			}

			if (m.date.equals(d) == false) {
				return false;
			}
		}

		return true;
	}
}
