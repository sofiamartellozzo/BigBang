package it.polimi.tiw.bigbang.beans;

public class Vendor {
	private int id;
	private String name;
	private int score;
	private int free_limit;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getFree_limit() {
		return free_limit;
	}
	public void setFree_limit(int free_limit) {
		this.free_limit = free_limit;
	}
	
}
