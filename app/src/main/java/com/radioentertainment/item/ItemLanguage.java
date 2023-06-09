package com.radioentertainment.item;

import java.util.ArrayList;

public class ItemLanguage {

	private String id;
	private String name;
	private ArrayList<ItemRadio> arrayList;

	public ItemLanguage(String id, String name, ArrayList<ItemRadio> arrayList) {
		this.id = id;
		this.name = name;
		this.arrayList = arrayList;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<ItemRadio> getArrayList() {
		return arrayList;
	}

	public void setArrayList(ArrayList<ItemRadio> arrayList) {
		this.arrayList = arrayList;
	}
}
