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
package org.gephi.visualization.screenshot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.windows.WindowManager;

/**
 *
 * @author Mathieu Bastian
 */
public class ScreenshotSettingsPanel extends javax.swing.JPanel {

    /**
     * Creates new form ScreenshotSettingsPanel
     */
    public ScreenshotSettingsPanel() {
        initComponents();

        autoSaveCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                selectDirectoryButton.setEnabled(autoSaveCheckBox.isSelected());
            }
        });
    }

    public void setup(final ScreenshotMaker screenshotMaker) {
        autoSaveCheckBox.setSelected(screenshotMaker.isAutoSave());
        selectDirectoryButton.setEnabled(autoSaveCheckBox.isSelected());
        widthTextField.setText(String.format("%d", screenshotMaker.getWidth()));
        heightTextField.setText(String.format("%d",screenshotMaker.getHeight()));
        switch (screenshotMaker.getAntiAliasing()) {
            case 0:
                antiAliasingCombo.setSelectedIndex(0);
                break;
            case 2:
                antiAliasingCombo.setSelectedIndex(1);
                break;
            case 4:
                antiAliasingCombo.setSelectedIndex(2);
                break;
            case 8:
                antiAliasingCombo.setSelectedIndex(3);
                break;
            case 16:
                antiAliasingCombo.setSelectedIndex(4);
                break;
            default:
                antiAliasingCombo.setSelectedIndex(4);
                break;
        }
        //transparentBackgroundCheckBox.setSelected(screenshotMaker.isTransparentBackground());
        selectDirectoryButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(screenshotMaker.getDefaultDirectory());
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
                if (result == JFileChooser.APPROVE_OPTION) {
                    screenshotMaker.setDefaultDirectory(fileChooser.getSelectedFile());
                }
            }
        });
    }

    public void unsetup(ScreenshotMaker screenshotMaker) {
        screenshotMaker.setAutoSave(autoSaveCheckBox.isSelected());
        screenshotMaker.setWidth(Integer.parseInt(widthTextField.getText()));
        screenshotMaker.setHeight(Integer.parseInt(heightTextField.getText()));
        switch (antiAliasingCombo.getSelectedIndex()) {
            case 0:
                screenshotMaker.setAntiAliasing(0);
                break;
            case 1:
                screenshotMaker.setAntiAliasing(2);
                break;
            case 2:
                screenshotMaker.setAntiAliasing(4);
                break;
            case 3:
                screenshotMaker.setAntiAliasing(8);
                break;
            case 4:
                screenshotMaker.setAntiAliasing(16);
                break;
            default:
                screenshotMaker.setAntiAliasing(0);
                break;
        } 
        //screenshotMaker.setTransparentBackground(transparentBackgroundCheckBox.isSelected());
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

        topPanel = new javax.swing.JPanel();
        labelHeight = new javax.swing.JLabel();
        labelWidth = new javax.swing.JLabel();
        java.text.NumberFormat widthFormat = java.text.NumberFormat.getIntegerInstance(Locale.ENGLISH);
        widthFormat.setGroupingUsed(false);
        javax.swing.text.NumberFormatter widthFormatter = new javax.swing.text.NumberFormatter(widthFormat);
        widthFormatter.setValueClass(Integer.class);
        widthFormatter.setMinimum(1);
        widthFormatter.setAllowsInvalid(false);
        widthTextField = new javax.swing.JFormattedTextField(widthFormatter);
        java.text.NumberFormat heightFormat = java.text.NumberFormat.getIntegerInstance(Locale.ENGLISH);
        heightFormat.setGroupingUsed(false);
        javax.swing.text.NumberFormatter heightFormatter = new javax.swing.text.NumberFormatter(heightFormat);
        heightFormatter.setMinimum(1);
        heightFormatter.setAllowsInvalid(false);
        heightTextField = new javax.swing.JFormattedTextField(heightFormatter);
        bottomPanel = new javax.swing.JPanel();
        labelAntiAliasing = new javax.swing.JLabel();
        antiAliasingCombo = new javax.swing.JComboBox();
        autoSaveCheckBox = new javax.swing.JCheckBox();
        selectDirectoryButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        topPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        topPanel.setLayout(new java.awt.GridBagLayout());

        labelHeight.setText(org.openide.util.NbBundle.getMessage(ScreenshotSettingsPanel.class, "ScreenshotSettingsPanel.labelHeight.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        topPanel.add(labelHeight, gridBagConstraints);

        labelWidth.setText(org.openide.util.NbBundle.getMessage(ScreenshotSettingsPanel.class, "ScreenshotSettingsPanel.labelWidth.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        topPanel.add(labelWidth, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 5);
        topPanel.add(widthTextField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 5);
        topPanel.add(heightTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(topPanel, gridBagConstraints);

        bottomPanel.setLayout(new java.awt.GridBagLayout());

        labelAntiAliasing.setText(org.openide.util.NbBundle.getMessage(ScreenshotSettingsPanel.class, "ScreenshotSettingsPanel.labelAntiAliasing.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        bottomPanel.add(labelAntiAliasing, gridBagConstraints);

        antiAliasingCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0x", "2x", "4x", "8x", "16x" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        bottomPanel.add(antiAliasingCombo, gridBagConstraints);

        autoSaveCheckBox.setText(org.openide.util.NbBundle.getMessage(ScreenshotSettingsPanel.class, "ScreenshotSettingsPanel.autoSaveCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        bottomPanel.add(autoSaveCheckBox, gridBagConstraints);

        selectDirectoryButton.setText(org.openide.util.NbBundle.getMessage(ScreenshotSettingsPanel.class, "ScreenshotSettingsPanel.selectDirectoryButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        bottomPanel.add(selectDirectoryButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox antiAliasingCombo;
    private javax.swing.JCheckBox autoSaveCheckBox;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JTextField heightTextField;
    private javax.swing.JLabel labelAntiAliasing;
    private javax.swing.JLabel labelHeight;
    private javax.swing.JLabel labelWidth;
    private javax.swing.JButton selectDirectoryButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField widthTextField;
    // End of variables declaration//GEN-END:variables
}
