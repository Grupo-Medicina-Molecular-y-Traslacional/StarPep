/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.awt.Component;
import java.util.Collection;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.bapedis.chemspace.spi.ThreeDTransformerFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public final class VisualTwoDTransformer extends JPanel {

    public static final String TRANSFORMER_FACTORY = "transformer_factory";
    private final DefaultMutableTreeNode treeNode;
    private ThreeDTransformerFactory factory;

    public VisualTwoDTransformer() {
        initComponents();
        treeNode = new DefaultMutableTreeNode(NbBundle.getMessage(VisualTwoDTransformer.class, "VisualTwoDTransformer.root.name"), true);
        populateJTree();
        jTree1.setModel(new DefaultTreeModel(treeNode));
        jTree1.setRootVisible(true);
        jTree1.setCellRenderer(new TransformerFactoryNodeRenderer());
    }

    public ThreeDTransformerFactory getThreeDTransformerFactory() {
        return factory;
    }        

    private void populateJTree() {
        Collection<? extends ThreeDTransformerFactory> factories = Lookup.getDefault().lookupAll(ThreeDTransformerFactory.class);
        for (ThreeDTransformerFactory factory : factories) {
            treeNode.add(new TransformerFactoryTreeNode(factory));
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(VisualTwoDTransformer.class, "VisualTwoDTransformer.name");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jInfoLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(460, 400));
        setPreferredSize(new java.awt.Dimension(500, 460));
        setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jInfoLabel, org.openide.util.NbBundle.getMessage(VisualTwoDTransformer.class, "VisualTwoDTransformer.jInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jInfoLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        TreePath newPath = evt.getNewLeadSelectionPath();
        
        if (newPath != null && newPath.getLastPathComponent() instanceof TransformerFactoryTreeNode) {
            TransformerFactoryTreeNode newNode = (TransformerFactoryTreeNode) newPath.getLastPathComponent();
            factory = newNode.getFactory();
            jTextArea1.setText(factory.getDescription());            
        } else {
            factory = null;
            jTextArea1.setText("");
        }
        firePropertyChange(TRANSFORMER_FACTORY, null, factory);
    }//GEN-LAST:event_jTree1ValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jInfoLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables

    private static class TransformerFactoryTreeNode extends DefaultMutableTreeNode {

        public TransformerFactoryTreeNode(ThreeDTransformerFactory factory) {
            super(factory, false);
        }

        public ThreeDTransformerFactory getFactory() {
            return (ThreeDTransformerFactory) userObject;
        }

        @Override
        public String toString() {
            return getFactory().getName();
        }

    }

    private static class TransformerFactoryNodeRenderer extends DefaultTreeCellRenderer {

        Icon icon;

        public TransformerFactoryNodeRenderer() {
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
            if (!(node instanceof TransformerFactoryTreeNode)) {
                // Root node
                setIcon(icon);
            }
            return this;
        }
    }

}
