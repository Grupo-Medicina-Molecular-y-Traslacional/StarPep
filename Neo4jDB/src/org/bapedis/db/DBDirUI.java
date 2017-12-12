/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import javax.swing.JFileChooser;
import org.bapedis.core.ui.components.ValidationSupportUI;
import org.openide.util.NbPreferences;

/**
 *
 * @author loge
 */
public class DBDirUI extends javax.swing.JPanel implements ValidationSupportUI {

    protected JFileChooser chooser;
    protected File selectedDir;
    protected boolean validState;
    protected final PropertyChangeSupport changeSupport;
    final String LAST_PATH = "DBDirUI_Last_Path";
    public static final File DEFAULT_DIR = new File(System.getProperty("netbeans.user"));

    /**
     * Creates new form DBDirUI
     */
    public DBDirUI() {
        initComponents();
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        validState = false;
        changeSupport = new PropertyChangeSupport(this);
    }

    public File getSelectedDir() {
        return selectedDir;
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

        dirLabel = new javax.swing.JLabel();
        dirTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(489, 50));
        setPreferredSize(new java.awt.Dimension(489, 50));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(dirLabel, org.openide.util.NbBundle.getMessage(DBDirUI.class, "DBDirUI.dirLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(dirLabel, gridBagConstraints);

        dirTextField.setText(org.openide.util.NbBundle.getMessage(DBDirUI.class, "DBDirUI.dirTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(dirTextField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(DBDirUI.class, "DBDirUI.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(browseButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(DBDirUI.class, "DBDirUI.resetButton.text")); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(resetButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String dbDir = NbPreferences.forModule(DBDirUI.class).get(LAST_PATH, DEFAULT_DIR.getAbsolutePath());
        chooser.setCurrentDirectory(new File(dbDir));
        int returnVal = chooser.showSaveDialog(DBDirUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            setFile(chooser.getSelectedFile());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        setFile(DEFAULT_DIR);
        chooser.setCurrentDirectory(DEFAULT_DIR);
    }//GEN-LAST:event_resetButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel dirLabel;
    private javax.swing.JTextField dirTextField;
    private javax.swing.JButton resetButton;
    // End of variables declaration//GEN-END:variables

    private void setFile(File file) {
        dirTextField.setText(file.getAbsolutePath());
        boolean oldValidState = validState;
        validState = true;
        changeSupport.firePropertyChange(VALID_STATE, oldValidState, validState);
    }

    @Override
    public boolean isValidState() {
        return validState;
    }

    @Override
    public void finishSettings() {
        selectedDir = new File(dirTextField.getText());
        if (selectedDir.exists() || selectedDir.mkdir()) {
            NbPreferences.forModule(DBDirUI.class).put(LAST_PATH, dirTextField.getText());
        }
    }

    @Override
    public void cancelSettings() {
        selectedDir = null;
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
