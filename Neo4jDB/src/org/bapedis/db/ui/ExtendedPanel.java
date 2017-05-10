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
package org.bapedis.db.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.spi.TransformerUI;
import org.gephi.desktop.appearance.AppearanceToolbar;
import org.gephi.desktop.appearance.AppearanceUIController;
import org.gephi.desktop.appearance.AppearanceUIModel;
import org.gephi.desktop.appearance.AppearanceUIModelEvent;
import org.gephi.desktop.appearance.AppearanceUIModelListener;
import org.gephi.ui.utils.UIUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class ExtendedPanel extends javax.swing.JPanel implements AppearanceUIModelListener {

    private transient final AppearanceToolbar toolbar;
    private transient final AppearanceUIController controller;
    private transient AppearanceUIModel model;
    private transient ItemListener attributeListener;
    private final String NO_SELECTION = NbBundle.getMessage(ExtendedPanel.class, "ExtendedPanel.choose.text");

    /**
     * Creates new form VizExtendedBar
     */
    public ExtendedPanel(AppearanceUIController controller, AppearanceToolbar toolbar) {
        this.controller = controller;
        this.toolbar = toolbar;
        model = controller.getModel();
        controller.addPropertyChangeListener(this);

        initComponents();
        if (UIUtils.isAquaLookAndFeel()) {
            setBackground(UIManager.getColor("NbExplorerView.background"));
        }

        transformerPanel.add(toolbar.getTransformerToolbar(), BorderLayout.CENTER);
        controlPanel.add(toolbar.getControlToolbar(), BorderLayout.CENTER);
        refreshModel(model);
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

        transformerPanel = new javax.swing.JPanel();
        attributePanel = new javax.swing.JPanel();
        attibuteBox = new javax.swing.JComboBox();
        centerPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(640, 480));
        setLayout(new java.awt.GridBagLayout());

        transformerPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(transformerPanel, gridBagConstraints);

        attributePanel.setLayout(new java.awt.BorderLayout());

        attributePanel.add(attibuteBox, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(attributePanel, gridBagConstraints);

        centerPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(centerPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(controlPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox attibuteBox;
    private javax.swing.JPanel attributePanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel transformerPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AppearanceUIModelEvent.MODEL)) {
            refreshModel((AppearanceUIModel) evt.getNewValue());
        } else if (evt.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_CATEGORY)
                || evt.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_TRANSFORMER_UI)) {
            refreshCenterPanel();
            refreshCombo();
            refreshControls();
        } else if (evt.getPropertyName().equals(AppearanceUIModelEvent.SELECTED_FUNCTION)) {
            refreshCenterPanel();
            refreshCombo();
            refreshControls();
        }
//        else if (evt.getPropertyName().equals(AppearanceUIModelEvent.SET_AUTO_APPLY)) {
//            refreshControls();
//        } else if (evt.getPropertyName().equals(AppearanceUIModelEvent.START_STOP_AUTO_APPLY)) {
//            refreshControls();
//        }
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
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                if (model != null) {
//                    if (model.getSelectedFunction() != null) {
//                        enableAutoButton.setEnabled(true);
//                        if (model.getAutoAppyTransformer() != null) {
//                            applyButton.setVisible(false);
//                            enableAutoButton.setSelected(true);
//                            AutoAppyTransformer aat = model.getAutoAppyTransformer();
//                            if (aat.isRunning()) {
//                                autoApplyButton.setVisible(false);
//                                stopAutoApplyButton.setVisible(true);
//                                stopAutoApplyButton.setSelected(true);
//                            } else {
//                                autoApplyButton.setVisible(true);
//                                autoApplyButton.setSelected(false);
//                                stopAutoApplyButton.setVisible(false);
//                            }
//                        } else {
//                            autoApplyButton.setVisible(false);
//                            stopAutoApplyButton.setVisible(false);
//                            enableAutoButton.setSelected(false);
//                            applyButton.setVisible(true);
//                            applyButton.setEnabled(true);
//                        }
//
//                        Function func = model.getSelectedFunction();
//                        rankingButton.setEnabled(true);
//                        partitionButton.setEnabled(true);
//                        if (func.isPartition()) {
//                            if (!func.isAttribute()) {
//                                rankingButton.setEnabled(false);
//                            } else {
//                                AttributeFunction af = (AttributeFunction) func;
//                                Column col = af.getColumn();
//                                if (!col.isNumber()) {
//                                    rankingButton.setEnabled(false);
//                                }
//                            }
//                        } else if (func.isRanking()) {
//                            if (!func.isAttribute()) {
//                                partitionButton.setEnabled(false);
//                            }
//                        }
//                    }
//                    localScaleButton.setSelected(model.isLocalScale());
//                    return;
//                }
//                //Disable
//                stopAutoApplyButton.setVisible(false);
//                autoApplyButton.setVisible(false);
//                applyButton.setVisible(true);
//                applyButton.setEnabled(false);
//                enableAutoButton.setEnabled(false);
//            }
//        });
    }
}
