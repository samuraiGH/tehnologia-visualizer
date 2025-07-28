package com.samurai.visualizer.model;

import com.samurai.visualizer.domain.*;
import java.util.*;

public interface IModel {
	void configure(
		String objectName,
		Collection<MainObject> objects,
		int referenceObjectNumber
	);
	
	boolean isConfigured();
	
	List<MainObject> getObjects();

	MainObject getReferenceObject();
	
	List<PossibleAttribute> getPossibleAttributes();
	
	void setNonComparableAttributes(List<PossibleAttribute> value);

	
	String makeHtmlTable();
}
