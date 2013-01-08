package org.wikicleta.adapters;

public class MenuEntry {

	public int textResource;
	public int iconResource;
	public Class<?> activity;

	public MenuEntry(int text, int icon, Class<?> activity) {
		this.textResource = text;
		this.iconResource = icon;
		this.activity = activity;
	}
}