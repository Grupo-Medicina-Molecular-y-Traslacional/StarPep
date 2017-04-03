/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.bapedis.core.io.ExporterUI;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class FastaExporterUI extends javax.swing.JPanel implements ExporterUI {

    protected JFileChooser chooser;
    protected static File parentDirectory;
    protected File selectedFile;
    protected boolean validState;
    protected final PropertyChangeSupport changeSupport;
    protected PeptideAttribute[] attributes;
    protected ArrayList<PeptideAttribute> selectedAttr;

    /**
     * Creates new form ExportFastaUI
     */
    public FastaExporterUI(PeptideAttribute[] attributes) {
        initComponents();
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        validState = false;
        changeSupport = new PropertyChangeSupport(this);
        this.attributes = attributes;
        selectedAttr = new ArrayList<>(attributes.length);
        populateAttributes(attributes);
    }

    private void populateAttributes(PeptideAttribute[] attributes) {
        jPanelAtrr.removeAll();
        int count = 0;
        GridBagConstraints c;
        JCheckBox cb;
        for (PeptideAttribute attr : attributes) {
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = count;
            c.fill = GridBagConstraints.HORIZONTAL;
            cb = new JCheckBox(attr.getDisplayName());
            cb.setName(attr.getId());
            if (attr.equals(Peptide.ID) || attr.equals(Peptide.SEQ) ){
                cb.setSelected(true);
                cb.setEnabled(false);
            }
            jPanelAtrr.add(cb, c);
            count++;
        }
    }

    public List<PeptideAttribute> getSelectedAttributes() {
        return selectedAttr;
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
        jLabelFileName = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanelAtrr = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FastaExporterUI.class, "FastaExporterUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelFileName, org.openide.util.NbBundle.getMessage(FastaExporterUI.class, "FastaExporterUI.jLabelFileName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(jLabelFileName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(FastaExporterUI.class, "FastaExporterUI.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jButton1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(FastaExporterUI.class, "FastaExporterUI.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        add(jLabel2, gridBagConstraints);

        jPanelAtrr.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanelAtrr, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (parentDirectory != null) {
            chooser.setCurrentDirectory(parentDirectory);
        }
        ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
        Workspace workspace = pm.getCurrentWorkspace();
        chooser.setSelectedFile(new File(parentDirectory, workspace.getName() + ".fasta"));
        int returnVal = chooser.showSaveDialog(FastaExporterUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            parentDirectory = selectedFile.getParentFile();
            jLabelFileName.setText(selectedFile.getName());
            boolean oldValidState = validState;
            validState = true;
            changeSupport.firePropertyChange(VALID_STATE, oldValidState, validState);
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelFileName;
    private javax.swing.JPanel jPanelAtrr;
    // End of variables declaration//GEN-END:variables

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public void finishSettings() {
        JCheckBox cb;
        for (Component comp : jPanelAtrr.getComponents()) {
            cb = (JCheckBox) comp;
            if (cb.isSelected()) {
                for (PeptideAttribute attr : attributes) {
                    if (cb.getName().equals(attr.getId())) {
                        selectedAttr.add(attr);
                    }
                }
            }
        }
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    @Override
    public void cancelSettings() {
        selectedFile = null;
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