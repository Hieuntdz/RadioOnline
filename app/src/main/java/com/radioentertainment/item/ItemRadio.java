package com.radioentertainment.item;

public class ItemRadio {
	
	private String RadioId="";
	private String RadioName="";
	private String RadioUrl="";
	private String RadioFreq="";
	private String RadioImageUrl="", imageThumb="";
	private String views="";
	private String language="";
	private String city_id="";
	private String city_name="";
	private String description="";
	private String duration="";
	private String type="";

	public ItemRadio(String Radioid, String Radioname, String Radiourl, String RadioFreq, String image, String views, String city_id, String city_name, String language, String description, String type) {
		this.RadioId=Radioid;
		this.RadioName=Radioname;
		this.RadioUrl=Radiourl;
		this.RadioImageUrl=image;
		this.views=views;
		this.city_id=city_id;
		this.city_name=city_name;
		this.RadioFreq=RadioFreq;
		this.language=language;
		this.description=description;
		this.type=type;
	}

	public ItemRadio(String Radioid, String Radioname, String Radiourl, String image, String thumb, String duration, String views, String description, String type) {
		this.RadioId=Radioid;
		this.RadioName=Radioname;
		this.RadioUrl=Radiourl;
		this.RadioImageUrl=image;
		this.views=views;
		this.description = description;
		this.duration = duration;
		this.imageThumb = thumb;
		this.type = type;
	}
	
	public String getRadioId() {
		return RadioId;
	}

	public void setRadioId(String Radioid) {
		this.RadioId = Radioid;
	}

	public String getRadioName() {
		return RadioName;
	}

	public void setRadioName(String Radioname) {
		this.RadioName = Radioname;
	}
	
	public String getRadioImageurl() {
		return RadioImageUrl;
		
	}

	public String getRadioFreq() {
		return RadioFreq;
	}
	
	public void setRadioImageurl(String radioimage)
	{
		this.RadioImageUrl=radioimage;
	}
	
	public String getRadiourl()
	{
		return RadioUrl;
		
	}
	
	public void setRadiourl(String radiourl)
	{
		this.RadioUrl=radiourl;
	}

	public String getViews() {
		return views;
	}

	public void setViews(String views)
	{
		this.views=views;
	}

	public String getCityId() {
		return city_id;
	}

	public String getCityName() {
		return city_name;
	}

	public String getLanguage() {
		return language;
	}

	public String getDescription() {
		return description;
	}

	public String getImageThumb() {
		return imageThumb;
	}

	public String getDuration() {
		return duration;
	}

	public String getType() {
		return type;
	}
}
