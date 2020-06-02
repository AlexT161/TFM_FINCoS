package pt.uc.dei.fincos.controller.gui;

import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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

@SuppressWarnings("serial")
public class EditSchema extends ComponentDetail{
	
	/** Path for the file containing the Streams. */
    public static final String STREAM_SET_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Prueba_set.xml";
    
	@SuppressWarnings("rawtypes")
	private JComboBox streamCombo;
	private JLabel nameLabel;
	private JButton setBtn;
	private JButton delBtn;
	private JButton cancelBtn;
	private HashMap<String, EventType> list;
	
	public EditSchema(int p) throws ParserConfigurationException, SAXException, IOException{
		super(null);
		File f = new File(STREAM_SET_FILE);
        if (!f.exists()) {
        	JOptionPane.showMessageDialog(null, "You must first create an Stream","Error", JOptionPane.ERROR_MESSAGE);
        	dispose();
        }
        initComponents(p);
		addListeners();	
		
		if (p==0) {
			setTitle("Edit Event Type");
        } else {
        	setTitle("Delete Event Type");
        }
		
        this.setSize(250, 150);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents(int p) throws ParserConfigurationException, SAXException, IOException {
		streamCombo = new JComboBox();
		nameLabel = new JLabel();
		setBtn = new JButton();
		delBtn = new JButton();
		cancelBtn = new JButton();
		
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        
        nameLabel.setText("Event Type:");
        nameLabel.setBounds(14,10,100,25);
        add(nameLabel);
        
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(null);
        cancelBtn.setBounds(14,80,100,34);
        add(cancelBtn);
		
        if (p==0) {
        	setBtn.setText("Edit");
        	setBtn.setPreferredSize(null);
        	setBtn.setBounds(128,80,100,34);
        	add(setBtn);
        }else {
        	delBtn.setText("Delete");
            delBtn.setPreferredSize(null);
            delBtn.setBounds(128,80,100,34);
            add(delBtn);
        }
        
		this.list = WriteStream.loadStreams(STREAM_SET_FILE);
		String[] combo = new String[list.size()];
		int count = 0;
		for (String i : list.keySet()) {
			  combo[count]=i;
			  count++;
			}
	    streamCombo.setModel(new DefaultComboBoxModel(combo));
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
        
        setBtn.setIcon(new ImageIcon("imgs/checks.png"));
        setBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String type = (String) streamCombo.getSelectedItem();
            	EventType setType = list.get(type);
            	new SchemaDetail(setType).setVisible(true);
                dispose();
            }
        });
        
        delBtn.setIcon(new ImageIcon("imgs/red.png"));
        delBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String type = (String) streamCombo.getSelectedItem();
            	if (JOptionPane.showConfirmDialog(null, "Delete Schema"+type+"?", "Confirm Delete", JOptionPane.YES_NO_OPTION)
                        == JOptionPane.YES_OPTION) {
            	EventType setType = list.get(type);
            	try {
					WriteStream.updateEventType(null,setType);
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                dispose();
            	} else {
            		dispose();
            	}
            }
        });
		
	}

	@Override
	protected boolean validateFields() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
