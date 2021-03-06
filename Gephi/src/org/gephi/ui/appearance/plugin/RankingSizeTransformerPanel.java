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
package org.gephi.ui.appearance.plugin;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.appearance.api.RankingFunction;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author Mathieu Bastian
 */
public class RankingSizeTransformerPanel extends javax.swing.JPanel {

    private RankingNodeSizeTransformer sizeTransformer;
    private final JXHyperlink resetSize;

    public RankingSizeTransformerPanel() {
        initComponents();

        resetSize = new JXHyperlink();
        resetSize.setIcon(ImageUtilities.loadImageIcon("/org/gephi/ui/appearance/plugin/resources/chain.png", false));
        resetSize.setText(NbBundle.getMessage(RankingSizeTransformerPanel.class, "RankingSizeTransformerPanel.resetSize.text"));
        resetSize.setToolTipText(NbBundle.getMessage(RankingSizeTransformerPanel.class, "RankingSizeTransformerPanel.resetSize.toolTipText"));
        resetSize.setClickedColor(new Color(0, 51, 255));
        resetSize.setFocusPainted(false);
        resetSize.setFocusable(false);
        resetSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minSize.setValue(10f);
                maxSize.setValue(100f);
            }
        });
        jToolBar1.add(resetSize);
    }

    public void setup(RankingFunction function) {
        sizeTransformer = (RankingNodeSizeTransformer) function.getTransformer();

        final String MIN_SIZE = "RankingSizeTransformerPanel_" + sizeTransformer.getClass().getSimpleName() + "_min";
        final String MAX_SIZE = "RankingSizeTransformerPanel_" + sizeTransformer.getClass().getSimpleName() + "_max";

        float minSizeStart = NbPreferences.forModule(RankingSizeTransformerPanel.class).getFloat(MIN_SIZE, sizeTransformer.getMinSize());
        float maxSizeStart = NbPreferences.forModule(RankingSizeTransformerPanel.class).getFloat(MAX_SIZE, sizeTransformer.getMaxSize());
        sizeTransformer.setMinSize(minSizeStart);
        sizeTransformer.setMaxSize(maxSizeStart);

        minSize.setValue(minSizeStart);
        maxSize.setValue(maxSizeStart);
        minSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sizeTransformer.setMinSize((Float) minSize.getValue());
                NbPreferences.forModule(RankingSizeTransformerPanel.class).putFloat(MIN_SIZE, (Float) minSize.getValue());
            }
        });
        maxSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sizeTransformer.setMaxSize((Float) maxSize.getValue());
                NbPreferences.forModule(RankingSizeTransformerPanel.class).putFloat(MAX_SIZE, (Float) maxSize.getValue());
            }
        });
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

        labelMinSize = new javax.swing.JLabel();
        minSize = new javax.swing.JSpinner();
        labelMaxSize = new javax.swing.JLabel();
        maxSize = new javax.swing.JSpinner();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();

        setPreferredSize(new java.awt.Dimension(225, 114));
        setLayout(new java.awt.GridBagLayout());

        labelMinSize.setText(org.openide.util.NbBundle.getMessage(RankingSizeTransformerPanel.class, "RankingSizeTransformerPanel.labelMinSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(labelMinSize, gridBagConstraints);

        minSize.setModel(new javax.swing.SpinnerNumberModel(1.0f, 0.1f, null, 0.5f));
        minSize.setPreferredSize(new java.awt.Dimension(90, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(minSize, gridBagConstraints);

        labelMaxSize.setText(org.openide.util.NbBundle.getMessage(RankingSizeTransformerPanel.class, "RankingSizeTransformerPanel.labelMaxSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(labelMaxSize, gridBagConstraints);

        maxSize.setModel(new javax.swing.SpinnerNumberModel(4.0f, 0.5f, null, 0.5f));
        maxSize.setPreferredSize(new java.awt.Dimension(90, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(maxSize, gridBagConstraints);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.add(jSeparator1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(jToolBar1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labelMaxSize;
    private javax.swing.JLabel labelMinSize;
    private javax.swing.JSpinner maxSize;
    private javax.swing.JSpinner minSize;
    // End of variables declaration//GEN-END:variables
}
