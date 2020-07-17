package pt.uc.dei.fincos.adapters.cep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;
import io.siddhi.core.util.EventPrinter;
import pt.uc.dei.fincos.basic.Attribute;
import pt.uc.dei.fincos.basic.CSV_Event;
import pt.uc.dei.fincos.basic.Datatype;
import pt.uc.dei.fincos.basic.Event;
import pt.uc.dei.fincos.basic.EventType;
import pt.uc.dei.fincos.basic.Globals;
import pt.uc.dei.fincos.basic.Status;
import pt.uc.dei.fincos.basic.Step;
import pt.uc.dei.fincos.sink.Sink;

public final class SiddhiInterface extends CEP_EngineInterface {

    /** Single instance of Siddhi adapter. */
    private static SiddhiInterface instance;
    
    /** Format used to submit event to the Siddhi engine. */
	private int eventFormat;

	/** Component of the Siddhi engine that manages Siddhi Application Runtimes. */
	private SiddhiManager siddhiManager;

    /** Retrieving InputHandler to push events into Siddhi */
	private InputHandler inputHandler;
	
    /** Siddhi runtime. */
	private SiddhiAppRuntime siddhiAppRuntime;
	
    /** Indicates if an external clock should be used. */
    private boolean useExternalTimer;

    /** The name of the stream that carries time information. */
    private String extTSEventType;

    /** Index of the external timestamp field. */
    private int extTSIndex;

    /** External timestamp of the last received event. */
    private long lastExtTS;
    
    /** List of queries for which there is no registered listener. */
    private ArrayList<SiddhiAppRuntime> unlistenedQueries;
    
    /** A map query identifier -> query EPL text.*/
    private LinkedHashMap<String, String> queryNamesAndTexts;
	
    /** Stores schemas for streams whose events are represented as Maps or Object-arrays.*/
    private HashMap<String, LinkedHashMap<String, String>> streamsSchemas;
    
    /** List of input streams.*/
    private String[] inputStreamList;

    /** List of output streams.*/
    private String[] outputStreamList;
    
    /** Events submitted as Maps. */
    protected static final int MAP_FORMAT = 0;

    /** Events submitted as Plain java Objects. */
    protected static final int POJO_FORMAT = 1;

    /** Events submitted as Objects arrays. */
    protected static final int OBJECT_ARRAY_FORMAT = 2;
	
    
    /**
    *
    * @param connProps     parameters required for connecting with Siddhi
    * @param rtMode        response time measurement mode
    *                      (either END-TO-END, ADAPTER or NO_RT)
    * @param rtResolution  response time measurement resolution
    *                      (either Milliseconds or Nanoseconds)
    */
	public SiddhiInterface(Properties connProps, int rtMode, int rtResolution) {
		super(rtMode, rtResolution);
        this.status = new Status(Step.DISCONNECTED, 0);
        this.setConnProperties(connProps);
        try {
            String useExtClockStr = retrieveConnectionProperty("Use_external_timer");
            boolean useExtTimer = Boolean.parseBoolean(useExtClockStr);
            if (useExtTimer) {
                    this.extTSEventType = retrieveConnectionProperty("External_timer_stream");
                    String extTSFieldIndex = retrieveConnectionProperty("External_timer_index");
                    if (extTSEventType != null && extTSFieldIndex != null) {
                        this.extTSIndex = Integer.parseInt(extTSFieldIndex);
                        this.useExternalTimer = true;
                    } else {
                        this.extTSIndex = -1;
                        this.useExternalTimer = false;
                    }
            } else {
                this.useExternalTimer = false;
                this.extTSEventType = null;
                this.extTSIndex = -1;
            }
        } catch (Exception e) {
            System.err.println("Warning \"Use_External_Timer\" property is missing. "
                    + "Setting to default (false).");
            this.useExternalTimer = false;
            this.extTSEventType = null;
            this.extTSIndex = -1;
        }

        lastExtTS = -1;
	}

	/**
	 * @param props		parameters required for connecting with Siddhi
	 * @param rtMode	response time measurement mode
     *                      (either END-TO-END, ADAPTER or NO_RT)
	 * @param rtResolution	response time measurement resolution
     *                      (either Milliseconds or Nanoseconds)
	 * @return	the single instance of Siddhi adapter
	 */
	public static synchronized SiddhiInterface getInstance(Properties props, int rtMode, int rtResolution) {
        if (instance == null) {
            instance = new SiddhiInterface(props, rtMode, rtResolution);
        }
        return instance;
	}

    /**
     * Destroys the singleton instance of the Siddhi adapter so that it is
     * possible to create a new one.
     */
    private static synchronized void destroyInstance() {
        instance = null;
    }
	
