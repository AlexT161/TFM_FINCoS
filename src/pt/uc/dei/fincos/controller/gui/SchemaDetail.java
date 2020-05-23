
package pt.uc.dei.fincos.controller.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import pt.uc.dei.fincos.basic.Attribute;
import pt.uc.dei.fincos.basic.EventType;


/**
 * GUI for configuration of Schemas.
 *
 * @author  John Alexander Torres
 *
 */
public class SchemaDetail extends ComponentDetail{

	private JButton okBtn;
	private JButton cancelBtn;
	private JTable detailTable;
	private JLabel typeLabel;
	private JTextField eventNameField;
    private JScrollPane schemaScroll;
    private JPanel schemaPanel;
	
	/** Previous properties of the data type (when the form is open for update). */
    private EventType oldType;
    
    /** List of attributes of this data type. */
    private ArrayList<Attribute> columns;    
    
    
	public SchemaDetail(EventType st){ //Cambiar cuando se tenga el StreamList
		super(null);
		initComponents();
		
        if (st != null) {
            this.oldType = st;
            this.op = UPDATE;
            fillProperties(st);
        } else {
            this.op = INSERT;
            setTitle("New Event Type");
        }
        
 //       okBtn.setIcon(new ImageIcon("imgs/ok.png"));
 //       cancelBtn.setIcon(new ImageIcon("imgs/cancel.png"));
        
        this.setSize(200, 300);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
		
	}
	
	private void initComponents() {
		JTable detailTable = new JTable();
		JLabel typeLabel = new JLabel();
		JButton okBtn = new JButton();
		JButton cancelBtn = new JButton();
		JTextField eventNameField = new JTextField();
        JScrollPane schemaScroll = new JScrollPane();
        JPanel schemaPanel = new JPanel();
        
        this.getContentPane().add(schemaPanel);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Event Type Detail");
        
        schemaPanel.setPreferredSize(new Dimension(200, 200));
        schemaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),"Stream Schema"));
        typeLabel.setText("Event Type:");
        
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(null);

        okBtn.setText("OK");
        okBtn.setPreferredSize(null);
        
        detailTable.setModel(new DefaultTableModel(
                new Object [][] {
                    {null, null}
                },
                new String [] {
                    "Name", "Data Type"
                }
        	){
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
                };
                boolean[] canEdit = new boolean [] {
                    true, false
                };
                
                @Override
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
        detailTable.setMaximumSize(new Dimension(200, 150));
        schemaScroll.setViewportView(detailTable);

        schemaPanel.add(detailTable);
        
        
        
       GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(eventNameField, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                            .addComponent(typeLabel))
                        .addGap(160, 160, 160))
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(schemaScroll, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                            .addComponent(cancelBtn, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(okBtn, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))))
                .addGap(19, 19, 19))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(typeLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eventNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(schemaScroll, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(cancelBtn, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
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
            model.addRow(new Object[] {atts[i].getName(), atts[i].getType(), atts[i].getDomain()});
        }

        model.addRow(new Object[] {null, null, null});
    }

	@Override
	protected boolean validateFields() throws Exception { //temporal
		// TODO Auto-generated method stub
		return false;
	}

	
}
