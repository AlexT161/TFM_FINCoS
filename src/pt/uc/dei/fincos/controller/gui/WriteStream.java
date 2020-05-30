package pt.uc.dei.fincos.controller.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pt.uc.dei.fincos.basic.EventType;
import pt.uc.dei.fincos.basic.Globals;
import pt.uc.dei.fincos.controller.ConfigurationParser;


//Create or Update Streams
public class WriteStream {
	
	/** Path for the file containing the Streams. */
    public static final String STREAM_SET_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Prueba_set.xml";
	
	public static void updateEventType(EventType oldType, EventType newType) {
		JOptionPane.showMessageDialog(null, "Stream correctly updated.", "Update", JOptionPane.INFORMATION_MESSAGE);		
	}

	public static void addEventType(EventType newType) throws Exception {
        File f = new File(STREAM_SET_FILE);
        if (!f.exists()) {
            createEmptyFile(STREAM_SET_FILE);
        }
        saveToFile(newType, STREAM_SET_FILE);
		JOptionPane.showMessageDialog(null, "¡Stream created!", "Create", JOptionPane.INFORMATION_MESSAGE);		
		System.out.println("WriteStreams:NewType: "+newType);
	}
		
    /**
     * Saves a list of Streams into a XML file.
     *
     * @param newType  the eventType to add  //Cambiar por lista
     * @param filePath  the path of XML file
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws TransformerFactoryConfigurationError 
     * @throws TransformerException 
     *
     */
	public static void saveToFile(EventType newType, String filePath) 
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
		Element eventType = docu.createElement("event-type");
		eventType.setAttribute("name", newType.getName());
		eventType.setAttribute("type", "Input");
		Element element = docu.createElement("objectarray");
		
		for(int i=0; i < newType.getAttributeCount();i++) {
			Element property = docu.createElement("objectarray-property");
			String type = newType.getAttributes()[i].getType().toString();
			property.setAttribute("name", newType.getAttributesNames()[i]);
			property.setAttribute("class", type);		
			element.appendChild(property);
		}
		
		eventType.appendChild(element);
		common.appendChild(eventType);
		
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
