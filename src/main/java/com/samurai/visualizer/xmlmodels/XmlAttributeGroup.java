package com.samurai.visualizer.xmlmodels;

import java.util.*;
import javax.xml.bind.annotation.*;

public class XmlAttributeGroup {
	@XmlAttribute
	public String name;
	
	@XmlElement(name = "C")
	public List<XmlParameter> attributes;
}
