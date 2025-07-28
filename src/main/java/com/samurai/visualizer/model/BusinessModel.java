package com.samurai.visualizer.model;

import com.samurai.visualizer.domain.*;

import java.util.*;

public class BusinessModel implements IModel {
	private boolean isConfigured = false;
	
	private List<PossibleAttribute> possibleAttrs;
	
	private String objectName;
	private List<MainObject> mainObjects;
	private int refObjectNumber;

	@Override
	public List<MainObject> getObjects() {
		return Collections.unmodifiableList(mainObjects);
	}

	@Override
	public MainObject getReferenceObject() {
		return mainObjects.get(refObjectNumber);
	}

	@Override
	public void configure(String objectName, Collection<MainObject> objects, int referenceObjectNumber) {
		this.objectName = objectName;
		this.mainObjects = List.copyOf(objects);
		this.refObjectNumber = referenceObjectNumber;
		
		this.possibleAttrs = calcPossibleAttributes();
		
		isConfigured = true;
	}

	@Override
	public boolean isConfigured() {
		return isConfigured;
	}
	
	@Override
	public List<PossibleAttribute> getPossibleAttributes(){
		return possibleAttrs;
	}

	@Override
	public void setNonComparableAttributes(List<PossibleAttribute> value) {
		for(var attr: value){
			attr.type = PossibleAttributeType.NonComparable;
		}
	}	
	
	@Override
	public String makeHtmlTable() {
		var htmlMaker = new HtmlMaker(
			objectName, mainObjects, refObjectNumber, possibleAttrs
		);
		
		return htmlMaker.make();
	}
	
	private List<PossibleAttribute> calcPossibleAttributes(){
		var result = new ArrayList<PossibleAttribute>();
		
		var allGroups = getObjects()
			.stream()
			.filter(item -> !item.hasError())
			.map(item -> item.getAttributeGroups())
			.flatMap(item -> item.stream())
			.toList();
		
		for(var group: allGroups){
			for (var attr: group.getAttributes()){
				var attrName = attr.getName();
				var groupName = group.getName();
				
				if (result.stream().noneMatch(item -> 
					item.name.equals(attrName) 
					&& item.includingGroup.equals(groupName))
				){
					PossibleAttribute possibleAttr;
					if (refObjContainsAttr(groupName, attrName))
						possibleAttr = new PossibleAttribute(attrName, groupName);
					else
						possibleAttr = new PossibleAttribute(attrName, groupName, PossibleAttributeType.MissedInRefObj);
					
					result.add(possibleAttr);
				}
			}
		}
		
		return result;
	}
	
	private boolean refObjContainsAttr(String groupName, String attrName){
		var groupOpt = getReferenceObject().getAttributeGroups()
			.stream()
			.filter(item -> item.getName().equals(groupName) )
			.findFirst();
		
		if (groupOpt.isEmpty())
			return false;
		
		var group = groupOpt.get();
		
		var targetAttrOpt = group.getAttributes()
			.stream()
			.filter(item -> item.getName().equals(attrName))
			.findFirst();
		
		return targetAttrOpt.isPresent();
	}
}