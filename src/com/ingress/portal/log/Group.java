package com.ingress.portal.log;

import java.util.ArrayList;

public class Group {

	private String string;
	private ArrayList<String> children = new ArrayList<String>();

	public Group(String string) {
		this.string = string;
	}
	
	public void insertChild(String name) {
		children.add(name);
		
		return;
	}

	public String getGroup() {
		return this.string;
	}
	
	public ArrayList<String> getChildren() {
		return this.children;
	}
}