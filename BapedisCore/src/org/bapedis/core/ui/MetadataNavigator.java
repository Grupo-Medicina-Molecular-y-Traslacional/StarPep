/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.data.MetadataDAO;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.jdesktop.swingx.JXTree;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "peptide/metadata", displayName = "#MetadataNavigator.name")
public class MetadataNavigator extends JComponent implements 
        WorkspaceEventListener, PropertyChangeListener, LookupListener, NavigatorPanelWithToolbar {

    protected final ExplorerManager explorerMgr;
    private final DefaultComboBoxModel comboBoxModel;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected AttributesModel currentModel;
    protected final JToolBar toolBar;
    protected final JCheckBox showAllCheckBox;
    protected final JButton findButton;
    protected final JComboBox comboBox;
    protected final JXTree tree;
    private boolean activated;

    /**
     * Creates new form LibraryPanel
     */
    public MetadataNavigator() {
        initComponents();
        explorerMgr = new ExplorerManager();

        tree = new JXTree();
        tree.setModel(null);
        scrollPane.setViewportView(tree);

        showAllCheckBox = new JCheckBox();
        showAllCheckBox.setSelected(true);
        showAllCheckBox.setText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.showAllCheckBox.text"));
        showAllCheckBox.setToolTipText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.showAllCheckBox.toolTipText"));
        showAllCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllCheckBoxActionPerformed(evt);
            }
        });

        findButton = new JButton(tree.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(GraphElementNavigator.class, "MetadataNavigator.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);
        
        comboBoxModel = new DefaultComboBoxModel();
        String NO_SELECTION = NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.choose.text");
        comboBoxModel.addElement(NO_SELECTION);

        for (AnnotationType aType : AnnotationType.values()) {
            comboBoxModel.addElement(new AnnotationItem(aType, showAllCheckBox.isSelected()));
        }

        comboBox = new JComboBox(comboBoxModel);
        comboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxItemStateChanged(evt);
            }
        });
        comboBoxModel.setSelectedItem(NO_SELECTION);
        showAllCheckBox.setEnabled(false);
        findButton.setEnabled(false);

        toolBar = new JToolBar();
        toolBar.add(comboBox);
        toolBar.add(showAllCheckBox);
        toolBar.addSeparator();
        toolBar.add(findButton);

        activated = false;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);

        lookup = ExplorerUtils.createLookup(explorerMgr, getActionMap());
    }

    private void comboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (comboBox.getSelectedItem() instanceof AnnotationItem) {
                AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();                
                showAllCheckBox.setEnabled(true);
                findButton.setEnabled(true);
                showAllCheckBox.setSelected(item.isShowAll());
                item.reload();
            } else {
                tree.setModel(null);
                findButton.setEnabled(false);
                showAllCheckBox.setEnabled(false);
            }
        }
    }

    private void showAllCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (comboBox.getSelectedItem() instanceof AnnotationItem) {
            AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
            item.setShowAll(showAllCheckBox.isSelected());
            item.reload();
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


    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        if (oldWs != null) {
            AttributesModel oldAttrModel = pc.getAttributesModel(oldWs);
            if (oldAttrModel != null) {
                oldAttrModel.removeQuickFilterChangeListener(this);

            }
        }
        peptideLkpResult = newWs.getLookup().lookupResult(AttributesModel.class
        );
        peptideLkpResult.addLookupListener(this);

        AttributesModel peptidesModel = pc.getAttributesModel(newWs);
        if (currentModel != null) {
            currentModel.removeQuickFilterChangeListener(this);
        }
        this.currentModel = peptidesModel;
        if (currentModel != null) {
            currentModel.addQuickFilterChangeListener(this);
        }
        setDirtyMetadata();
    }

    protected void setDirtyMetadata() {
        for (int i = 1; i < comboBoxModel.getSize(); i++) {
            AnnotationItem item = (AnnotationItem) comboBoxModel.getElementAt(i);
            if (!item.isShowAll()) {
                item.setDirty(true);
            }
        }
        if (activated && comboBox.getSelectedItem() instanceof AnnotationItem) {
            AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
            if (!item.isShowAll()) {
                item.reload();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)
                && evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
            setDirtyMetadata();
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (ev.getSource().equals(peptideLkpResult)) {
            setDirtyMetadata();
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
        activated = true;
        if (comboBox.getSelectedItem() instanceof AnnotationItem) {
            AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
            if (!item.isShowAll() && item.isDirty()) {
                item.reload();
            }
        }
    }

    @Override
    public void panelDeactivated() {
        activated = false;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public JComponent getToolbarComponent() {
        return toolBar;

    }

    private class AnnotationItem {

        private final AnnotationType annotationType;
        private final MetadataDAO metadataDAO;
        private List<Metadata> metadatas;
        private boolean dirty;
        private boolean showAll;

        public AnnotationItem(AnnotationType annotationType, boolean showAll) {
            this.annotationType = annotationType;
            metadataDAO = Lookup.getDefault().lookup(MetadataDAO.class);
            dirty = false;
            this.showAll = showAll;
        }

        public boolean isShowAll() {
            return showAll;
        }

        public void setShowAll(boolean showAll) {
            this.showAll = showAll;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        public void reload() {
            final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
            final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
            tree.setModel(treeModel);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    if (metadatas == null) {
                        metadatas = metadataDAO.getMetadata(annotationType);
                    }
                    if (showAll) {
                        for (Metadata m : metadatas) {
                            rootNode.add(createNode(m));
                        }
                    } else {
                        GraphModel graphModel = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel();
                        GraphView view = graphModel.getVisibleView();
                        Graph graph = graphModel.getGraph(view);
                        org.gephi.graph.api.Node node;
                        graph.readLock();
                        try {
                            for (Metadata m : metadatas) {
                                node = graph.getNode(m.getUnderlyingNodeID());
                                if (node != null) {
                                    m.setNode(node);
                                    rootNode.add(createNode(m));
                                }
                            }
                        } finally {
                            graph.readUnlock();
                        }
                    }
                    return null;
                }

                private DefaultMutableTreeNode createNode(Metadata metadata) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(metadata);
                    if (metadata.hasChilds()) {
                        for (Metadata m : metadata.getChilds()) {
                            node.add(createNode(m));
                        }
                    }
                    return node;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        treeModel.reload();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

            };
            worker.execute();
            if (!isShowAll()) {
                dirty = false;
            }
        }

        public AnnotationType getAnnotationType() {
            return annotationType;
        }

        @Override
        public String toString() {
            return annotationType.getDisplayName();
        }

    }
}
