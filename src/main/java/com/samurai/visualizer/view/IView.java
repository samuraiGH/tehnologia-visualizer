package com.samurai.visualizer.view;

import com.samurai.visualizer.domain.*;
import java.util.*;
import java.util.function.*;

public interface IView {
	void start(String defaultLinkTamplate);
	
	void showPossibleAttributes(List<PossibleAttribute> attrGroups);
	
	void setStartInformationFilledHandler(Consumer<StartInformationFilledEventArgs> handler);
	void setNonComparableAttributesFilledHandler(Consumer<List<Integer>> handler);
	void setProcessingRequestedHandler(Runnable handler);
	
	void setXmlFilesPassedHandler(Consumer<XmlFilesPassedEventArgs> handler);
	
	void showError(String msg);
	void showFileLocation(String path);
}
