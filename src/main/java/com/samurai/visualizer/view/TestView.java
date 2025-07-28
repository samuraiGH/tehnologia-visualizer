package com.samurai.visualizer.view;

import com.samurai.visualizer.domain.PossibleAttribute;
import java.io.File;
import java.util.*;
import java.util.function.*;


public class TestView implements IView{
	private Consumer<StartInformationFilledEventArgs> startInformationFilledHandler;
	private Consumer<List<Integer>> nonComparableAttributesFilledHandler;
	private Runnable processingRequestedHandler;
	private Consumer<XmlFilesPassedEventArgs> xmlFilesPassedHandler;
	
	@Override
	public void start(String defaultTemplate) {
		
		var name = "fdf";

		var dirPath = "D:\\work\\тестовое\\java\\задание\\xml";

		var dir = new File(dirPath);

		var xmlFiles = new ArrayList<String>();
		
		for(var file: dir.listFiles())
			xmlFiles.add(file.getAbsolutePath());

		var numberOfReference = 1;
		
		var linkTemplate = defaultTemplate;
		
		if (startInformationFilledHandler != null){
			var args = new StartInformationFilledEventArgs(name, xmlFiles, numberOfReference, linkTemplate);
			startInformationFilledHandler.accept(args);
		}
		
		var nonComparableAttrs = List.of(6, 7, 8);
		
		if (nonComparableAttributesFilledHandler != null){
			nonComparableAttributesFilledHandler.accept(nonComparableAttrs);
		}
		
		if (processingRequestedHandler != null){
			processingRequestedHandler.run();
		}
	}

	@Override
	public void showPossibleAttributes(List<PossibleAttribute> possibleAttrs) {
		var firstGroupName = possibleAttrs.get(0).includingGroup;
		
		var isOnlyOneGroup = possibleAttrs
			.stream()
			.allMatch( item -> item.includingGroup.equals(firstGroupName) );
		
		System.out.print("Найдены следующие атрибуты:\n");
		
		for (var i = 0; i < possibleAttrs.size(); i++){
			var attrInfo = possibleAttrs.get(i);
			
			var attrStr = isOnlyOneGroup ? attrInfo.name : attrInfo.includingGroup + "." + attrInfo.name;
			
			System.out.print(i+1 + ": " + attrStr + "\n");
		}
	}
	
	@Override
	public void setStartInformationFilledHandler(Consumer<StartInformationFilledEventArgs> handler) {
		startInformationFilledHandler = handler;
	}

	@Override
	public void setNonComparableAttributesFilledHandler(Consumer<List<Integer>> handler) {
		nonComparableAttributesFilledHandler = handler;
	}
	
	@Override
	public void setProcessingRequestedHandler(Runnable handler){
		processingRequestedHandler = handler;
	}

	@Override
	public void setXmlFilesPassedHandler(Consumer<XmlFilesPassedEventArgs> handler) {
		xmlFilesPassedHandler = handler;
	}

	@Override
	public void showError(String msg) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}
	
}
