/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.BorderLayout;

/**
 *
 * @author Loge
 */
public class NetworkReportPanel extends javax.swing.JPanel {

    protected MapperAlgorithm csMapper;
    protected NetworkReport netReport;

    /**
     * Creates new form NetworkReportPanel
     */
    public NetworkReportPanel() {
        initComponents();
    }
    
    public void setUp(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        this.netReport = csMapper.getNetworkReport();
        setupReport();
    }    
    
    public void setupReport(){
        centerPanel.removeAll();
        if (netReport != null && !csMapper.isRunning() && netReport.getChartPanel() != null){
            centerPanel.add(netReport.getChartPanel(), BorderLayout.CENTER);
        }
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        centerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        centerPanel.setLayout(new java.awt.BorderLayout());
        add(centerPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    // End of variables declaration//GEN-END:variables
}
