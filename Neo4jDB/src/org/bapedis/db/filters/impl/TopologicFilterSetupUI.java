/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.services.NeoPeptideManager;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.filters.spi.FilterSetupUI;
import static org.bapedis.db.filters.spi.FilterSetupUI.VALID_STATE;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class TopologicFilterSetupUI extends javax.swing.JPanel implements FilterSetupUI, LookupListener {

    protected boolean validState;
    protected TopologicFilter topologicFilter;
    protected final ProjectManager pc;
    protected final PropertyChangeSupport changeSupport;
    protected final JLabel busyLabel;

    /**
     * Creates new form TopologicFilterSetupUI
     */
    public TopologicFilterSetupUI() {
        initComponents();
        busyLabel = new JLabel(NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.busyLabel.text"));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setVerticalAlignment(SwingConstants.CENTER);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        changeSupport = new PropertyChangeSupport(this);
        validState = false;
        for (IntegerFilterOperator operator : IntegerFilterOperator.values()) {
            degreeOpComboBox.addItem(operator);
        }
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
        degreeValueTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c)
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });
        degreeValueTextField.getDocument().addDocumentListener(new DocumentListener() {

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

        buttonGroup1 = new javax.swing.ButtonGroup();
        filterPanel = new javax.swing.JPanel();
        topologicCondPanel = new javax.swing.JPanel();
        degreePanel = new javax.swing.JPanel();
        degreeRadioButton = new javax.swing.JRadioButton();
        degreeOpComboBox = new javax.swing.JComboBox();
        degreeValueTextField = new javax.swing.JTextField();
        relationPanel = new javax.swing.JPanel();
        relationshipRadioButton = new javax.swing.JRadioButton();
        neighborCondPanel = new javax.swing.JPanel();
        notCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        attrComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        opComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        valueTextField = new javax.swing.JTextField();
        matchCaseCheckBox = new javax.swing.JCheckBox();
        blankLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(300, 250));
        setLayout(new java.awt.BorderLayout());

        filterPanel.setLayout(new java.awt.GridBagLayout());

        topologicCondPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.topologicCondPanel.border.title"))); // NOI18N
        topologicCondPanel.setPreferredSize(new java.awt.Dimension(300, 90));
        topologicCondPanel.setLayout(new java.awt.GridBagLayout());

        degreePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup1.add(degreeRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(degreeRadioButton, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.degreeRadioButton.text")); // NOI18N
        degreeRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                degreeRadioButtonItemStateChanged(evt);
            }
        });
        degreePanel.add(degreeRadioButton);
        degreePanel.add(degreeOpComboBox);

        degreeValueTextField.setText(org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.degreeValueTextField.text")); // NOI18N
        degreeValueTextField.setPreferredSize(new java.awt.Dimension(90, 20));
        degreePanel.add(degreeValueTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        topologicCondPanel.add(degreePanel, gridBagConstraints);

        buttonGroup1.add(relationshipRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(relationshipRadioButton, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.relationshipRadioButton.text")); // NOI18N
        relationshipRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                relationshipRadioButtonItemStateChanged(evt);
            }
        });
        relationPanel.add(relationshipRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        topologicCondPanel.add(relationPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        filterPanel.add(topologicCondPanel, gridBagConstraints);

        neighborCondPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.neighborCondPanel.border.title"))); // NOI18N
        neighborCondPanel.setPreferredSize(new java.awt.Dimension(300, 150));
        neighborCondPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(notCheckBox, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.notCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        neighborCondPanel.add(notCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        neighborCondPanel.add(jLabel1, gridBagConstraints);

        attrComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attrComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        neighborCondPanel.add(attrComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        neighborCondPanel.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        neighborCondPanel.add(opComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        neighborCondPanel.add(jLabel3, gridBagConstraints);

        valueTextField.setText(org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.valueTextField.text")); // NOI18N
        valueTextField.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        neighborCondPanel.add(valueTextField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(matchCaseCheckBox, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.matchCaseCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        neighborCondPanel.add(matchCaseCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(blankLabel, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.blankLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        neighborCondPanel.add(blankLabel, gridBagConstraints);

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.errorLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        neighborCondPanel.add(errorLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        filterPanel.add(neighborCondPanel, gridBagConstraints);

        add(filterPanel, java.awt.BorderLayout.CENTER);
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
        // TODO add your handling code here:
    }//GEN-LAST:event_attrComboBoxActionPerformed

    private void relationshipRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_relationshipRadioButtonItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            degreeOpComboBox.setEnabled(false);
            degreeValueTextField.setEnabled(false);
            valueTextField.requestFocus();
            updateValidState();
        }
    }//GEN-LAST:event_relationshipRadioButtonItemStateChanged

    private void degreeRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_degreeRadioButtonItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            degreeOpComboBox.setEnabled(true);
            degreeValueTextField.setEnabled(true);
            degreeValueTextField.requestFocus();
            updateValidState();
        }
    }//GEN-LAST:event_degreeRadioButtonItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox attrComboBox;
    private javax.swing.JLabel blankLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox degreeOpComboBox;
    private javax.swing.JPanel degreePanel;
    private javax.swing.JRadioButton degreeRadioButton;
    private javax.swing.JTextField degreeValueTextField;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JCheckBox matchCaseCheckBox;
    private javax.swing.JPanel neighborCondPanel;
    private javax.swing.JCheckBox notCheckBox;
    private javax.swing.JComboBox opComboBox;
    private javax.swing.JPanel relationPanel;
    private javax.swing.JRadioButton relationshipRadioButton;
    private javax.swing.JPanel topologicCondPanel;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public JPanel getEditPanel(Filter filter) {
        topologicFilter = (TopologicFilter) filter;
        TopologicFilter.TopologicCondition topologicCond = topologicFilter.getTopologicCondition();
        switch (topologicCond) {
            case RFLATIONSHIP:
                relationshipRadioButton.setSelected(true);
                break;
            case DEGREE:
                degreeRadioButton.setSelected(true);
                IntegerFilterOperator opDegree = topologicFilter.getDegreeOperator();
                if (opDegree != null && !opDegree.equals(degreeOpComboBox.getSelectedItem())) {
                    degreeOpComboBox.setSelectedItem(opDegree);
                }
                String degreeValue = topologicFilter.getDegreeValue() != null ? topologicFilter.getDegreeValue() : "";
                if (!degreeValue.equals(degreeValueTextField.getText())) {
                    degreeValueTextField.setText(degreeValue);
                }
        }
        notCheckBox.setSelected(topologicFilter.isNeighborCondNegative());
        Workspace ws = pc.getCurrentWorkspace();
        initAttrComboBox(ws);
        PeptideAttribute attr = topologicFilter.getNeighborCondAttribute();
        if (attr != null && !attr.equals(attrComboBox.getSelectedItem())) {
            attrComboBox.setSelectedItem(attr);
        }
        FilterOperator operator = topologicFilter.getNeighborCondOperator();
        if (operator != null && !operator.equals(opComboBox.getSelectedItem())) {
            opComboBox.setSelectedItem(operator);
        }
        String value = topologicFilter.getNeighborCondValue() != null ? topologicFilter.getNeighborCondValue() : "";
        if (!value.equals(valueTextField.getText())) {
            valueTextField.setText(value);
        } else {
            updateValidState();
        }
        matchCaseCheckBox.setSelected(topologicFilter.isNeighborCondMatchCase());
        return this;
    }

    protected void initAttrComboBox(final Workspace workspace) {
        attrComboBox.removeAllItems();
//        NeoNeighborsModel nModel = workspace.getLookup().lookup(NeoNeighborsModel.class);
//        if (nModel != null) {
//            for (PeptideAttribute attr : nModel.getAttributes()) {
//                attrComboBox.addItem(attr);
//            }
//        } else {
//            remove(filterPanel);
//            add(busyLabel, BorderLayout.CENTER);
//            lkpResult = workspace.getLookup().lookupResult(NeoNeighborsModel.class);
//            lkpResult.addLookupListener(this);
//            final NeoPeptideManager npc = Lookup.getDefault().lookup(NeoPeptideManager.class);
//            SwingUtilities.invokeLater(new Runnable() {
//
//                @Override
//                public void run() {
//                    npc.setNeoNeighborsTo(workspace);
//                }
//            });
//            revalidate();
//        }
    }

    private void updateValidState() {
        boolean oldValue = validState;
        validState = true;
        errorLabel.setText("");
        if (degreeRadioButton.isSelected() && !((IntegerFilterOperator) degreeOpComboBox.getSelectedItem()).isValid(degreeValueTextField.getText())) {
            validState = false;
            errorLabel.setText(NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.errorLabel.invalidDegreeValue"));
        }
        if (validState && attrComboBox.getSelectedIndex() == -1) {
            validState = false;
            errorLabel.setText(NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.errorLabel.invalidAttribute"));
        }
        if (validState && opComboBox.getSelectedIndex() == -1) {
            validState = false;
            errorLabel.setText(NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.errorLabel.invalidOperator"));
        }
        if (validState && !((FilterOperator) opComboBox.getSelectedItem()).isValid(valueTextField.getText())) {
            validState = false;
            errorLabel.setText(NbBundle.getMessage(TopologicFilterSetupUI.class, "TopologicFilterSetupUI.errorLabel.invalidValue"));
        }
        changeSupport.firePropertyChange(VALID_STATE, oldValue, validState);
    }

    @Override
    public void finishSettings() {
        if (relationshipRadioButton.isSelected()) {
            topologicFilter.setTopologicCondition(TopologicFilter.TopologicCondition.RFLATIONSHIP);
        } else if (degreeRadioButton.isSelected()) {
            topologicFilter.setTopologicCondition(TopologicFilter.TopologicCondition.DEGREE);
            topologicFilter.setDegreeOperator((IntegerFilterOperator) degreeOpComboBox.getSelectedItem());
            topologicFilter.setDegreeValue(degreeValueTextField.getText());
        }
        topologicFilter.setNeighborCondNegative(notCheckBox.isSelected());
        topologicFilter.setNeighborCondAttribute((PeptideAttribute) attrComboBox.getSelectedItem());
        topologicFilter.setNeighborCondOperator((FilterOperator) opComboBox.getSelectedItem());
        topologicFilter.setNeighborCondValue(valueTextField.getText());
        topologicFilter.setNeighborCondMatchCase(matchCaseCheckBox.isSelected());
//        if (lkpResult != null) {
//            lkpResult.removeLookupListener(this);
//        }
    }

    @Override
    public void cancelSettings() {
        topologicFilter = null;
//        if (lkpResult != null) {
//            lkpResult.removeLookupListener(this);
//        }
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

    @Override
    public void resultChanged(LookupEvent le) {
//        Collection<? extends NeoNeighborsModel> attrModels = lkpResult.allInstances();
//        if (!attrModels.isEmpty()) {
//            remove(busyLabel);
//            add(filterPanel, BorderLayout.CENTER);
//            ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
//            initAttrComboBox(pc.getCurrentWorkspace());
//            revalidate();
//        }

    }
}
