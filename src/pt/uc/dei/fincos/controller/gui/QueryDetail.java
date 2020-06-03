package pt.uc.dei.fincos.controller.gui;

import java.awt.Dialog;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class QueryDetail extends ComponentDetail{

	private JLabel nameLabel;
	private JTextField nameField, queryField;
	private JButton cancelBtn, okBtn;
	private JPanel queryPanel;
	private JScrollBar scrollType;

	public QueryDetail(String[] os) {
		super(null);
		initComponents();
		addListeners();
		
		 this.setSize(500, 200);
	     this.setLocationRelativeTo(null);
	     this.setResizable(false);
	     this.setVisible(true);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean validateFields() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
