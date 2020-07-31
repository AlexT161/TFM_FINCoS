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

/**
 * 
 * Creates or Updates Streams and set those in the Stream_Set file
 * 
 * @author John Alexander Torres
 *
 */
public class WriteStream {
	
	/** Path for the file containing the Streams. */
    public static final String STREAM_SET_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Stream_Set.xml";
	private static File streamFile;
	private static Element xmlFileRoot;
    
    /**
     * Update an Event Type with new attributes.
     *
     * @param oldType 	the Name of the Event to modify
     * @param newType	the New event with the attributes, "null" for delete
     * @param type		0 for Input, 1 for Output
     *
     */
    public static void updateEventType(String oldType, EventType newType, int type) throws ParserConfigurationException, TransformerException, IOException, SAXException {
		openFile();
    	if (newType!=null) {
    		HashMap<String[], EventType> list = updateList(oldType, newType, type);   		
            saveToFile(list, STREAM_SET_FILE);
            JOptionPane.showMessageDialog(null, "Stream correctly updated.", "Update", JOptionPane.INFORMATION_MESSAGE);
    	} else {
    		HashMap<String[], EventType> list = deleteFromList(oldType, type);
    		saveToFile(list, STREAM_SET_FILE);
            JOptionPane.showMessageDialog(null, "¡Stream deleted!", "Delete", JOptionPane.WARNING_MESSAGE);			
    	}
    	closeFile();
	}

    private static HashMap<String[], EventType> updateList(String oldType, EventType newType, int type) throws ParserConfigurationException, SAXException, IOException, TransformerException {
    	HashMap<String, EventType> inputList = loadStreams(0);
    	HashMap<String, EventType> outputList = loadStreams(1);
    	if (type==0) {
    		inputList.replace(oldType, newType);
    	} else {
    		outputList.replace(oldType, newType);
    	}
    	HashMap<String[], EventType> list = mergeLists(inputList, outputList); 	
       	return list;
	}
    
    private static HashMap<String[], EventType> deleteFromList(String oldType, int type) throws ParserConfigurationException, SAXException, IOException, TransformerException {
    	HashMap<String, EventType> inputList = loadStreams(0);
    	HashMap<String, EventType> outputList = loadStreams(1);
    	if (type==0) {
    		inputList.remove(oldType);
    	} else {
    		outputList.remove(oldType);
    	}
    	HashMap<String[], EventType> list = mergeLists(inputList, outputList); 	
    	return list;
	}
    
	private static HashMap<String[], EventType> mergeLists(HashMap<String, EventType> inputList,
			HashMap<String, EventType> outputList) {
		HashMap<String[], EventType> list = new HashMap<String[], EventType>(1);

    	for(String i : inputList.keySet()) {
    		String[] key = {i,"Input"};
    		list.put(key, inputList.get(i));
    	}
    	for(String j : outputList.keySet()) {
    		String[] key = {j,"Output"};
    		list.put(key, outputList.get(j));
    	}
		return list;
	}

	/**
     * Add a new Event Type to the Stream_Set file.
     *
     * @param newType 	the Event to add
     * @param type		the type of the Stream 0 for Input, 1 for Output
     *
     */
	public static void addEventType(EventType newType, int type) throws Exception {		
		openFile();
		HashMap<String[], EventType> list = loadStreams();		
		String typeClass = setType(type);
		String[] key = {newType.getName(),typeClass};
		list.put(key, newType);
        saveToFile(list, STREAM_SET_FILE);
		JOptionPane.showMessageDialog(null, "¡Stream created!", "Create", JOptionPane.INFORMATION_MESSAGE);
		closeFile();
	}
	
    private static void openFile() throws ParserConfigurationException, TransformerException, IOException, SAXException{
    	File f = new File(STREAM_SET_FILE);
        if (!f.exists()) {
            createEmptyFile(STREAM_SET_FILE);
        }
        open(STREAM_SET_FILE);
	}

	/**
     * Return the type of the Stream.
     *
     * @param type		the type of the Stream 0 for Input, 1 for Output
     *
     */
    private static String setType(int type) {
    	String typeClass;
		if(type == 0) {
			typeClass="Input";
		} else if (type == 1){
			typeClass="Output";
		} else {
			typeClass= "";
		}
		return typeClass;		
	}