	@Override
	public void send(Event e) throws Exception {
		if (this.status.getStep() == Step.READY || this.status.getStep() == Step.CONNECTED) {
            if (this.eventFormat == OBJECT_ARRAY_FORMAT) {
                sendObjectArrayEvent(e);
            }/* else if (this.eventFormat == POJO_FORMAT) {
                sendPOJOEvent(e);
            } else {
                sendMapEvent(e);
            }

            if (this.useExternalTimer && e.getType().getName().equals(extTSEventType)) {
                advanceClock((Long) e.getAttributeValue(extTSIndex));
            }*/
        }		
	}

    /**
     * Sends an Object-array event to Siddhi.
     *
     * Event record is initially represented using the FINCoS internal format
     * and it is converted to a an Object-array format before sending to Siddhi.
     *
     * @param event     the event to be sent
     * @throws InterruptedException 
     */
	private void sendObjectArrayEvent(Event event) throws InterruptedException {
		String eventTypeName = event.getType().getName();
        LinkedHashMap<String, String> eventSchema = streamsSchemas.get(eventTypeName);
        if (eventSchema != null) {
            Object[] objArrEvent = null;
            Object[] payload = event.getValues();

            int fieldCount = this.rtMode != Globals.NO_RT
                           ? payload.length + 1
                           : payload.length;

            if (eventSchema.size() != fieldCount) {
                System.err.println("ERROR: Number of fields in event \""
                        + event + "\" (" + (fieldCount)
                        + ") does not match schema of event type Object Array \""
                        + eventTypeName + "\" ("
                        + eventSchema.size() + ").");
                return;
            }

            if (this.rtMode == Globals.NO_RT) { // No RT measurement: send event's payload
            	objArrEvent = payload;           
            } else { // With RT measurement: send event's payload and timestamp
                objArrEvent = new Object[fieldCount];
                for (int i = 0; i < fieldCount; i++) {
                    if (i == fieldCount - 1) {    // Timestamp field (last one)
                        if (this.rtMode == Globals.ADAPTER_RT) {
                            /* Assigns a timestamp to the event just after conversion
                               (i.e., just before sending the event to the target system) */
                            long timestamp = 0;
                            if (rtResolution == Globals.MILLIS_RT) {
                                timestamp = System.currentTimeMillis();
                            } else if (rtResolution == Globals.NANO_RT) {
                                timestamp = System.nanoTime();
                            }
                            objArrEvent[i] = timestamp;
                        } else if (rtMode == Globals.END_TO_END_RT) {
                            // The timestamp comes from the Driver
                            objArrEvent[i] = event.getTimestamp();
                        }
                    } else {
                        objArrEvent[i] = payload[i];
                    }
                }
            }
            synchronized (inputHandler) {
            	inputHandler.send(new Object[]{"IBM", 700f, 100L});
                inputHandler.send(new Object[]{"WSO2", 60.5f, 200L});
                inputHandler.send(new Object[]{"GOOG", 50f, 30L});
                inputHandler.send(new Object[]{"IBM", 76.6f, 400L});
                inputHandler.send(new Object[]{"WSO2", 45.6f, 50L});
                Thread.sleep(500);
            }
        } else {
            System.err.println("Unknown event 3 type \"" + eventTypeName + "\"."
                    + "It is not possible to send event.");
        }
		
	}

	@Override
	public void send(CSV_Event event) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized boolean connect() throws Exception {
		try {
            String eventFormat = retrieveConnectionProperty("Event_format");
            if (eventFormat.equalsIgnoreCase("Object-Array")) {
                this.eventFormat = OBJECT_ARRAY_FORMAT;
            } else if (eventFormat.equalsIgnoreCase("POJO")) {
                this.eventFormat = POJO_FORMAT;
            } else {
                this.eventFormat = MAP_FORMAT;
            }
        } catch (Exception e) {
            System.err.println("Warning \"Event_format\" property is missing. "
                    + "Setting to default: \"Object_array_format\" format.");
            this.eventFormat = OBJECT_ARRAY_FORMAT;
        }
		
        String queriesFile = retrieveConnectionProperty("Queries_path");
        String siddhiConfigurationFile = retrieveConnectionProperty("Configuration_file_path");

        this.siddhiManager = new SiddhiManager();
        
        parseStreamsList(queriesFile, siddhiConfigurationFile);
        
       this.status.setStep(Step.CONNECTED);
       this.unlistenedQueries = new ArrayList<SiddhiAppRuntime>();
       
       return true;
	}

