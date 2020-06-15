package pt.uc.dei.fincos.controller.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import pt.uc.dei.fincos.basic.EventType;
import pt.uc.dei.fincos.basic.Globals;
import pt.uc.dei.fincos.controller.ConfigurationParser;

/**
 * Create, edit or delete a pattern in the Query_Set file and calls the QueryStream to assign types.
 * 
 * @author John Alexander Torres
 *
 */
public class WritePattern {
	
    /** Root of the xml queries file. */
    private static Element xmlFileRoot;

    /** Queries file. */
    private static File queryFile;
    
	/** Path for the file containing the Queries. */
    public static final String QUERY_SET_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Query_Set.xml";

    
	/**
	 * Adds a new Pattern to the Query_Set file
	 * 
	 * @param name	Name of the pattern
	 * @param text	Query text
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void addPattern(String name, String text) throws ParserConfigurationException, TransformerException, IOException, SAXException{
		HashMap<String, String> queryList = openAndGetList();
        queryList.put(name, text);
        new QueryStream(name, text, null);
        saveToFile(queryList, QUERY_SET_FILE);
        JOptionPane.showMessageDialog(null, "Pattern created, please fill out the Attributes", "Create", JOptionPane.INFORMATION_MESSAGE);
	}

	
	/**
	 * Updates a Pattern From the Query_Set file
	 * 
	 * @param name	Name of the pattern
	 * @param text	Query text
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public static void updatePattern(String name, String text) throws ParserConfigurationException, SAXException, IOException, TransformerException{
		HashMap<String, String> list = openAndGetList();
    	if (text!=null) {
    		updateQuery(name,text,list);    		
    	} else {
    		deletePattern(name,list);
    	}
    	closeFile();
	}

	
	/**
	 * Creates a Query_Set file instance and loads the patterns from the Query_Set file
	 * 
	 * @return	List of Queries
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	private static HashMap<String, String> openAndGetList() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		File f = new File(QUERY_SET_FILE);
        if (!f.exists()) {
            createEmptyFile(QUERY_SET_FILE);
        }
        open(QUERY_SET_FILE);
        HashMap<String, String> queryList = getPatternList();
		return queryList;
	}
    
	/**
	 * Delete a query from the Query_Set file and calls the WriteStream in order to delete the corresponding Stream 
	 * 
	 * @param name	The name of the query to delete
	 * @param list	A Hashmap<String,String> with the list of the current queries in the Query_set file
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws SAXException
	 */
	private static void deletePattern(String name, HashMap<String, String> list) throws ParserConfigurationException, IOException, TransformerException, SAXException {
    	list.remove(name);
        saveToFile(list, QUERY_SET_FILE);
        WriteStream.updateEventType(name, null, 1);
        JOptionPane.showMessageDialog(null, "¡Pattern deleted!", "Delete", JOptionPane.WARNING_MESSAGE);			
	}

	
	/**
	 * Updates an existing Query in the Query_Set File and calls the QueryStream GUI to modify the Stream if is necessary
	 * 
	 * @param name	The name of the Query to modify
	 * @param text	The new Query text
	 * @param list	The list with the queries in the Qeury_Set file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	private static void updateQuery(String name, String text, HashMap<String, String> list) throws ParserConfigurationException, SAXException, IOException, TransformerException {
    	list.put(name, text);
    	HashMap<String, EventType> streamList = WriteStream.loadStreams(1);
		EventType stream = streamList.get(name);
		System.out.print(streamList);
		System.out.print(stream);
        new QueryStream(name , text, stream);
        saveToFile(list, QUERY_SET_FILE);
        JOptionPane.showMessageDialog(null, "Pattern correctly updated, edit the Attributes if is necessary.", "Update", JOptionPane.INFORMATION_MESSAGE);		
	}

	/**
     * Opens a XML file containing the queries.
     *
     * @param path      Path to queries file
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
        queryFile = new File(path);
        Document doc = builder.parse(queryFile);
        xmlFileRoot = doc.getDocumentElement();
    }
	
	/**
	 * Returns the query List from the path file
	 * 
	 * @return queries	HashMap with the query names and text
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static HashMap<String, String> getPatternList() throws ParserConfigurationException, SAXException, IOException {
		HashMap<String, String> queries = new HashMap<String,String>(1);
		if (isFileOpen()) {
		            NodeList query = xmlFileRoot.getElementsByTagName("Query");
		            Element element;
		            String name;
		            String text;
		            for (int i = 0; i < query.getLength(); i++) {
		            	element = (Element) query.item(i);
		                name = element.getAttribute("name");
		                text = element.getAttribute("text");
		            	queries.put(name, text);
		            }   
	        	
	        }		
		return queries;
	}

	public static boolean isFileOpen() {
		return (xmlFileRoot != null);
	}

    /**
     * Unloads parser's queries file.
     *
     */
    public static void closeFile() {
        xmlFileRoot = null;
    }
    
	/**
     * Creates a empty Query_Set file.
     *
     * @param filePath  The path of XML file
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
     * Save queries into the Query_Set XML file.
     *
     * @param list  	the list of Queries
     * @param filePath  the path of XML file
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws TransformerException 
     *
     */
	public static void saveToFile( HashMap<String,String> list, String filePath)
	throws ParserConfigurationException, IOException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document docu = builder.newDocument();
		docu.setXmlVersion("1.0");
		Element root = docu.createElement("Esper-Queries");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		for(String name : list.keySet()) {
			Element query = docu.createElement("Query");			
			query.setAttribute("name", name);
			query.setAttribute("text", list.get(name));
			root.appendChild(query);	
		}
		docu.appendChild(root);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath), false));
        bw.write(ConfigurationParser.fromXMLDocToString(docu));
        bw.flush();
        bw.close();	

	}


}
