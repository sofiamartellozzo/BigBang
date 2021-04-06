package it.polimi.tiw.bigbang.beans;

public class Shipping {
	private int idVendor;
	private int shippingCost;
	private int freeLimit; //over this number of item ordered you will recive free delivery
	
	public int getIdVendor() {
		return idVendor;
	}
	public void setIdVendor(int idVendor) {
		this.idVendor = idVendor;
	}
	public int getFreeLimit() {
		return freeLimit;
	}
	public void setFreeLimit(int freeLimit) {
		this.freeLimit = freeLimit;
	}
	public int getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(int shippingCost) {
		this.shippingCost = shippingCost;
	}
}
