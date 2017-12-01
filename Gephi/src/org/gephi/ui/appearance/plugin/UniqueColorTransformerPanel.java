/*
 Copyright 2008-2013 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Gephi Consortium. All rights reserved.

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

 Portions Copyrighted 2013 Gephi Consortium.
 */
package org.gephi.ui.appearance.plugin;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import net.java.dev.colorchooser.ColorChooser;
import org.bapedis.core.project.ProjectManager;
import org.gephi.appearance.api.SimpleFunction;
import org.gephi.appearance.plugin.AbstractUniqueColorTransformer;

/**
 *
 * @author mbastian
 */
public class UniqueColorTransformerPanel extends javax.swing.JPanel {

    private AbstractUniqueColorTransformer transformer;
    private final Color defaultColor;

    public UniqueColorTransformerPanel() {
        initComponents();

        colorChooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ColorChooser.PROP_COLOR)) {
                    if (!transformer.getColor().equals(colorChooser.getColor())) {
                        transformer.setColor(colorChooser.getColor());
                        colorLabel.setText(getHex(colorChooser.getColor()));
                    }
                }
            }
        });

        float r = ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f;
        float g = ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f;
        float b = ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f;

        int rgba = 255 << 24; // Alpha set to 1
        rgba = (rgba & 0xFF00FFFF) | (((int) (r * 255f)) << 16); // set R
        rgba = (rgba & 0xFFFF00FF) | ((int) (g * 255f)) << 8; // set G
        rgba = (rgba & 0xFFFFFF00) | ((int) (b * 255f)); // set B        
        
//        rgba = (rgba & 0xFFFFFF) | ((int) (a * 255f)) << 24; // set Alpha
        
        defaultColor = new Color(rgba, true);
    }

    public void setup(SimpleFunction function) {
        transformer = (AbstractUniqueColorTransformer) function.getTransformer();
        colorChooser.setColor(transformer.getColor());
        colorLabel.setText(getHex(transformer.getColor()));
    }

    private String getHex(Color color) {
        return "#" + String.format("%06x", color.getRGB() & 0x00FFFFFF);
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

        colorLabel = new javax.swing.JLabel();
        labelColor = new javax.swing.JLabel();
        colorToolbar = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonReset = new javax.swing.JButton();
        colorChooser = new net.java.dev.colorchooser.ColorChooser();

        setLayout(new java.awt.GridBagLayout());

        colorLabel.setText(org.openide.util.NbBundle.getMessage(UniqueColorTransformerPanel.class, "UniqueColorTransformerPanel.colorLabel.text")); // NOI18N
        colorLabel.setToolTipText(org.openide.util.NbBundle.getMessage(UniqueColorTransformerPanel.class, "UniqueColorTransformerPanel.colorLabel.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(colorLabel, gridBagConstraints);

        labelColor.setText(org.openide.util.NbBundle.getMessage(UniqueColorTransformerPanel.class, "UniqueColorTransformerPanel.labelColor.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(labelColor, gridBagConstraints);

        colorToolbar.setFloatable(false);
        colorToolbar.setRollover(true);
        colorToolbar.setOpaque(false);
        colorToolbar.add(jSeparator1);

        jButtonReset.setText(org.openide.util.NbBundle.getMessage(UniqueColorTransformerPanel.class, "UniqueColorTransformerPanel.jButtonReset.text")); // NOI18N
        jButtonReset.setToolTipText(org.openide.util.NbBundle.getMessage(UniqueColorTransformerPanel.class, "UniqueColorTransformerPanel.jButtonReset.toolTipText")); // NOI18N
        jButtonReset.setFocusable(false);
        jButtonReset.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonReset.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });
        colorToolbar.add(jButtonReset);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(colorToolbar, gridBagConstraints);

        colorChooser.setMinimumSize(new java.awt.Dimension(14, 14));
        colorChooser.setPreferredSize(new java.awt.Dimension(16, 16));
        colorChooser.setToolTipText(org.openide.util.NbBundle.getMessage(UniqueColorTransformerPanel.class, "UniqueColorTransformerPanel.colorChooser.toolTipText")); // NOI18N

        javax.swing.GroupLayout colorChooserLayout = new javax.swing.GroupLayout(colorChooser);
        colorChooser.setLayout(colorChooserLayout);
        colorChooserLayout.setHorizontalGroup(
            colorChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );
        colorChooserLayout.setVerticalGroup(
            colorChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(colorChooser, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
        colorChooser.setColor(defaultColor);
        colorLabel.setText(getHex(defaultColor));
    }//GEN-LAST:event_jButtonResetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private net.java.dev.colorchooser.ColorChooser colorChooser;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JToolBar colorToolbar;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JLabel labelColor;
    // End of variables declaration//GEN-END:variables
}
