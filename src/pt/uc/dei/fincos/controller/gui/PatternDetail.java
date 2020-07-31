package pt.uc.dei.fincos.controller.gui;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

/**
 * GUI for configuration of Patterns.
 *
 * @author  John Alexander Torres
 *
 */
@SuppressWarnings("serial")
public class PatternDetail extends ComponentDetail{

	private JLabel nameLabel, cepLabel;
	private JTextField nameField, patternField;
	private JButton cancelBtn, okBtn;
	private JPanel patternPanel;
	private JScrollBar scrollType;
	private String oldPattern;
	@SuppressWarnings("rawtypes")
	private JComboBox cepPatternBox;

	public PatternDetail(String os, String tx) {
		super(null);
		initComponents();
		addListeners();
		
		if (os!=null) {
            this.op = 1; //Update
            this.oldPattern = os;
            fillProperties(os,tx);
        } else {
        	this.op = 0; //Insert
        	setTitle("New Pattern");
        }
		
		this.setSize(500, 200);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    this.setVisible(true);
	}
	
	private void fillProperties(String os, String tx) {
		this.nameField.setText(os);
		this.patternField.setText(tx);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponents() {
		nameLabel = new JLabel();
		cepLabel = new JLabel();
		nameField = new JTextField();
		patternField = new JTextField();
		cepPatternBox = new JComboBox();
		cancelBtn = new JButton();
		okBtn = new JButton();
		patternPanel = new JPanel();
		scrollType = new JScrollBar(JScrollBar.HORIZONTAL);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
		
        nameLabel.setText("Pattern Name:");
        nameLabel.setBounds(14,10,100,25);
        add(nameLabel);
        
        nameField.setBounds(110,10,100,25);
        add(nameField);
        
        switch (op) {       	
        case 1:
            nameField.setText(oldPattern);
            nameField.setEditable(false);
            break;
        case 0:
        	nameField.setEnabled(true);
        }
        
        cepLabel.setText("CEP Engine:");
        cepLabel.setBounds(250,10,100,25);
        add(cepLabel);
        
	    cepPatternBox.setModel(new DefaultComboBoxModel(new String[] { "Esper" , "Siddhi" }));
	    cepPatternBox.setBounds(330,10,110,25);
	    add(cepPatternBox);
        
        patternPanel.setBounds(10,50,480,70);
        patternPanel.setBorder(BorderFactory.createTitledBorder("Pattern"));
        patternPanel.setLayout(new BoxLayout(patternPanel, BoxLayout.Y_AXIS));

        getContentPane().add(patternPanel);
        
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(null);
        cancelBtn.setBounds(147,130,100,34);
        add(cancelBtn);
        
        okBtn.setText("OK");
        okBtn.setPreferredSize(null);
        okBtn.setBounds(252,130,100,34);
        add(okBtn);
        
        BoundedRangeModel brm = patternField.getHorizontalVisibility();
        scrollType.setModel(brm);
        patternField.setBounds(14,60,450,25);
        patternPanel.add(patternField);
        patternPanel.add(scrollType);

        pack();
       
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
            	String name= nameField.getText();
            	String qtext = patternField.getText();
            	int engine = chooseEngine((String) cepPatternBox.getSelectedItem());
            	try {
					if (validateFields()) {						
							switch (op) {
	                        case 1:
	                        	WritePattern.updatePattern(name,qtext,engine);
	                            dispose();
	                            break;
	                        case 0:
	                        try {
								WritePattern.addPattern(name,qtext,engine);
							} catch (ParserConfigurationException e) {
								e.printStackTrace();
							} catch (TransformerException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (SAXException e) {
								e.printStackTrace();
							}
							dispose();
						}
					} else {
						JOptionPane.showMessageDialog(null, "One or more required fields were not correctly filled.", "Invalid Input", JOptionPane.ERROR_MESSAGE);                    	
					}
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
		
	}

	private int chooseEngine(String engine) {
    	int engineNo = 0;
    	if(engine == "Esper") {
    		engineNo = 1;
    	} else if(engine== "Siddhi") {
    		engineNo = 2;
    	}
		return engineNo;
	}
	
	@Override
	protected boolean validateFields() throws Exception {
		boolean ret = true;
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            nameField.setBackground(INVALID_INPUT_COLOR);
            ret = false;
        } else {
            Color defaultColor = UIManager.getColor("TextField.background");
            nameField.setBackground(defaultColor);
        }
        if (patternField.getText() == null || patternField.getText().isEmpty()) {
            patternField.setBackground(INVALID_INPUT_COLOR);
            ret = false;
        } else {
            Color defaultColor = UIManager.getColor("TextField.background");
            patternField.setBackground(defaultColor);
        }
        return ret;
	}

}
