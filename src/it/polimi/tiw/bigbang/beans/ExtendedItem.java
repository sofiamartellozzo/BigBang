package it.polimi.tiw.bigbang.beans;

import java.util.HashMap;
import java.util.Map;

public class ExtendedItem {
	private Item item;
	private HashMap<Vendor,Price> value;
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public HashMap<Vendor, Price> getValue() {
		return value;
	}
	public void setValue(HashMap<Vendor, Price> value) {
		this.value = value;
	}
	

}
