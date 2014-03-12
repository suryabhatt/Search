package com.example.googlemystuff.beans;

import android.graphics.Bitmap;


public class Item {
	
	public Integer id;
	
	public String name;
	
	public Bitmap image;
	
	public String location;
			
	
	public Item(String name, Bitmap image, String location) {
		super();
		
		this.name = name;
		this.image = image;
		this.location = location;
	}

	public Item() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
		
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getName());
		sb.append(getLocation());
		return sb.toString();
	}
}
