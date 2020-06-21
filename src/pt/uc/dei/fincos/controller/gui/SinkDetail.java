/* FINCoS Framework
 * Copyright (C) 2013 CISUC, University of Coimbra
 *
 * Licensed under the terms of The GNU General Public License, Version 2.
 * A copy of the License has been included with this distribution in the
 * fincos-license.txt file.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 */


package pt.uc.dei.fincos.controller.gui;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import pt.uc.dei.fincos.basic.Globals;
import pt.uc.dei.fincos.controller.ConnectionConfig;
import pt.uc.dei.fincos.controller.SinkConfig;


/**
 * GUI for configuration of Sinks.
 *
 * @author  Marcelo R.N. Mendes
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class SinkDetail extends ComponentDetail {

    /** serial id. */
    private static final long serialVersionUID = 594175403586841062L;

    private SinkConfig oldCfg;
    
    private LinkedHashMap<String, String> queryNamesAndTexts;

    private JPopupMenu streamsPopup = new JPopupMenu();
	
	/**
     * Creates new form for editing a Sink configuration.
     *
     * @param sink  a Sink configuration
     *              (used when in editing mode)
     */
    public SinkDetail(SinkConfig sink) {
        super(null);
        initComponents();

        logAllRadio.setSelected(true);
        okBtn.setIcon(new ImageIcon("imgs/ok.png"));
        cancelBtn.setIcon(new ImageIcon("imgs/cancel.png"));
        initConnCombo();

        addListeners();

        this.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);

        if (sink != null) {
            this.oldCfg = sink;
            this.op = UPDATE;
            setTitle("Editing \"" + sink.getAlias() + "\"");
            fillProperties(sink);
        } else {
            this.op = INSERT;
            setTitle("New Sink");
        }

        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    /**
     * Fills the UI with the parameters of a Sink configuration.
     *
     * @param sink  the Sink configuration
     */
    public void fillProperties(SinkConfig sink) {
        this.aliasField.setText(sink.getAlias());
        this.addressField.setText(sink.getAddress().getHostAddress());
        if (sink.getConnection() != null) {
            int connIndex =
                    Controller_GUI.getInstance().getConnectionIndex(sink.getConnection().getAlias());
            this.connCombo.setSelectedIndex(connIndex);
        } else {
            this.connCombo.setSelectedIndex(-1);
        }

        DefaultListModel model = (DefaultListModel) this.streamsList.getModel();
        String[] streams  = sink.getOutputStreamList();

        for (String stream : streams) {
            model.addElement(stream);
        }

        logCheckBox.setSelected(sink.isLoggingEnabled());
        setLoggingEnabled(sink.isLoggingEnabled());
        if (sink.isLoggingEnabled()) {
            if (sink.getFieldsToLog() == Globals.LOG_ALL_FIELDS) {
                logAllRadio.setSelected(true);
            } else {
                logTSRadio.setSelected(true);
            }
            double logSamplRate = sink.getLoggingSamplingRate();
            if (logSamplRate == 1) {
                logSamplingComboBox.setSelectedItem("1");
            } else if (logSamplRate == 0.001) {
                logSamplingComboBox.setSelectedItem("0.001");
            } else {
                logSamplingComboBox.setSelectedItem("" + logSamplRate);
            }
            logFlushField.setText("" + sink.getLogFlushInterval());
        }
    }

    private void setLoggingEnabled(boolean enabled) {
        logAllRadio.setEnabled(enabled);
        logTSRadio.setEnabled(enabled);
        logSamplingLabel.setEnabled(enabled);
        logSamplingComboBox.setEnabled(enabled);
        logFlushLbl.setEnabled(enabled);
        logFlushField.setEnabled(enabled);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logBtnGroup = new javax.swing.ButtonGroup();
        aliasLabel = new javax.swing.JLabel();
        aliasField = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        connLbl = new javax.swing.JLabel();
        connCombo = new javax.swing.JComboBox();
        streamsPanel = new javax.swing.JPanel();
        streamsScrollPane1 = new javax.swing.JScrollPane();
        streamsList = new javax.swing.JList();
        loggingPanel = new javax.swing.JPanel();
        logCheckBox = new javax.swing.JCheckBox();
        logAllRadio = new javax.swing.JRadioButton();
        logTSRadio = new javax.swing.JRadioButton();
        logSamplingLabel = new javax.swing.JLabel();
        logSamplingComboBox = new javax.swing.JComboBox();
        logFlushLbl = new javax.swing.JLabel();
        logFlushField = new javax.swing.JTextField();
        cancelBtn = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        aliasLabel.setText("Alias");

        addressLabel.setText("Address");

        connLbl.setText("Receive Events from:");

        streamsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Streams"));

        streamsList.setModel(new DefaultListModel());
        streamsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        streamsScrollPane1.setViewportView(streamsList);

        javax.swing.GroupLayout streamsPanelLayout = new javax.swing.GroupLayout(streamsPanel);
        streamsPanel.setLayout(streamsPanelLayout);
        streamsPanelLayout.setHorizontalGroup(
            streamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(streamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(streamsScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );
        streamsPanelLayout.setVerticalGroup(
            streamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(streamsPanelLayout.createSequentialGroup()
                .addComponent(streamsScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        loggingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Logging"));

        logCheckBox.setSelected(true);
        logCheckBox.setText("Log Events to Disk");

        logBtnGroup.add(logAllRadio);
        logAllRadio.setText("All Fields");

        logBtnGroup.add(logTSRadio);
        logTSRadio.setText("Only Timestamps");

        logSamplingLabel.setText("Sampling Rate");

        logSamplingComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 ", "0.5", "0.25", "0.2", "0.1", "0.05", "0.025", "0.01", "0.001" }));

        logFlushLbl.setText("Flush Interval");

        logFlushField.setText("10");

        javax.swing.GroupLayout loggingPanelLayout = new javax.swing.GroupLayout(loggingPanel);
        loggingPanel.setLayout(loggingPanelLayout);
        loggingPanelLayout.setHorizontalGroup(
            loggingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loggingPanelLayout.createSequentialGroup()
                .addGroup(loggingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loggingPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(loggingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(logTSRadio)
                            .addComponent(logAllRadio)))
                    .addComponent(logCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(loggingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logSamplingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logSamplingLabel))
                .addGap(18, 18, 18)
                .addGroup(loggingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(logFlushField)
                    .addComponent(logFlushLbl))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        loggingPanelLayout.setVerticalGroup(
            loggingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loggingPanelLayout.createSequentialGroup()
                .addComponent(logCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logAllRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logTSRadio))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loggingPanelLayout.createSequentialGroup()
                .addGroup(loggingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(loggingPanelLayout.createSequentialGroup()
                        .addComponent(logFlushLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logFlushField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(loggingPanelLayout.createSequentialGroup()
                        .addComponent(logSamplingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logSamplingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        cancelBtn.setText("Cancel");

        okBtn.setText("OK");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loggingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(aliasLabel)
                            .addComponent(aliasField, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(addressLabel)
                            .addComponent(addressField, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(connLbl)
                            .addComponent(connCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(14, 14, 14)
                        .addComponent(streamsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(aliasLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aliasField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addressLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(connLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(connCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(streamsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loggingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @SuppressWarnings("deprecation")
	private void initConnCombo() {
        ConnectionConfig[] conns = Controller_GUI.getInstance().getConnections();
        String[] txts = new String[conns.length + 1];
        ImageIcon[] imgs = new ImageIcon[conns.length + 1];
        for (int i = 0; i < conns.length; i++) {
            txts[i] = conns[i].getAlias();
        }
        txts[txts.length - 1] = "New Connection...";
        imgs[txts.length - 1] = new ImageIcon("imgs/connection_new.png");
        ComboBoxRenderer renderer = new ComboBoxRenderer(txts, imgs);
        //renderer.setPreferredSize(new Dimension(200, 130));
        connCombo.setRenderer(renderer);
        Integer[] intArray = new Integer[txts.length];
        for (int i = 0; i < txts.length; i++) {
            intArray[i] = new Integer(i);
            if (imgs[i] != null) {
                imgs[i].setDescription(txts[i]);
            }
        }
        connCombo.setModel(new javax.swing.DefaultComboBoxModel(intArray));
        connCombo.setSelectedIndex(-1);
        connCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connCombo.getSelectedIndex() == connCombo.getModel().getSize() - 1) {
                    openNewConnectionForm();
                }
            }
        });
    }

    private void openNewConnectionForm() {
        ConnectionDetail cDetail = new ConnectionDetail(this, null);
        cDetail.setVisible(true);
    }

    public void updateConnectionsList() {
        initConnCombo();
        connCombo.setSelectedIndex(connCombo.getModel().getSize() - 2);
    }

    private String[] parseStreamsList() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        String queriesFile = "./queries/esper/Q1/Query_Set.xml";
        // Parsing of Queries file
        Document doc = builder.parse(new File(queriesFile));
        Element queriesList = doc.getDocumentElement();
        NodeList queries = queriesList.getElementsByTagName("Query");
        Element query;
        String queryName;
        this.queryNamesAndTexts = new LinkedHashMap<String, String>(queries.getLength());
        String [] streamList;
        // Iterates over list of queries/output streams
        for (int i = 0; i < queries.getLength(); i++) {
            query = (Element) queries.item(i);
            queryName = query.getAttribute("name");
            queryNamesAndTexts.put(queryName,null);
        }
        streamList = queryNamesAndTexts.keySet().toArray(new String[0]);
		return streamList;
    }
    
    private void addListeners() {
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    DefaultListModel model = (DefaultListModel) streamsList.getModel();
                    if (model.size() == 0) {
                        JOptionPane.showMessageDialog(null, "There must be at least one stream.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String[] streamsArr = new String[model.size()];
                    for (int i = 0; i < model.size(); i++) {
                        streamsArr[i] = (String) model.elementAt(i);
                    }
                    SinkConfig newCfg;
                    try {
                        int connIndex = connCombo.getSelectedIndex();
                        ConnectionConfig connCfg = Controller_GUI.getInstance().getConnection(connIndex);
                        int logFlushInterval = logCheckBox.isSelected()
                                               ? Integer.parseInt(logFlushField.getText())
                                               : 10;
                        newCfg = new SinkConfig(aliasField.getText(), InetAddress.getByName(addressField.getText()),
                                connCfg, streamsArr, logCheckBox.isSelected(),
                                logAllRadio.isSelected() ? Globals.LOG_ALL_FIELDS : Globals.LOG_ONLY_TIMESTAMPS,
                                        Double.parseDouble((String) logSamplingComboBox.getSelectedItem()),
                                        logFlushInterval);
                        if (Controller_GUI.getInstance().checkSinkUniqueConstraint(oldCfg, newCfg)) {
                            aliasField.setBackground(UIManager.getColor("TextField.background"));
                            switch (op) {
                            case UPDATE:
                                Controller_GUI.getInstance().updateSink(oldCfg, newCfg);
                                dispose();
                                break;
                            case INSERT:
                                Controller_GUI.getInstance().addSink(newCfg);
                                dispose();
                            }
                        } else {
                            aliasField.setBackground(INVALID_INPUT_COLOR);
                            JOptionPane.showMessageDialog(null, "New configuration violates unique constraint.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (UnknownHostException e2) {
                        JOptionPane.showMessageDialog(null, "Invalid IP address.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "One or more required fields were not correctly filled.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        streamsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                int index = list.locationToIndex(evt.getPoint());
                if (evt.getClickCount() == 2 && index != -1) {
                    String currentName =
                            (String) ((DefaultListModel) streamsList.getModel()).get(index);
                    String[] lista = null; //JAT
    				try {
    					lista = parseStreamsList();
    				} catch (Exception e1) {
    					e1.printStackTrace();
    				}
                    String newName = (String) JOptionPane.showInputDialog(null,"Stream name:","Streams",JOptionPane.QUESTION_MESSAGE,null,lista,currentName); //JAT                 
//                   String newName = JOptionPane.showInputDialog("New stream name:", currentName);
                    if (newName == null) {
                        return;
                    } else if (newName.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Invalid stream name.",
                                                      "Invalid input",
                                                      JOptionPane.ERROR_MESSAGE);
                    } else if (!newName.equals(currentName) && !checkUniqueStreamName(newName)) {
                        JOptionPane.showMessageDialog(null, "Duplicate stream name.",
                                                      "Invalid input",
                                                      JOptionPane.ERROR_MESSAGE);
                    } else {
                        ((DefaultListModel) streamsList.getModel()).setElementAt(newName, index);
                    }

                }
            }
        });

        streamsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteStream();
                }
            }
        });

        streamsList.addMouseListener(new PopupListener(streamsPopup));
        JMenuItem addStream = new JMenuItem("Add...");
        JMenuItem deleteStream = new JMenuItem("Delete");
        deleteStream.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStream();
            }
        });
        streamsPopup.add(addStream);
        streamsPopup.add(deleteStream);

        addStream.addActionListener(new ActionListener() {

			@Override
            public void actionPerformed(ActionEvent e) {
                String[] lista = null; //JAT
				try {
					lista = parseStreamsList();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	String streamName = (String) JOptionPane.showInputDialog(null,"Stream name:","Streams",JOptionPane.QUESTION_MESSAGE,null,lista,lista[1]); //JAT
                if (streamName == null) {
                    return;
                } else if (streamName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Invalid stream name.",
                                                  "Invalid input",
                                                  JOptionPane.ERROR_MESSAGE);
                } else if (!checkUniqueStreamName(streamName)) {
                    JOptionPane.showMessageDialog(null, "Duplicate stream name.",
                                                  "Invalid input",
                                                  JOptionPane.ERROR_MESSAGE);
                } else {
                    ((DefaultListModel) streamsList.getModel()).addElement(streamName);
                }

            }
        });

        logCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLoggingEnabled(logCheckBox.isSelected());
            }
        });
    }

    /**
     * Deletes the selected stream(s) on the Jlist.
     */
    protected void deleteStream() {
        int[] indices = streamsList.getSelectedIndices();

        int removedCount = 0;
        for (int index : indices) {
            ((DefaultListModel) streamsList.getModel()).remove(index - removedCount);
            removedCount++;
        }
    }



    @Override
    protected boolean validateFields() {
        boolean ret = true;
        if (this.aliasField.getText() == null || this.aliasField.getText().isEmpty()) {
            this.aliasField.setBackground(INVALID_INPUT_COLOR);
            ret = false;
        } else {
            this.aliasField.setBackground(UIManager.getColor("TextField.background"));
        }

        if (this.addressField.getText() == null
         || this.addressField.getText().isEmpty()) {
            this.addressField.setBackground(INVALID_INPUT_COLOR);
            ret = false;
        } else {
            this.addressField.setBackground(UIManager.getColor("TextField.background"));
        }

        if (this.connCombo.getSelectedIndex() == -1
         || this.connCombo.getSelectedIndex() >= Controller_GUI.getInstance().
                                                 getConnections().length) {
            this.connCombo.setBackground(INVALID_INPUT_COLOR);
            ret = false;
        } else {
            this.connCombo.setBackground(UIManager.getColor("ComboBox.background"));
        }

        if (logCheckBox.isSelected() && (logFlushField.getText() == null
                || logFlushField.getText().isEmpty())) {
            ret = false;
            this.logFlushField.setBackground(INVALID_INPUT_COLOR);
        } else if (logCheckBox.isSelected()) {
            try {
                Integer.parseInt(logFlushField.getText());
                this.logFlushField.setBackground(UIManager.getColor("TextField.background"));
            } catch (NumberFormatException e) {
                ret = false;
                this.logFlushField.setBackground(INVALID_INPUT_COLOR);
            }
        } else {
            this.logFlushField.setBackground(UIManager.getColor("TextField.background"));
        }

        return ret;
    }

    class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    /**
     * Checks if a given stream name is unique.
     *
     * @param newStream    the stream whose uniqueness must be checked
     * @return             <tt>true</tt> if there is no stream in this Sink with the specified name,
     *                     <tt>true</tt> otherwise.
     */
    private boolean checkUniqueStreamName(String newStream) {
        DefaultListModel model = (DefaultListModel) this.streamsList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (newStream.equals(model.elementAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Disables user input on this form.
     */
    public void disableGUI() {
        this.aliasField.setEnabled(false);
        this.addressField.setEnabled(false);
        this.connLbl.setEnabled(false);
        this.connCombo.setEnabled(false);
        this.streamsList.setEnabled(false);
        this.logCheckBox.setEnabled(false);
        this.logAllRadio.setEnabled(false);
        this.logTSRadio.setEnabled(false);
        this.logSamplingComboBox.setEnabled(false);
        this.logSamplingLabel.setEnabled(false);
        this.logFlushLbl.setEnabled(false);
        this.logFlushField.setEnabled(false);
        this.okBtn.setEnabled(false);

    }

	// Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JTextField aliasField;
    private javax.swing.JLabel aliasLabel;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JComboBox connCombo;
    private javax.swing.JLabel connLbl;
    private javax.swing.JRadioButton logAllRadio;
    private javax.swing.ButtonGroup logBtnGroup;
    private javax.swing.JCheckBox logCheckBox;
    private javax.swing.JTextField logFlushField;
    private javax.swing.JLabel logFlushLbl;
    private javax.swing.JComboBox logSamplingComboBox;
    private javax.swing.JLabel logSamplingLabel;
    private javax.swing.JRadioButton logTSRadio;
    private javax.swing.JPanel loggingPanel;
    private javax.swing.JButton okBtn;
    private javax.swing.JList streamsList;
    private javax.swing.JPanel streamsPanel;
    private javax.swing.JScrollPane streamsScrollPane1;
    // End of variables declaration//GEN-END:variables

}
