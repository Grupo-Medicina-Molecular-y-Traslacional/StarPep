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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.gephi.ui.components.gradientslider.GradientSlider;
import org.gephi.utils.PaletteUtils;
import org.gephi.utils.PaletteUtils.Palette;

/**
 *
 * @author Mathieu Bastian
 */
public class HeatMapPanel extends javax.swing.JPanel {

    private PaletteComboBox paletteComboBox;
    private GradientSlider slider;
    private JCheckBox dontPaintUnreachableCheckbox;
    private JCheckBox invertPaletteCheckbox;
    private boolean usePalette = true;

    /** Creates new form HeatMapPanel */
    public HeatMapPanel(Color[] gradientColors, float[] gradientPositions, boolean dontPaintUnreachable) {
        initComponents();

        //Slider
        slider = new GradientSlider(GradientSlider.HORIZONTAL, gradientPositions, gradientColors);
        slider.setPreferredSize(new Dimension(70, 18));
        slider.putClientProperty("GradientSlider.includeOpacity", "false");
        gradientPanel.add(slider);

        //Paint
        dontPaintUnreachableCheckbox = new JCheckBox();
        dontPaintUnreachableCheckbox.setSelected(dontPaintUnreachable);
        dontPaintUnreachableCheckbox.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        dontPaintUnreachableCheckbox.setText(org.openide.util.NbBundle.getMessage(HeatMapPanel.class, "HeatMapPanel.dontPaintUnreachableCheckbox.text")); // NOI18N
        dontPaintUnreachableCheckbox.setToolTipText(org.openide.util.NbBundle.getMessage(HeatMapPanel.class, "HeatMapPanel.dontPaintUnreachableCheckbox.text")); // NOI18N
        dontPaintUnreachableCheckbox.setPreferredSize(new java.awt.Dimension(140, 28));
        gradientPanel.add(dontPaintUnreachableCheckbox);

        //Invert
        invertPaletteCheckbox = new JCheckBox();
        invertPaletteCheckbox.setSelected(dontPaintUnreachable);
        invertPaletteCheckbox.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        invertPaletteCheckbox.setText(org.openide.util.NbBundle.getMessage(HeatMapPanel.class, "HeatMapPanel.invertPalette.text")); // NOI18N
        invertPaletteCheckbox.setPreferredSize(new java.awt.Dimension(140, 28));
        invertPaletteCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                paletteComboBox.initReverse();
            }
        });

        //Palette combo
        paletteComboBox = new PaletteComboBox(PaletteUtils.getSequencialPalettes());
        palettePanel.add(paletteComboBox);
        palettePanel.add(invertPaletteCheckbox);

        //Init events
        modeComboBox.setSelectedIndex(usePalette? 0 : 1);
        modeComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (modeComboBox.getSelectedIndex() == 0) {
                    usePalette = true;                    
                } else {
                    usePalette = false;
                }
                initMode();
            }
        });
        
        initMode();
    }

    private void initMode() {
        if (usePalette) {
            gradientPanel.setVisible(false);
            palettePanel.setVisible(true);
        } else {
            gradientPanel.setVisible(true);
            palettePanel.setVisible(false);
        }
    }

    public boolean isUsePalette() {
        return usePalette;
    }

    public void setStatus(String status) {
        heatMapInfoLabel.setText(status);
        heatMapInfoLabel.setToolTipText(status);
    }

    public Color[] getGradientColors() {
        return (Color[]) slider.getColors();
    }

    public float[] getGradientPositions() {
        return slider.getThumbPositions();
    }

    public boolean isDontPaintUnreachable() {
        return dontPaintUnreachableCheckbox.isSelected();
    }

    public Palette getSelectedPalette() {
        return (Palette) paletteComboBox.getSelectedItem();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        heatMapInfoLabel = new javax.swing.JLabel();
        labelMode = new javax.swing.JLabel();
        modeComboBox = new javax.swing.JComboBox();
        gradientPanel = new javax.swing.JPanel();
        palettePanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        heatMapInfoLabel.setFont(heatMapInfoLabel.getFont().deriveFont((float)10));
        heatMapInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/resources/info.png"))); // NOI18N
        heatMapInfoLabel.setText(org.openide.util.NbBundle.getMessage(HeatMapPanel.class, "HeatMapPanel.heatMapInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(heatMapInfoLabel, gridBagConstraints);

        labelMode.setFont(labelMode.getFont().deriveFont((float)10));
        labelMode.setText(org.openide.util.NbBundle.getMessage(HeatMapPanel.class, "HeatMapPanel.labelMode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(labelMode, gridBagConstraints);

        modeComboBox.setFont(modeComboBox.getFont().deriveFont((float)10));
        modeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Palette", "Gradient" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(modeComboBox, gridBagConstraints);

        gradientPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(gradientPanel, gridBagConstraints);

        palettePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(palettePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel gradientPanel;
    private javax.swing.JLabel heatMapInfoLabel;
    private javax.swing.JLabel labelMode;
    private javax.swing.JComboBox modeComboBox;
    private javax.swing.JPanel palettePanel;
    // End of variables declaration//GEN-END:variables

    private class PaletteComboBox extends JComboBox {

        private Palette[] palettes;

        public PaletteComboBox(Palette[] pallettes) {
            super(pallettes);
            this.palettes = pallettes;
            PaletteListCellRenderer r = new PaletteListCellRenderer();
            r.setPreferredSize(new Dimension(70, 18));
            r.setOpaque(true);
            setRenderer(r);
            initReverse();
        }

        public void initReverse() {
            if (invertPaletteCheckbox.isSelected()) {
                int selectedIndex = getSelectedIndex();
                DefaultComboBoxModel newModel = new DefaultComboBoxModel();
                for (int i = 0; i < getModel().getSize(); i++) {
                    newModel.addElement(PaletteUtils.reversePalette((Palette) getModel().getElementAt(i)));
                }
                setModel(newModel);
                setSelectedIndex(selectedIndex);
            } else {
                int selectedIndex = getSelectedIndex();
                setModel(new DefaultComboBoxModel(palettes));
                setSelectedIndex(selectedIndex);
            }
        }

        //Renderer
        private class PaletteListCellRenderer extends JLabel implements ListCellRenderer {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                //int selectedIndex = ((Integer) value).intValue();

                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }

                //Set icon
                Palette p = (Palette) value;
                PaletteIcon icon = new PaletteIcon(p.getColors());
                setIcon(icon);
                return this;
            }
        }
    }

    private static class PaletteIcon implements Icon {

        private static int COLOR_WIDTH = 13;
        private static int COLOR_HEIGHT = 13;
        private static Color BORDER_COLOR = new Color(0x444444);
        private Color[] colors;

        public PaletteIcon(Color[] colors) {
            this.colors = colors;
        }

        @Override
        public int getIconWidth() {
            return COLOR_WIDTH * colors.length;
        }

        @Override
        public int getIconHeight() {
            return COLOR_HEIGHT + 2;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {

            for (int i = 0; i < colors.length; i++) {
                g.setColor(BORDER_COLOR);
                g.drawRect(x + 2 + i * COLOR_WIDTH, y, COLOR_WIDTH, COLOR_HEIGHT);
                g.setColor(colors[i]);
                g.fillRect(x + 2 + i * COLOR_WIDTH + 1, y + 1, COLOR_WIDTH - 1, COLOR_HEIGHT - 1);
            }
        }
    }
}
