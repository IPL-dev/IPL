package com.ingress.portal.log;

public class Portal {

	private int id;
	private String name;
	private String date;
	private String recharge;
	private String[] pos = {"", ""};
	
	public Portal(String n, String c, String r, double lat, double lon) {
		super();
		this.name = n;
		this.date = c;
		this.recharge = r;
		this.pos[0] = String.valueOf(lat);
		this.pos[1] = String.valueOf(lon);
	}
	
	public Portal(int id, String n, String c, String r, double lat, double lon) {
		super();
		this.id = id;
		this.name = n;
		this.date = c;
		this.recharge = r;
		this.pos[0] = String.valueOf(lat);
		this.pos[1] = String.valueOf(lon);
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
	
	public String[] getPos(){
		return this.pos;
	}
	
	 @Override
	public String toString() {
		 return "Book [id=" + id + ", name=" + name + ", date=" + date + ", recharge=" + recharge + "]";
	}
}
