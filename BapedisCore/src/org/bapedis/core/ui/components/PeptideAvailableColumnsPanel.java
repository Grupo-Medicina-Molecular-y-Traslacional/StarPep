/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;

/**
 *
 * @author Home
 */
public class PeptideAvailableColumnsPanel extends javax.swing.JPanel {

    protected final JList<PeptideAttribute> leftList, rightList;
    protected final DefaultListModel<PeptideAttribute> unselectedModel, selectedModel;
    protected final AttributesModel attrModel;

    /**
     * Creates new form PeptideAvailableColumnsPanel
     *
     * @param attrModel
     */
    public PeptideAvailableColumnsPanel(AttributesModel attrModel) {
        initComponents();
        this.attrModel = attrModel;
        unselectedModel = new DefaultListModel<>();
        selectedModel = new DefaultListModel<>();
        leftList = new JList<>(unselectedModel);
        leftScrollPane.setViewportView(leftList);
        rightList = new JList<>(selectedModel);
        rightScrollPane.setViewportView(rightList);

        for (PeptideAttribute attr : attrModel.getAttributes()) {
            if (!attrModel.getAvailableColumnsModel().contains(attr)) {
                unselectedModel.addElement(attr);
            }
        }

        for (PeptideAttribute attr : attrModel.getAvailableColumnsModel()) {
            selectedModel.addElement(attr);
        }
    }

    private void moveFromTo(int[] indices, DefaultListModel<PeptideAttribute> srcModel, DefaultListModel<PeptideAttribute> desModel) {
        for (int i = indices.length - 1; i >= 0; i--) {
            int j = 0;
            while (j < desModel.getSize() && srcModel.get(indices[i]).toString().compareTo(desModel.get(j).toString()) > 0) {
                j++;
            }
            desModel.insertElementAt(srcModel.get(indices[i]), j);
            srcModel.removeElementAt(indices[i]);
        }
    }

    private void add() {
        int[] indices = leftList.getSelectedIndices();
        for (int i = 0; i < indices.length; i++) {
            attrModel.addAvailableColumn(unselectedModel.get(indices[i]));
        }
        moveFromTo(indices, unselectedModel, selectedModel);
    }
    
    private void remove(){
        int[] indices = rightList.getSelectedIndices();
        for (int i = 0; i < indices.length; i++) {
            attrModel.removeAvailableColumn(selectedModel.get(indices[i]));
        }
        moveFromTo(indices, selectedModel, unselectedModel);    
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        leftScrollPane = new javax.swing.JScrollPane();
        rightScrollPane = new javax.swing.JScrollPane();
        controlToolBar = new javax.swing.JToolBar();
        addButton = new javax.swing.JButton();
        addAllButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(336, 280));
        setPreferredSize(new java.awt.Dimension(336, 280));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(leftScrollPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(rightScrollPane, gridBagConstraints);

        controlToolBar.setFloatable(false);
        controlToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        controlToolBar.setRollover(true);
        controlToolBar.setPreferredSize(new java.awt.Dimension(30, 100));

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.addButton.text")); // NOI18N
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setMaximumSize(new java.awt.Dimension(50, 21));
        addButton.setMinimumSize(new java.awt.Dimension(23, 21));
        addButton.setPreferredSize(new java.awt.Dimension(50, 21));
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(addButton);

        org.openide.awt.Mnemonics.setLocalizedText(addAllButton, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.addAllButton.text")); // NOI18N
        addAllButton.setFocusable(false);
        addAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addAllButton.setMaximumSize(new java.awt.Dimension(50, 21));
        addAllButton.setPreferredSize(new java.awt.Dimension(50, 21));
        addAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAllButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(addAllButton);

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.removeButton.text")); // NOI18N
        removeButton.setFocusable(false);
        removeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeButton.setMaximumSize(new java.awt.Dimension(50, 21));
        removeButton.setMinimumSize(new java.awt.Dimension(23, 21));
        removeButton.setPreferredSize(new java.awt.Dimension(50, 21));
        removeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(removeButton);

        org.openide.awt.Mnemonics.setLocalizedText(removeAllButton, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.removeAllButton.text")); // NOI18N
        removeAllButton.setFocusable(false);
        removeAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeAllButton.setMaximumSize(new java.awt.Dimension(50, 21));
        removeAllButton.setPreferredSize(new java.awt.Dimension(50, 21));
        removeAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(removeAllButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 1.0;
        add(controlToolBar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        add();
    }//GEN-LAST:event_addButtonActionPerformed

    private void addAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAllButtonActionPerformed
        leftList.setSelectionInterval(0, unselectedModel.getSize() - 1);
        add();
    }//GEN-LAST:event_addAllButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        remove();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed
        rightList.setSelectionInterval(0, selectedModel.getSize() - 1);
        remove();
    }//GEN-LAST:event_removeAllButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAllButton;
    private javax.swing.JButton addButton;
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane leftScrollPane;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane rightScrollPane;
    // End of variables declaration//GEN-END:variables
}