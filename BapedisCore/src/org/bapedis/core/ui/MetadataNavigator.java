/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.MetadataNavigatorModel;
import org.bapedis.core.model.MetadataNode;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.MetadataTreeNodeLoader;
import org.bapedis.core.ui.actions.AddToQueryModel;
import org.bapedis.core.ui.actions.CenterNodeOnGraph;
import org.bapedis.core.ui.actions.RemoveFromQueryModel;
import org.bapedis.core.ui.actions.SelectNodeOnGraph;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTree;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.awt.MouseUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "peptide/metadata", displayName = "#MetadataNavigator.name")
public class MetadataNavigator extends JComponent implements
        WorkspaceEventListener, NavigatorPanelWithToolbar {

    protected final InstanceContent content;
    private final DefaultComboBoxModel comboBoxModel;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected final JToolBar toolBar, bottomToolbar;
    protected final JButton findButton;
    protected final JComboBox comboBox;
    protected final JXTree tree;
    protected final JXBusyLabel busyLabel;
    protected final JLabel metadataSizeLabel;
    private final String NO_SELECTION;
    private MetadataNavigatorModel navigatorModel;

    /**
     * Creates new form LibraryPanel
     */
    public MetadataNavigator() {
        initComponents();
        content = new InstanceContent();
        lookup = new AbstractLookup(content);

        tree = new JXTree();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(new MetadataPopupAdapter(tree));
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                treeValueChanged(e);
            }
        });

        scrollPane.setViewportView(tree);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(GraphElementNavigator.class, "MetadataNavigator.busyLabel.text"));

        findButton = new JButton(tree.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(GraphElementNavigator.class, "MetadataNavigator.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);

        comboBoxModel = new DefaultComboBoxModel();
        NO_SELECTION = NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.choose.text");
        comboBoxModel.addElement(NO_SELECTION);

        for (AnnotationType aType : AnnotationType.values()) {
            comboBoxModel.addElement(new AnnotationItem(aType));
        }

        comboBox = new JComboBox(comboBoxModel);
        comboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxItemStateChanged(evt);
            }
        });

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(comboBox);
        toolBar.addSeparator();
        toolBar.add(findButton);

        pc = Lookup.getDefault().lookup(ProjectManager.class);

        // Botton toolbar
        bottomToolbar = new JToolBar();
        bottomToolbar.setFloatable(false);
        metadataSizeLabel = new JLabel();
        bottomToolbar.add(metadataSizeLabel);
        add(bottomToolbar, BorderLayout.SOUTH);
    }

    private void comboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (comboBox.getSelectedItem() instanceof AnnotationItem) {
                AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
                findButton.setEnabled(true);
                item.reload();
                if (navigatorModel.getSelectedIndex() != item.getAnnotationType().ordinal()) {
                    navigatorModel.setSelectedIndex(item.getAnnotationType().ordinal());
                }
            } else {
                tree.setModel(null);
                findButton.setEnabled(false);
                if (navigatorModel.getSelectedIndex() != -1) {
                    navigatorModel.setSelectedIndex(-1);
                }
                metadataSizeLabel.setText("");
            }
        }
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : tree);
        for (Component c : toolBar.getComponents()) {
            c.setEnabled(!busy);
        }
        bottomToolbar.setVisible(!busy);
    }

    private void treeValueChanged(TreeSelectionEvent e) {
        Collection<? extends MetadataNode> oldNodes = lookup.lookupAll(MetadataNode.class);
        for (MetadataNode node : oldNodes) {
            content.remove(node);
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null && node.getUserObject() != null) {
            Metadata metadata = (Metadata) node.getUserObject();
            content.add(new MetadataNode(metadata));
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

        scrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        navigatorModel = newWs.getLookup().lookup(MetadataNavigatorModel.class);
        if (navigatorModel == null) {
            navigatorModel = new MetadataNavigatorModel();
            newWs.add(navigatorModel);
        }

        comboBox.setSelectedIndex(-1);
        if (navigatorModel.getSelectedIndex() == -1) {
            comboBox.setSelectedItem(NO_SELECTION);
        } else {
            comboBox.setSelectedIndex(navigatorModel.getSelectedIndex() + 1);
        }
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(MetadataNavigator.class,
                "MetadataNavigator.name");
    }

    @Override
    public String getDisplayHint() {
        return NbBundle.getMessage(MetadataNavigator.class,
                "MetadataNavigator.hint");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void panelActivated(Lookup lkp) {
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    @Override
    public void panelDeactivated() {
        pc.removeWorkspaceEventListener(this);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public JComponent getToolbarComponent() {
        return toolBar;

    }

    private class AnnotationItem implements PropertyChangeListener {

        private final AnnotationType annotationType;

        public AnnotationItem(AnnotationType annotationType) {
            this.annotationType = annotationType;
        }

        public void reload() {
            setBusyLabel(true);
            MetadataTreeNodeLoader loader = new MetadataTreeNodeLoader(annotationType);
            loader.addPropertyChangeListener(this);
            loader.execute();
        }

        public AnnotationType getAnnotationType() {
            return annotationType;
        }

        @Override
        public String toString() {
            return annotationType.getDisplayName();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(MetadataTreeNodeLoader.FINISH)) {
                MetadataTreeNodeLoader loader = (MetadataTreeNodeLoader) evt.getSource();
                DefaultMutableTreeNode rootNode = loader.getRootNode();
                DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
                tree.setModel(treeModel);
                metadataSizeLabel.setText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.metadataSizeLabel.text", loader.getEntriesloaded()));
                if (rootNode.getChildCount() > 0) {
                    treeModel.reload();
                } else {
                    tree.setModel(null);
                }
                setBusyLabel(false);
            }
        }

    }
}

class MetadataPopupAdapter extends MouseUtils.PopupMouseAdapter {

    protected final JXTree tree;

    public MetadataPopupAdapter(JXTree tree) {
        this.tree = tree;
    }

    @Override
    protected void showPopup(MouseEvent evt) {
        TreePath treePath = tree.getPathForLocation(evt.getX(), evt.getY());
        if (treePath != null) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null && !selectedNode.equals(treeNode)) {
                tree.getSelectionModel().clearSelection();
                tree.getSelectionModel().setSelectionPath(treePath);
            }
            if (treeNode.getUserObject() != null) {
                QueryModel queryModel = Lookup.getDefault().lookup(ProjectManager.class).getQueryModel();
                Metadata metadata = (Metadata) treeNode.getUserObject();
                boolean isAdded = queryModel.contains(metadata);
                Action[] actions = new Action[]{new AddToQueryModel(metadata), new RemoveFromQueryModel(metadata)};
                JPopupMenu contextMenu = new JPopupMenu();
                contextMenu.add(actions[0]);
                contextMenu.add(actions[1]);
                actions[0].setEnabled(!isAdded);
                actions[1].setEnabled(isAdded);

                if (metadata.getGraphNode() != null) {
                    contextMenu.addSeparator();
                    contextMenu.add(new SelectNodeOnGraph(metadata.getGraphNode()));
                    contextMenu.add(new CenterNodeOnGraph(metadata.getGraphNode()));

                }

                contextMenu.show(tree, evt.getX(), evt.getY());
            }
        }
        evt.consume();
    }
}
