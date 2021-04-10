package it.polimi.tiw.bigbang.beans;

import java.sql.Timestamp;

public class OrderInfo {
	private String id;
	private int id_user;
	private int id_vendor;
	private Timestamp date;
	private float shipping_cost = (float) 10.2;
	private float total_items_cost;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getId_user() {
		return id_user;
	}
	public void setId_user(int id_user) {
		this.id_user = id_user;
	}
	public int getId_vendor() {
		return id_vendor;
	}
	public void setId_vendor(int id_vendor) {
		this.id_vendor = id_vendor;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public float getShipping_cost() {
		return shipping_cost;
	}
	public void setShipping_cost(float shipping_cost) {
		this.shipping_cost = shipping_cost;
	}
	public float getTotal_items_cost() {
		return total_items_cost;
	}
	public void setTotal_items_cost(float total_items_cost) {
		this.total_items_cost = total_items_cost;
	}
	public float getOrderTotal() {
		return this.total_items_cost + this.shipping_cost;
	}
}
