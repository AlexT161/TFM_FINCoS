package pt.uc.dei.fincos.adapters.cep;

import java.util.Properties;

import pt.uc.dei.fincos.basic.CSV_Event;
import pt.uc.dei.fincos.basic.Event;
import pt.uc.dei.fincos.sink.Sink;

public final class SiddhiInterface extends CEP_EngineInterface {

	public SiddhiInterface(int rtMode, int rtResolution) {
		super(rtMode, rtResolution);
		// TODO Auto-generated constructor stub
	}

	public static synchronized SiddhiInterface getInstance(Properties prop, int rtMode, int rtResolution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(Event e) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(CSV_Event event) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean connect() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean load(String[] outputStreams, Sink sinkInstance) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect2() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getInputStreamList() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getOutputStreamList() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
