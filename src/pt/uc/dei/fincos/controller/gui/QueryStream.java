package pt.uc.dei.fincos.controller.gui;

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
import javax.swing.JMenuItem;
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
import pt.uc.dei.fincos.basic.EventType;

/**
 * 
 * GUI for split the Patterns or queries and assign the types to each attribute
 * 
 * @author Alexander Torres Rivera
 *
 */

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
	private String name, text;
	private ArrayList<String> props = new ArrayList<String>();
	private int engine;
	
    /** List of attributes of this data type. */
    private ArrayList<Attribute> columns;
    
	public QueryStream(String name, String text, EventType st, int engine) {
		super(null);
        this.columns = new ArrayList<Attribute>();
        this.name = name;
        this.text = text;		
		this.engine = engine;
        
        if (st!=null) {
            this.op = 0;
            if (engine == 1){
            	splitEsperQuery(this.text);
            } else if (engine ==2) {
            	splitSiddhiQuery(this.text);
            }
        } else {
        	this.op = 1;
        	setTitle("New Event Type");
        	if (engine == 1){
        		splitEsperQuery(this.text);
        	} else if (engine == 2) {
            	splitSiddhiQuery(this.text);
            }
        }
		
		initComponents(name);
		addListeners();
		
        this.setSize(270, 280);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);

	}
	
	/**
	 * 
	 * Split the Pattern or query and add each Attribute to the ArrayList
	 * 
	 * @param text2		String with the pattern detail
	 */
	private void splitEsperQuery(String text2) {
		int beginIndex = text2.indexOf("select") + 6;
		int endIndex = text2.lastIndexOf(" from");
		String text3 = text2.substring(beginIndex, endIndex);
		String[] parts = text3.split(",");
		for (int j = 0; j < parts.length; j++) {
			String[] split2 = parts[j].split(" ");
			props.add(split2[split2.length - 1]);
		}
	}
	
	/**
	 * 
	 * Split the Pattern or query and add each Attribute to the ArrayList
	 * 
	 * @param text2		String with the pattern detail
	 */
	private void splitSiddhiQuery(String text2) {
		int beginIndex = text2.indexOf("select") + 6;
		int endIndex = text2.lastIndexOf(" insert");
		String text3 = text2.substring(beginIndex, endIndex);
		String[] parts = text3.split(",");
		for (int j = 0; j < parts.length; j++) {
			String[] split2 = parts[j].split(" ");
			props.add(split2[split2.length - 1]);
		}
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
        
        String[] fill = new String[props.size()];
        for(int i=0; i < props.size(); i++) {
        	fill[i] = props.get(i);
        }
        
        propertyCombo.setModel(new DefaultComboBoxModel(fill));
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
		
        okBtn.setIcon(new ImageIcon("imgs/ok.png"));
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                try {
                    if (validateFields()) {               
                        Attribute[] atts = new Attribute[columns.size()];
                        atts = columns.toArray(atts);
                        EventType newType = new EventType(name, atts);
                        switch (op) {
                        case 0:
                        	if (engine == 1) {
                            	WriteStream.updateEventType(name, newType, 1);
                        	} else if (engine ==2) {
                        		WriteSiddhiStream.updateEventType(name, newType, 1);
                            }
                            dispose();
                            break;
                        case 1:
                        	if (engine == 1) {
                        		WriteStream.addEventType(newType, 1);
                        	} else if (engine == 2) {
                        		WriteSiddhiStream.addEventType(newType, 1);
                        	}
                        	dispose();      
                        }
                    } else {
                    	JOptionPane.showMessageDialog(null, "You must assign a type to every attribute","Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(null, exc.getMessage());
                }
            }
        });
        
        detailTable.addMouseListener(new PopupListener(detailTablePopup));
        
        JMenuItem deleteColMenuItem = new JMenuItem("Remove");
        
        deleteColMenuItem.addActionListener(new ActionListener() {
            @SuppressWarnings("unchecked")
			@Override
            public void actionPerformed(ActionEvent e) {
            	DefaultTableModel deleteModel = (DefaultTableModel) detailTable.getModel();
            	if (detailTable.getSelectedRowCount() == 1) {
                	int row = detailTable.getSelectedRow();
                	columns.remove(row);
                	String value = deleteModel.getValueAt(row, 0).toString();
                    propertyCombo.addItem(value);
                	deleteModel.removeRow(row);             
            	} else {
                    JOptionPane.showMessageDialog(null, "Select a column to delete");
                }
            }
        });
        
        detailTablePopup.add(deleteColMenuItem);
	}

	@Override
	protected boolean validateFields() throws Exception {
		boolean ret= true;
		String propertyName = (String) propertyCombo.getSelectedItem();
		if (propertyName == null) {
			ret = true;
		} else {
			ret = false;
			propertyCombo.setBackground(INVALID_INPUT_COLOR);
		}
		return ret;
	}

}
