package com.radioentertainment.item;

public class ItemCity {
	
	private String id;
	private String name;
	private String tagLine;

	public ItemCity(String id, String name, String tagLine) {
		this.id = id;
		this.name = name;
		this.tagLine = tagLine;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTagLine() {
		return tagLine;
	}
}
