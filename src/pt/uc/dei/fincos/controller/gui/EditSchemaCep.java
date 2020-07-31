package pt.uc.dei.fincos.controller.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import pt.uc.dei.fincos.basic.EventType;
import pt.uc.dei.fincos.basic.Globals;


/**
 * Choose the CEP Engine before launch the GUI for edit or delete Schemas.
 *
 * @author  John Alexander Torres
 *
 */
@SuppressWarnings("serial")
public class EditSchemaCep extends ComponentDetail{

	@SuppressWarnings("rawtypes")
	private JComboBox streamCombo;
	private JLabel nameLabel;
	private JButton okBtn;
	private JButton cancelBtn;
	private int edit;
	
	/** Path for the file containing the Streams. */
    public static final String STREAM_SET_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Stream_Set.xml";
    /** Path for the file containing the Streams. */
    public static final String STREAM_SET_SIDDHI_FILE = Globals.APP_PATH + "queries" + File.separator + "siddhi" + File.separator + "Q1" + File.separator + "Stream_Siddhi_Set.xml";

    /**
	 * Menu for Delete or Edit Stream Schemas depends on the CEP Engine chosen
	 * 
	 * @param edit	0 for edit, 1 for delete
	 */
    public EditSchemaCep(int edit) {
    	super(null);
    	this.edit = edit;

    	initComponents(edit);
    	addListeners();	
		if (edit==0) {
			setTitle("Edit Event Type");
        } else {
        	setTitle("Delete Event Type");
        }
				
        this.setSize(250, 150);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void initComponents(int edit2){
			
			streamCombo = new JComboBox();
			nameLabel = new JLabel();
			okBtn = new JButton();
			cancelBtn = new JButton();
			
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	        getContentPane().setLayout(null);
	                
	        nameLabel.setText("CEP Engine:");
	        nameLabel.setBounds(14,10,100,25);
	        add(nameLabel);
	        
	        cancelBtn.setText("Cancel");
	        cancelBtn.setPreferredSize(null);
	        cancelBtn.setBounds(14,80,100,34);
	        add(cancelBtn);
			

	        okBtn.setText("OK");
	        okBtn.setPreferredSize(null);
	        okBtn.setBounds(128,80,100,34);
	        add(okBtn);
	        
		    streamCombo.setModel(new DefaultComboBoxModel(new String[] { "Esper" , "Siddhi" }));
		    streamCombo.setBounds(14,40,110,25);
		    add(streamCombo);
			
	
    }
		private void addListeners() {
			cancelBtn.setIcon(new ImageIcon("imgs/cancel.png"));
	        cancelBtn.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                dispose();
	            }
	        });
	        
	        okBtn.setIcon(new ImageIcon("imgs/ok.png"));
	        okBtn.addActionListener(new ActionListener() {
	        	@Override
	        	public void actionPerformed(ActionEvent ev) {
	        		String engine = (String) streamCombo.getSelectedItem();
	        		if(engine =="Esper") {
	        			File f = new File(STREAM_SET_FILE);
	        			try {
							WriteStream.open(STREAM_SET_FILE);
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
	        			HashMap<String, EventType> list = new HashMap<String, EventType>(1);
	        			try {
							list = WriteStream.loadStreams(0);
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (TransformerException e) {
							e.printStackTrace();
						}
	        			if (!f.exists() || list.isEmpty()) {
	        				JOptionPane.showMessageDialog(null, "You must create a Stream Schema first","Error", JOptionPane.ERROR_MESSAGE);
	        				WriteStream.closeFile();
	        			} else {
	        				try {
								new EditSchema(list, edit).setVisible(true);
							} catch (ParserConfigurationException e) {
								e.printStackTrace();
							} catch (SAXException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (TransformerException e) {
								e.printStackTrace();
							}
	        			}
	        		} else if (engine == "Siddhi") {
	        			File f = new File(STREAM_SET_SIDDHI_FILE);
	        			try {
							WriteSiddhiStream.open(STREAM_SET_SIDDHI_FILE);
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
	        			HashMap<String, EventType> list = new HashMap<String, EventType>(1);
	        			try {
							list = WriteSiddhiStream.loadStreams(0);
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (TransformerException e) {
							e.printStackTrace();
						}
	        			if (!f.exists() || list.isEmpty()) {
	        				JOptionPane.showMessageDialog(null, "You must create a Stream Schema first","Error", JOptionPane.ERROR_MESSAGE);
	        				WriteSiddhiStream.closeFile();
	        			} else {
	        				try {
								new EditSchema(list, edit).setVisible(true);
							} catch (ParserConfigurationException e) {
								e.printStackTrace();
							} catch (SAXException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (TransformerException e) {
								e.printStackTrace();
							}
	        			}
	        		}
	        	}
	        });
		}

	@Override
	protected boolean validateFields() throws Exception {
		return false;
	}
}