package pt.uc.dei.fincos.adapters.cep;

import java.util.LinkedHashMap;

import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeDestroyedException;
import com.espertech.esper.runtime.client.EPUndeployException;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;
import io.siddhi.core.util.EventPrinter;
import pt.uc.dei.fincos.adapters.OutputListener;
import pt.uc.dei.fincos.sink.Sink;

public final class SiddhiListener extends OutputListener {
	
	private SiddhiAppRuntime runtime;
	
	private StreamCallback streamCallback;
	
	private InputHandler inputHandler;
	
	private SiddhiManager siddhiManager;
	
    /** The name of the output stream of the query this listener listens to. */
    private String queryOutputName;

    /** The EPL code of the query this listener listens to. */
    private String queryText;

    /** The schema of the output produced by the query. */
    LinkedHashMap<String, String> querySchema;

    /** The format used to exchange events with the Esper engine. */
    private int eventFormat;
	
	public SiddhiListener(String lsnrID, int rtMode, int rtResolution, Sink sinkInstance,
			SiddhiManager siddhiManager, String queryOutputName, String queryText,
            LinkedHashMap<String, String> querySchema, int eventFormat, InputHandler inputHandler) {
		super(lsnrID, rtMode, rtResolution, sinkInstance);
		this.inputHandler = inputHandler;
//		this.runtime = siddhiAppRuntime;
        this.queryText = queryText;
        this.querySchema = querySchema;
        this.queryOutputName = queryOutputName;
        this.setEventFormat(eventFormat);
        this.siddhiManager  = siddhiManager;
        this.runtime = siddhiManager.createSiddhiAppRuntime(queryText);
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
		runtime.addCallback("OutputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
            	SiddhiListener.this.onOutput(events);
            }
        });
//		runtime.start();
    }
    
	@Override
	public void load() throws Exception {
//		SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(queryText);
		runtime.start();
	}

	@Override
	public void disconnect() {
/*    	if (query != null) {
			query.getStatements()[0].removeListener(this);
			if (!query.getStatements()[0].isDestroyed()) {
			try {
				runtime.getDeploymentService().undeploy(query.getDeploymentId());
			} catch (EPRuntimeDestroyedException e) {
				e.printStackTrace();
			} catch (EPUndeployException e) {
				e.printStackTrace();
			}
    		}
    	} */		
	}

}
