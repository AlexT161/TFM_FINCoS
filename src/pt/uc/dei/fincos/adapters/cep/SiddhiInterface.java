package pt.uc.dei.fincos.adapters.cep;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.NotFoundException;
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
    
    /** Stores schemas for streams whose events are represented as Maps or Object-arrays.*/
    private HashMap<String, InputHandler> inputHandlers;
    
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
       this.inputHandlers = new HashMap<String, InputHandler>();
       
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
	                            } else if (attType.equals("bool")) {
	                                attTypes[j] = boolean.class;
	                            } else {
	                                throw new Exception("Unsupported data type for"
	                                        + " attribute \"" + attNames[j]
	                                        + "\", in event type \"" + typeName
	                                        + "\".");
	                            }
	                        }
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
	                    createBean(eType);
	                }
	            }
	        }
	        // Removes output streams from input streams list
	        for (String outputStream: this.outputStreamList) {
	            inputStreams.remove(outputStream);
	        }
	        this.inputStreamList = inputStreams.toArray(new String[0]);
	}

	/**
     * Creates and loads into JVM a class with the same schema
     * as the event type passed as argument.
     *
     * @param eType			Schema of new class
     *
     * @throws CannotCompileException   if class creation fails
     * @throws NotFoundException        if class creation fails
     */
    private void createBean(EventType eType)
    throws CannotCompileException, NotFoundException {
        try {
            Class.forName(eType.getName());
        } catch (ClassNotFoundException e) {
            ClassPool pool = ClassPool.getDefault();
            CtClass schemaClass = pool.makeClass(eType.getName());

            for (Attribute att : eType.getAttributes()) {
                CtField cfield = null;
                switch (att.getType()) {
                case INTEGER:
                    cfield = new CtField(CtClass.intType, att.getName(), schemaClass);
                    break;
                case FLOAT:
                    cfield = new CtField(CtClass.floatType, att.getName(), schemaClass);
                    break;
                case DOUBLE:
                    cfield = new CtField(CtClass.doubleType, att.getName(), schemaClass);
                    break;
                case TEXT:
                    cfield = new CtField(pool.get("java.lang.String"), att.getName(), schemaClass);
                    break;
                case BOOLEAN:
                    cfield = new CtField(CtClass.booleanType, att.getName(), schemaClass);
                    break;
                case LONG:
                    cfield = new CtField(CtClass.longType, att.getName(), schemaClass);
                    break;
                }
                if (cfield != null) {
                    cfield.setModifiers(Modifier.PUBLIC);
                    schemaClass.addField(cfield);
                    String getterName = "get" + att.getName().substring(0, 1).toUpperCase()
                                      + att.getName().substring(1);
                    String setterName = "set" + att.getName().substring(0, 1).toUpperCase()
                                      + att.getName().substring(1);
                    schemaClass.addMethod(CtNewMethod.getter(getterName, cfield));
                    schemaClass.addMethod(CtNewMethod.setter(setterName, cfield));
                }
            }

            schemaClass.toClass();
        }
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
                    	String streamName = getStreamName(query.getValue());
                        String streamAtt = getAttributes(streamName);
                    	outputListeners[i] = new SiddhiListener("lsnr-0" + (i + 1),
                                rtMode, rtResolution, sinkInstance, this.siddhiManager,
                                query.getKey(), query.getValue(),
                                this.streamsSchemas.get(query.getKey()), streamName, streamAtt, this.eventFormat);
                        outputListeners[i].load();
                        inputHandlers.put(streamName,((SiddhiListener) outputListeners[i]).getInputHandler());
                        for (String n : inputHandlers.keySet()) {
                        	  System.out.println(n);
                        	}
                        i++;
                    } else {
                    	String streamName = getStreamName(query.getValue());
                    	String streamAtt = getAttributes(streamName);
                        System.err.println("WARNING: Siddhi Query \"" + query.getKey()
                                           + "\" has no registered listener.");
                        System.out.println("Loading Siddhi query: \n"  + query.getValue());
                        String siddhiApp = "" +
                        		"define stream "+ streamName + " ("+ streamAtt + "); " +
                                "" +
                                "@info(name = '"+query.getKey()+"') " + query.getValue() + ";";
                        this.siddhiAppRuntime = this.siddhiManager.createSiddhiAppRuntime(siddhiApp);
                        inputHandler = siddhiAppRuntime.getInputHandler(streamName);
                        unlistenedQueries.add(siddhiAppRuntime);
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

	private String getAttributes(String streamName) {
		String att = "";
		System.out.println("SiddhiInterface:StreamName: " + streamName);
		for(String i : streamsSchemas.get(streamName).keySet()) {
			att = att + i + " " + streamsSchemas.get(streamName).get(i) + ",";
		}
		String att2 = att.substring(0, att.length() - 1);
		return att2;
	}

	private String getStreamName(String queryText) {
		String[] text = queryText.split("[ \\[\\#]");
		String name = "";
		for (int i=0; i < text.length; i++) {
			if(text[i].equals("from")) {
				name = text[i+1];
			}
		}
//		String [] name2 = name.split("#");
//		String name3 = name2[0];
		//String [] name4= name3.split("[");
		//String name5 = name4[0];
		System.out.println("Split: " + name);
		return name;
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
     * Sends a Map event to Siddhi.
     *
     * Event record is initially represented using the FINCoS internal format
     * and it is converted to a Map format before sending to Siddhi.
     *
     * @param event     the event to be sent
     */
	 /*   private void sendMapEvent(Event event) {
        String eventTypeName = event.getType().getName();
        LinkedHashMap<String, String> eventSchema = streamsSchemas.get(eventTypeName);

        if (eventSchema != null) {
            int fieldCount = this.rtMode != Globals.NO_RT
                                  ? event.getType().getAttributeCount() + 1
                                  : event.getType().getAttributeCount();

            if (eventSchema.size() != fieldCount) {
                System.err.println("ERROR: Number of fields in event \""
                                   + event + "\" (" + (fieldCount)
                                   + ") does not match schema of event type Map \""
                                   + eventTypeName + "\" ("
                                   + eventSchema.size() + ").");
                return;
            }

            Map<String, Object> mapEvent = new HashMap<String, Object>();
            int i = 0;

            for (Entry <String, String> field: eventSchema.entrySet()) {
                if (i == eventSchema.size() - 1) { // Timestamp field (last one, if there is)
                    if (this.rtMode == Globals.ADAPTER_RT) {
                        long timestamp = 0;
                        if (rtResolution == Globals.MILLIS_RT) {
                            timestamp = System.currentTimeMillis();
                        } else if (rtResolution == Globals.NANO_RT) {
                            timestamp = System.nanoTime();
                        }
                        mapEvent.put(field.getKey(), timestamp);
                    } else if (rtMode == Globals.END_TO_END_RT) {
                        // The timestamp comes from the Driver
                        mapEvent.put(field.getKey(), event.getTimestamp());
                    } else if (rtMode == Globals.NO_RT) {
                        mapEvent.put(field.getKey(), event.getAttributeValue(i));
                    }
                } else {
                    mapEvent.put(field.getKey(), event.getAttributeValue(i));
                }
                i++;
            }
            synchronized (this) {
            	inputHandlers.get(eventTypeName).send(mapEvent);
            }
        } else {
            System.err.println("Unknown event 1 type \"" + eventTypeName + "\"."
                    + "It is not possible to send event.");
        }
    }*/

    /**
     * Sends a POJO event to Siddhi.
     *
     * Event record is initially represented using the FINCoS internal format
     * and it is converted to a Plain Java Object before sending to Siddhi.
     *
     * @param event     the event to be sent
     */
  /*  private void sendPOJOEvent(Event event) {
        String eventTypeName = event.getType().getName();
        try {
            Class<?> eventSchema = Class.forName(eventTypeName);
            Field[] eventFields = eventSchema.getDeclaredFields();
            Object pojoEvent = eventSchema.getDeclaredConstructor().newInstance();

            int eventFieldCount = this.rtMode != Globals.NO_RT
                                  ? event.getType().getAttributeCount() + 1
                                  : event.getType().getAttributeCount();

            if (eventFields.length != eventFieldCount) {
                System.err.println("ERROR: Number of fields in event \"" + event
                                    + "\" (" + (eventFieldCount)
                                    + ") does not match schema of POJO event type \""
                                    + eventTypeName + "\" ("
                                    + eventFields.length + ").");
                return;
            }

            // Fill object attributes with event data
            Field f;
            for (int i = 0; i < eventFields.length; i++) {
                f = eventFields[i];
                try {
                    // Assigns Timestamp
                    if (i == eventFields.length - 1) { // timestamp field (the last one)
                        if (this.rtMode == Globals.ADAPTER_RT) {
                            long timestamp = 0;
                            if (rtResolution == Globals.MILLIS_RT) {
                                timestamp = System.currentTimeMillis();
                            } else if (rtResolution == Globals.NANO_RT) {
                                timestamp = System.nanoTime();
                            }
                            f.setLong(pojoEvent, timestamp);
                        } else if (rtMode == Globals.END_TO_END_RT) {
                            // The timestamp comes from the Driver
                            f.setLong(pojoEvent, event.getTimestamp());
                        }
                    } else {
                        if (f.getType() == int.class) {
                            f.setInt(pojoEvent, (Integer) event.getAttributeValue(i));
                        } else if (f.getType() == long.class) {
                            f.setLong(pojoEvent, (Long) event.getAttributeValue(i));
                        } else if (f.getType() == String.class) {
                            f.set(pojoEvent, event.getAttributeValue(i));
                        } else if (f.getType() == double.class) {
                            f.setDouble(pojoEvent, (Double) event.getAttributeValue(i));
                        } else if (f.getType() == float.class) {
                            f.setFloat(pojoEvent, (Float) event.getAttributeValue(i));
                        }
                    }
                } catch (ClassCastException cce) {
                    System.err.println("Invalid field value (" + event.getAttributeValue(i)
                                     + ") for field [" + f
                                     + "]. It is not possible to send event.");
                    return;
                }
            }

            synchronized (this) {
            	inputHandlers.get(eventTypeName).send(pojoEvent);
            }
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Unknown event 2 type \"" + eventTypeName
                             + "\"." + "It is not possible to send event.");
            return;
        } catch (Exception e) {
            System.err.println("Unexpected exception: " + e.getMessage()
                    + ". It is not possible to send event.");
            e.printStackTrace();
            return;
        }
    }*/

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
            synchronized (this) {
                    	inputHandlers.get(eventTypeName).send(objArrEvent);
            }
        } else {
            System.err.println("Unknown event 3 type \"" + eventTypeName + "\"."
                    + "It is not possible to send event to Siddhi.");
        }
		
	}

	@Override
	public void send(CSV_Event event) throws Exception {
		// TODO Auto-generated method stub
		
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
