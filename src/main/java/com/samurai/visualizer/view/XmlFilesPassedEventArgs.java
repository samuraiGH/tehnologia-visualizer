package com.samurai.visualizer.view;

import java.util.*;

public class XmlFilesPassedEventArgs {
	public List<String> xmlFiles;
	public boolean isValid = true;
	
	public XmlFilesPassedEventArgs(List<String> xmlFiles){
		this.xmlFiles = xmlFiles;
	}
}
