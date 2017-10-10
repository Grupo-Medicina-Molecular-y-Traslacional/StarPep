/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.bapedis.core.model.AlgorithmNode;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.bapedis.modamp.impl.AllDescriptors;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ChemicalSpaceNetworkPanel extends javax.swing.JPanel implements AlgorithmSetupUI {

    /**
     * Creates new form ChemicalSpaceNetworkPanel
     */
    protected ChemicalSpaceNetwork csnAlgo;
    protected final ThresholdRangePanel thresholdPanel;
    JXHyperlink checkAll, uncheckAll, subset;

    public ChemicalSpaceNetworkPanel() {
        initComponents();
        thresholdPanel = new ThresholdRangePanel();
        southPanel.add(thresholdPanel, BorderLayout.CENTER);
        ((PropertySheet) propSheetPanel).setDescriptionAreaVisible(false);
        checkAll = new JXHyperlink();
        checkAll.setText(NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.checkAll.text"));
        checkAll.setToolTipText(org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.checkAll.toolTipText"));         
        checkAll.setClickedColor(new java.awt.Color(0, 51, 255));
        checkAll.setFocusPainted(false);
        checkAll.setFocusable(false);
        checkAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        checkAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);        
        checkAll.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAll(true);
            }
        });
        topPanel.add(checkAll);

        uncheckAll = new JXHyperlink();
        uncheckAll.setText(NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.unCheckAll.text"));
        uncheckAll.setToolTipText(org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.unCheckAll.toolTipText"));       
        uncheckAll.setClickedColor(new java.awt.Color(0, 51, 255));
        uncheckAll.setFocusPainted(false);
        uncheckAll.setFocusable(false);
        uncheckAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        uncheckAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);                
        uncheckAll.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAll(false);
            }
        });
        topPanel.add(uncheckAll);
        
        subset = new JXHyperlink();
        subset.setText(NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.subset.text"));
        subset.setToolTipText(org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.subset.toolTipText"));       
        subset.setClickedColor(new java.awt.Color(0, 51, 255));
        subset.setFocusPainted(false);
        subset.setFocusable(false);
        subset.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        subset.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);                
        subset.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSubset();
            }
        });
        topPanel.add(subset);
        
    }

    private void checkAll(boolean selected) {
        if (csnAlgo != null){
            csnAlgo.getDescriptorAlgorithm().setAllMD(selected);
            propSheetPanel.repaint();
        }
    }
    
    private void setSubset(){
        if (csnAlgo != null){
            AllDescriptors allDescriptor =csnAlgo.getDescriptorAlgorithm();
            allDescriptor.setAllMD(true);
            allDescriptor.setAaComposition(false);
            allDescriptor.setdComposition(false);
            allDescriptor.setRaComposition(false);
            allDescriptor.setRaDistribution(false);
            allDescriptor.setRaTransition(false);
            allDescriptor.setTriComposition(false);
            propSheetPanel.repaint();
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

        buttonGroup = new javax.swing.ButtonGroup();
        descriptorsPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        propSheetPanel = new PropertySheet();
        featurePanel = new javax.swing.JPanel();
        selectAllRButton = new javax.swing.JRadioButton();
        removeUselessRButton = new javax.swing.JRadioButton();
        selectRankedRButton = new javax.swing.JRadioButton();
        southPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        descriptorsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.descriptorsPanel.border.title"))); // NOI18N
        descriptorsPanel.setMinimumSize(new java.awt.Dimension(442, 180));
        descriptorsPanel.setPreferredSize(new java.awt.Dimension(442, 180));
        descriptorsPanel.setLayout(new java.awt.GridBagLayout());

        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        descriptorsPanel.add(topPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        descriptorsPanel.add(propSheetPanel, gridBagConstraints);

        featurePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.featurePanel.border.title"))); // NOI18N
        featurePanel.setLayout(new javax.swing.BoxLayout(featurePanel, javax.swing.BoxLayout.Y_AXIS));

        buttonGroup.add(selectAllRButton);
        org.openide.awt.Mnemonics.setLocalizedText(selectAllRButton, org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.selectAllRButton.text")); // NOI18N
        selectAllRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllRButtonActionPerformed(evt);
            }
        });
        featurePanel.add(selectAllRButton);

        buttonGroup.add(removeUselessRButton);
        org.openide.awt.Mnemonics.setLocalizedText(removeUselessRButton, org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.removeUselessRButton.text")); // NOI18N
        removeUselessRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeUselessRButtonActionPerformed(evt);
            }
        });
        featurePanel.add(removeUselessRButton);

        buttonGroup.add(selectRankedRButton);
        org.openide.awt.Mnemonics.setLocalizedText(selectRankedRButton, org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.selectRankedRButton.text")); // NOI18N
        selectRankedRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectRankedRButtonActionPerformed(evt);
            }
        });
        featurePanel.add(selectRankedRButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        descriptorsPanel.add(featurePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(descriptorsPanel, gridBagConstraints);

        southPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(southPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void selectAllRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllRButtonActionPerformed
        if (csnAlgo != null) {
            csnAlgo.setButtonGroupIndex(0);
        }
    }//GEN-LAST:event_selectAllRButtonActionPerformed

    private void removeUselessRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeUselessRButtonActionPerformed
        if (csnAlgo != null) {
            csnAlgo.setButtonGroupIndex(1);
        }
    }//GEN-LAST:event_removeUselessRButtonActionPerformed

    private void selectRankedRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectRankedRButtonActionPerformed
        if (csnAlgo != null) {
            csnAlgo.setButtonGroupIndex(2);
        }
    }//GEN-LAST:event_selectRankedRButtonActionPerformed

    @Override
    public JPanel getEditPanel(Algorithm algo) {
        this.csnAlgo = (ChemicalSpaceNetwork) algo;
        ((PropertySheet) propSheetPanel).setNodes(new Node[]{new AlgorithmNode(csnAlgo.getDescriptorAlgorithm())});
        setSelectedGroupIndex(csnAlgo.getButtonGroupIndex());
        return this;
    }

    private void setSelectedGroupIndex(int index) {
        switch (index) {
            case 0:
                selectAllRButton.setSelected(true);
                break;
            case 1:
                removeUselessRButton.setSelected(true);
                break;
            case 2:
                selectRankedRButton.setSelected(true);
                break;
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel descriptorsPanel;
    private javax.swing.JPanel featurePanel;
    private javax.swing.JPanel propSheetPanel;
    private javax.swing.JRadioButton removeUselessRButton;
    private javax.swing.JRadioButton selectAllRButton;
    private javax.swing.JRadioButton selectRankedRButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
