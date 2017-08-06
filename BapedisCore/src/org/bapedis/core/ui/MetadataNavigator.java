/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.MetadataNavigatorModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.data.MetadataDAO;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.jdesktop.swingx.JXBusyLabel;
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
    protected final JXBusyLabel busyLabel;
    private final String NO_SELECTION;
    private MetadataNavigatorModel navigatorModel;

    /**
     * Creates new form LibraryPanel
     */
    public MetadataNavigator() {
        initComponents();
        explorerMgr = new ExplorerManager();

        tree = new JXTree();
        scrollPane.setViewportView(tree);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(GraphElementNavigator.class, "MetadataNavigator.busyLabel.text"));

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
        NO_SELECTION = NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.choose.text");
        comboBoxModel.addElement(NO_SELECTION);

        for (AnnotationType aType : AnnotationType.values()) {
            comboBoxModel.addElement(new AnnotationItem(aType));
        }

        comboBox = new JComboBox(comboBoxModel);
        comboBox.setSelectedIndex(-1);
        comboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxItemStateChanged(evt);
            }
        });

        toolBar = new JToolBar();
        toolBar.add(comboBox);
        toolBar.add(showAllCheckBox);
        toolBar.addSeparator();
        toolBar.add(findButton);

        pc = Lookup.getDefault().lookup(ProjectManager.class);
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
                if (navigatorModel.getSelectedIndex() != item.getAnnotationType().ordinal()) {
                    navigatorModel.setSelectedIndex(item.getAnnotationType().ordinal());
                }
            } else {
                tree.setModel(null);
                findButton.setEnabled(false);
                showAllCheckBox.setEnabled(false);
                if (navigatorModel.getSelectedIndex() != -1) {
                    navigatorModel.setSelectedIndex(-1);
                }
            }
        }
    }

    private void showAllCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (comboBox.getSelectedItem() instanceof AnnotationItem) {
            AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
            item.setShowAll(showAllCheckBox.isSelected());
            item.reload();
            navigatorModel.setShowAll(item.getAnnotationType().ordinal(), showAllCheckBox.isSelected());
        }
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : tree);
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
        if (peptidesModel != null) {
            peptidesModel.addQuickFilterChangeListener(this);
        }
        this.currentModel = peptidesModel;

        navigatorModel = newWs.getLookup().lookup(MetadataNavigatorModel.class);
        if (navigatorModel == null) {
            navigatorModel = new MetadataNavigatorModel();
            newWs.add(navigatorModel);
        }

        for (int i = 0; i < AnnotationType.values().length; i++) {
            AnnotationItem item = (AnnotationItem) comboBoxModel.getElementAt(i + 1);
            item.setShowAll(navigatorModel.isShowAll(i));
        }
        if (navigatorModel.getSelectedIndex() == -1) {
            comboBox.setSelectedItem(NO_SELECTION);
        } else {
            comboBox.setSelectedIndex(navigatorModel.getSelectedIndex() + 1);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)
                && evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
            if (comboBox.getSelectedItem() instanceof AnnotationItem) {
                AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
                item.reload();
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (ev.getSource().equals(peptideLkpResult)) {
            if (currentModel != null) {
                currentModel.removeQuickFilterChangeListener(this);
            }
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                currentModel = attrModels.iterator().next();
                currentModel.addQuickFilterChangeListener(this);
                if (comboBox.getSelectedItem() instanceof AnnotationItem) {
                    AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
                    item.reload();
                }
            }
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
        removeLookupListener();
        pc.removeWorkspaceEventListener(this);
        if (currentModel != null) {
            currentModel.removeQuickFilterChangeListener(this);
        }
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
        private boolean showAll;

        public AnnotationItem(AnnotationType annotationType) {
            this.annotationType = annotationType;
            metadataDAO = Lookup.getDefault().lookup(MetadataDAO.class);
            this.showAll = true;
        }

        public boolean isShowAll() {
            return showAll;
        }

        public void setShowAll(boolean showAll) {
            this.showAll = showAll;
        }

        public void reload() {
            setBusyLabel(true);
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
                        if (rootNode.getChildCount() > 0) {
                            treeModel.reload();
                        } else{
                            tree.setModel(null);
                        }                        
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        setBusyLabel(false);
                    }
                }

            };
            worker.execute();
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
