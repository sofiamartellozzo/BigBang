package it.polimi.tiw.bigbang.beans;

public class Price {
	private float price;
	private int idItem;
	private int idVendor;
	private int quantity = 0;
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getIdItem() {
		return idItem;
	}
	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}
	public int getIdVendor() {
		return idVendor;
	}
	public void setIdVendor(int idVendor) {
		this.idVendor = idVendor;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
