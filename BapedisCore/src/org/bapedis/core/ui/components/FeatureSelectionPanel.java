/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FeatureSelectionModel;
import org.bapedis.core.services.ProjectManager;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSelectionPanel extends javax.swing.JPanel {
    
    protected final ProjectManager pc;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected final JXHyperlink select;
    
    public FeatureSelectionPanel() {
        initComponents();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        
        select = new JXHyperlink();
        select.setText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.select.text"));
        select.setToolTipText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.select.toolTipText"));
        select.setClickedColor(new java.awt.Color(0, 51, 255));
        select.setFocusPainted(false);
        select.setFocusable(false);
        select.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        select.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        select.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setMolecularDescriptors();
            }
        });
        
        topPanel.add(new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.input.label")));
        topPanel.add(new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.input.text")));
        topPanel.add(select);

        // Configure sliders
        useless1Slider.setMinimum(FeatureSelectionModel.ENTROPY_THRESHOLD[0]);
        useless1Slider.setMaximum(FeatureSelectionModel.ENTROPY_THRESHOLD[2]);
        useless1Slider.setMajorTickSpacing(10);
        useless1Slider.setMinorTickSpacing(10);
        useless2Slider.setMinimum(FeatureSelectionModel.ENTROPY_THRESHOLD[0]);
        useless2Slider.setMaximum(FeatureSelectionModel.ENTROPY_THRESHOLD[2]);
        useless2Slider.setMajorTickSpacing(10);
        useless2Slider.setMinorTickSpacing(10);

        redundantSlider.setMinimum(FeatureSelectionModel.CORRELATION_THRESHOLD[0]);
        redundantSlider.setMaximum(FeatureSelectionModel.CORRELATION_THRESHOLD[2]);
        redundantSlider.setMajorTickSpacing(10);
        redundantSlider.setMinorTickSpacing(10);
        
        Hashtable<Integer, JLabel> uselessLabelTable = new Hashtable<>();
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_THRESHOLD[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.slider.low")));
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_THRESHOLD[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.slider.medium")));
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_THRESHOLD[2], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.slider.high")));
        
        useless1Slider.setLabelTable(uselessLabelTable);
        useless2Slider.setLabelTable(uselessLabelTable);
        
        Hashtable<Integer, JLabel> redundantLabelTable = new Hashtable<>();
        redundantLabelTable.put(FeatureSelectionModel.CORRELATION_THRESHOLD[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.slider.low")));
        redundantLabelTable.put(FeatureSelectionModel.CORRELATION_THRESHOLD[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.slider.medium")));
        redundantLabelTable.put(FeatureSelectionModel.CORRELATION_THRESHOLD[2], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.slider.high")));
        
        redundantSlider.setLabelTable(redundantLabelTable);
    }
    
    public void setup(FeatureSelectionModel model) {
        
    }
    
    private void setMolecularDescriptors() {
        AttributesModel attrModel = pc.getAttributesModel();
        if (attrModel != null) {
            DescriptorSelectionPanel selectionPanel = new DescriptorSelectionPanel(attrModel);
//        selectionPanel.setSelectedDescriptorKeys(keys);
            selectionPanel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    TableModel model = (TableModel) e.getSource();
                    for (int row = 0; row < model.getRowCount(); row++) {
                        if ((boolean) model.getValueAt(row, 0)) {
//                        keys.add((String) model.getValueAt(row, 1));
                        } else {
//                        keys.remove((String) model.getValueAt(row, 1));
                        }
                    }
                }
            });
            DialogDescriptor dd = new DialogDescriptor(selectionPanel, NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.DescriptorSelectionPanel.title"));
            dd.setOptions(new Object[]{DialogDescriptor.CLOSED_OPTION});
            DialogDisplayer.getDefault().notify(dd);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        topPanel = new javax.swing.JPanel();
        featurePanel = new javax.swing.JPanel();
        option1Button = new javax.swing.JRadioButton();
        option2Button = new javax.swing.JRadioButton();
        option3Button = new javax.swing.JRadioButton();
        useless1Slider = new javax.swing.JSlider();
        useless2Slider = new javax.swing.JSlider();
        redundantSlider = new javax.swing.JSlider();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.border.title"))); // NOI18N
        setPreferredSize(new java.awt.Dimension(271, 160));
        setLayout(new java.awt.GridBagLayout());

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 5);
        flowLayout1.setAlignOnBaseline(true);
        topPanel.setLayout(flowLayout1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(topPanel, gridBagConstraints);

        featurePanel.setPreferredSize(new java.awt.Dimension(259, 130));
        featurePanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(option1Button);
        org.openide.awt.Mnemonics.setLocalizedText(option1Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.option1Button.text")); // NOI18N
        option1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option1ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        featurePanel.add(option1Button, gridBagConstraints);

        buttonGroup1.add(option2Button);
        org.openide.awt.Mnemonics.setLocalizedText(option2Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removeUselessRButton.text")); // NOI18N
        option2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        featurePanel.add(option2Button, gridBagConstraints);

        buttonGroup1.add(option3Button);
        option3Button.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(option3Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.selectRankedRButton.text")); // NOI18N
        option3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option3ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        featurePanel.add(option3Button, gridBagConstraints);

        useless1Slider.setPaintLabels(true);
        useless1Slider.setPaintTicks(true);
        useless1Slider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.useless1Slider.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        featurePanel.add(useless1Slider, gridBagConstraints);

        useless2Slider.setPaintLabels(true);
        useless2Slider.setPaintTicks(true);
        useless2Slider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.useless2Slider.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        featurePanel.add(useless2Slider, gridBagConstraints);

        redundantSlider.setPaintLabels(true);
        redundantSlider.setPaintTicks(true);
        redundantSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        featurePanel.add(redundantSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(featurePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void option3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option3ButtonActionPerformed
//        if (algo != null) {
//            algo.setButtonGroupIndex(2);
//        }
    }//GEN-LAST:event_option3ButtonActionPerformed

    private void option2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option2ButtonActionPerformed
//        if (algo != null) {
//            algo.setButtonGroupIndex(1);
//        }
    }//GEN-LAST:event_option2ButtonActionPerformed

    private void option1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option1ButtonActionPerformed
//        if (algo != null) {
//            algo.setButtonGroupIndex(0);
//        }
    }//GEN-LAST:event_option1ButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel featurePanel;
    private javax.swing.JRadioButton option1Button;
    private javax.swing.JRadioButton option2Button;
    private javax.swing.JRadioButton option3Button;
    private javax.swing.JSlider redundantSlider;
    private javax.swing.JPanel topPanel;
    private javax.swing.JSlider useless1Slider;
    private javax.swing.JSlider useless2Slider;
    // End of variables declaration//GEN-END:variables

}
