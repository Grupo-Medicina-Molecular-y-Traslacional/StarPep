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

import java.awt.Color;
import javax.swing.DefaultComboBoxModel;
import org.gephi.ui.components.JColorButton;

/**
 *
 * @author Mathieu Bastian
 */
public class BrushPanel extends javax.swing.JPanel {

    /** Creates new form BrushPanel */
    public BrushPanel() {
        initComponents();

        DefaultComboBoxModel diffusionComboModel = new DefaultComboBoxModel(DiffusionMethods.DiffusionMethod.values());
        diffusionCombobox.setModel(diffusionComboModel);
    }

    public void setIntensity(float intensity) {
        intensitySpinner.setValue((int) (intensity * 100));
    }

    public float getIntensity() {
        return ((Integer) intensitySpinner.getModel().getValue()).floatValue() / 100f;
    }

    public void setColor(Color color) {
        ((JColorButton) colorButton).setColor(color);
    }

    public Color getColor() {
        return ((JColorButton) colorButton).getColor();
    }

    public void setDiffusionMethod(DiffusionMethods.DiffusionMethod diffusionMethod) {
        diffusionCombobox.setSelectedItem(diffusionMethod);
    }

    public DiffusionMethods.DiffusionMethod getDiffusionMethod() {
        return (DiffusionMethods.DiffusionMethod) diffusionCombobox.getSelectedItem();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelDiffusion = new javax.swing.JLabel();
        labelColor = new javax.swing.JLabel();
        diffusionCombobox = new javax.swing.JComboBox();
        colorButton = new JColorButton(Color.BLACK);
        labelIntensity = new javax.swing.JLabel();
        intensitySpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        labelDiffusion.setFont(labelDiffusion.getFont().deriveFont((float)10));
        labelDiffusion.setText(org.openide.util.NbBundle.getMessage(BrushPanel.class, "BrushPanel.labelDiffusion.text")); // NOI18N
        add(labelDiffusion);

        labelColor.setFont(labelColor.getFont().deriveFont((float)10));
        labelColor.setText(org.openide.util.NbBundle.getMessage(BrushPanel.class, "BrushPanel.labelColor.text")); // NOI18N
        add(labelColor);

        diffusionCombobox.setFont(diffusionCombobox.getFont().deriveFont((float)10));
        diffusionCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(diffusionCombobox);

        colorButton.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        colorButton.setText(org.openide.util.NbBundle.getMessage(BrushPanel.class, "BrushPanel.colorButton.text")); // NOI18N
        colorButton.setContentAreaFilled(false);
        colorButton.setFocusPainted(false);
        add(colorButton);

        labelIntensity.setFont(labelIntensity.getFont().deriveFont((float)10));
        labelIntensity.setText(org.openide.util.NbBundle.getMessage(BrushPanel.class, "BrushPanel.labelIntensity.text")); // NOI18N
        add(labelIntensity);

        intensitySpinner.setFont(intensitySpinner.getFont().deriveFont((float)10));
        intensitySpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        add(intensitySpinner);

        jLabel1.setFont(jLabel1.getFont().deriveFont((float)10));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(BrushPanel.class, "BrushPanel.jLabel1.text")); // NOI18N
        add(jLabel1);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton colorButton;
    private javax.swing.JComboBox diffusionCombobox;
    private javax.swing.JSpinner intensitySpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel labelColor;
    private javax.swing.JLabel labelDiffusion;
    private javax.swing.JLabel labelIntensity;
    // End of variables declaration//GEN-END:variables
}
