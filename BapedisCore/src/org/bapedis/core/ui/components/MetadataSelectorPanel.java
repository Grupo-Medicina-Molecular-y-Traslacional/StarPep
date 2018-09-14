/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.Metadata;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTree;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MetadataSelectorPanel extends javax.swing.JPanel implements PropertyChangeListener, ValidationSupportUI {

    private final StarPepAnnotationType annotationType;
    private boolean validState;
    protected final JXTree tree;
    protected final JButton findButton;
    protected final JToolBar topToolBar, bottomToolbar;
    protected final JXBusyLabel busyLabel;
    protected final JLabel metadataSizeLabel;
    protected final PropertyChangeSupport changeSupport;
    protected final List<Metadata> selectedMetadata;

    /**
     * Creates new form MetadataSelectorPanel
     *
     * @param annotationType
     */
    public MetadataSelectorPanel(StarPepAnnotationType annotationType) {
        this.annotationType = annotationType;
        selectedMetadata = new LinkedList<>();
        validState = false;

        initComponents();

        tree = new JXTree();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                treeValueChanged(e);
            }
        });
        scrollPane.setViewportView(tree);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(MetadataSelectorPanel.class, "MetadataSelectorPanel.busyLabel.text"));

        findButton = new JButton(tree.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(MetadataSelectorPanel.class, "MetadataSelectorPanel.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);

        topToolBar = new JToolBar();
        topToolBar.setFloatable(false);
        topToolBar.addSeparator();        
        topToolBar.add(new JLabel(annotationType.getDisplayName()));
        topToolBar.add(Box.createHorizontalGlue());
        topToolBar.add(findButton);

        add(topToolBar, BorderLayout.NORTH);

        metadataSizeLabel = new JLabel();

        bottomToolbar = new JToolBar();
        bottomToolbar.setFloatable(false);
        bottomToolbar.addSeparator();
        bottomToolbar.add(metadataSizeLabel);

        add(bottomToolbar, BorderLayout.SOUTH);

        populateTree();
        changeSupport = new PropertyChangeSupport(this);
    }

    public List<Metadata> getSelectedMetadata() {
        return selectedMetadata;
    }

    private void populateTree() {
        setBusyLabel(true);
        MetadataTreeNodeLoader loader = new MetadataTreeNodeLoader(annotationType);
        loader.addPropertyChangeListener(this);
        loader.execute();
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : tree);
        for (Component c : topToolBar.getComponents()) {
            c.setEnabled(!busy);
        }
        bottomToolbar.setVisible(!busy);
    }

    private void treeValueChanged(TreeSelectionEvent e) {
        TreePath[] paths = tree.getSelectionPaths();
        boolean oldValidState = validState;
        validState = paths != null;
        changeSupport.firePropertyChange(VALID_STATE, oldValidState, validState);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();

        setPreferredSize(new java.awt.Dimension(360, 480));
        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(MetadataTreeNodeLoader.FINISH)) {
            MetadataTreeNodeLoader loader = (MetadataTreeNodeLoader) evt.getSource();
            DefaultMutableTreeNode rootNode = loader.getRootNode();
            DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
            tree.setModel(treeModel);
            metadataSizeLabel.setText(NbBundle.getMessage(MetadataSelectorPanel.class, "MetadataSelectorPanel.metadataSizeLabel.text", loader.getEntriesloaded()));
            if (rootNode.getChildCount() > 0) {
                treeModel.reload();
            } else {
                tree.setModel(null);
            }
            setBusyLabel(false);
        }
    }

    @Override
    public boolean isValidState() {
        return validState;
    }

    @Override
    public void saveSettings() {
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            DefaultMutableTreeNode node;
            Metadata metadata;
            for (TreePath p : paths) {
                node = (DefaultMutableTreeNode) p.getLastPathComponent();
                metadata = (Metadata) node.getUserObject();
                selectedMetadata.add(metadata);
            }
        }
    }

    @Override
    public void cancelSettings() {
        selectedMetadata.clear();
    }

    @Override
    public void addValidStateListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removeValidStateListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
