package com.samurai.visualizer.presenter;

import com.samurai.visualizer.domain.*;
import com.samurai.visualizer.xmlmodels.*;
import com.samurai.visualizer.model.*;
import com.samurai.visualizer.view.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.*;

public class Presenter {
	private final IModel model;
	private final IView view;
	
	private final String defaultLinkTemplate = "ba://node=<<//NODE/@ID>>&ei=<<//NODE/ROW/T/C[@name='id_entity_instance']>>";
	
	public Presenter(IModel model, IView view){
		this.model = model;
		this.view = view;
		
		view.setStartInformationFilledHandler( ea -> onStartInformationFilled(ea) );
		view.setNonComparableAttributesFilledHandler( attrs -> onNonComparableAttributesFilled(attrs) );
		view.setProcessingRequestedHandler(()-> onProcessingRequested());
		view.setXmlFilesPassedHandler( ea -> onXmlFilesPassed(ea) );
	}
	
	public void Start(){
		view.start(defaultLinkTemplate);
	}
	
	private MainObject buildMainObject(String path, Unmarshaller deserializer, DocumentBuilder docBuilder, String linkTemplate){
		try {
			var source = new InputSource(new FileInputStream(path));
			source.setEncoding("Cp1251");
			
			var node = (XmlNode)deserializer.unmarshal(source);
			
			source.setByteStream(new FileInputStream(path));
			
			var doc = docBuilder.parse(source);

			var converter = new XmlToDomainConverter(node, doc, linkTemplate);
			return converter.convert();	
		} 
		catch (Throwable ex) {
			throw null;
		}
	}
	
	private List<MainObject> buildMainObjects(List<String> xmlFiles, String linkTemplate){		
		try{
			var deserializer = JAXBContext
				.newInstance(XmlNode.class)
				.createUnmarshaller();
			
			var docBuilder = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder();
			
			return xmlFiles.stream()
				.map(path -> buildMainObject(path, deserializer, docBuilder, linkTemplate))
				.toList();
		}
		catch(Throwable e){
			throw null;
		}	
	}
	
	private void onStartInformationFilled(StartInformationFilledEventArgs ea){
		var mainObjects = buildMainObjects(ea.xmlFiles, ea.linkTemplate);
		
		model.configure(ea.objectName, mainObjects, ea.referenceObjectNumber - 1);
		
		view.showPossibleAttributes( model.getPossibleAttributes() );
	}
	
	private void onNonComparableAttributesFilled(List<Integer> attrNumbers){
		var attrs = attrNumbers.stream()
			.map( i-> model.getPossibleAttributes().get(i-1) )
			.toList();
		
		model.setNonComparableAttributes(attrs);
	}
	
	private void onProcessingRequested(){
		try {
			var html = model.makeHtmlTable();
			
			Files.writeString(Path.of("result.html"), html, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ex) {
			System.getLogger(Presenter.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
		}
	}
	
	private void onXmlFilesPassed(XmlFilesPassedEventArgs ea){
		try {
			var inputSource = new StreamSource(getClass().getResourceAsStream("/NodeSchema.xsd"));
			
			var validator = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
				.newSchema(inputSource)
				.newValidator();
			
			for (var filePath: ea.xmlFiles){
				inputSource = new StreamSource(
					new InputStreamReader(
						new FileInputStream(filePath), Charset.forName("Cp1251")
					)
				);
				
				try{
					validator.validate(inputSource);
				}
				catch(SAXException ex){
					var msg = String.format("Файл '%s' не соответствует схеме\nИсправьте файл и повторите ввод", filePath);
					
					view.showError(msg);
					ea.isValid = false;
				}
				
			}
		} catch (Throwable ex) {
			throw null;
		}
	}
}
