/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.subnet;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.ui.components.ColorChooserButton;
/**
 *
 * @author Loge
 */
public class ShortestPathPanel extends javax.swing.JPanel implements AlgorithmSetupUI, ColorChooserButton.ColorChangedListener {

    private ShortestPath shortestPath;
    /**
     * Creates new form ShortestPathPanel
     */
    public ShortestPathPanel() {
        initComponents();
        jTF_sourceNode.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSourceNode();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSourceNode();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        }); 
        jTF_targetNode.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTargetNode();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTargetNode();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });          
    }

    private void updateSourceNode(){
        if (shortestPath != null){
            shortestPath.setSourceNodeName(jTF_sourceNode.getText());
        }
    }
    
    private void updateTargetNode(){
        if (shortestPath != null){
            shortestPath.setTargetNodeName(jTF_targetNode.getText());
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTF_sourceNode = new javax.swing.JTextField();
        jTF_targetNode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        colorChooser = new ColorChooserButton(Color.GRAY);
        jLabel5 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ShortestPathPanel.class, "ShortestPathPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ShortestPathPanel.class, "ShortestPathPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jLabel2, gridBagConstraints);

        jTF_sourceNode.setText(org.openide.util.NbBundle.getMessage(ShortestPathPanel.class, "ShortestPathPanel.jTF_sourceNode.text")); // NOI18N
        jTF_sourceNode.setPreferredSize(new java.awt.Dimension(90, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jTF_sourceNode, gridBagConstraints);

        jTF_targetNode.setText(org.openide.util.NbBundle.getMessage(ShortestPathPanel.class, "ShortestPathPanel.jTF_targetNode.text")); // NOI18N
        jTF_targetNode.setPreferredSize(new java.awt.Dimension(90, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jTF_targetNode, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ShortestPathPanel.class, "ShortestPathPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(colorChooser, org.openide.util.NbBundle.getMessage(ShortestPathPanel.class, "ShortestPathPanel.colorChooser.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(colorChooser, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(ShortestPathPanel.class, "ShortestPathPanel.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        add(jLabel5, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        shortestPath = (ShortestPath) algo;
        jTF_sourceNode.setText(shortestPath.getSourceNodeName()==null?"":shortestPath.getSourceNodeName());
        jTF_targetNode.setText(shortestPath.getTargetNodeName()==null?"":shortestPath.getTargetNodeName());
        ((ColorChooserButton)colorChooser).setSelectedColor(shortestPath.getColor());
        return this;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton colorChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jTF_sourceNode;
    private javax.swing.JTextField jTF_targetNode;
    // End of variables declaration//GEN-END:variables

    @Override
    public void colorChanged(Color newColor) {
        if (shortestPath != null){
            shortestPath.setColor(((ColorChooserButton)colorChooser).getSelectedColor());
        }
    }
}