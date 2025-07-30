package com.samurai.visualizer.view;

import com.samurai.visualizer.domain.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class ConsoleView implements IView {
	private Consumer<StartInformationFilledEventArgs> startInformationFilledHandler;
	private Consumer<List<Integer>> nonComparableAttributesFilledHandler;
	private Runnable processingRequestedHandler;
	private Consumer<XmlFilesPassedEventArgs> xmlFilesPassedHandler;
	
	@Override
	public void start(String defaultLinkTamplate) {
		var in = new Scanner(System.in);

		System.out.print("Имя объекта для сравнения: ");
		var name = in.nextLine();
		
		System.out.print("\n");
		
		var xmlFiles = getXmlFiles(in);
		
		System.out.print("\n");
		
		System.out.print("Найдены следюущие файлы:\n");
		
		for(var i = 0; i < xmlFiles.size(); i++)
			System.out.print(i+1 + ": " + xmlFiles.get(i) + "\n");
		
		System.out.print("\n");

		var numberOfReference = getRefObjNumber(in, xmlFiles.size());
		
		System.out.print("\n");
		
		System.out.print("Шаблон гиперссылки\n");
		System.out.print("Или Enter чтобы оставить по умолчанию\n");
		System.out.print(defaultLinkTamplate+"\n");
		
		var linkTemplate = in.nextLine();
		if (linkTemplate.isEmpty())
			linkTemplate = defaultLinkTamplate;
		
		System.out.print("\n");
		
		if (startInformationFilledHandler != null){
			var args = new StartInformationFilledEventArgs(name, xmlFiles, numberOfReference, linkTemplate);
			startInformationFilledHandler.accept(args);
		}
		
		var nonComparableAttrs = getNonComparableAttrs(in);
		
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
		System.out.print(msg+"\n\n");
	}
	
	@Override
	public void showFileLocation(String path) {
		System.out.print("Результат расположен в: "+path);
	}
	
	private List<String> getXmlFiles(Scanner in){
		var result = new ArrayList<String>();
		
		while (true) {			
			System.out.print("Путь к папке с xml документами:\n");
			var dirPath = in.nextLine();
			
			var dir = new File(dirPath);
			
			if (!dir.isDirectory()){
				System.out.print("Путь не является папкой. Повторите ввод\n\n");
				continue;
			}
			
			if (!dir.exists()){
				System.out.print("Папка не существует. Повторите ввод\n\n");
				continue;
			}
			
			for(var file: dir.listFiles(path -> path.getName().endsWith(".xml")))
				result.add(file.getAbsolutePath());
			
			if (xmlFilesPassedHandler != null){
				var eventArgs = new XmlFilesPassedEventArgs(result);
				xmlFilesPassedHandler.accept(eventArgs);
				
				if (!eventArgs.isValid)
					continue;
			}
				
			return result;
		}
	}
	
	private int getRefObjNumber(Scanner in, int maxNumber){
		while(true){
			System.out.print("Номер эталонного файла: ");
			
			var temp = in.nextLine();
			
			int value;
			
			try {
				value = Integer.parseInt(temp);
			}
			catch(NumberFormatException ex){
				System.out.print("Неверный формат. Повторите ввод\n");
				continue;
			}
			
			if (value > maxNumber){
				System.out.print("Ввод превышает количество файлов. Повторите ввод\n");
				continue;
			}
			
			return value;
		}
	}
	
	private List<Integer> getNonComparableAttrs(Scanner in){
		System.out.print("Номера атрибутов, которые не будут участвовать в сравнении, через пробел:\n");
		
		while(true){
			try{
				var nonComparableAttrs = Stream.of( in.nextLine().split(" ") )
					.map(item -> Integer.valueOf(item))
					.toList();
				
				return nonComparableAttrs;
			}
			catch(NumberFormatException ex){
				System.out.print("Неверный формат. Повторите ввод\n");
			}
		}
	}
}
