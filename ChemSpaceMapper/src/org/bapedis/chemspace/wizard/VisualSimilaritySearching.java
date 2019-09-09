/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.bapedis.chemspace.model.SimilaritySearchingOption;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class VisualSimilaritySearching extends javax.swing.JPanel {

    /**
     * Creates new form VisualQuerySequence
     */
    private final JPanel settingPanel;
    
    public VisualSimilaritySearching(JPanel settingPanel) {
        initComponents();
        this.settingPanel = settingPanel;        
        centerPanel.add(settingPanel, BorderLayout.CENTER);
    }
           
    
    @Override
    public String getName() {
        return NbBundle.getMessage(VisualSimilaritySearching.class, "RankingOutput.name");
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
        jInfoLabel = new javax.swing.JLabel();
        centerPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(460, 400));
        setPreferredSize(new java.awt.Dimension(560, 580));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jInfoLabel, org.openide.util.NbBundle.getMessage(VisualSimilaritySearching.class, "VisualSimilaritySearching.jInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jInfoLabel, gridBagConstraints);

        centerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        centerPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel jInfoLabel;
    // End of variables declaration//GEN-END:variables
}
