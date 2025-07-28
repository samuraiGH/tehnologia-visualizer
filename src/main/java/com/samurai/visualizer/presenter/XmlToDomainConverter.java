package com.samurai.visualizer.presenter;

import com.samurai.visualizer.domain.*;
import com.samurai.visualizer.xmlmodels.*;
import java.util.regex.Pattern;
import javax.xml.xpath.*;
import org.w3c.dom.*;

class XmlToDomainConverter {
	private final XmlNode xmlNode;
	private final Document doc;
	private final String linkTemplate;
	
	public XmlToDomainConverter(XmlNode node, Document doc, String linkTemplate){
		this.xmlNode = node;
		this.doc = doc;
		this.linkTemplate = linkTemplate;
	}
	
	public MainObject convert(){
		return convertNode();
	}
	
	private MainObject convertNode(){
		var link = linkTemplate;
		
		var regex = Pattern.compile("(?<=<<)[^<]+(?=>>)");
		
		var matcher = regex.matcher(link);
		while (matcher.find()){
			try {
				var xpathString = link.substring(matcher.start(), matcher.end());
				
				var xPath = XPathFactory.newInstance().newXPath().compile(xpathString);
				
				var result = xPath.evaluate(doc, XPathConstants.STRING);
				
				link = link.substring(0, matcher.start()-2)
					+ result
					+ link.substring(matcher.end()+2);
				
				matcher = regex.matcher(link);
			} catch (XPathExpressionException ex) {
				return null;
			}
		}
		
		if (xmlNode.error != null)
			return new MainObject(xmlNode.id, xmlNode.name, link, xmlNode.error);
		
		var domainGroups = xmlNode.row.groups
			.stream()
			.map( item -> convertGroup(item) )
			.toList();
		
		return new MainObject(xmlNode.id, xmlNode.name, link, domainGroups);
	}
	
	private AttributeGroup convertGroup(XmlAttributeGroup xmlGroup){
		var attrs = xmlGroup.attributes
			.stream()
			.map( item -> convertAttr(item) )
			.toList();
		
		return new AttributeGroup(xmlGroup.name, attrs);
	}
	
	private Attribute convertAttr(XmlParameter xmlParam){
		return new Attribute(xmlParam.name, xmlParam.value);
	}
}
