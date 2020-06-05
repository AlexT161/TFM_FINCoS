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

import pt.uc.dei.fincos.basic.Globals;
import pt.uc.dei.fincos.controller.ConfigurationParser;

/**
 * @author John Alexander Torres
 *
 */
public class WriteQuery {
	
    /** Root of the xml queries file. */
    private static Element xmlFileRoot;

    /** Queries file. */
    private static File queryFile;
	
	/** Path for the file containing the Queries. */
    public static final String QUERY_SET_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Q_Prueba_set.xml";

	public static void addQuery(String name, String text) throws ParserConfigurationException, TransformerException, IOException, SAXException {
		File f = new File(QUERY_SET_FILE);
        if (!f.exists()) {
        	System.out.println("F no existe");
            createEmptyFile(QUERY_SET_FILE);
        }
        open(QUERY_SET_FILE);
        HashMap<String, String> queryList = loadQueries();
        queryList.put(name, text);
        saveToFile(queryList, QUERY_SET_FILE);
        JOptionPane.showMessageDialog(null, "Query created, please fill out the Attributes", "Create", JOptionPane.INFORMATION_MESSAGE);
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
	 * @return queries	HashMap with the query names and text
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static HashMap<String, String> loadQueries() throws ParserConfigurationException, SAXException, IOException {
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
    public void closeFile() {
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
