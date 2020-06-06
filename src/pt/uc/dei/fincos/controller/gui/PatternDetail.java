package pt.uc.dei.fincos.controller.gui;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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


@SuppressWarnings("serial")
public class PatternDetail extends ComponentDetail{

	private JLabel nameLabel;
	private JTextField nameField, patternField;
	private JButton cancelBtn, okBtn;
	private JPanel patternPanel;
	private JScrollBar scrollType;
	private String oldPattern;

	public PatternDetail(String os, String tx) {
		super(null);
		initComponents();
		addListeners();
		
		if (os!=null) {
            this.op = UPDATE;
            this.oldPattern = os;
            fillProperties(os,tx);
        } else {
        	this.op = INSERT;
        	setTitle("New Query");
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

	private void initComponents() {
		nameLabel = new JLabel();
		nameField = new JTextField();
		patternField = new JTextField();
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
            	try {
					if (validateFields()) {
						try {
							WritePattern.addPattern(name,qtext);
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (TransformerException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						}
						//QuerySchema(name);
						dispose();
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
