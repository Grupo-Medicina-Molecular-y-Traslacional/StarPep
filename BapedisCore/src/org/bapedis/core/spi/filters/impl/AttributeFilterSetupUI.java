/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class AttributeFilterSetupUI extends javax.swing.JPanel implements FilterSetupUI {
    
    protected AttributeFilter filter;
    protected boolean validState;
    protected final ProjectManager pc;
    protected final PropertyChangeSupport changeSupport;

    /**
     * Creates new form AttributeFilterSetupUI
     */
    public AttributeFilterSetupUI() {
        initComponents();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        changeSupport = new PropertyChangeSupport(this);
        validState = false;
        valueTextField.getDocument().addDocumentListener(new DocumentListener() {
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateValidState();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateValidState();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                
            }
        });
        valueTextField.addAncestorListener(new AncestorListener() {
            
            @Override
            public void ancestorAdded(AncestorEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        valueTextField.requestFocusInWindow();
                    }
                });
            }
            
            @Override
            public void ancestorRemoved(AncestorEvent event) {                
            }
            
            @Override
            public void ancestorMoved(AncestorEvent event) {               
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        attrLabel = new javax.swing.JLabel();
        attrComboBox = new javax.swing.JComboBox();
        opLabel = new javax.swing.JLabel();
        opComboBox = new javax.swing.JComboBox();
        valueLabel = new javax.swing.JLabel();
        valueTextField = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();
        matchCaseCheckBox = new javax.swing.JCheckBox();
        notCheckBox = new javax.swing.JCheckBox();

        setMinimumSize(new java.awt.Dimension(126, 90));
        setPreferredSize(new java.awt.Dimension(340, 170));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(attrLabel, org.openide.util.NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.attrLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        add(attrLabel, gridBagConstraints);

        attrComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attrComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        add(attrComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(opLabel, org.openide.util.NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.opLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        add(opLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        add(opComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(valueLabel, org.openide.util.NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.valueLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        add(valueLabel, gridBagConstraints);

        valueTextField.setText(org.openide.util.NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.valueTextField.text")); // NOI18N
        valueTextField.setPreferredSize(new java.awt.Dimension(150, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        add(valueTextField, gridBagConstraints);

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.errorLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        add(errorLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(matchCaseCheckBox, org.openide.util.NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.matchCaseCheckBox.text")); // NOI18N
        matchCaseCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(matchCaseCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(notCheckBox, org.openide.util.NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.notCheckBox.text")); // NOI18N
        notCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(notCheckBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void attrComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attrComboBoxActionPerformed
        opComboBox.removeAllItems();
        if (attrComboBox.getSelectedItem() instanceof PeptideAttribute) {
            PeptideAttribute attr = (PeptideAttribute) attrComboBox.getSelectedItem();
            FilterOperator[] operators = FilterHelper.getOperators(attr.getType());
            for (FilterOperator operator : operators) {
                opComboBox.addItem(operator);
            }
        }
    }//GEN-LAST:event_attrComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox attrComboBox;
    private javax.swing.JLabel attrLabel;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JCheckBox matchCaseCheckBox;
    private javax.swing.JCheckBox notCheckBox;
    private javax.swing.JComboBox opComboBox;
    private javax.swing.JLabel opLabel;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public JPanel getEditPanel(Filter filter) {
        this.filter = (AttributeFilter) filter;
        notCheckBox.setSelected(this.filter.isNegative());
        initAttrComboBox();
        PeptideAttribute attr = this.filter.getAttribute();
        if (attr != null && !attr.equals(attrComboBox.getSelectedItem())) {
            attrComboBox.setSelectedItem(attr);
        }
        FilterOperator operator = this.filter.getOperator();
        if (operator != null && !operator.equals(opComboBox.getSelectedItem())) {
            opComboBox.setSelectedItem(operator);
        }
        String value = this.filter.getValue() != null ? this.filter.getValue() : "";
        if (!value.equals(valueTextField.getText())) {
            valueTextField.setText(value);
        } else {
            updateValidState();
        }
        matchCaseCheckBox.setSelected(this.filter.isMatchCase());
        return this;
    }
    
    protected void initAttrComboBox() {
        attrComboBox.removeAllItems();
        AttributesModel attrModel = pc.getAttributesModel();
        if (attrModel != null) {
            for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
                PeptideAttribute attr = it.next();
                attrComboBox.addItem(attr);
            }
            attrComboBox.setSelectedIndex(0);
        }
    }
    
    @Override
    public void finishSettings() {
        filter.setNegative(notCheckBox.isSelected());
        filter.setAttribute((PeptideAttribute) attrComboBox.getSelectedItem());
        filter.setOperator((FilterOperator) opComboBox.getSelectedItem());
        filter.setValue(valueTextField.getText().trim());
        filter.setMatchCase(matchCaseCheckBox.isSelected());
    }
    
    @Override
    public void cancelSettings() {
        filter = null;
    }
    
    private void updateValidState() {
        boolean oldValue = validState;
        validState = true;
        errorLabel.setText("");
        if (attrComboBox.getSelectedIndex() == -1) {
            validState = false;
            errorLabel.setText(NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.errorLabel.invalidAttribute"));
        }
        if (validState && opComboBox.getSelectedIndex() == -1) {
            validState = false;
            errorLabel.setText(NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.errorLabel.invalidOperator"));
        }
        if (validState && !((FilterOperator) opComboBox.getSelectedItem()).isValid(valueTextField.getText())) {
            validState = false;
            errorLabel.setText(NbBundle.getMessage(AttributeFilterSetupUI.class, "AttributeFilterSetupUI.errorLabel.invalidValue"));
        }
        changeSupport.firePropertyChange(VALID_STATE, oldValue, validState);
    }
    
    @Override
    public boolean isValidState() {
        return validState;
    }
    
    @Override
    public void addValidStateListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }
    
    @Override
    public void removeValidStateListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
