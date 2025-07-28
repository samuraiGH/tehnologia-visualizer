package com.samurai.visualizer.xmlmodels;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="NODE")
public class XmlNode {
	@XmlAttribute(name="ID")
	public int id;
	@XmlAttribute(name="NAME")
	public String name;
	@XmlElement(name="ROW")
	public XmlRow row;
	@XmlElement(name="ERROR")
	public String error;
}