	private void parseStreamsList(String queriesFile, String siddhiConfigurationFile) throws Exception {
		 ArrayList<String> inputStreams = new ArrayList<String>();

	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder;
	        builder = factory.newDocumentBuilder();
	        
	        // Parsing of Queries file
	        Document doc = builder.parse(new File(queriesFile));
	        Element queriesList = doc.getDocumentElement();
	        NodeList queries = queriesList.getElementsByTagName("Query");
	        this.queryNamesAndTexts = new LinkedHashMap<String, String>(queries.getLength());
	        Element query;
	        String queryName, queryText;
	        // Iterates over list of queries/output streams
	        for (int i = 0; i < queries.getLength(); i++) {
	            query = (Element) queries.item(i);
	            queryName = query.getAttribute("name");
	            queryText = query.getAttribute("text");
	            queryNamesAndTexts.put(queryName, queryText);
	        }
	        this.outputStreamList = queryNamesAndTexts.keySet().toArray(new String[0]);
	     // Parsing of Siddhi's config file (contains streams list)
	        Document confDoc = builder.parse(new File(siddhiConfigurationFile));
	        Element xmlFileRoot = confDoc.getDocumentElement();
	        NodeList types = xmlFileRoot.getElementsByTagName("event-type");
	        this.streamsSchemas = new HashMap<String, LinkedHashMap<String, String>>();
	        Element type;
	        String typeName, attName, attType;
	        EventType eType;
	        LinkedHashMap<String, String> typeAtts = null;
	     // Iterates over all streams (input and output) defined in Siddhi's conf file
	        for (int i = 0; i < types.getLength(); i++) {
	            type = (Element) types.item(i);
	            typeName = type.getAttribute("name");
	            inputStreams.add(typeName);

	            if (type.getElementsByTagName("objectarray") != null
	                    && type.getElementsByTagName("objectarray").getLength() > 0) {
	                NodeList attributes = ((Element) type.getElementsByTagName("objectarray").
	                                      item(0)).getElementsByTagName("objectarray-property");
	                // Add streams whose events are represented as Maps or Object-arrays
	                if (this.eventFormat == MAP_FORMAT || this.eventFormat == OBJECT_ARRAY_FORMAT) {
	                    typeAtts = new LinkedHashMap<String, String>(attributes.getLength());
	                    for (int j = 0; j < attributes.getLength(); j++) {
	                        Element attribute = (Element) attributes.item(j);
	                        attName = attribute.getAttribute("name");
	                        attType = attribute.getAttribute("class");
	                        typeAtts.put(attName, attType);
	                    }
	                    streamsSchemas.put(typeName, typeAtts);
	                    /*
	                     *  If the representation is object array,
	                     *  replace event definition as a Map by one as Object array.
	                     */
	                    if (this.eventFormat == OBJECT_ARRAY_FORMAT) {
	                        String[] attNames = new String[attributes.getLength()];
	                        Object[] attTypes = new Object[attributes.getLength()];
	                        for (int j = 0; j < attributes.getLength(); j++) {
	                            Element attribute = (Element) attributes.item(j);
	                            attNames[j] = attribute.getAttribute("name");
	                            attType = attribute.getAttribute("class");
	                            if (attType.equals("int")) {
	                                attTypes[j] = int.class;
	                            } else if (attType.equals("long")) {
	                                attTypes[j] = long.class;
	                            } else if (attType.equals("string")) {
	                                attTypes[j] = String.class;
	                            } else if (attType.equals("double")) {
	                                attTypes[j] = double.class;
	                            } else if (attType.equals("float")) {
	                                attTypes[j] = float.class;
	                            } else if (attType.equals("boolean")) {
	                                attTypes[j] = boolean.class;
	                            } else {
	                                throw new Exception("Unsupported data type for"
	                                        + " attribute \"" + attNames[j]
	                                        + "\", in event type \"" + typeName
	                                        + "\".");
	                            }
	                        }
	                       // esperConfig.getCommon().addEventType(typeName, attNames, attTypes); //JAT
	                    }

	                } else if (this.eventFormat == POJO_FORMAT) {
	                    Attribute[] atts = new Attribute[attributes.getLength()];
	                    for (int j = 0; j < attributes.getLength(); j++) {
	                        Element attribute = (Element) attributes.item(j);
	                        attName = attribute.getAttribute("name");
	                        attType = attribute.getAttribute("class");
	                        if (attType.equals("int")) {
	                            atts[j] = new Attribute(Datatype.INTEGER, attName);
	                        } else if (attType.equals("long")) {
	                            atts[j] = new Attribute(Datatype.LONG, attName);
	                        } else if (attType.equals("string")) {
	                            atts[j] = new Attribute(Datatype.TEXT, attName);
	                        } else if (attType.equals("double")) {
	                            atts[j] = new Attribute(Datatype.DOUBLE, attName);
	                        } else if (attType.equals("float")) {
	                            atts[j] = new Attribute(Datatype.FLOAT, attName);
	                        } else if (attType.equals("boolean")) {
	                            atts[j] = new Attribute(Datatype.BOOLEAN, attName);
	                        } else {
	                            throw new Exception("Unsupported data type for"
	                                    + " attribute \"" + attName
	                                    + "\", in event type \"" + typeName
	                                    + "\".");
	                        }
	                    }
	                    eType = new EventType(typeName, atts);

	                    // Load new class definition into JVM using javassist API
//	                    createBean(eType);

//	                    esperConfig.addEventType(typeName, Class.forName(typeName).getName());
	                }
	            }
	        }
	        // Removes output streams from input streams list
	        for (String outputStream: this.outputStreamList) {
	            inputStreams.remove(outputStream);
	        }
	        this.inputStreamList = inputStreams.toArray(new String[0]);

	}

