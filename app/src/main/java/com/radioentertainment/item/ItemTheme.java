package com.radioentertainment.item;

public class ItemTheme {

	String id;
	private int firstColor, secondColor;

	public ItemTheme(String id, int firstColor, int secondColor) {
		this.id = id;
		this.firstColor = firstColor;
		this.secondColor = secondColor;
	}

	public int getFirstColor() {
		return firstColor;
	}

	public int getSecondColor() {
		return secondColor;
	}

	public String getId() {
		return id;
	}
}
