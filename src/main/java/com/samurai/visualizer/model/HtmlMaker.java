package com.samurai.visualizer.model;

import com.samurai.visualizer.domain.*;
import java.io.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class HtmlMaker {
	private List<PossibleAttribute> possibleAttrs;
	
	private String objectName;
	private List<MainObject> mainObjects;
	private MainObject refObject;
	
	private Document doc;
	
	public HtmlMaker(
		String objectName,
		List<MainObject> mainObjects,
		int refObjectNumber,
		List<PossibleAttribute> possibleAttrs
	){
		this.objectName = objectName;
		this.mainObjects = mainObjects;
		this.refObject = mainObjects.get(refObjectNumber);
		this.possibleAttrs = possibleAttrs;
		
		try {	
			var template = getClass().getResourceAsStream("/template.html");
			doc = Jsoup.parse(template, "UTF-8", "local");
		} catch (IOException ex) {
			throw null;
		}
	}
	
	public String make(){
		makeHeader();

		var tableNode = doc.selectXpath("/html/body/table").first();

		makeTableCaption(tableNode);
		makeTableHeader(tableNode);

		var tableBody = tableNode.appendElement("tbody");

		makeTableBody(tableBody);

		return doc.html();
	}
	
	private void makeHeader(){
		var styleNode = doc.selectXpath("/html/head/style").first();
		var scriptNode = doc.selectXpath("/html/head/script").first();
		
		var stream = getClass().getResourceAsStream("/style.css");
		
		var in = new Scanner(stream);
		
		while(in.hasNext())
			styleNode.appendText(in.nextLine());
		
		in.close();
		
		stream = getClass().getResourceAsStream("/scripts.js");
		
		in = new Scanner(stream);
		
		var script = new StringBuilder();
		
		while(in.hasNext()){
			script.append(in.nextLine());
			script.append("\n");
		}
		
		scriptNode.text(script.toString());
		
		in.close();
	}
	
	private void makeTableCaption(Element tableNode){
		var tableCaptionNode = tableNode.selectXpath("caption").first();
		tableCaptionNode.text(objectName);
	}
	
	private void makeTableHeader(Element tableNode){
		var tableHead = tableNode.appendElement("thead");
		var tableRow = tableHead.appendElement("tr");

		tableRow.appendElement("th")
			.addClass("diff_table_header");

		for(var obj: mainObjects){
			var colHeader = tableRow.appendElement("th");
			
			if (obj.hasError()){
				colHeader.appendElement("p")
					.text("❌")
					.addClass("clickable")
					.attr("full_value", obj.getError())
					.attr("onclick", "openExpanded(this)");
				
				colHeader.addClass("diff_table_header_error");
			}
			else
				colHeader.addClass("diff_table_header");
			
			var link = colHeader.appendElement("a");

			var headerString = "[" + obj.getId() + "] " + obj.getName();

			link.attributes().add("href", obj.getLink());
			link.text(headerString);
		}
	}
	
	private void makeTableBody(Element tableBody){
		var possibleGroups = new LinkedHashSet<String>(
			possibleAttrs.stream()
				.map(item -> item.includingGroup)
				.toList()
		);
		
		int index = 1;
		
		for (var group: possibleGroups){
			var headerDataMap = makeGroupHeader(tableBody, group);
			
			var diffMap = new HashMap<MainObject, Integer>();
			for (var mainObj: mainObjects)
				diffMap.put(mainObj, 0);
			
			var allAttrsInGroup = possibleAttrs.stream()
				.filter( item -> item.includingGroup.equals(group) )
				.sorted( (o1, o2)-> o1.type.compareTo(o2.type) );
			
			for(var attr: allAttrsInGroup.toList()){
				makeRow(tableBody, attr, diffMap);
			}
			
			fillGroupHeader(headerDataMap, diffMap);
			
			if (index != possibleGroups.size()){
				makeSeparatorRow(tableBody);
			}
			
			index++;
		}
	}
	
	private HashMap<MainObject, Element> makeGroupHeader(Element tableBody, String groupName){
		var result = new HashMap<MainObject, Element>();
		
		var tableRow = tableBody.appendElement("tr");
		tableRow.appendElement("td")
			.text(groupName)
			.addClass("group_name_header");
		
		for(var mainObj: mainObjects){
			var tableData = tableRow.appendElement("td");
			
			if (mainObj == refObject){
				tableData.text("EXAMPLE OBJECT");
				tableData.addClass("reference_header");
			}
			
			if (mainObj.hasError())
				tableData.addClass("error");
			
			result.put(mainObj, tableData);
		}
		
		return result;
	}
	
	private void makeRow(Element tableBody, PossibleAttribute attrInfo, HashMap<MainObject, Integer> diffMap){
		var tableRow = tableBody.appendElement("tr");
		
		var tableData = tableRow.appendElement("td").text(attrInfo.name);
		
		switch (attrInfo.type){
			case Comparable -> tableData.addClass("comparable_attr_name");
			case NonComparable -> tableData.addClass("noncomparable_attr_name");
			case MissedInRefObj -> tableData.addClass("missed_attr_name");
		}
		
		for(var mainObj: mainObjects){
			if (mainObj.hasError()){
				makeErrorCell(tableRow);
				continue;
			}
			
			var attrValue = getAttrValueIfExists(attrInfo, mainObj);

			switch (attrInfo.type) {
				case NonComparable -> makeNonComparableCell(tableRow, attrValue);
				case MissedInRefObj -> makeMissingInRefCell(tableRow, attrValue);
				case Comparable -> {
					if (mainObj == refObject)
						makeRefCell(tableRow, attrValue.get());
					
					else{
						var refValue = getAttrValueIfExists(attrInfo, refObject);
						var incDiff = makeComparableCell(tableRow, refValue.get(), attrValue);
						
						if (incDiff)
							diffMap.replace( mainObj, diffMap.get(mainObj)+1 );
					}
				}
				default -> throw new AssertionError();
			}
		}
		
	}
	
	private void makeErrorCell(Element tableRow){
		tableRow.appendElement("td")
			.addClass("error");
	}
	
	private void makeNonComparableCell(Element tableRow, Optional<String> attrValue){
		var tableDataText = processAttrValue(attrValue);

		tableRow.appendElement("td").text(tableDataText);
	}
	
	private void makeMissingInRefCell(Element tableRow, Optional<String> attrValue){
		var tableDataText = processAttrValue(attrValue);
		
		var tableData = tableRow.appendElement("td");
		tableData.text(tableDataText);
		
		if (attrValue.isEmpty())
			tableData.addClass("missed_in_ref");
		else
			tableData.addClass("missed_in_ref_neq");
	}
	
	private void makeRefCell(Element tableRow, String attrValue){
		var tableDataText = processAttrValue(attrValue);
		
		var isReducted = tableDataText.length() < attrValue.length();
		
		var tableData = tableRow.appendElement("td")
			.text(tableDataText)
			.addClass("reference");
		
		if (isReducted)
			setOpenReductedEvent(tableData, attrValue);
	}
	
	private boolean makeComparableCell(Element tableRow, String refValue, Optional<String> targetValue){
		if (targetValue.isEmpty()){
			tableRow.appendElement("td")
				.text("<MISSED>")
				.addClass("missed");
			return false;
		}
		
		if (targetValue.get().equals(refValue)){
			makeEqualCell(tableRow);
			return false;
		}
		else{
			makeNotEqualCell(tableRow, refValue, targetValue);
			return true;
		}
	}
	
	private void makeEqualCell(Element tableRow){
		tableRow.appendElement("td").attr("class", "eq");
		
	}
	
	private void makeNotEqualCell(Element tableRow, String refValue, Optional<String> targetValue){
		var originalLength = targetValue.isPresent() ? targetValue.get().length() : 0;
		
		var tableDataText = processAttrValue(targetValue);
		
		var isReducted = tableDataText.length() < originalLength;
		
		var tableData = tableRow.appendElement("td")
			.text(tableDataText)
			.addClass("neq");
		
		if (isReducted){
			var fullValue = new StringBuilder(targetValue.get());
			
			var diffPos = findFirstDiffPos(refValue, fullValue.toString());
			fullValue.insert(diffPos, "🚩");
			
			setOpenReductedEvent(tableData, fullValue.toString());
		}
	}
	
	private void setOpenReductedEvent(Element cell, String fullValue){
		cell.addClass("clickable");
		
		cell.attr("onclick", "openExpanded(this)");
		cell.attr("full_value", fullValue);
	}
	
	private void fillGroupHeader(
		HashMap<MainObject, Element> headerMap,
		HashMap<MainObject, Integer> diffMap
	){
		for(var mainObj: mainObjects){
			if (mainObj == refObject || mainObj.hasError())
				continue;
			
			var tableData = headerMap.get(mainObj);
			var diffCount = diffMap.get(mainObj);
			
			tableData.text("{" + diffCount + "} diff");
			
			if (diffCount == 0)
				tableData.addClass("no_diff_header");
			else
				tableData.addClass("has_diff_header");
		}
	}
	
	private void makeSeparatorRow(Element tableBody){
		var tableRow = tableBody.appendElement("tr");
		
		tableRow.appendElement("td").addClass("sep");
		
		for(var mainObj: mainObjects){
			var tableData = tableRow.appendElement("td").addClass("sep");
			
			if (mainObj.hasError())
				tableData.addClass("error");
		}
	}
	
	private Optional<String> getAttrValueIfExists(PossibleAttribute attrInfo, MainObject obj){
		var groupOpt = obj.getAttributeGroups()
			.stream()
			.filter(item -> item.getName().equals(attrInfo.includingGroup) )
			.findFirst();
		
		if (groupOpt.isEmpty())
			return Optional.empty();
		
		var group = groupOpt.get();
		
		var targetAttrOpt = group.getAttributes()
			.stream()
			.filter(item -> item.getName().equals(attrInfo.name))
			.findFirst();
		
		if (targetAttrOpt.isEmpty())
			return Optional.empty();
		
		return Optional.of(targetAttrOpt.get().getValue());
	}
	
	private String processAttrValue(Optional<String> valueOpt){		
		if (valueOpt.isEmpty())
			return "<MISSED>";

		return processAttrValue(valueOpt.get());
	}
	
	private String processAttrValue(String value){		
		if (value.equals(""))
			return "<EMPTY>";
		
		return Reduce(value);
	}
	
	private String Reduce(String value){
		var largeValueSign = 100;
		var n = 23;
		var k = 27;
		
		var isLargeValue = value.length() > largeValueSign;
		
		if (isLargeValue)
			value = value.substring(0, n - 1) 
				+ "{СОКРАЩЕНО}"
				+ value.substring(value.length() - k - 1);

		return value;
	}
	
	private int findFirstDiffPos(String s1, String s2){
		var minLength = Math.min(s1.length(), s2.length());
		
		for (var i = 0; i < minLength; i++){
			if (s1.charAt(i) != s2.charAt(i))
				return i;
		}
		
		if (s1.length() != s2.length())
			return minLength;
		
		return -1;
	}
}