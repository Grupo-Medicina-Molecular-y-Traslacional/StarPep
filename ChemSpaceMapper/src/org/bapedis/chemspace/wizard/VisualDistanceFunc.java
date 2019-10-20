/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.awt.Component;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

public final class VisualDistanceFunc extends JPanel {

    public static final String DISTANCE_FUNCTION = "distance_function";
    private final DefaultMutableTreeNode treeNode;
    private AlgorithmFactory distFactory;

    public VisualDistanceFunc() {
        initComponents();
        treeNode = new DefaultMutableTreeNode(NbBundle.getMessage(VisualDistanceFunc.class, "DistanceFunction.root.name"), true);
        jTree1.setModel(new DefaultTreeModel(treeNode));
        jTree1.setRootVisible(true);
        jTree1.setCellRenderer(new DistanceFactoryNodeRenderer());
    }

    public AlgorithmFactory getDistanceFactory() {
        return distFactory;
    }    

    public void setDistanceFactory(AlgorithmFactory distFactory) {        
        DistanceFactoryTreeNode distNode;
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            distNode = (DistanceFactoryTreeNode) treeNode.getChildAt(i);
            if (distNode.getDistanceFactory().getName().equals(distFactory .getName())) {
                jTree1.setSelectionPath(new TreePath(distNode.getPath()));
                this.distFactory = distFactory;
            }
        }
    }
    
    public void setOption(MD_OUTPUT_OPTION option){
        switch(option){
            case Z_SCORE:
                jOptionZscore.setSelected(true);
                break;
            case MIN_MAX:
                jOptionMinMax.setSelected(true);
                break;
        }    
    }
    
    public MD_OUTPUT_OPTION getOption(){
        if (jOptionZscore.isSelected()){
            return MD_OUTPUT_OPTION.Z_SCORE;
        }
        if (jOptionMinMax.isSelected()){
            return MD_OUTPUT_OPTION.MIN_MAX;
        }        
        return MD_OUTPUT_OPTION.None;
    }

    void populateJTree(List<AlgorithmFactory> distFactory) {       
        for (AlgorithmFactory factory : distFactory) {
            treeNode.add(new DistanceFactoryTreeNode(factory));
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(VisualDistanceFunc.class, "DistanceFunction.name");
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
        jInfoLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        optionPanel = new javax.swing.JPanel();
        optLabel = new javax.swing.JLabel();
        jOptionMinMax = new javax.swing.JRadioButton();
        jOptionZscore = new javax.swing.JRadioButton();
        jDescLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(460, 400));
        setPreferredSize(new java.awt.Dimension(560, 580));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jInfoLabel, org.openide.util.NbBundle.getMessage(VisualDistanceFunc.class, "VisualDistanceFunc.jInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jInfoLabel, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(220, 322));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(220, 322));

        jTree1.setRootVisible(false);
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        optionPanel.setLayout(new java.awt.GridBagLayout());

        optLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(optLabel, org.openide.util.NbBundle.getMessage(VisualDistanceFunc.class, "VisualDistanceFunc.optLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        optionPanel.add(optLabel, gridBagConstraints);

        buttonGroup1.add(jOptionMinMax);
        org.openide.awt.Mnemonics.setLocalizedText(jOptionMinMax, org.openide.util.NbBundle.getMessage(VisualDistanceFunc.class, "VisualDistanceFunc.jOptionMinMax.text")); // NOI18N
        jOptionMinMax.setToolTipText(org.openide.util.NbBundle.getMessage(VisualDistanceFunc.class, "VisualDistanceFunc.jOptionMinMax.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        optionPanel.add(jOptionMinMax, gridBagConstraints);

        buttonGroup1.add(jOptionZscore);
        org.openide.awt.Mnemonics.setLocalizedText(jOptionZscore, org.openide.util.NbBundle.getMessage(VisualDistanceFunc.class, "VisualDistanceFunc.jOptionZscore.text")); // NOI18N
        jOptionZscore.setToolTipText(org.openide.util.NbBundle.getMessage(VisualDistanceFunc.class, "VisualDistanceFunc.jOptionZscore.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        optionPanel.add(jOptionZscore, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jDescLabel, org.openide.util.NbBundle.getMessage(VisualDistanceFunc.class, "VisualDistanceFunc.jDescLabel.text")); // NOI18N
        jDescLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        optionPanel.add(jDescLabel, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(VisualDistanceFunc.class, "VisualDistanceFunc.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        optionPanel.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(optionPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        TreePath newPath = evt.getNewLeadSelectionPath();
        if (newPath != null && newPath.getLastPathComponent() instanceof DistanceFactoryTreeNode) {
            DistanceFactoryTreeNode newNode = (DistanceFactoryTreeNode) newPath.getLastPathComponent();
            distFactory = newNode.getDistanceFactory();
            jDescLabel.setText(distFactory.getDescription());
            jDescLabel.setVisible(true);            
        } else {
            distFactory = null;
            jDescLabel.setText("");
        }
        firePropertyChange(DISTANCE_FUNCTION, null, distFactory);
    }//GEN-LAST:event_jTree1ValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jDescLabel;
    private javax.swing.JLabel jInfoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jOptionMinMax;
    private javax.swing.JRadioButton jOptionZscore;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel optLabel;
    private javax.swing.JPanel optionPanel;
    // End of variables declaration//GEN-END:variables

    private static class DistanceFactoryTreeNode extends DefaultMutableTreeNode {

        public DistanceFactoryTreeNode(AlgorithmFactory distFactory) {
            super(distFactory, false);
        }

        public AlgorithmFactory getDistanceFactory() {
            return (AlgorithmFactory) userObject;
        }

        @Override
        public String toString() {
            return getDistanceFactory().getName();
        }

    }

    private static class DistanceFactoryNodeRenderer extends DefaultTreeCellRenderer {

        Icon icon;

        public DistanceFactoryNodeRenderer() {
            icon = ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/network.png", false);
        }

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (!(node instanceof DistanceFactoryTreeNode)) {
                // Root node
                setIcon(icon);
            }
            return this;
        }
    }

}
