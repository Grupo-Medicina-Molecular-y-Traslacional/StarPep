/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.openide.util.NbBundle;

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

        for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
            PeptideAttribute attr = it.next();
            if (attr.isMolecularDescriptor() && !attrModel.getAvailableColumnsModel().contains(attr)) {
                unselectedModel.addElement(attr);
            }
        }
        leftSizeLabel.setText(NbBundle.getMessage(PeptideAvailableColumnsPanel.class,"PeptideAvailableColumnsPanel.sizeLabel.text", unselectedModel.size()));
        
        for (PeptideAttribute attr : attrModel.getAvailableColumnsModel()) {
            if (attr.isMolecularDescriptor()) {
                selectedModel.addElement(attr);
            }
        }
        rightSizeLabel.setText(NbBundle.getMessage(PeptideAvailableColumnsPanel.class,"PeptideAvailableColumnsPanel.sizeLabel.text", selectedModel.size()));

        addButton.setEnabled(false);
        removeButton.setEnabled(false);

        leftList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                addButton.setEnabled(!lsm.isSelectionEmpty());
            }
        });

        rightList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                removeButton.setEnabled(!lsm.isSelectionEmpty());
            }
        });        
        infoLabel.setVisible(!attrModel.canAddAvailableColumn());
        leftList.setEnabled(attrModel.canAddAvailableColumn());
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
        leftSizeLabel.setText(NbBundle.getMessage(PeptideAvailableColumnsPanel.class,"PeptideAvailableColumnsPanel.sizeLabel.text", unselectedModel.size()));
        rightSizeLabel.setText(NbBundle.getMessage(PeptideAvailableColumnsPanel.class,"PeptideAvailableColumnsPanel.sizeLabel.text", selectedModel.size()));        
        infoLabel.setVisible(!attrModel.canAddAvailableColumn());
        leftList.setEnabled(attrModel.canAddAvailableColumn());
        leftList.clearSelection();
        rightList.clearSelection();
        
    }

    private void add() {
        int[] indices = leftList.getSelectedIndices();
        List<Integer> toMove = new LinkedList<>();
        for (int i = 0; i < indices.length && attrModel.canAddAvailableColumn(); i++) {
            attrModel.addAvailableColumn(unselectedModel.get(indices[i]));
            toMove.add(indices[i]);
        }
        int[] toMoveArr = new int[toMove.size()];
        int cursor = 0;
        for (int i : toMove) {
            toMoveArr[cursor++] = i;
        }
        moveFromTo(toMoveArr, unselectedModel, selectedModel);
    }

    private void remove() {
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
        jSeparator1 = new javax.swing.JToolBar.Separator();
        removeButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();
        leftSizeLabel = new javax.swing.JLabel();
        rightSizeLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(590, 380));
        setPreferredSize(new java.awt.Dimension(590, 380));
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

        leftScrollPane.setMinimumSize(new java.awt.Dimension(275, 23));
        leftScrollPane.setPreferredSize(new java.awt.Dimension(275, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(leftScrollPane, gridBagConstraints);

        rightScrollPane.setMinimumSize(new java.awt.Dimension(275, 23));
        rightScrollPane.setPreferredSize(new java.awt.Dimension(275, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(rightScrollPane, gridBagConstraints);

        controlToolBar.setFloatable(false);
        controlToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        controlToolBar.setRollover(true);
        controlToolBar.setMinimumSize(new java.awt.Dimension(40, 102));
        controlToolBar.setPreferredSize(new java.awt.Dimension(40, 102));

        addButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/arrow.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.addButton.text")); // NOI18N
        addButton.setToolTipText(org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.addButton.toolTipText")); // NOI18N
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
        controlToolBar.add(jSeparator1);

        removeButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/arrow-180.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.removeButton.text")); // NOI18N
        removeButton.setToolTipText(org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.removeButton.toolTipText")); // NOI18N
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 1.0;
        add(controlToolBar, gridBagConstraints);

        infoLabel.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.infoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(infoLabel, gridBagConstraints);

        leftSizeLabel.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(leftSizeLabel, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.leftSizeLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(leftSizeLabel, gridBagConstraints);

        rightSizeLabel.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(rightSizeLabel, org.openide.util.NbBundle.getMessage(PeptideAvailableColumnsPanel.class, "PeptideAvailableColumnsPanel.rightSizeLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(rightSizeLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        add();
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        remove();
    }//GEN-LAST:event_removeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JScrollPane leftScrollPane;
    private javax.swing.JLabel leftSizeLabel;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane rightScrollPane;
    private javax.swing.JLabel rightSizeLabel;
    // End of variables declaration//GEN-END:variables
}