	@Override
	public synchronized boolean load(String[] outputStreams, Sink sinkInstance) throws Exception {
		// This interface instance has already been loaded
        if (this.status.getStep() == Step.READY) {
            return true;
        } else { // If it is not connected yet, try to connect
            if (this.status.getStep() != Step.CONNECTED) {
                this.connect();
            }
        }
        if (this.status.getStep() == Step.CONNECTED) {
            this.status.setStep(Step.LOADING);
            if (outputStreams != null) {
                this.outputListeners = new SiddhiListener[outputStreams.length];
                int i = 0;
                for (Entry<String, String> query : this.queryNamesAndTexts.entrySet()) {                           	
                    if (hasListener(query.getKey(), outputStreams)) {
                        outputListeners[i] = new SiddhiListener("lsnr-0" + (i + 1),
                                rtMode, rtResolution, sinkInstance, this.siddhiAppRuntime,
                                query.getKey(), query.getValue(),
                                this.streamsSchemas.get(query.getKey()), this.eventFormat, inputHandler);
                        outputListeners[i].load();
                        i++;
                    } else {
                        System.err.println("WARNING: Query \"" + query.getKey()
                                           + "\" has no registered listener.");
                        System.out.println("Loading query: \n"  + query.getValue());
                        String siddhiApp = "" +
                        		"define stream StockStream (symbol string, price float, volume long); " +
                                "" +
                                "@info(name = '"+query.getKey()+"') " + 
//                                query.getValue();
								"from StockStream[volume < 150] " +
								"select symbol, price " +
								"insert into OutputStream;";
                        SiddhiAppRuntime runtime = this.siddhiManager.createSiddhiAppRuntime(siddhiApp);
                        this.inputHandler = runtime.getInputHandler("StockStream");
                        unlistenedQueries.add(runtime);
                    }
                }
                try {
                    this.startAllListeners();
                } catch (Exception e) {
                    throw new Exception("Could not load event listener (" + e.getMessage() + ").");
                }
            }

            this.status.setStep(Step.READY);
            return true;
        } else {
            return false;
        }
	}

	private boolean hasListener(String key, String[] outputStreams) {
        for (int i = 0; i < outputStreams.length; i++) {
        	if (key.equals(outputStreams[i])) {
                return true;
            }
        }
		return false;
	}

	@Override
	public void disconnect() {
		this.status.setStep(Step.DISCONNECTED);
        // Stops all queries with a listener attached
        stopAllListeners();
        // Stops all "internal" queries            
        for (SiddhiAppRuntime q: unlistenedQueries) {
            		q.shutdown();
        }
        this.siddhiManager.shutdown();
        destroyInstance();	
	}

	@Override
	public void disconnect2() {
		// TODO Auto-generated method stub
		
	}

    @Override
    public synchronized String[] getInputStreamList() throws Exception {
        return inputStreamList != null ? inputStreamList : new String[0];
    }

    @Override
    public synchronized String[] getOutputStreamList() throws Exception {
        return outputStreamList != null ? outputStreamList : new String[0];
    }

    /**
     * Advances the external clock of the Siddhi instance, if necessary.
     *
     * @param extTimestamp  the latest timestamp
     */
    /*  private void advanceClock(Long extTimestamp) {
        if (extTimestamp != lastExtTS) { // Time advanced       	
        	this.inputHandler.sendEvent(new externalTimestamp(extTimestamp));
            lastExtTS = extTimestamp;
        }
    }*/
    
}
