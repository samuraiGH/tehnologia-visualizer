package com.samurai.visualizer.view;

import java.util.*;

public class StartInformationFilledEventArgs {
	public String objectName;
	public List<String> xmlFiles;
	public int referenceObjectNumber;
	public String linkTemplate;
	
	public StartInformationFilledEventArgs(
		String objectName,
		List<String> xmlFiles,
		int referenceObjectNumber,
		String linkTemplate
	){
		this.objectName = objectName;
		this.xmlFiles = xmlFiles;
		this.referenceObjectNumber = referenceObjectNumber;
		this.linkTemplate = linkTemplate;
	}
}
