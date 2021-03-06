/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import java.awt.BorderLayout;
import javax.swing.UIManager;
import org.gephi.ui.utils.UIUtils;

public class ExtendedBar extends javax.swing.JPanel {

    private final DesktopToolController toolController;
    private final Toolbar toolbar;
    private final PropertiesBar propertiesBar;

    /**
     * Creates new form ExtendedBar
     */
    public ExtendedBar() {        
        initComponents();
        if (UIUtils.isAquaLookAndFeel()) {
            setBackground(UIManager.getColor("NbExplorerView.background"));
        }

        toolController = new DesktopToolController();
        toolbar = toolController.getToolbar();
        propertiesBar = toolController.getPropertiesBar();
        leftPanel.add(toolbar, BorderLayout.CENTER);
        rightPanel.add(propertiesBar, BorderLayout.CENTER);        
    }

     public void unselect(){
         toolController.unselect();
         toolbar.clearSelection();
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

        leftPanel = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        leftPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(leftPanel, gridBagConstraints);

        rightPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(rightPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel rightPanel;
    // End of variables declaration//GEN-END:variables
}
