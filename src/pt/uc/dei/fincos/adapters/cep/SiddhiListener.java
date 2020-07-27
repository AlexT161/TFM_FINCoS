package pt.uc.dei.fincos.adapters.cep;

import java.util.LinkedHashMap;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;
import pt.uc.dei.fincos.adapters.OutputListener;
import pt.uc.dei.fincos.sink.Sink;

public final class SiddhiListener extends OutputListener {
	
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
                "@info(name = '" + queryOutputName + "') " + queryText;
        this.runtime = this.siddhiManager.createSiddhiAppRuntime(siddhiApp);
		runtime.addCallback("OutputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
            	SiddhiListener.this.onOutput(events);
            }
        });
        inputHandler = runtime.getInputHandler(streamName);
		runtime.start();
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

}
