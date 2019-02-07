/*
 Copyright 2008-2010 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */
package org.bapedis.core.io.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import javax.swing.JFileChooser;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.ValidationSupportUI;
import org.openide.util.Lookup;


/**
 *
 * @author Mathieu Bastian
 */
public class GraphMLExportPanel extends javax.swing.JPanel implements ValidationSupportUI {

    protected JFileChooser chooser;
    protected static File parentDirectory;
    protected File selectedFile;
    protected boolean validState;
    protected final PropertyChangeSupport changeSupport;
    
    
    public GraphMLExportPanel() {
        initComponents();
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        validState = false;
        changeSupport = new PropertyChangeSupport(this);
        
    }

    public void setup(GraphMLExporter exporter) {
        colorsExportCheckbox.setSelected(exporter.isExportColors());
        positionExportCheckbox.setSelected(exporter.isExportPosition());
        sizeExportCheckbox.setSelected(exporter.isExportSize());
        attributesExportCheckbox.setSelected(exporter.isExportAttributes());
        normalizeCheckbox.setSelected(exporter.isNormalize());
    }

    public void unsetup(GraphMLExporter exporterGraphML) {
        exporterGraphML.setExportAttributes(attributesExportCheckbox.isSelected());
        exporterGraphML.setExportColors(colorsExportCheckbox.isSelected());
        exporterGraphML.setExportSize(sizeExportCheckbox.isSelected());
        exporterGraphML.setExportPosition(positionExportCheckbox.isSelected());
        exporterGraphML.setNormalize(normalizeCheckbox.isSelected());
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

        labelExport = new javax.swing.JLabel();
        positionExportCheckbox = new javax.swing.JCheckBox();
        colorsExportCheckbox = new javax.swing.JCheckBox();
        attributesExportCheckbox = new javax.swing.JCheckBox();
        sizeExportCheckbox = new javax.swing.JCheckBox();
        labelNormalize = new javax.swing.JLabel();
        normalizeCheckbox = new javax.swing.JCheckBox();
        fileLabel = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        labelExport.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.labelExport.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 5);
        add(labelExport, gridBagConstraints);

        positionExportCheckbox.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.positionExportCheckbox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(positionExportCheckbox, gridBagConstraints);

        colorsExportCheckbox.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.colorsExportCheckbox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(colorsExportCheckbox, gridBagConstraints);

        attributesExportCheckbox.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.attributesExportCheckbox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(attributesExportCheckbox, gridBagConstraints);

        sizeExportCheckbox.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.sizeExportCheckbox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(sizeExportCheckbox, gridBagConstraints);

        labelNormalize.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelNormalize.setForeground(new java.awt.Color(102, 102, 102));
        labelNormalize.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.labelNormalize.text")); // NOI18N
        labelNormalize.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        add(labelNormalize, gridBagConstraints);

        normalizeCheckbox.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.normalizeCheckbox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(normalizeCheckbox, gridBagConstraints);

        fileLabel.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.fileLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(fileLabel, gridBagConstraints);

        fileTextField.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.fileTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        add(fileTextField, gridBagConstraints);

        browseButton.setText(org.openide.util.NbBundle.getMessage(GraphMLExportPanel.class, "GraphMLExportPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 2);
        add(browseButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        if (parentDirectory != null) {
            chooser.setCurrentDirectory(parentDirectory);
        }
        ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
        Workspace workspace = pm.getCurrentWorkspace();
        chooser.setSelectedFile(new File(parentDirectory, workspace.getName() + "_graph.graphml"));
        int returnVal = chooser.showSaveDialog(GraphMLExportPanel.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileTextField.setText(chooser.getSelectedFile().getAbsolutePath());
            boolean oldValidState = validState;
            validState = true;
            changeSupport.firePropertyChange(VALID_STATE, oldValidState, validState);
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox attributesExportCheckbox;
    private javax.swing.JButton browseButton;
    private javax.swing.JCheckBox colorsExportCheckbox;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JLabel labelExport;
    private javax.swing.JLabel labelNormalize;
    private javax.swing.JCheckBox normalizeCheckbox;
    private javax.swing.JCheckBox positionExportCheckbox;
    private javax.swing.JCheckBox sizeExportCheckbox;
    // End of variables declaration//GEN-END:variables

    public File getSelectedFile() {
        return selectedFile;
    }
    
    @Override
    public boolean isValidState() {
        return validState;
    }

    @Override
    public void saveSettings() {
        selectedFile = new File(fileTextField.getText());
        parentDirectory = selectedFile.getParentFile();
    }

    @Override
    public void cancelSettings() {
        selectedFile = null;
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
