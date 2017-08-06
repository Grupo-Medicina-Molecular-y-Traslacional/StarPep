/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.GraphElementAttributeColumn;
import org.bapedis.core.model.GraphElementDataColumn;
import org.bapedis.core.model.GraphEdgeAttributeColumn;
import org.bapedis.core.model.GraphElementAvailableColumnsModel;
import org.bapedis.core.model.GraphElementNavigatorModel;
import org.bapedis.core.model.GraphElementType;
import org.bapedis.core.model.GraphElementsDataTable;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.ui.components.AvailableColumnsPanel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTable;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "graph/table", displayName = "#GraphElementNavigator.name")
public class GraphElementNavigator extends JComponent implements
        WorkspaceEventListener, PropertyChangeListener, LookupListener, NavigatorPanelWithToolbar {
    
    protected final ExplorerManager explorerMgr;
    protected final JToolBar toolBar;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected AttributesModel currentModel;
    
    protected final JToggleButton nodesBtn, edgesBtn;
    protected final JButton availableColumnsButton;
    protected final JXBusyLabel busyLabel;
    protected final JXTable table;
    protected GraphElementNavigatorModel navigatorModel;
    private final GraphElementDataColumn sourceColumn = new GraphEdgeAttributeColumn(GraphEdgeAttributeColumn.Direction.Source);
    private final GraphElementDataColumn targetColumn = new GraphEdgeAttributeColumn(GraphEdgeAttributeColumn.Direction.Targe);
    private final GraphElementDataColumn[] edgeColumns = new GraphElementDataColumn[3];

    /**
     * Creates new form GraphElementNavigator
     */
    public GraphElementNavigator() {
        initComponents();
        
        table = new JXTable();
//        table.setHighlighters(HighlighterFactory.createAlternateStriping());
        table.setColumnControlVisible(false);
        table.setSortable(true);
        table.setAutoCreateRowSorter(true);

        explorerMgr = new ExplorerManager();
        
        nodesBtn = new JToggleButton(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.node.name"));
        initToogleButton(nodesBtn);
        
        edgesBtn = new JToggleButton(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.name"));
        initToogleButton(edgesBtn);
        
        ButtonGroup elementGroup = new ButtonGroup();
        elementGroup.add(nodesBtn);
        elementGroup.add(edgesBtn);
        
        nodesBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodesButtonActionPerformed(evt);
            }
        });
        edgesBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgesButtonActionPerformed(evt);
            }
        });

        // Tool bar
        toolBar = new JToolBar();
        toolBar.add(nodesBtn);
        toolBar.add(edgesBtn);
        toolBar.addSeparator();
        
        JButton findButton = new JButton(table.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);
        toolBar.add(findButton);
        
        availableColumnsButton = new JButton();
        availableColumnsButton.setText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.availableColumnsButton.text"));
        availableColumnsButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/config.png", false));
        availableColumnsButton.setToolTipText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.availableColumnsButton.toolTipText"));
        availableColumnsButton.setFocusable(false);
        availableColumnsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                availableColumnsButtonActionPerformed(evt);
            }
        });
        toolBar.add(availableColumnsButton);
        //----------

        lookup = ExplorerUtils.createLookup(explorerMgr, getActionMap());
        
        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.busyLabel.text"));
        
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }
    
    private void initToogleButton(JToggleButton btn) {
        btn.setFocusable(false);
        btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    }
    
    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : table);
    }
    
    private void nodesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (navigatorModel.getVisualElement() != GraphElementType.Node) {
            navigatorModel.setVisualElement(GraphElementType.Node);
            reload();
            availableColumnsButton.setEnabled(true);
        }
        
    }
    
    private void edgesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (navigatorModel.getVisualElement() != GraphElementType.Edge) {
            navigatorModel.setVisualElement(GraphElementType.Edge);
            reload();
            availableColumnsButton.setEnabled(false);
        }
    }
    
    private void availableColumnsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (navigatorModel.getVisualElement() == GraphElementType.Node) {
            Table columns = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel().getNodeTable();
            GraphElementAvailableColumnsModel nodeAvailableColumnsModel = navigatorModel.getNodeAvailableColumnsModel();
            nodeAvailableColumnsModel.syncronizeTableColumns(columns);
            DialogDescriptor dd = new DialogDescriptor(new AvailableColumnsPanel(nodeAvailableColumnsModel), NbBundle.getMessage(AvailableColumnsPanel.class, "AvailableColumnsPanel.title"));
            dd.setOptions(new Object[]{DialogDescriptor.OK_OPTION});
            DialogDisplayer.getDefault().notify(dd);
            ((GraphElementsDataTable) table.getModel()).resetColumns(nodeAvailableColumnsModel.getAvailableColumns());
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
        
        navigatorModel = newWs.getLookup().lookup(GraphElementNavigatorModel.class);
        if (navigatorModel == null) {
            navigatorModel = new GraphElementNavigatorModel();
            newWs.add(navigatorModel);
        }
        
        switch (navigatorModel.getVisualElement()) {
            case Node:
                nodesBtn.setSelected(true);
                availableColumnsButton.setEnabled(true);
                break;
            case Edge:
                edgesBtn.setSelected(true);
                availableColumnsButton.setEnabled(false);
                break;
        }
        reload();
    }
    
    private void reload() {
        setBusyLabel(true);
        GraphModel graphModel = pc.getGraphModel();
        Table columns = navigatorModel.getVisualElement() == GraphElementType.Node ? graphModel.getNodeTable() : graphModel.getEdgeTable();
        GraphView view = graphModel.getVisibleView();
        final Graph graph = graphModel.getGraph(view);
        final GraphElementsDataTable dataModel = navigatorModel.getVisualElement() == GraphElementType.Node ? new GraphElementsDataTable(graph.getNodeCount(), getNodeColumns(columns))
                : new GraphElementsDataTable(graph.getEdgeCount(), getEdgeColumns(columns));
        table.setModel(dataModel);
        
        SwingWorker worker = new SwingWorker<Void, Element>() {
            @Override
            protected Void doInBackground() throws Exception {
                graph.readLock();
                try {
                    switch (navigatorModel.getVisualElement()) {
                        case Node:
                            for (Node node : graph.getNodes()) {
                                publish(node);
                            }
                            break;
                        case Edge:
                            for (Edge edge : graph.getEdges()) {
                                publish(edge);
                            }
                            break;
                    }
                } finally {
                    graph.readUnlock();
                }
                return null;
            }
            
            @Override
            protected void process(List<Element> chunks) {
                dataModel.addRow(chunks);
            }
            
            @Override
            protected void done() {
                try {
                    get();
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
    
    private GraphElementDataColumn[] getEdgeColumns(Table table) {
        edgeColumns[0] = sourceColumn;
        edgeColumns[1] = new GraphElementAttributeColumn(table.getColumn("label"));
        edgeColumns[2] = targetColumn;
        return edgeColumns;
    }
    
    private GraphElementDataColumn[] getNodeColumns(Table table) {
        GraphElementAvailableColumnsModel nodeAvailableColumnsModel = navigatorModel.getNodeAvailableColumnsModel();
        nodeAvailableColumnsModel.syncronizeTableColumns(table);
        return nodeAvailableColumnsModel.getAvailableColumns();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)
                && evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
            reload();
        }
    }
    
    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            if (currentModel != null) {
                currentModel.removeQuickFilterChangeListener(this);
            }
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                currentModel = attrModels.iterator().next();
                currentModel.addQuickFilterChangeListener(this);
                reload();
            }            
        }
    }
    
    @Override
    public JComponent getToolbarComponent() {
        return toolBar;
    }
    
    @Override
    public String
            getDisplayName() {
        return NbBundle.getMessage(GraphElementNavigator.class,
                "GraphElementNavigator.name");
    }
    
    @Override
    public String getDisplayHint() {
        return NbBundle.getMessage(GraphElementNavigator.class,
                "GraphElementNavigator.hint");
    }
    
    @Override
    public JComponent getComponent() {
        return this;
    }
    
    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
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
    
}
