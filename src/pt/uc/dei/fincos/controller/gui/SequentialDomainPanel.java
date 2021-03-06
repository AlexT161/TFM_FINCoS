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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.UIManager;

import pt.uc.dei.fincos.basic.SequentialDomain;
import pt.uc.dei.fincos.random.ConstantVariate;
import pt.uc.dei.fincos.random.RandomExponentialVariate;
import pt.uc.dei.fincos.random.RandomNormalVariate;
import pt.uc.dei.fincos.random.RandomUniformVariate;
import pt.uc.dei.fincos.random.Variate;

/**
 *
 * GUI for comfiguring sequential domains.
 *
 * @author  Marcelo R.N. Mendes
 *
 *
 */
@SuppressWarnings("rawtypes")
public final class SequentialDomainPanel extends javax.swing.JPanel {

    /** serial id. */
    private static final long serialVersionUID = 5551963651687036502L;

    /** Creates new form SequentialDomainPanel. */
    public SequentialDomainPanel() {
        initComponents();
        addListeners();
        initialConstantRadio.setSelected(true);
        incrementConstantRadio.setSelected(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        initialBtnGroup = new javax.swing.ButtonGroup();
        incrementBtnGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        initialConstantRadio = new javax.swing.JRadioButton();
        initialRandomRadio = new javax.swing.JRadioButton();
        initialConstantField = new javax.swing.JTextField();
        initialRandomVariateCombo = new javax.swing.JComboBox();
        initialParam1Field = new javax.swing.JTextField();
        initialParam1Lbl = new javax.swing.JLabel();
        initialParam2Lbl = new javax.swing.JLabel();
        initialParam2Field = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        incrementConstantRadio = new javax.swing.JRadioButton();
        incrementRandomRadio = new javax.swing.JRadioButton();
        incrementConstantField = new javax.swing.JTextField();
        incrementRandomVariateCombo = new javax.swing.JComboBox();
        incrParam1Field = new javax.swing.JTextField();
        incrParam1Lbl = new javax.swing.JLabel();
        incrParam2Lbl = new javax.swing.JLabel();
        incrParam2Field = new javax.swing.JTextField();

        setMaximumSize(new java.awt.Dimension(266, 211));
        setMinimumSize(new java.awt.Dimension(266, 211));
        setPreferredSize(new java.awt.Dimension(266, 211));

        jLabel1.setText("Initial Value:");

        initialBtnGroup.add(initialConstantRadio);
        initialConstantRadio.setFont(new java.awt.Font("Tahoma", 0, 10));
        initialConstantRadio.setText("Constant:");

        initialBtnGroup.add(initialRandomRadio);
        initialRandomRadio.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        initialRandomRadio.setSelected(true);
        initialRandomRadio.setText("Random Variate:");

        initialConstantField.setFont(new java.awt.Font("Tahoma", 0, 10));

        initialRandomVariateCombo.setFont(new java.awt.Font("Tahoma", 0, 10));
        initialRandomVariateCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Uniform", "Normal", "Exponential" }));

        initialParam1Field.setFont(new java.awt.Font("Tahoma", 0, 10));

        initialParam1Lbl.setFont(new java.awt.Font("Tahoma", 0, 10));
        initialParam1Lbl.setText("lower");

        initialParam2Lbl.setFont(new java.awt.Font("Tahoma", 0, 10));
        initialParam2Lbl.setText("upper");

        initialParam2Field.setFont(new java.awt.Font("Tahoma", 0, 10));

        jLabel2.setText("Increment:");

        incrementBtnGroup.add(incrementConstantRadio);
        incrementConstantRadio.setFont(new java.awt.Font("Tahoma", 0, 10));
        incrementConstantRadio.setText("Constant:");

        incrementBtnGroup.add(incrementRandomRadio);
        incrementRandomRadio.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        incrementRandomRadio.setSelected(true);
        incrementRandomRadio.setText("Random Variate:");

        incrementConstantField.setFont(new java.awt.Font("Tahoma", 0, 10));

        incrementRandomVariateCombo.setFont(new java.awt.Font("Tahoma", 0, 10));
        incrementRandomVariateCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Uniform", "Normal", "Exponential" }));

        incrParam1Field.setFont(new java.awt.Font("Tahoma", 0, 10));

        incrParam1Lbl.setFont(new java.awt.Font("Tahoma", 0, 10));
        incrParam1Lbl.setText("lower");

        incrParam2Lbl.setFont(new java.awt.Font("Tahoma", 0, 10));
        incrParam2Lbl.setText("upper");

        incrParam2Field.setFont(new java.awt.Font("Tahoma", 0, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(initialRandomRadio)
                                    .addComponent(initialConstantRadio))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(initialConstantField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(initialRandomVariateCombo, 0, 143, Short.MAX_VALUE)))
                            .addComponent(jLabel1)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(initialParam1Lbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(initialParam1Field, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(initialParam2Lbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(initialParam2Field, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(incrementRandomRadio)
                            .addComponent(incrementConstantRadio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(incrementConstantField, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(incrementRandomVariateCombo, 0, 143, Short.MAX_VALUE)))
                    .addComponent(jLabel2))
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(incrParam1Lbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(incrParam1Field, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(incrParam2Lbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(incrParam2Field, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(initialConstantRadio)
                    .addComponent(initialConstantField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(initialRandomRadio)
                    .addComponent(initialRandomVariateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(initialParam1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initialParam1Lbl)
                    .addComponent(initialParam2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initialParam2Lbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(incrementConstantRadio)
                    .addComponent(incrementConstantField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(incrementRandomRadio)
                    .addComponent(incrementRandomVariateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(incrParam1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(incrParam1Lbl)
                    .addComponent(incrParam2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(incrParam2Lbl))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addListeners() {
        ItemListener l1 = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                initialConstantField.setEnabled(initialConstantRadio.isSelected());
                initialRandomVariateCombo.setEnabled(initialRandomRadio.isSelected());
                initialParam1Lbl.setEnabled(initialRandomRadio.isSelected());
                initialParam1Field.setEnabled(initialRandomRadio.isSelected());
                initialParam2Lbl.setEnabled(initialRandomRadio.isSelected());
                initialParam2Field.setEnabled(initialRandomRadio.isSelected());
                Color defaultColor = UIManager.getColor("TextField.background");
                if (!initialConstantRadio.isSelected()) {
                    initialConstantField.setBackground(defaultColor);
                } else {
                    initialParam1Field.setBackground(defaultColor);
                    initialParam2Field.setBackground(defaultColor);
                }
            }
        };
        initialConstantRadio.addItemListener(l1);
        initialRandomRadio.addItemListener(l1);

        ItemListener l2 = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                incrementConstantField.setEnabled(incrementConstantRadio.isSelected());
                incrementRandomVariateCombo.setEnabled(incrementRandomRadio.isSelected());
                incrParam1Lbl.setEnabled(incrementRandomRadio.isSelected());
                incrParam1Field.setEnabled(incrementRandomRadio.isSelected());
                incrParam2Lbl.setEnabled(incrementRandomRadio.isSelected());
                incrParam2Field.setEnabled(incrementRandomRadio.isSelected());
                Color defaultColor = UIManager.getColor("TextField.background");
                if (!incrementConstantRadio.isSelected()) {
                    incrementConstantField.setBackground(defaultColor);
                } else {
                    incrParam1Field.setBackground(defaultColor);
                    incrParam2Field.setBackground(defaultColor);
                }
            }
        };
        incrementConstantRadio.addItemListener(l2);
        incrementRandomRadio.addItemListener(l2);

        initialConstantRadio.setSelected(true);
        incrementConstantRadio.setSelected(true);

        initialRandomVariateCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (initialRandomVariateCombo.getSelectedItem().equals("Uniform")) {
                    initialParam1Lbl.setText("lower");
                    initialParam2Lbl.setText("upper");
                    initialParam2Lbl.setVisible(true);
                    initialParam2Field.setVisible(true);
                } else if (initialRandomVariateCombo.getSelectedItem().equals("Normal")) {
                    initialParam1Lbl.setText("mean");
                    initialParam2Lbl.setText("stdev");
                    initialParam2Lbl.setVisible(true);
                    initialParam2Field.setVisible(true);
                } else if (initialRandomVariateCombo.getSelectedItem().equals("Exponential")) {
                    initialParam1Lbl.setText("lambda");
                    initialParam2Lbl.setVisible(false);
                    initialParam2Field.setVisible(false);
                }
            }
        });

        incrementRandomVariateCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (incrementRandomVariateCombo.getSelectedItem().equals("Uniform")) {
                    incrParam1Lbl.setText("lower");
                    incrParam2Lbl.setText("upper");
                    incrParam2Lbl.setVisible(true);
                    incrParam2Field.setVisible(true);
                } else if (incrementRandomVariateCombo.getSelectedItem().equals("Normal")) {
                    incrParam1Lbl.setText("mean");
                    incrParam2Lbl.setText("stdev");
                    incrParam2Lbl.setVisible(true);
                    incrParam2Field.setVisible(true);
                } else if (incrementRandomVariateCombo.getSelectedItem().equals("Exponential")) {
                    incrParam1Lbl.setText("lambda");
                    incrParam2Lbl.setVisible(false);
                    incrParam2Field.setVisible(false);
                }
            }
        });
    }

    public void fillProperties(SequentialDomain domain) {
        Variate initialVariate = domain.getInitialVariate();
        Variate incrementVariate = domain.getIncrementVariate();

        if (initialVariate instanceof ConstantVariate) {
            initialConstantRadio.setSelected(true);
            initialConstantField.setText(((ConstantVariate) initialVariate).getValue() + "");
        } else {
            initialRandomRadio.setSelected(true);
            if (initialVariate instanceof RandomExponentialVariate) {
                initialRandomVariateCombo.setSelectedItem("Exponential");
                initialParam1Field.setText(((RandomExponentialVariate) initialVariate).getLambda() + "");
            } else if (initialVariate instanceof RandomNormalVariate) {
                initialRandomVariateCombo.setSelectedItem("Normal");
                initialParam1Field.setText(((RandomNormalVariate) initialVariate).getMean() + "");
                initialParam2Field.setText(((RandomNormalVariate) initialVariate).getStdev() + "");
            } else if (initialVariate instanceof RandomUniformVariate) {
                initialRandomVariateCombo.setSelectedItem("Uniform");
                initialParam1Field.setText(((RandomUniformVariate) initialVariate).getLower() + "");
                initialParam2Field.setText(((RandomUniformVariate) initialVariate).getUpper() + "");
            }
        }

        if (incrementVariate instanceof ConstantVariate) {
            incrementConstantRadio.setSelected(true);
            incrementConstantField.setText(((ConstantVariate) incrementVariate).getValue() + "");
        } else {
            incrementRandomRadio.setSelected(true);
            if (incrementVariate instanceof RandomExponentialVariate) {
                incrementRandomVariateCombo.setSelectedItem("Exponential");
            } else if (incrementVariate instanceof RandomNormalVariate) {
                incrementRandomVariateCombo.setSelectedItem("Normal");
                incrParam1Field.setText(((RandomNormalVariate) incrementVariate).getMean() + "");
                incrParam2Field.setText(((RandomNormalVariate) incrementVariate).getStdev() + "");
            } else if (incrementVariate instanceof RandomUniformVariate) {
                incrementRandomVariateCombo.setSelectedItem("Uniform");
                incrParam1Field.setText(((RandomUniformVariate) incrementVariate).getLower() + "");
                incrParam2Field.setText(((RandomUniformVariate) incrementVariate).getUpper() + "");
            }
        }
    }

    /**
    *
    * Checks if the fields in the UI have been correctly filled.
    *
    * @return  <tt>true</tt> if all the parameters have been correctly filled,
    *          <tt>false</tt> otherwise
    */
    public boolean validateFields() {
        boolean ret = true;

        if (initialConstantRadio.isSelected()) {
            if (initialConstantField.getText() == null
             || initialConstantField.getText().isEmpty()) {
                initialConstantField.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                ret = false;
            } else {
                try {
                    String initConst = initialConstantField.getText();
                    Double.parseDouble(initConst);
                    Color defaultColor = UIManager.getColor("TextField.background");
                    initialConstantField.setBackground(defaultColor);
                } catch (NumberFormatException nfe) {
                    initialConstantField.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                    ret = false;
                }
            }
        }

        if (initialRandomRadio.isSelected()) {
            if (initialParam1Field.getText() == null
             || initialParam1Field.getText().isEmpty()) {
                initialParam1Field.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                ret = false;
            } else {
                try {
                    String p1 = initialParam1Field.getText();
                    Double.parseDouble(p1);
                    Color defaultColor = UIManager.getColor("TextField.background");
                    initialParam1Field.setBackground(defaultColor);
                } catch (NumberFormatException nfe) {
                    initialParam1Field.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                    ret = false;
                }
            }

            if (!initialRandomVariateCombo.getSelectedItem().equals("Exponential")) {
                if (initialParam2Field.getText() == null
                 || initialParam2Field.getText().isEmpty()) {
                    initialParam2Field.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                    ret = false;
                } else {
                    try {
                        String p2 = initialParam2Field.getText();
                        Double.parseDouble(p2);
                        Color defaultColor = UIManager.getColor("TextField.background");
                        initialParam2Field.setBackground(defaultColor);
                    } catch (NumberFormatException nfe) {
                        initialParam2Field.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                        ret = false;
                    }
                }
            }
        }

        if (incrementConstantRadio.isSelected()) {
            if (incrementConstantField.getText() == null
             || incrementConstantField.getText().isEmpty()) {
                incrementConstantField.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                ret = false;
            } else {
                try {
                    String initConst = incrementConstantField.getText();
                    Double.parseDouble(initConst);
                    Color defaultColor = UIManager.getColor("TextField.background");
                    incrementConstantField.setBackground(defaultColor);
                } catch (NumberFormatException nfe) {
                    incrementConstantField.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                    ret = false;
                }
            }
        }

        if (incrementRandomRadio.isSelected()) {
            if (incrParam1Field.getText() == null
             || incrParam1Field.getText().isEmpty()) {
                incrParam1Field.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                ret = false;
            } else {
                try {
                    String p1 = incrParam1Field.getText();
                    Double.parseDouble(p1);
                    Color defaultColor = UIManager.getColor("TextField.background");
                    incrParam1Field.setBackground(defaultColor);
                } catch (NumberFormatException nfe) {
                    incrParam1Field.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                    ret = false;
                }
            }

            if (!incrementRandomVariateCombo.getSelectedItem().equals("Exponential")) {
                if (incrParam2Field.getText() == null
                 || incrParam2Field.getText().isEmpty()) {
                    incrParam2Field.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                    ret = false;
                } else {
                    try {
                        String p2 = incrParam2Field.getText();
                        Double.parseDouble(p2);
                        Color defaultColor = UIManager.getColor("TextField.background");
                        incrParam2Field.setBackground(defaultColor);
                    } catch (NumberFormatException nfe) {
                        incrParam2Field.setBackground(ComponentDetail.INVALID_INPUT_COLOR);
                        ret = false;
                    }
                }
            }
        }

        return ret;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTextField incrParam1Field;
    private javax.swing.JLabel incrParam1Lbl;
    protected javax.swing.JTextField incrParam2Field;
    protected javax.swing.JLabel incrParam2Lbl;
    private javax.swing.ButtonGroup incrementBtnGroup;
    protected javax.swing.JTextField incrementConstantField;
    protected javax.swing.JRadioButton incrementConstantRadio;
    protected javax.swing.JRadioButton incrementRandomRadio;
    protected javax.swing.JComboBox incrementRandomVariateCombo;
    private javax.swing.ButtonGroup initialBtnGroup;
    protected javax.swing.JTextField initialConstantField;
    protected javax.swing.JRadioButton initialConstantRadio;
    protected javax.swing.JTextField initialParam1Field;
    protected javax.swing.JLabel initialParam1Lbl;
    protected javax.swing.JTextField initialParam2Field;
    protected javax.swing.JLabel initialParam2Lbl;
    protected javax.swing.JRadioButton initialRandomRadio;
    protected javax.swing.JComboBox initialRandomVariateCombo;
    private javax.swing.JLabel jLabel1;
    protected javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
