package pt.uc.dei.fincos.controller.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import pt.uc.dei.fincos.basic.Attribute;
import pt.uc.dei.fincos.basic.Datatype;

@SuppressWarnings("serial")
public class QueryStream extends ComponentDetail{
	
	private JTable detailTable;
	private JLabel propertyLabel, typeLabel;
	private JButton okBtn, cancelBtn, addBtn;
	private JPanel schemaPanel;
	private JScrollPane scrollType;
	@SuppressWarnings("rawtypes")
	private JComboBox dataTypeCombo, propertyCombo;
	private JPopupMenu detailTablePopup;

    /** List of attributes of this data type. */
    private ArrayList<Attribute> columns;
    
	public QueryStream(String name, String text) {
		super(null);
        this.columns = new ArrayList<Attribute>();
		initComponents(name);
		addListeners();
		
        this.setSize(270, 280);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponents(String name) {
		detailTable = new JTable();
		propertyLabel = new JLabel();
		typeLabel = new JLabel();
		okBtn = new JButton();
		cancelBtn = new JButton();
		addBtn = new JButton();
        schemaPanel = new JPanel();
        scrollType = new JScrollPane();
        dataTypeCombo = new JComboBox();
        propertyCombo = new JComboBox();
        detailTablePopup = new JPopupMenu();
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(name);
        getContentPane().setLayout(null);
        
        schemaPanel.setBounds(30,83,200,130);
        schemaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),"Properties"));    
        this.getContentPane().add(schemaPanel);
        
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(null);
        cancelBtn.setBounds(43,212,100,34);
        add(cancelBtn);
        
        okBtn.setText("OK");
        okBtn.setPreferredSize(null);
        okBtn.setBounds(145,212,100,34);
        add(okBtn);
        
        propertyLabel.setText("Property");
        propertyLabel.setBounds(14,10,100,20);
        add(propertyLabel);
        
        typeLabel.setText("Type");
        typeLabel.setBounds(120,10,100,20);
        add(typeLabel);
        
        String[] props = { "P1", "P2", "P3", "P4"};
        
        propertyCombo.setModel(new DefaultComboBoxModel(props));
        propertyCombo.setBounds(14,30,110,25);
        add(propertyCombo);
        
        dataTypeCombo.setModel(new DefaultComboBoxModel(new String[] { "BOOLEAN", "DOUBLE", "FLOAT", "INTEGER", "LONG", "TEXT" }));
        dataTypeCombo.setBounds(120,30,110,25);
        add(dataTypeCombo);
        
        addBtn.setText("Add property");
        addBtn.setPreferredSize(null);
        addBtn.setBounds(64,60,120,20);
        add(addBtn);
        
        String [] columnsName = {"Name", "Data Type"};
        Object	[][] data = {};
        DefaultTableModel model = new DefaultTableModel(data,columnsName) {
            Class[] types = new Class [] {
                    String.class, String.class
                };
                boolean[] canEdit = new boolean [] {
                    false, false
                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            };

        detailTable.setModel(model);
        
        detailTable.setPreferredScrollableViewportSize(new Dimension(150, 70));
        scrollType.setViewportView(detailTable);
        schemaPanel.add(scrollType);
	}

	private void addListeners() {
		cancelBtn.setIcon(new ImageIcon("imgs/cancel.png"));
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        /**
         * 
         * Adds the data to the JTable and the columns Array List with the properties of the event.
         *
         */
        addBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
					String propertyName = (String) propertyCombo.getSelectedItem();
					if (propertyName == null) {
						JOptionPane.showMessageDialog(null, "There is no Streams to add", "Finished", JOptionPane.WARNING_MESSAGE);
					} else {
					String type = (String) dataTypeCombo.getSelectedItem();
					String[] data = {propertyName,type};
					DefaultTableModel newModel = (DefaultTableModel) detailTable.getModel();
					newModel.addRow(data);					
                    Datatype dataType = null;
                    if (type.equals("INTEGER")) {
                        dataType = Datatype.INTEGER;
                    } else if (type.equals("LONG")) {
                        dataType = Datatype.LONG;
                    } else if (type.equals("FLOAT")) {
                        dataType = Datatype.FLOAT;
                    } else if (type.equals("DOUBLE")) {
                        dataType = Datatype.DOUBLE;
                    } else if (type.equals("TEXT")) {
                        dataType = Datatype.TEXT;
                    } else if (type.equals("BOOLEAN")) {
                        dataType = Datatype.BOOLEAN;
                    }
					Attribute att = new Attribute(dataType, propertyName);					
					columns.add(att);
					propertyCombo.removeItem(propertyName);
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
