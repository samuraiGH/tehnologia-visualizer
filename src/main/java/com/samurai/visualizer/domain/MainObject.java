package com.samurai.visualizer.domain;

import java.util.*;

public class MainObject {
	private final int id;
	private final String name;
	private final String link;
	private final List<AttributeGroup> attrGpoups;
	private final String error;
	
	public MainObject(int id, String name, String link, List<AttributeGroup> groups){
		this.id = id;
		this.name = name;
		this.link = link;
		attrGpoups = groups;
		error = null;
	}
	
	public MainObject(int id, String name, String link, String error){
		this.id = id;
		this.name = name;
		this.link = link;
		this.error = error;
		attrGpoups = null;
	}
	
	public boolean hasError(){
		return error != null;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getLink(){
		return link;
	}
	
	public String getError(){
		return error;
	}
	
	public List<AttributeGroup> getAttributeGroups(){
		return attrGpoups;
	}
}
