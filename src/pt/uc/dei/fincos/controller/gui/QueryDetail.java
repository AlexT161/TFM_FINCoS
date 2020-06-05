package pt.uc.dei.fincos.controller.gui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class QueryDetail extends ComponentDetail{

	private JLabel nameLabel;
	private JTextField nameField, queryField;
	private JButton cancelBtn, okBtn;
	private JPanel queryPanel;
	private JScrollBar scrollType;
	private String oldQuery;

	public QueryDetail(String os) {
		super(null);
		initComponents();
		addListeners();
		
		if (os!=null) {
            this.op = UPDATE;
            this.oldQuery = os;
            fillProperties(os);
        } else {
        	this.op = INSERT;
        	setTitle("New Query");
        }
		
		this.setSize(500, 200);
	    this.setLocationRelativeTo(null);
	    this.setResizable(false);
	    this.setVisible(true);
	}
	
	private void fillProperties(String os) {
		// TODO Auto-generated method stub
		
	}

	private void initComponents() {
		nameLabel = new JLabel();
		nameField = new JTextField();
		queryField = new JTextField();
		cancelBtn = new JButton();
		okBtn = new JButton();
		queryPanel = new JPanel();
		scrollType = new JScrollBar(JScrollBar.HORIZONTAL);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
		
        nameLabel.setText("Query Name:");
        nameLabel.setBounds(14,10,100,25);
        add(nameLabel);
        
        nameField.setBounds(110,10,100,25);
        add(nameField);
        
        queryPanel.setBounds(10,50,480,70);
        queryPanel.setBorder(BorderFactory.createTitledBorder("Query Text"));
        queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.Y_AXIS));

        getContentPane().add(queryPanel);
        
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(null);
        cancelBtn.setBounds(147,130,100,34);
        add(cancelBtn);
        
        okBtn.setText("OK");
        okBtn.setPreferredSize(null);
        okBtn.setBounds(252,130,100,34);
        add(okBtn);
        
        BoundedRangeModel brm = queryField.getHorizontalVisibility();
        scrollType.setModel(brm);
        queryField.setBounds(14,60,450,25);
        queryPanel.add(queryField);
        queryPanel.add(scrollType);

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
            	String qtext = queryField.getText();
            	try {
					if (validateFields()) {
						try {
							WriteQuery.addQuery(name,qtext);
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
        if (queryField.getText() == null || queryField.getText().isEmpty()) {
            queryField.setBackground(INVALID_INPUT_COLOR);
            ret = false;
        } else {
            Color defaultColor = UIManager.getColor("TextField.background");
            queryField.setBackground(defaultColor);
        }
        return ret;
	}

}
