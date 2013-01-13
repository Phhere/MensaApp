package rehs.app.mensa;

public class Meal {

	public String title;
	public String type;
	public Float price;
	public String image;
	public String date;
	public String beilage;
	public String zusatz;
	public String ort;

	public Meal(String title, String type, Float price, String image, String date, String beilage, String zusatz, String ort) {
		this.title = title;
		this.type = type;
		this.price = price;
		this.image = image;
		this.date = date;
		this.beilage = beilage;
		this.zusatz = zusatz;
		this.ort = ort;
	}

}
