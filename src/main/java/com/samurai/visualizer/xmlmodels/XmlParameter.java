package com.samurai.visualizer.xmlmodels;

import javax.xml.bind.annotation.*;

public class XmlParameter {
	@XmlAttribute
	public String name;
	@XmlValue
	public String value;
}
