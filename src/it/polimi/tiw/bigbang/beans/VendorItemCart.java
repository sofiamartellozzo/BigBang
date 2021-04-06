package it.polimi.tiw.bigbang.beans;

import java.util.ArrayList;
import java.util.List;

public class VendorItemCart {
	
	private String vendorName;
	private int vendorScore;
	private float shippingCost;
	private List<ItemCart> itemVendor = new ArrayList<ItemCart>();
	private float total;
	
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public int getVendorScore() {
		return vendorScore;
	}
	public void setVendorScore(int vendorScore) {
		this.vendorScore = vendorScore;
	}
	public float getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}
	public List<ItemCart> getItemVendor() {
		return itemVendor;
	}
	public void setItemVendor(ItemCart ic) {
		itemVendor.add(ic);
	}
	public float getTotal() {
		return total;
	}
	public void setTotal(float total) {
		this.total = total;
	}
	
	
}
