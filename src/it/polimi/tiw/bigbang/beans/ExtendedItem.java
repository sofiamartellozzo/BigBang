package it.polimi.tiw.bigbang.beans;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtendedItem {
	private Item item;
	private LinkedHashMap<Vendor,Price> value;
	
	public int getId() {
		return item.getId();
	}
	public String getName() {
		return item.getName();
	}
	public String getDescription() {
		return item.getDescription();
	}
	public String getCategory() {
		return item.getCategory();
	}
	public String getPicture() {
		return item.getPicture();
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public HashMap<Vendor, Price> getValue() {
		return value;
	}
	public void setValue(LinkedHashMap<Vendor, Price> value) {
		this.value = value;
	}
}
