package pt.uc.dei.fincos.adapters.cep;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;
import pt.uc.dei.fincos.adapters.OutputListener;
import pt.uc.dei.fincos.basic.Globals;
import pt.uc.dei.fincos.sink.Sink;

public final class SiddhiListener extends OutputListener{
	
	private SiddhiAppRuntime runtime;
	
	private InputHandler inputHandler;												
	
	private SiddhiManager siddhiManager;
	
    /** The name of the output stream of the query this listener listens to. */
    private String queryOutputName;

    /** The EPL code of the query this listener listens to. */
    private String queryText;

    /** The name of the input stream. */
    private String streamName;
    
    /** Attributes of the input stream. */
    private String streamAtts;
    
    /** The schema of the output produced by the query. */
    LinkedHashMap<String, String> querySchema;

    /** The format used to exchange events with the Esper engine. */
    private int eventFormat;
	
	public SiddhiListener(String lsnrID, int rtMode, int rtResolution, Sink sinkInstance,
			SiddhiManager siddhiManager, String queryOutputName, String queryText,
            LinkedHashMap<String, String> querySchema, String streamName, String streamAtt, int eventFormat) {
		super(lsnrID, rtMode, rtResolution, sinkInstance);
        this.queryText = queryText;
        this.querySchema = querySchema;
        this.queryOutputName = queryOutputName;
        this.streamName = streamName;
        this.streamAtts = streamAtt;
        this.setEventFormat(eventFormat);
        this.siddhiManager  = siddhiManager;
	}

    /**
     * Sets the format used to exchange events with the Siddhi engine.
     *
     * @param eventFormat  the event format (either POJO or Map)
     */
    private void setEventFormat(int eventFormat) {
        if (eventFormat == EsperInterface.POJO_FORMAT) {
            this.eventFormat = eventFormat;
        } else {
            this.eventFormat = EsperInterface.MAP_FORMAT;
        }		
	}
    
    /**
    *
    * @return     the format used to exchange events with the Siddhi engine.
    */
   public int getEventFormat() {
       return eventFormat;
   }

	@Override
    public void run() {
    }
    
	@Override
	public void load() throws Exception {
        String siddhiApp = "" +
        		"define stream " + streamName + " (" + streamAtts + "); " +
                "" +
                "@info(name ='" + queryOutputName + "') " + queryText + ";";
        this.runtime = this.siddhiManager.createSiddhiAppRuntime(siddhiApp);
        String callback = getCallback();
		runtime.addCallback(callback, new StreamCallback() {
            @Override
            public void receive(Event[] events) {
            	SiddhiListener.this.update(events,null);
            }
        });
        inputHandler = runtime.getInputHandler(streamName);
		runtime.start();
	}
	
	private String getCallback() {
		String[] text = queryText.split(" ");
		String name = "";
		for (int i=0; i < text.length; i++) {
			if(text[i].equals("into")) {
				name = text[i+1];
			}
		}
		System.out.println("SiddhiListener:callback: " + name);
		return name;
	}
	
	public InputHandler getInputHandler() {
		return this.inputHandler;
	}

	@Override
	public void disconnect() {
    	if (inputHandler != null) {
    		runtime.shutdown();
    		siddhiManager.shutdown();
    	}		
	}

    public void update(Event[] newEvents, Event[] oldEvents) {
        long timestamp = -1;
        if (this.rtMode == Globals.ADAPTER_RT) {
            if (this.rtResolution == Globals.MILLIS_RT) {
                timestamp = System.currentTimeMillis();
            } else if (this.rtResolution == Globals.NANO_RT) {
                timestamp = System.nanoTime();
            }
        }

        for (int i = 0; i < newEvents.length; i++) {
            try {
                processIncomingEvent(newEvents[i], timestamp);
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
        }
    }
    
    /**
     * Processes an event coming from Siddhi.
     *
     * @param event         the incoming event
     * @param timestamp     the timestamp associated with the event
     * @throws Exception    if the incoming event is of an unknown type
     */
    private void processIncomingEvent(Event event, long timestamp) throws Exception {
        onOutput(toFieldArray(event, timestamp));
    }
    
    /**
     * Translates the event from the Siddhi native format to Array of Objects.
     *
     * @param event         The event in Siddhi's native representation
     * @param timestamp     The timestamp associated with the incoming event
     * @return              The event as an array of objects
     * @throws Exception    if the incoming event is of an unknown type
     *
     */
    private Object[] toFieldArray(Event event, long timestamp) throws Exception {
        Object[] eventObj = null;
        int fieldCount = 0;
        if (querySchema != null) { ////Input events are MAPs
            int i = 1;
            /* If response time is being measured, leave a slot for the arrival
             *  time of the event (filled here or at the Sink). */
            fieldCount = rtMode != Globals.NO_RT ? querySchema.size() + 2
                    : querySchema.size() + 1;
            eventObj = new Object[fieldCount];
            for (String att: querySchema.keySet()) {
            	eventObj[i] = event.getData(i-1);
            	i++;
            }
        } else { //Input events are POJO
            try {
                Field[] fields = Class.forName(queryOutputName).getFields();
                /* If response time is being measured, leave a slot for the arrival time of the
                event (filled here or at the Sink). */
                fieldCount = rtMode != Globals.NO_RT ? fields.length + 2
                                                     : fields.length + 1;
                eventObj = new Object[fieldCount];
                int i = 1;
                for (Field f : fields) {
                	eventObj[i] = event.getData(i-1);
                    i++;
                }
            } catch (ClassNotFoundException cne) {
                throw new Exception("The type \"" + queryOutputName + "\" has not been defined. ");
            }
        }
        if (eventObj != null) {
            // First element is the stream name
            eventObj[0] = queryOutputName;
            // Last element is the arrival time
            if (rtMode == Globals.ADAPTER_RT) {
                eventObj[fieldCount - 1] = timestamp;
            }
        }
        return eventObj;
    }
    
    /**
    *
    * Translates the event from the Siddhi native format to the framework's CSV format.
    *
    * @param event The event in Siddhi's native representation
    * @return      The event in CSV representation
    */
   public String toCSV(Event event) {
       StringBuilder sb = new StringBuilder();
       sb.append(queryOutputName);
       if (eventFormat == SiddhiInterface.MAP_FORMAT) { // Input events are MAPs
           if (querySchema != null) {
               for (String att: querySchema.keySet()) {
                   sb.append(Globals.CSV_DELIMITER);
                 //  sb.append(event.get(att));
               }
           }
       } else { //Input events are POJO
           try {
               for (Field f : Class.forName(queryOutputName).getFields()) {
                   sb.append(Globals.CSV_DELIMITER);
                 //  sb.append(event.get(f.getName()));
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       return sb.toString();
   }

}
