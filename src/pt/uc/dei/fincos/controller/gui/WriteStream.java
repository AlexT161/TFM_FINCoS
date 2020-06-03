package pt.uc.dei.fincos.controller.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pt.uc.dei.fincos.basic.Attribute;
import pt.uc.dei.fincos.basic.Datatype;
import pt.uc.dei.fincos.basic.EventType;
import pt.uc.dei.fincos.basic.Globals;
import pt.uc.dei.fincos.controller.ConfigurationParser;


//Create or Update Streams
public class WriteStream {
	
	/** Path for the file containing the Streams. */
    public static final String STREAM_SET_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Prueba_set.xml";
    
    /**
     * Update an Event Type with new attributes.
     *
     * @param udType 	the Event to modify
     *
     */
    public static void updateEventType(EventType oldType,EventType newType) throws ParserConfigurationException, TransformerException, IOException, SAXException {
		File f = new File(STREAM_SET_FILE);
        if (!f.exists()) {
            createEmptyFile(STREAM_SET_FILE);
        }
		HashMap<String, EventType> list = loadStreams();
    	if (oldType!=null) {
    		list.put(newType.getName(), newType);
            saveToFile(list, STREAM_SET_FILE);
            JOptionPane.showMessageDialog(null, "Stream correctly updated.", "Update", JOptionPane.INFORMATION_MESSAGE);
    	} else {
    		list.remove(newType.getName(), newType);
            saveToFile(list, STREAM_SET_FILE);
            JOptionPane.showMessageDialog(null, "¡Stream deleted!", "Delete", JOptionPane.WARNING_MESSAGE);			
    	}
	}

    /**
     * Add a new Event Type to the Stream_Set file.
     *
     * @param newType 	the Event to add
     *
     */
	public static void addEventType(EventType newType) throws Exception {		
		File f = new File(STREAM_SET_FILE);
        if (!f.exists()) {
            createEmptyFile(STREAM_SET_FILE);
        }
		HashMap<String, EventType> list = loadStreams();
		list.put(newType.getName(), newType);
        saveToFile(list, STREAM_SET_FILE);
		JOptionPane.showMessageDialog(null, "¡Stream created!", "Create", JOptionPane.INFORMATION_MESSAGE);		
		System.out.println("WriteStreams:NewType: "+newType);
	}
		
	public static HashMap<String, EventType> loadStreams() throws ParserConfigurationException, SAXException, IOException {
		HashMap<String, EventType> streams = new HashMap<String,EventType>(1);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        File streamFile = new File(STREAM_SET_FILE);
        Document doc = builder.parse(streamFile);
        Element xmlFileRoot = doc.getDocumentElement();
        if (xmlFileRoot != null) {
            Element streamList = (Element) xmlFileRoot.
                    getElementsByTagName("common").item(0);
            NodeList stream = streamList.getElementsByTagName("event-type");
            Element element;
            String name;
//            String typeStr;
            for (int i = 0; i < stream.getLength(); i++) {
            	element = (Element) stream.item(i);
                name = element.getAttribute("name");
//                typeStr = element.getAttribute("type");
                Element propList = (Element) element.getElementsByTagName("objectarray").item(0);
                NodeList properties = propList.getElementsByTagName("objectarray-property");
                ArrayList<Attribute> columns = new ArrayList<Attribute>(properties.getLength());
                for (int j = 0; j < properties.getLength(); j++) {
                    Element prop = (Element) properties.item(j);
                    String propName = prop.getAttribute("name");
                    String type = prop.getAttribute("class");
                    Datatype dataType = null;
                    if (type.equals("INTEGER")) {
                        dataType = Datatype.INTEGER;
                    } else if (type.equals("LONG")) {
                        dataType = Datatype.LONG;
                    } else if (type.equals("FLOAT")) {
                        dataType = Datatype.FLOAT;
                    } else if (type.equals("DOUBLE")) {
                        dataType = Datatype.DOUBLE;
                    } else if (type.equals("TEXT")) {
                        dataType = Datatype.TEXT;
                    } else if (type.equals("BOOLEAN")) {
                        dataType = Datatype.BOOLEAN;
                    }                    
                    Attribute att = new Attribute(dataType, propName);	
					columns.add(att);
                }
                Attribute[] atts = new Attribute[columns.size()];
                atts = columns.toArray(atts);                
                EventType event = new EventType(name, atts);
                streams.put(name, event);
            }      
        }
		return streams;
	}
    
    /**
     * Saves a list of Streams into the Stream_Set XML file.
     *
     * @param list  	the list of Streams
     * @param filePath  the path of XML file
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws TransformerException 
     *
     */
	public static void saveToFile(HashMap<String, EventType> list, String filePath) 
	throws IOException, ParserConfigurationException, TransformerException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document docu = builder.newDocument();
		docu.setXmlVersion("1.0");
		
		Element root = docu.createElement("espercon-figuration");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xmlns", "http://www.espertech.com/schema/esper");
		root.setAttribute("xsi:noNamespaceSchemaLocation", "esper-configuration-8-0.xsd");
		
		Element common = docu.createElement("common");
		for(String name : list.keySet()) {
			Element eventType = docu.createElement("event-type");			
			eventType.setAttribute("name", name);
			eventType.setAttribute("type", "Input");
			Element element = docu.createElement("objectarray");
			EventType event = list.get(name);
			Attribute[] atributes = event.getAttributes();
			for(int i=0; i < event.getAttributeCount();i++) {
				Element property = docu.createElement("objectarray-property");
				property.setAttribute("name", event.getAttributesNames()[i]);
				property.setAttribute("class", atributes[i].getType().toString());		
				element.appendChild(property);
				}
			eventType.appendChild(element);
			common.appendChild(eventType);
		}
		Element variantStream = docu.createElement("variant-stream");
		variantStream.setAttribute("name", "MyVariantStream");
		Element variantEvent = docu.createElement("variant-event-type");
		variantEvent.setAttribute("name", "salida");
		variantStream.appendChild(variantEvent);
		common.appendChild(variantStream);
		
		root.appendChild(common);
		
		Element compiler = docu.createElement("compiler");
		root.appendChild(compiler);
		
		Element runtime = docu.createElement("runtime");
		root.appendChild(runtime);
		
		docu.appendChild(root);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath), false));
        bw.write(ConfigurationParser.fromXMLDocToString(docu));
        bw.flush();
        bw.close();
		
	}
	
	/**
     * Creates a empty stream_Set file.
     *
     * @param filePath  the path of XML file
     *
     * @throws ParserConfigurationException     if an error occurs while
     *                                          creating the XML document
     * @throws TransformerException             if an error occurs while trying
     *                                          to transform the XML into text
     * @throws IOException                      if an error occurs while trying
     *                                          to open/write the Stream_Set file
     */
    public static void createEmptyFile(String filePath)
    throws ParserConfigurationException, TransformerException, IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            f.createNewFile();
        }
        saveToFile(null, filePath);
    }
    
}
