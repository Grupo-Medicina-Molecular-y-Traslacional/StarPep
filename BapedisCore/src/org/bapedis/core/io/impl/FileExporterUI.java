/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.ValidationSupportUI;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class FileExporterUI extends javax.swing.JPanel implements ValidationSupportUI {

    protected JFileChooser chooser;
    protected static File parentDirectory;
    protected final String fileName;
    protected File selectedFile;
    protected boolean validState;
    protected final PropertyChangeSupport changeSupport;

    /**
     * Creates new form ExportFastaUI
     * @param name
     * @param ext
     */
    public FileExporterUI(String name, String ext) {
        initComponents();
        this.fileName = "_" + name + ext;        
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        validState = false;
        changeSupport = new PropertyChangeSupport(this);
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

        fileLabel = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        fileTextField = new javax.swing.JTextField();

        setMinimumSize(new java.awt.Dimension(489, 50));
        setPreferredSize(new java.awt.Dimension(489, 50));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(fileLabel, org.openide.util.NbBundle.getMessage(FileExporterUI.class, "FileExporterUI.fileLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 5);
        add(fileLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(FileExporterUI.class, "FileExporterUI.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(browseButton, gridBagConstraints);

        fileTextField.setText(org.openide.util.NbBundle.getMessage(FileExporterUI.class, "FileExporterUI.fileTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(fileTextField, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        if (parentDirectory != null) {
            chooser.setCurrentDirectory(parentDirectory);
        }
        ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
        Workspace workspace = pm.getCurrentWorkspace();
        chooser.setSelectedFile(new File(parentDirectory, workspace.getName() + fileName));
        int returnVal = chooser.showSaveDialog(FileExporterUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileTextField.setText(chooser.getSelectedFile().getAbsolutePath());
            boolean oldValidState = validState;
            validState = true;
            changeSupport.firePropertyChange(VALID_STATE, oldValidState, validState);
        }
    }//GEN-LAST:event_browseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileTextField;
    // End of variables declaration//GEN-END:variables


    @Override
    public void finishSettings() {
        selectedFile = new File(fileTextField.getText());
        parentDirectory = selectedFile.getParentFile();
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
