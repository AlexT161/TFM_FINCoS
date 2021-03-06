package pt.uc.dei.fincos.controller.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

/**
 *	Edit or delete the selected pattern depends on the mode selected
 *	0 for edit, > 0 for delete
 *
 * @author John Alexander Torres
 *
 */
@SuppressWarnings("serial")
public class EditPattern extends ComponentDetail{

	@SuppressWarnings("rawtypes")
	private JComboBox patternCombo;
	private JLabel nameLabel;
	private JButton setBtn, delBtn, cancelBtn;
	private HashMap<String, String> list;
	private int engine;
	private String[] combo;
    
	/**
	 * Edit or delete the selected pattern depends on the mode selected
	 * 
	 * @param mode	0 for edit, > 0 for delete
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public EditPattern(HashMap<String, String> list, int mode, int engine) throws ParserConfigurationException, SAXException, IOException {
		super(null);
		this.list = list;
		this.engine = engine;
		this.combo = new String[list.size()];
		initComponents(mode);
		addListeners();

		this.setSize(250, 150);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
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
						WritePattern.updatePattern(type,null,engine);
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