	/**
     * Opens a XML file containing the streams.
     *
     * @param path      Path to stream file
     *
     * @throws ParserConfigurationException     if an error occurs while parsing
     *                                          the XML setup file
     * @throws SAXException                     if an error occurs while parsing
     *                                          the XML setup file
     * @throws IOException                      for disk I/O errors
     */
    public static void open(String path)
    throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        streamFile = new File(path);
        Document doc = builder.parse(streamFile);
        xmlFileRoot = doc.getDocumentElement();
    }
		
	public static boolean isFileOpen() {
		return (xmlFileRoot != null);
	}

    /**
     * Unloads parser's stream file.
     *
     */
    public static void closeFile() {
        xmlFileRoot = null;
    }
    
    /**
     * Creates a HashMap with the Streams loaded from the Stream_Set XML file.
     *
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws TransformerException
     * @return streams		HashMap with names and Events
     *
     */
	public static HashMap<String[], EventType> loadStreams() throws ParserConfigurationException, SAXException, IOException {
		HashMap<String[], EventType> streams = new HashMap<String[],EventType>(1);
		if(isFileOpen()) {
            Element streamList = (Element) xmlFileRoot.
                    getElementsByTagName("common").item(0);
            NodeList stream = streamList.getElementsByTagName("event-type");
            Element element;
            String name;
            String typeStr;
            for (int i = 0; i < stream.getLength(); i++) {
            	element = (Element) stream.item(i);
                name = element.getAttribute("name");
                typeStr = element.getAttribute("type");
                Element propList = (Element) element.getElementsByTagName("objectarray").item(0);
                NodeList properties = propList.getElementsByTagName("objectarray-property");
                ArrayList<Attribute> columns = new ArrayList<Attribute>(properties.getLength());
                for (int j = 0; j < properties.getLength(); j++) {
                    Element prop = (Element) properties.item(j);
                    String propName = prop.getAttribute("name");
                    String type = prop.getAttribute("class");
                    Datatype dataType = null;
                    if (type.equals("int")) {
                        dataType = Datatype.INTEGER;
                    } else if (type.equals("long")) {
                        dataType = Datatype.LONG;
                    } else if (type.equals("float")) {
                        dataType = Datatype.FLOAT;
                    } else if (type.equals("double")) {
                        dataType = Datatype.DOUBLE;
                    } else if (type.equals("string")) {
                        dataType = Datatype.TEXT;
                    } else if (type.equals("boolean")) {
                        dataType = Datatype.BOOLEAN;
                    }                    
                    Attribute att = new Attribute(dataType, propName);	
					columns.add(att);
                }
                Attribute[] atts = new Attribute[columns.size()];
                atts = columns.toArray(atts);                
                EventType event = new EventType(name, atts);
                String[] key = {name,typeStr};
                streams.put(key, event);
            }      
        }
		return streams;
	}
    
    /**
     * Saves a list of Streams into the Stream_Set XML file.
     *
     * @param list  	the list of Streams
     * @param filePath  the path of XML file
     * @param Choose	0 for Input Streams, 1 for Output Streams
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws TransformerException 
     *
     */
	public static void saveToFile(HashMap<String[], EventType> list, String filePath) 
	throws IOException, ParserConfigurationException, TransformerException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document docu = builder.newDocument();
		docu.setXmlVersion("1.0");
		
		Element root = docu.createElement("esper-configuration");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xmlns", "http://www.espertech.com/schema/esper");
		root.setAttribute("xsi:noNamespaceSchemaLocation", "esper-configuration-8-0.xsd");
		boolean variant = false;
		Element common = docu.createElement("common");
		for(String[] name : list.keySet()) {
			Element eventType = docu.createElement("event-type");
			if(name[1].equals("Output")) {
				variant = true;
			}
			eventType.setAttribute("name", name[0]);			
			eventType.setAttribute("type", name[1]);
			Element element = docu.createElement("objectarray");
			EventType event = list.get(name);
			Attribute[] atributes = event.getAttributes();
			
			for(int i=0; i < event.getAttributeCount();i++) {
				Element property = docu.createElement("objectarray-property");
				String type = atributes[i].getType().toString();
				String dataType = null;
				if (type.equals("INTEGER")) {
                    dataType = "int";
                } else if (type.equals("LONG")) {
                    dataType = "long";
                } else if (type.equals("FLOAT")) {
                    dataType = "float";
                } else if (type.equals("DOUBLE")) {
                    dataType = "double";
                } else if (type.equals("TEXT")) {
                    dataType = "string";
                } else if (type.equals("BOOLEAN")) {
                    dataType = "boolean";
                }
				property.setAttribute("name", event.getAttributesNames()[i]);
				property.setAttribute("class", dataType);		
				element.appendChild(property);
				}
			eventType.appendChild(element);
			common.appendChild(eventType);
		}
		if(variant) {
			Element variantStream = docu.createElement("variant-stream");
			variantStream.setAttribute("name", "MyVariantStream");
			for(String[] name : list.keySet()) {
				if(name[1].equals("Output")) {
					Element variantEvent = docu.createElement("variant-event-type");
					variantEvent.setAttribute("name", name[0]);
					variantStream.appendChild(variantEvent);
				}
			}	
			common.appendChild(variantStream);
		}
		
		Element loggin = docu.createElement("loggin");
		Element jdbc = docu.createElement("jdbc");		
		jdbc.setAttribute("enabled", "false");
		Element queryPlan = docu.createElement("query-plan");		
		queryPlan.setAttribute("enabled", "false");
		loggin.appendChild(jdbc);
		loggin.appendChild(queryPlan);
		common.appendChild(loggin);
		
		root.appendChild(common);
		
		Element compiler = docu.createElement("compiler");
		
		Element resources = docu.createElement("view-resources");
		Element policy = docu.createElement("allow-multiple-expiry-policy");
		policy.setAttribute("enabled", "true");
		Element streamSelection = docu.createElement("stream-selection");
		Element selector = docu.createElement("stream-selector");
		selector.setAttribute("value", "istream");
		streamSelection.appendChild(selector);
		resources.appendChild(policy);
		resources.appendChild(streamSelection);
		compiler.appendChild(resources);
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
    
    /**
     * Loads the Stream List from the Stream_Set.xml file and returns a HashMap depends on type.
     * 
     * @param type	0 for Input, 1 for Output Streams
     * @return HashMap <String, EvenType> with the type selected
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException 
     */
    public static HashMap<String, EventType> loadStreams(int type) throws ParserConfigurationException, SAXException, IOException, TransformerException{
    	openFile();
    	HashMap<String[], EventType> streams = loadStreams();
		HashMap<String, EventType> streams2 = new HashMap<String,EventType>(1);
		if (type == 0) {
			for (String[] i : streams.keySet()) {
	        	if (i[1].equals("Input")) {
	        		streams2.put(i[0], streams.get(i));
	        	}
			}
		} else if (type == 1){
			for (String[] i : streams.keySet()) {
	        	if (i[1].equals("Output")) {
	        		streams2.put(i[0], streams.get(i));
	        	}
			}
		}
    	return streams2;    	
    }
    
}
