
package pt.uc.dei.fincos.controller.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import pt.uc.dei.fincos.basic.Attribute;
import pt.uc.dei.fincos.basic.EventType;


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
	private JTable detailTable;
	private JTextField eventNameField;
	private JLabel typeLabel;
	private JPanel schemaPanel;
	private JScrollPane scrollType;
	
	/** Previous properties of the data type (when the form is open for update). */
    private EventType oldType;
    
    /** List of attributes of this data type. */
    private ArrayList<Attribute> columns;
	@SuppressWarnings("rawtypes")
	private JComboBox dataTypeCombo;    
    
    
	public SchemaDetail(EventType st){
		super(null);
        this.columns = new ArrayList<Attribute>();
		initComponents();
		addListeners();
		
        if (st != null) {
            this.oldType = st;
            this.op = UPDATE;
            fillProperties(st);
        } else {
            this.op = INSERT;
            setTitle("New Event Type");
        }
        
        this.setSize(250, 300);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {
		detailTable = new JTable();
		typeLabel = new JLabel();
		okBtn = new JButton();
		cancelBtn = new JButton();
		eventNameField = new JTextField();
        schemaPanel = new JPanel();
        scrollType = new JScrollPane();
        dataTypeCombo = new JComboBox();
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Event Type Detail");
        getContentPane().setLayout(null);

        typeLabel.setText("Event Type:");
        typeLabel.setBounds(14,10,100,25);
        add(typeLabel);
        eventNameField.setBounds(14,38,100,25);
        add(eventNameField);
        
        schemaPanel.setBounds(30,100,200,130);
        schemaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),"Properties"));    
        this.getContentPane().add(schemaPanel);
        
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(null);
        cancelBtn.setBounds(43,240,100,34);
        add(cancelBtn);
        
        okBtn.setText("OK");
        okBtn.setPreferredSize(null);
        okBtn.setBounds(145,240,100,34);
        add(okBtn);
        
        String [] columnsName = {"Name", "Data Type"};
        Object	[][] data = {null};
        DefaultTableModel model = new DefaultTableModel(data,columnsName) {
            Class[] types = new Class [] {
                    String.class, JComboBox.class
                };
                boolean[] canEdit = new boolean [] {
                    true, true
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

        dataTypeCombo.setModel(new DefaultComboBoxModel(new String[] { "BOOLEAN", "DOUBLE", "FLOAT", "INTEGER", "LONG", "TEXT" }));
        detailTable.setModel(model);
        TableColumn tc = detailTable.getColumnModel().getColumn(1);
        TableCellEditor tce = new DefaultCellEditor(dataTypeCombo);
        tc.setCellEditor(tce);
        
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
            public void actionPerformed(ActionEvent e) {            	
            	System.out.println("SchemaDetail: Guardar Stream");
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

        model.addRow(new Object[] {null, null});
    }

	@Override
	protected boolean validateFields() throws Exception { //temporal
		// TODO Auto-generated method stub
		return false;
	}

	
}
