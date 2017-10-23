/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.data.PeptideDAO;

/**
 *
 * @author loge
 */
public class PeptideDeleteColumnsPanel extends javax.swing.JPanel {
    
    protected final AttributesModel attrModel;
    protected final JList<PeptideAttribute> columnList;
    protected final DefaultListModel<PeptideAttribute> listModel;
    
    public PeptideDeleteColumnsPanel(AttributesModel attrModel) {
        initComponents();
        this.attrModel = attrModel;
        listModel = new DefaultListModel<>();
        for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
            PeptideAttribute attr = it.next();
            if (attr.isMolecularDescriptor() && !attr.equals(PeptideDAO.LENGHT)) {
                listModel.addElement(attr);
            }
        }        
        columnList = new JList<>(listModel);
        scrollPane.setViewportView(columnList);
        
        deleteButton.setEnabled(false);
        deleteAllButton.setEnabled(!listModel.isEmpty());
        
        columnList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                deleteButton.setEnabled(!lsm.isSelectionEmpty());
            }
        });        
    }
    
    private void deleteSelectedAttributes(){
        int[] indices = columnList.getSelectedIndices();
        for(int i= indices.length-1; i>=0; i--){
            attrModel.deleteAttribute(listModel.get(indices[i]));
            listModel.remove(indices[i]);
        }        
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

        deleteButton = new javax.swing.JButton();
        deleteAllButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();

        setMinimumSize(new java.awt.Dimension(440, 380));
        setPreferredSize(new java.awt.Dimension(440, 380));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(deleteButton, org.openide.util.NbBundle.getMessage(PeptideDeleteColumnsPanel.class, "PeptideDeleteColumnsPanel.deleteButton.text")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(deleteButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(deleteAllButton, org.openide.util.NbBundle.getMessage(PeptideDeleteColumnsPanel.class, "PeptideDeleteColumnsPanel.deleteAllButton.text")); // NOI18N
        deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(deleteAllButton, gridBagConstraints);

        scrollPane.setMinimumSize(new java.awt.Dimension(275, 23));
        scrollPane.setPreferredSize(new java.awt.Dimension(275, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 0);
        add(scrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        deleteSelectedAttributes();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void deleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllButtonActionPerformed
        columnList.setSelectionInterval(0, listModel.getSize() - 1);
        deleteSelectedAttributes();
    }//GEN-LAST:event_deleteAllButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteAllButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}