package com.samurai.visualizer.domain;

public class PossibleAttribute{
	public String name;
	public String includingGroup;
	public PossibleAttributeType type = PossibleAttributeType.Comparable;
	
	public PossibleAttribute(String name, String includingGroup){
		this.name = name;
		this.includingGroup = includingGroup;
	}

	public PossibleAttribute(String name, String includingGroup, PossibleAttributeType type){
		this.name = name;
		this.includingGroup = includingGroup;
		this.type = type;
	}
}
