/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;


/**
 *
 * @author loge
 */
public class ContextPanel extends javax.swing.JPanel{

    /**
     * Creates new form ContextStatusBar
     */
    public ContextPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        peptidePanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        centerPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        graphPanel = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        centerPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        peptidePanel.setLayout(new java.awt.BorderLayout());

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        peptidePanel.add(jSeparator1, java.awt.BorderLayout.WEST);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ContextPanel.class, "ContextPanel.jLabel1.text")); // NOI18N
        centerPanel1.add(jLabel1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ContextPanel.class, "ContextPanel.jLabel2.text")); // NOI18N
        centerPanel1.add(jLabel2);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ContextPanel.class, "ContextPanel.jLabel3.text")); // NOI18N
        centerPanel1.add(jLabel3);

        peptidePanel.add(centerPanel1, java.awt.BorderLayout.CENTER);

        add(peptidePanel);

        graphPanel.setLayout(new java.awt.BorderLayout());

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        graphPanel.add(jSeparator2, java.awt.BorderLayout.WEST);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ContextPanel.class, "ContextPanel.jLabel4.text")); // NOI18N
        centerPanel2.add(jLabel4);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(ContextPanel.class, "ContextPanel.jLabel5.text")); // NOI18N
        centerPanel2.add(jLabel5);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(ContextPanel.class, "ContextPanel.jLabel6.text")); // NOI18N
        centerPanel2.add(jLabel6);

        graphPanel.add(centerPanel2, java.awt.BorderLayout.CENTER);

        add(graphPanel);
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel1;
    private javax.swing.JPanel centerPanel2;
    private javax.swing.JPanel graphPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel peptidePanel;
    // End of variables declaration//GEN-END:variables
}