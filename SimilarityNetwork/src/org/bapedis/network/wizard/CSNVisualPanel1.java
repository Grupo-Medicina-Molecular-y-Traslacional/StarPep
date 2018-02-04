/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.wizard;

import javax.swing.JPanel;
import org.bapedis.network.impl.CSNAlgorithm;
import org.openide.util.NbBundle;

public final class CSNVisualPanel1 extends JPanel {
    
    /**
     * Creates new form CSNVisualPanel1
     */
    public CSNVisualPanel1(CSNAlgorithm csnAlgo) {
        initComponents();
        
//        if (seqClustering.isClustering()) {
//            jOption1.setSelected(true);
//        } else {
//            jOption2.setSelected(true);
//        }
//        setClustering(seqClustering.isClustering());
        

    }
    
    @Override
    public String getName() {
        return NbBundle.getMessage(CSNVisualPanel1.class, "CSNVisualPanel1.name");
    }
    
    private void setClustering(boolean enabled) {
        bottomPanel.setEnabled(enabled);
        jwarningLabel.setVisible(!enabled);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        bottomPanel = new javax.swing.JPanel();
        jQuestionLabel = new javax.swing.JLabel();
        jOption1 = new javax.swing.JRadioButton();
        jOption2 = new javax.swing.JRadioButton();
        jwarningLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(460, 400));
        setPreferredSize(new java.awt.Dimension(460, 400));
        setLayout(new java.awt.GridBagLayout());

        bottomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        bottomPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        add(bottomPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jQuestionLabel, org.openide.util.NbBundle.getMessage(CSNVisualPanel1.class, "CSNVisualPanel1.jQuestionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jQuestionLabel, gridBagConstraints);

        buttonGroup1.add(jOption1);
        org.openide.awt.Mnemonics.setLocalizedText(jOption1, org.openide.util.NbBundle.getMessage(CSNVisualPanel1.class, "CSNVisualPanel1.jOption1.text")); // NOI18N
        jOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jOption1, gridBagConstraints);

        buttonGroup1.add(jOption2);
        org.openide.awt.Mnemonics.setLocalizedText(jOption2, org.openide.util.NbBundle.getMessage(CSNVisualPanel1.class, "CSNVisualPanel1.jOption2.text")); // NOI18N
        jOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jOption2, gridBagConstraints);

        jwarningLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/network/resources/warning.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jwarningLabel, org.openide.util.NbBundle.getMessage(CSNVisualPanel1.class, "CSNVisualPanel1.jwarningLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jwarningLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption1ActionPerformed
        setClustering(true);
    }//GEN-LAST:event_jOption1ActionPerformed

    private void jOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption2ActionPerformed
        setClustering(false);
    }//GEN-LAST:event_jOption2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton jOption1;
    private javax.swing.JRadioButton jOption2;
    private javax.swing.JLabel jQuestionLabel;
    private javax.swing.JLabel jwarningLabel;
    // End of variables declaration//GEN-END:variables
}
