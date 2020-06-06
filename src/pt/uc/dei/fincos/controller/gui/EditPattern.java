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

import pt.uc.dei.fincos.basic.Globals;

@SuppressWarnings("serial")
public class EditPattern extends ComponentDetail{

	@SuppressWarnings("rawtypes")
	private JComboBox patternCombo;
	private JLabel nameLabel;
	private JButton setBtn, delBtn, cancelBtn;
	private HashMap<String, String> list;

	private String[] combo;

	/** Path for the file containing the Queries. */
    public static final String PATTERNS_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Q_Prueba_set.xml";


	public EditPattern(int mode) throws ParserConfigurationException, SAXException, IOException {
		super(null);
		File f = new File(PATTERNS_FILE);
		WritePattern.open(PATTERNS_FILE);
		this.list = WritePattern.getPatternList();
		this.combo = new String[list.size()];
		if (!f.exists() || list.isEmpty()) {
        	JOptionPane.showMessageDialog(null, "You must create any Stream Schema first","Error", JOptionPane.ERROR_MESSAGE);
        	dispose();
        } else {	        
			initComponents(mode);
			addListeners();
			
			this.setSize(250, 150);
		    this.setLocationRelativeTo(null);
		    this.setResizable(false);
		    this.setVisible(true);
        }
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponents(int mode) {
		patternCombo = new JComboBox();
		nameLabel = new JLabel();
		setBtn = new JButton();
		delBtn = new JButton();
		cancelBtn = new JButton();
		
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
                
        nameLabel.setText("Pattern:");
        nameLabel.setBounds(14,10,100,25);
        add(nameLabel);
        
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(null);
        cancelBtn.setBounds(14,80,100,34);
        add(cancelBtn);
		
        if (mode==0) {
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
        int count = 0;
		for (String i : list.keySet()) {
			  combo[count]=i;
			  count++;
			}
	    patternCombo.setModel(new DefaultComboBoxModel(combo));
	    patternCombo.setBounds(14,40,110,25);
	    add(patternCombo);		
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
            	String type = (String) patternCombo.getSelectedItem();
            	String text = (String) list.get(type);
            	new PatternDetail(type,text).setVisible(true);
                dispose();
            }
        });
		
        delBtn.setIcon(new ImageIcon("imgs/red.png"));
        delBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String type = (String) patternCombo.getSelectedItem();
            	if (JOptionPane.showConfirmDialog(null, "Delete Schema " + type + " ?", "Confirm Delete", JOptionPane.YES_NO_OPTION)
                        == JOptionPane.YES_OPTION) {
					try {
						WritePattern.updatePattern(type,null);
					} catch (ParserConfigurationException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (TransformerException e1) {
						e1.printStackTrace();
					} catch (SAXException e1) {
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
