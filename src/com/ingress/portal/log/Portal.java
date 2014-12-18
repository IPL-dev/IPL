package com.ingress.portal.log;

import java.util.ArrayList;

public class Portal {

	private int id;
	private String name;
	private String date;
	private String time;
	private String recharge;
	private String decayed;
	private Double[] pos = {500.0, 500.0};
	
	public Portal(String n, String c, String r, double lat, double lon) {
		super();
		this.name = n;
		this.date = c;
		this.recharge = r;
		this.pos[0] = lat;
		this.pos[1] = lon;
	}
	
	public Portal(String n, String c, String r, String t, String d, double lat, double lon) {
		super();
		this.name = n;
		this.date = c;
		this.time = t;
		this.recharge = r;
		this.decayed = d;
		this.pos[0] = lat;
		this.pos[1] = lon;
	}
	
	public Portal(int id, String n, String c, String r, String t, String d, double lat, double lon) {
		super();
		this.id = id;
		this.name = n;
		this.date = c;
		this.time = t;
		this.recharge = r;
		this.decayed = d;
		this.pos[0] = lat;
		this.pos[1] = lon;
	}
	
	public Portal(int id, String n, String c, String r, double lat, double lon) {
		super();
		this.id = id;
		this.name = n;
		this.date = c;
		this.recharge = r;
		this.pos[0] = lat;
		this.pos[1] = lon;
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
	
	public String getTime() {
		return this.time;
	}
	
	public String getRecharge() {
		return this.recharge;
	}
	
	public String getDecayed() {
		return this.decayed;
	}
	
	public Double[] getPos(){
		return this.pos;
	}
	
	 @Override
	public String toString() {
		 return "Book [id=" + id + ", name=" + name + ", date=" + date + ", recharge=" + recharge + "]";
	}
	 
	 public ArrayList<String> getChildren() {
		 ArrayList<String> temp = new ArrayList<String>();
		 temp.add(date);
		 temp.add(time);
		 temp.add(recharge);
		 temp.add(decayed);
		 return temp;
	}
}
