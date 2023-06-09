package com.radioentertainment.item;

import java.io.Serializable;

public class ItemOnDemandCat implements Serializable{

	private String id, name, image, thumb, totalItems;

	public ItemOnDemandCat(String id, String name, String image, String thumb, String totalItems) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.thumb = thumb;
		this.totalItems = totalItems;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public String getThumb() {
		return thumb;
	}

	public String getTotalItems() {
		return totalItems;
	}
}