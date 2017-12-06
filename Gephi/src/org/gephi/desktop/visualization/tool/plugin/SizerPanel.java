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
package org.gephi.desktop.visualization.tool.plugin;

import java.text.DecimalFormat;

/**
 *
 * @author Mathieu Bastian
 */
public class SizerPanel extends javax.swing.JPanel {

    private float avgSize;
    private DecimalFormat formatter;

    /**
     * Creates new form SizePanel
     */
    public SizerPanel() {
        initComponents();
        formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(2);
    }

    public float getAvgSize() {
        return avgSize;
    }

    public void setAvgSize(float avgSize) {
        this.avgSize = avgSize;
        if (avgSize == -1) {
            sizeLabel.setText("NaN");
        } else {
            String str = formatter.format(avgSize);
            if (!str.equals(sizeLabel.getText())) {
                sizeLabel.setText(str);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        sizerInfoLabel = new javax.swing.JLabel();
        labelSize = new javax.swing.JLabel();
        sizeLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        sizerInfoLabel.setFont(sizerInfoLabel.getFont().deriveFont((float)10));
        sizerInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/resources/info.png"))); // NOI18N
        sizerInfoLabel.setText(org.openide.util.NbBundle.getMessage(SizerPanel.class, "SizerPanel.sizerInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(sizerInfoLabel, gridBagConstraints);

        labelSize.setFont(labelSize.getFont().deriveFont((float)10));
        labelSize.setText(org.openide.util.NbBundle.getMessage(SizerPanel.class, "SizerPanel.labelSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(labelSize, gridBagConstraints);

        sizeLabel.setFont(sizeLabel.getFont().deriveFont((float)10));
        sizeLabel.setText(org.openide.util.NbBundle.getMessage(SizerPanel.class, "SizerPanel.sizeLabel.text")); // NOI18N
        sizeLabel.setPreferredSize(new java.awt.Dimension(50, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(sizeLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelSize;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JLabel sizerInfoLabel;
    // End of variables declaration//GEN-END:variables
}
