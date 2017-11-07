/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Set;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.services.ProjectManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author loge
 */
public class FeatureSelectionPanel extends javax.swing.JPanel implements LookupListener {

    protected final ProjectManager pc;
    Lookup.Result<AttributesModel> peptideLkpResult;
    
    public FeatureSelectionPanel() {
        initComponents();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
//        DescriptorSelectionPanel selectionPanel = new DescriptorSelectionPanel(pc.getAttributesModel());
//        centerPanel.add(selectionPanel, BorderLayout.CENTER);
        
        
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
//                setMolecularDescriptors(pc.getAttributesModel());
                peptideLkpResult = pc.getCurrentWorkspace().getLookup().lookupResult(AttributesModel.class);
                peptideLkpResult.addLookupListener(FeatureSelectionPanel.this);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (peptideLkpResult != null) {
                    peptideLkpResult.removeLookupListener(FeatureSelectionPanel.this);
                    peptideLkpResult = null;
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });        
    }
    
//    private void setMolecularDescriptors(AttributesModel attrModel) {
//        descriptorsPanel.removeAll();
//        DescriptorSelectionPanel selectionPanel = new DescriptorSelectionPanel(attrModel);
//        final Set<String> keys = csnAlgo.getSelectedKeys();
//        selectionPanel.setSelectedDescriptorKeys(keys);
//        selectionPanel.addTableModelListener(new TableModelListener() {
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                TableModel model = (TableModel) e.getSource();
//                for (int row = 0; row < model.getRowCount(); row++) {
//                    if ((boolean) model.getValueAt(row, 0)) {
//                        keys.add((String) model.getValueAt(row, 1));
//                    } else {
//                        keys.remove((String) model.getValueAt(row, 1));
//                    }
//                }
//            }
//        });
//        descriptorsPanel.add(selectionPanel, BorderLayout.CENTER);
//        descriptorsPanel.revalidate();
//        descriptorsPanel.repaint();
//    }    

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
        featurePanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        option3Button = new javax.swing.JRadioButton();
        option2Button = new javax.swing.JRadioButton();
        option1Button = new javax.swing.JRadioButton();
        centerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        featurePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.featurePanel.border.title"))); // NOI18N
        featurePanel.setLayout(new java.awt.GridBagLayout());

        topPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(option3Button);
        org.openide.awt.Mnemonics.setLocalizedText(option3Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.option3Button.text")); // NOI18N
        option3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option3ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        topPanel.add(option3Button, gridBagConstraints);

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
        topPanel.add(option2Button, gridBagConstraints);

        buttonGroup1.add(option1Button);
        option1Button.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(option1Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.selectRankedRButton.text")); // NOI18N
        option1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option1ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        topPanel.add(option1Button, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        featurePanel.add(topPanel, gridBagConstraints);

        centerPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        featurePanel.add(centerPanel, gridBagConstraints);

        add(featurePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void option1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option1ButtonActionPerformed
//        if (algo != null) {
//            algo.setButtonGroupIndex(2);
//        }
    }//GEN-LAST:event_option1ButtonActionPerformed

    private void option2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option2ButtonActionPerformed
//        if (algo != null) {
//            algo.setButtonGroupIndex(1);
//        }
    }//GEN-LAST:event_option2ButtonActionPerformed

    private void option3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option3ButtonActionPerformed
//        if (algo != null) {
//            algo.setButtonGroupIndex(0);
//        }
    }//GEN-LAST:event_option3ButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel featurePanel;
    private javax.swing.JRadioButton option1Button;
    private javax.swing.JRadioButton option2Button;
    private javax.swing.JRadioButton option3Button;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)){
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
             if (!attrModels.isEmpty()){
//                 csnAlgo.getSelectedKeys().clear();
//                 setMolecularDescriptors(attrModels.iterator().next());
             }
        }
    }
}
