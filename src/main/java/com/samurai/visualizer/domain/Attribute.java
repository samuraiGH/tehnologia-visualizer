package com.samurai.visualizer.domain;

public class Attribute {
	private final String name;
	private final String value;
	private boolean isCompared = true;
	private AttributeGroup includingGroup;
	
	public Attribute(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	public String getName(){
		return name;
	}
	
	public String getValue(){
		return value;
	}
	
	public Boolean getIsCompared(){
		return isCompared;
	}
	
	public void setIsCompares(Boolean value){
		isCompared = value;
	}
	
	public AttributeGroup getIncludingGroup(){
		return includingGroup;
	}
}
