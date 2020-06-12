
package pt.uc.dei.fincos.controller.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import pt.uc.dei.fincos.basic.Attribute;
import pt.uc.dei.fincos.basic.Datatype;
import pt.uc.dei.fincos.basic.EventType;
import pt.uc.dei.fincos.basic.Globals;


/**
 * GUI for configuration of Schemas.
 *
 * @author  John Alexander Torres
 *
 */
@SuppressWarnings("serial")
public class SchemaDetail extends ComponentDetail{

	private JButton okBtn;
	private JButton cancelBtn;
	private JButton addBtn;
	private JTable detailTable;
	private JTextField eventNameField;
	private JTextField propertyNameField;
	private JLabel typeLabel;
	private JLabel propertyLabel;
	private JPanel schemaPanel;
	private JScrollPane scrollType;
	private JPopupMenu detailTablePopup;
	
	/** Path for the file containing the Streams. */
    public static final String STREAM_SET_FILE = Globals.APP_PATH + "queries" + File.separator + "esper" + File.separator + "Q1" + File.separator + "Prueba_set.xml";
	
	/** Previous properties of the data type (when the form is open for update). */
    private String oldType;
    
    /** List of attributes of this data type. */
    private ArrayList<Attribute> columns;
    
	@SuppressWarnings("rawtypes")
	private JComboBox dataTypeCombo; 
    
    
	public SchemaDetail(EventType st){
		super(null);
        this.columns = new ArrayList<Attribute>();
        initComponents();
		addListeners();	

		if (st!=null) {
            this.op = UPDATE;
            this.oldType = st.getName();
            fillProperties(st);
        } else {
        	this.op = INSERT;
        	setTitle("New Event Type");
        }
		
        this.setSize(270, 350);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {
		detailTable = new JTable();
		typeLabel = new JLabel();
		propertyLabel = new JLabel();
		okBtn = new JButton();
		cancelBtn = new JButton();
		addBtn = new JButton();
		eventNameField = new JTextField();
		propertyNameField = new JTextField();
        schemaPanel = new JPanel();
        scrollType = new JScrollPane();
        dataTypeCombo = new JComboBox();
        detailTablePopup = new JPopupMenu();
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Event Type Detail");
        getContentPane().setLayout(null);

        typeLabel.setText("Event Type:");
        typeLabel.setBounds(14,10,100,25);
        add(typeLabel);
        
        eventNameField.setBounds(14,38,100,25);
        add(eventNameField);
        
        schemaPanel.setBounds(30,153,200,130);
        schemaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),"Properties"));    
        this.getContentPane().add(schemaPanel);
        
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(null);
        cancelBtn.setBounds(43,282,100,34);
        add(cancelBtn);
        
        okBtn.setText("OK");
        okBtn.setPreferredSize(null);
        okBtn.setBounds(145,282,100,34);
        add(okBtn);
        
        propertyLabel.setText("Property:");
        propertyLabel.setBounds(14,70,100,20);
        add(propertyLabel);
        
        propertyNameField.setBounds(14,100,100,25);
        add(propertyNameField);
        
        dataTypeCombo.setModel(new DefaultComboBoxModel(new String[] { "BOOLEAN", "DOUBLE", "FLOAT", "INTEGER", "LONG", "TEXT" }));
        dataTypeCombo.setBounds(120,100,110,25);
        add(dataTypeCombo);
        
        addBtn.setText("Add property");
        addBtn.setPreferredSize(null);
        addBtn.setBounds(64,130,120,20);
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
        okBtn.setIcon(new ImageIcon("imgs/ok.png"));
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                try {
                    if (validateFields()) {
                        String typeName = eventNameField.getText();                        
                        Attribute[] atts = new Attribute[columns.size()];
                        atts = columns.toArray(atts);
                        EventType newType = new EventType(typeName, atts);
                        switch (op) {
                        case UPDATE:
                            WriteStream.updateEventType(oldType,newType,0);
                            dispose();
                            break;
                        case INSERT:
                        	File f = new File(STREAM_SET_FILE);
                            if (f.exists()) {
                            	WriteStream.open(STREAM_SET_FILE);
                            	HashMap<String[], EventType> list = WriteStream.loadStreams();
                            	boolean ver = true;
                            	for (String[] i : list.keySet()) {           			
                            		if(eventNameField.getText().equals(i[0]) && i[1]=="Input") {
                            			ver = false;
                            		}
                            	}
                            	if(ver == false) {
                            		eventNameField.setBackground(INVALID_INPUT_COLOR);
                            		JOptionPane.showMessageDialog(null, "Stream Name already used.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                            	}                   		
                            	else{
                            		WriteStream.addEventType(newType,0);
                            		dispose();
                            	}
                            } else {
                        		WriteStream.addEventType(newType,0);
                        		dispose();
                        	}
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "One or more required fields were not correctly filled.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(null, exc.getMessage());
                }
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
				//Validation (no leave blank data)
				if(propertyNameField.getText().equals("")) {
					JOptionPane.showMessageDialog(null,"Please fill out all data fields");
				} else {
					String type = (String) dataTypeCombo.getSelectedItem();
					String propertyName = propertyNameField.getText();
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
	                propertyNameField.setText("");
				}
			}
        });
        
        detailTable.addMouseListener(new PopupListener(detailTablePopup));
        
        JMenuItem deleteColMenuItem = new JMenuItem("Delete");
        
        deleteColMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	DefaultTableModel deleteModel = (DefaultTableModel) detailTable.getModel();
            	if (detailTable.getSelectedRowCount()==1) {
                	int row = detailTable.getSelectedRow();
                	columns.remove(row);
                    deleteModel.removeRow(row);
            	} else {
                    JOptionPane.showMessageDialog(null, "Select a column to delete");
                }
            }
        });
        
        detailTablePopup.add(deleteColMenuItem);
                
        detailTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
               int row = e.getFirstRow();
               int col = e.getColumn();
               if (row < detailTable.getRowCount() && col != -1
                   && detailTable.isEditing()) {
                   String newAttName = (String) detailTable.getValueAt(row, col);
                   Attribute oldAtt = columns.get(row);
                   Attribute newAtt = oldAtt.clone();
                   newAtt.setName(newAttName);
                   updateColumn(oldAtt, newAtt);
               }
            }
          });
	}
	
    /**
     * Fills the UI with the data type properties passed as argument.
     *
     * @param type   Driver configuration properties to be shown in UI, when in UPDATE mode,
     *               or <tt>null</tt>, in INSERTION mode.
     */
	public void fillProperties(EventType type) {
        this.eventNameField.setText(type.getName());

        Attribute[] atts = type.getAttributes();
        DefaultTableModel model = (DefaultTableModel) detailTable.getModel();

        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            model.removeRow(0);
        }

        for (int i = 0; i < atts.length; i++) {
            this.columns.add(atts[i]);
            model.addRow(new Object[] {atts[i].getName(), atts[i].getType()});
        }
    }

	@Override
    public boolean validateFields(){
        boolean ret = true;
        if (eventNameField.getText() == null || eventNameField.getText().isEmpty()) {
            eventNameField.setBackground(INVALID_INPUT_COLOR);
            ret = false;
        } else {
            Color defaultColor = UIManager.getColor("TextField.background");
            eventNameField.setBackground(defaultColor);
        }
        if (detailTable.getRowCount() < 1) {
            detailTable.setBackground(INVALID_INPUT_COLOR);
            ret = false;
        } else {
            Color defaultColor = UIManager.getColor("Table.background");
            detailTable.setBackground(defaultColor);
        }

        return ret;
	}
	
    /**
     * Updates the definition of an attribute of this data type.
     *
     * @param oldColumn     the old attribute configuration
     * @param newColumn     the new attribute configuration
     */
    public void updateColumn(Attribute oldColumn, Attribute newColumn) {
        int index = this.columns.indexOf(oldColumn);

        if (index > -1) {
            removeColumn(index);
            addColumn(index, newColumn);
        }
    }

    /**
     * Adds an attribute to this data type.
     *
     * @param index         attribute index
     * @param newColumn     the new attribute
     */
    public void addColumn(int index, Attribute newColumn) {
        this.columns.add(index, newColumn);
        ((DefaultTableModel) this.detailTable.getModel()).insertRow(index, new Object[] {newColumn.getName(), newColumn.getType()});
    }
    
    /**
     * Adds an attribute to this data type.
     *
     * @param newColumn     the new attribute
     */
    public void addColumn(Attribute newColumn) {
        this.columns.add(newColumn);
        DefaultTableModel model = ((DefaultTableModel) this.detailTable.getModel());
        model.insertRow(model.getRowCount() - 1, new Object[] {newColumn.getName(), newColumn.getType()});
    }
    
    /**
     * Removes an attribute to this data type.
     *
     * @param index     the position in the Array List
     */
    private void removeColumn(int index) {
        this.columns.remove(index);
        ((DefaultTableModel) detailTable.getModel()).removeRow(index);
    }
}
