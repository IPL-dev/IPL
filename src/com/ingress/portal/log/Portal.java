package com.ingress.portal.log;

public class Portal {

	private int id;
	private String name;
	private String date;
	private String recharge;
	
	public Portal(String n, String c, String r) {
		super();
		this.name = n;
		this.date = c;
		this.recharge = r;
	}
	
	public Portal(int id, String n, String c, String r) {
		super();
		this.id = id;
		this.name = n;
		this.date = c;
		this.recharge = r;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getRecharge() {
		return this.recharge;
	}
	
	 @Override
	public String toString() {
		 return "Book [id=" + id + ", name=" + name + ", date=" + date + ", recharge=" + recharge + "]";
	}
}
