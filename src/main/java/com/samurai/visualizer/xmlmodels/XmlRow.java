package com.samurai.visualizer.xmlmodels;

import java.util.*;
import javax.xml.bind.annotation.*;

public class XmlRow {
	@XmlElement(name = "T")
	public List<XmlAttributeGroup> groups;
}
