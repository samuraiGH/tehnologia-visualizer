package com.samurai.visualizer.domain;

import java.util.*;

public class AttributeGroup {
	private final String name;
	private final List<Attribute> attrs;
	
	public AttributeGroup(String groupName, List<Attribute> attrs){
		this.name = groupName;
		this.attrs = attrs;
	}
	
	public String getName(){
		return name;
	}
	
	public List<Attribute> getAttributes(){
		return attrs;
	} 
}
