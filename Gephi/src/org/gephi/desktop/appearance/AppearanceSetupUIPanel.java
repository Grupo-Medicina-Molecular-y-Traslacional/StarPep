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
package org.gephi.desktop.appearance;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Interpolator;
import org.gephi.appearance.api.RankingFunction;
import org.gephi.appearance.plugin.AppearanceAlgorithm;
import org.gephi.appearance.spi.TransformerUI;
import org.gephi.ui.components.splineeditor.SplineEditor;
import org.gephi.ui.utils.UIUtils;
import org.jdesktop.swingx.JXBusyLabel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class AppearanceSetupUIPanel extends javax.swing.JPanel implements AlgorithmSetupUI, AppearanceUIModelListener, PropertyChangeListener {

    private transient final AppearanceToolbar toolbar;
    private transient final AppearanceUIController controller;
    private transient AppearanceUIModel model;
    private transient ItemListener attributeListener;
    private final transient SplineEditor splineEditor;
    protected final JXBusyLabel busyLabel;
    private final String NO_SELECTION = NbBundle.getMessage(AppearanceSetupUIPanel.class, "AppearanceSetupUIPanel.choose.text");
    private AppearanceAlgorithm algorithm;

    /**
     * Creates new form VizExtendedBar
     */
    public AppearanceSetupUIPanel() {
        controller = Lookup.getDefault().lookup(AppearanceUIController.class);
        toolbar = new AppearanceToolbar(controller);

        model = controller.getModel();
        controller.addPropertyChangeListener(this);

        initComponents();
        if (UIUtils.isAquaLookAndFeel()) {
            setBackground(UIManager.getColor("NbExplorerView.background"));
        }

        categoryPanel.add(toolbar.getCategoryToolbar(), BorderLayout.CENTER);
        transformerPanel.add(toolbar.getTransformerToolbar(), BorderLayout.CENTER);
        splineEditor = new SplineEditor();
        splineEditor.setModal(true);
        splineEditor.setTitle(NbBundle.getMessage(AppearanceSetupUIPanel.class, "AppearanceSetupUIPanel.splineEditor.title"));
        splineEditor.setControl1(new Point2D.Float(0, 0));
        splineEditor.setControl2(new Point2D.Float(1, 1));
        refreshModel(model);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(AppearanceSetupUIPanel.class, "AppearanceSetupUIPanel.busyLabel.text"));

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (algorithm != null) {
                    algorithm.addRunningListener(AppearanceSetupUIPanel.this);
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (algorithm != null) {
                    algorithm.removeRunningListener(AppearanceSetupUIPanel.this);
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    private void setBusy(boolean running) {
        busyLabel.setBusy(running);
        if (running) {
            centerPanel.removeAll();
            centerPanel.add(busyLabel, BorderLayout.CENTER);
            centerPanel.revalidate();
            centerPanel.repaint();
        } else {
            refreshCenterPanel();
        }
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

        categoryPanel = new javax.swing.JPanel();
        transformerPanel = new javax.swing.JPanel();
        attributePanel = new javax.swing.JPanel();
        attibuteBox = new javax.swing.JComboBox();
        centerPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        jLabelInterpo = new javax.swing.JLabel();
        jComboBoxInterpo = new javax.swing.JComboBox<>();
        splineButton1 = new org.jdesktop.swingx.JXHyperlink();

        setMinimumSize(new java.awt.Dimension(368, 107));
        setPreferredSize(new java.awt.Dimension(380, 220));
        setLayout(new java.awt.GridBagLayout());

        categoryPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(categoryPanel, gridBagConstraints);

        transformerPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(transformerPanel, gridBagConstraints);

        attributePanel.setLayout(new java.awt.BorderLayout());

        attributePanel.add(attibuteBox, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(attributePanel, gridBagConstraints);

        centerPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(centerPanel, gridBagConstraints);

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelInterpo.setText(org.openide.util.NbBundle.getMessage(AppearanceSetupUIPanel.class, "AppearanceSetupUIPanel.jLabelInterpo.text")); // NOI18N
        controlPanel.add(jLabelInterpo);

        jComboBoxInterpo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Linear", "Log2", "Bezier" }));
        jComboBoxInterpo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInterpoActionPerformed(evt);
            }
        });
        controlPanel.add(jComboBoxInterpo);

        splineButton1.setText(org.openide.util.NbBundle.getMessage(AppearanceSetupUIPanel.class, "AppearanceSetupUIPanel.splineButton1.text")); // NOI18N
        splineButton1.setToolTipText(org.openide.util.NbBundle.getMessage(AppearanceSetupUIPanel.class, "AppearanceSetupUIPanel.splineButton1.toolTipText")); // NOI18N
        splineButton1.setClickedColor(new java.awt.Color(0, 51, 255));
        splineButton1.setFocusPainted(false);
        splineButton1.setFocusable(false);
        splineButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        splineButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        splineButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                splineButton1ActionPerformed(evt);
            }
        });
        controlPanel.add(splineButton1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(controlPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxInterpoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInterpoActionPerformed
        if (model != null && model.getSelectedFunction() != null && model.getSelectedFunction().isRanking()) {
            RankingFunction function = (RankingFunction) model.getSelectedFunction();
            switch (jComboBoxInterpo.getSelectedIndex()) {
                case 0:
                    function.setInterpolator(Interpolator.LINEAR);
                    splineButton1.setVisible(false);
                    break;
                case 1:
                    function.setInterpolator(Interpolator.LOG2);
                    splineButton1.setVisible(false);
                    break;
                case 2:
                    if (!(function.getInterpolator() instanceof Interpolator.BezierInterpolator)) {
                        splineEditor.setVisible(true);
                        function.setInterpolator(
                                new Interpolator.BezierInterpolator(
                                        (float) splineEditor.getControl1().getX(), (float) splineEditor.getControl1().getY(),
                                        (float) splineEditor.getControl2().getX(), (float) splineEditor.getControl2().getY()));
                    }
                    splineButton1.setVisible(true);
                    break;
            }
        }
    }//GEN-LAST:event_jComboBoxInterpoActionPerformed

    private void splineButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_splineButton1ActionPerformed
        if (model != null && model.getSelectedFunction() != null && model.getSelectedFunction().isRanking()) {
            RankingFunction function = (RankingFunction) model.getSelectedFunction();
            Interpolator.BezierInterpolator bezierInterpolator = (Interpolator.BezierInterpolator) function.getInterpolator();
            splineEditor.setControl1(bezierInterpolator.getControl1());
            splineEditor.setControl2(bezierInterpolator.getControl2());
            splineEditor.setVisible(true);
            function.setInterpolator(
                    new Interpolator.BezierInterpolator(
                            (float) splineEditor.getControl1().getX(), (float) splineEditor.getControl1().getY(),
                            (float) splineEditor.getControl2().getX(), (float) splineEditor.getControl2().getY()));
        }
    }//GEN-LAST:event_splineButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox attibuteBox;
    private javax.swing.JPanel attributePanel;
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JComboBox<String> jComboBoxInterpo;
    private javax.swing.JLabel jLabelInterpo;
    private org.jdesktop.swingx.JXHyperlink splineButton1;
    private javax.swing.JPanel transformerPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AppearanceUIModelEvent.MODEL)) {
            refreshModel((AppearanceUIModel) evt.getNewValue());
        } else if (evt.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_ELEMENT_CLASS)
                || evt.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_CATEGORY)
                || evt.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_TRANSFORMER_UI)
                || evt.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_FUNCTION)
                || evt.getPropertyName().equals(AppearanceUIModelEvent.REFRESHED_FUNCTIONS)){
            refreshCenterPanel();
            refreshCombo();
            refreshControls();
        } else if (algorithm != null && evt.getSource().equals(algorithm)
                && evt.getPropertyName().equals(AppearanceAlgorithm.RUNNING)) {
            setBusy((boolean) evt.getNewValue());
        }
    }

    public void refreshModel(AppearanceUIModel model) {
        this.model = model;
        refreshCenterPanel();
        refreshCombo();
        refreshControls();
    }

    private void refreshCenterPanel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                centerPanel.removeAll();
                JPanel panel = null;
                if (model != null) {
                    TransformerUI ui = model.getSelectedTransformerUI();
                    if (ui != null) {
                        boolean attribute = model.isAttributeTransformerUI(ui);

                        attributePanel.setVisible(attribute);
                        if (attribute) {
                            Function function = model.getSelectedFunction();
                            if (function != null) {
                                ui = function.getUI();
                                panel = ui.getPanel(function);
                            }
                        } else {
                            Function function = model.getSelectedFunction();
                            panel = ui.getPanel(function);
                        }

                        if (panel != null) {
                            panel.setOpaque(false);
                            centerPanel.add(panel, BorderLayout.CENTER);
                        }
                    }
                } else {
                    attributePanel.setVisible(false);
                }
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });
    }

    private void refreshCombo() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
                if (model != null) {
                    TransformerUI ui = model.getSelectedTransformerUI();
                    if (ui != null && model.isAttributeTransformerUI(ui)) {

                        //Ranking
                        Function selectedColumn = model.getSelectedFunction();
                        attibuteBox.removeItemListener(attributeListener);

                        comboBoxModel.addElement(NO_SELECTION);
                        comboBoxModel.setSelectedItem(NO_SELECTION);

                        List<Function> rows = new ArrayList<>();
                        rows.addAll(model.getFunctions());

                        Collections.sort(rows, new Comparator<Function>() {
                            @Override
                            public int compare(Function o1, Function o2) {
                                return o1.getUI().getDisplayName().compareTo(o2.getUI().getDisplayName());
                            }
                        });
                        for (Function r : rows) {
                            comboBoxModel.addElement(r);
                            if (selectedColumn != null && selectedColumn.equals(r)) {
                                comboBoxModel.setSelectedItem(r);
                            }
                        }
                        attributeListener = new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                if (model != null) {
                                    if (!attibuteBox.getSelectedItem().equals(NO_SELECTION)) {
                                        Function selectedItem = (Function) attibuteBox.getSelectedItem();
                                        Function selectedFunction = model.getSelectedFunction();
                                        if (selectedFunction != selectedItem) {
                                            controller.setSelectedFunction(selectedItem);
                                        }
                                    } else {
                                        controller.setSelectedFunction(null);
                                    }
                                }
                            }
                        };
                        attibuteBox.addItemListener(attributeListener);
                    }
                }
                attibuteBox.setModel(comboBoxModel);
            }
        });
    }

    private void refreshControls() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (model != null) {
                    if (model.getSelectedFunction() != null && model.getSelectedFunction().isRanking()) {
                        controlPanel.setVisible(true);
                        RankingFunction function = (RankingFunction) model.getSelectedFunction();
                        Interpolator interpolator = function.getInterpolator();
                        splineButton1.setVisible(false);
                        if (interpolator == Interpolator.LINEAR) {
                            jComboBoxInterpo.setSelectedIndex(0);
                        } else if (interpolator == Interpolator.LOG2) {
                            jComboBoxInterpo.setSelectedIndex(1);
                        } else if (interpolator instanceof Interpolator.BezierInterpolator) {
                            jComboBoxInterpo.setSelectedIndex(2);
                        }
                    } else {
                        controlPanel.setVisible(false);
                        jComboBoxInterpo.setSelectedIndex(-1);
                    }
                }
            }
        });
    }

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        this.algorithm = (AppearanceAlgorithm) algo;
        return this;
    }

}
