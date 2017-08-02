/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.GraphElement;
import org.bapedis.core.model.GraphElementChildFactory;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "graph/table", displayName = "#GraphElementNavigator.name")
public class GraphElementNavigator extends JComponent implements ExplorerManager.Provider,
        WorkspaceEventListener, PropertyChangeListener, LookupListener, NavigatorPanelWithToolbar {

    protected final ExplorerManager explorerMgr;
    protected final JToolBar toolBar;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected AttributesModel currentModel;

    protected final JToggleButton nodesBtn, edgesBtn;
    protected GraphElement type;
    protected final ElementItem[] rootContext;
    private boolean activated;

    /**
     * Creates new form GraphElementNavigator
     */
    public GraphElementNavigator() {
        initComponents();

        explorerMgr = new ExplorerManager();

        nodesBtn = new JToggleButton(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.node.name"));
        initToogleButton(nodesBtn);

        edgesBtn = new JToggleButton(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.name"));
        initToogleButton(edgesBtn);

        ButtonGroup elementGroup = new ButtonGroup();
        elementGroup.add(nodesBtn);
        elementGroup.add(edgesBtn);

        type = GraphElement.Node;
        nodesBtn.setSelected(true);
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

        toolBar = new JToolBar();
        toolBar.add(nodesBtn);
        toolBar.addSeparator();
        toolBar.add(edgesBtn);

        lookup = ExplorerUtils.createLookup(explorerMgr, getActionMap());

        rootContext = new ElementItem[]{new ElementItem(GraphElement.Node), new ElementItem(GraphElement.Edge)};

//        TreeTableView edgeView = new TreeTableView();
//        edgeView.setRootVisible(false);
//        edgeView.getOutline().setPopupUsedFromTheCorner(false);
//        edgeView.setPropertyColumns("source", NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.source"),
//                "target", NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.target"));
//        centerPanel.add(edgeView, "edgeCard");
//        BeanTreeView nodeView = new BeanTreeView();
//        nodeView.setRootVisible(false);
//        centerPanel.add(nodeView, "nodeCard");
//

        BeanTreeView view = new BeanTreeView();
        view.setRootVisible(false);
        centerPanel.add(view, BorderLayout.CENTER);

        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);

        activated = false;
    }

    private void initToogleButton(JToggleButton btn) {
        btn.setFocusable(false);
        btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    }

    private void nodesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (type != GraphElement.Node) {
            type = GraphElement.Node;
            setRootContext();
        }

    }

    private void edgesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (type != GraphElement.Edge) {
            type = GraphElement.Edge;
            setRootContext();      
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

        centerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        centerPanel.setLayout(new java.awt.BorderLayout());
        add(centerPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerMgr;
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
        setDirtyData();
    }

    private void setRootContext() {
        if (rootContext[type.ordinal()].isDirty()) {
            rootContext[type.ordinal()].reload();
        }
        explorerMgr.setRootContext(rootContext[type.ordinal()].getRootContext());
    }

    private void setDirtyData() {
        rootContext[0].setDirty(true);
        rootContext[1].setDirty(true);
        if (activated) {
            rootContext[type.ordinal()].reload();
            explorerMgr.setRootContext(rootContext[type.ordinal()].rootContext);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)
                && evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
            setDirtyData();
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            setDirtyData();
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
    public String
            getDisplayHint() {
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
        activated = true;
        if (rootContext[type.ordinal()].isDirty()) {
            rootContext[type.ordinal()].reload();
        }
        explorerMgr.setRootContext(rootContext[type.ordinal()].rootContext);
    }

    @Override
    public void panelDeactivated() {
        activated = false;
    }

    @Override
    public Lookup getLookup() {
        return lookup;

    }

    private class ElementItem {

        private AbstractNode rootContext;
        private final GraphElement type;
        private boolean dirty;

        public ElementItem(GraphElement type) {
            rootContext = new AbstractNode(Children.create(new GraphElementChildFactory(type), true));
            this.type = type;
            dirty = false;
        }

        public AbstractNode getRootContext() {
            return rootContext;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        public void reload() {
            rootContext = new AbstractNode(Children.create(new GraphElementChildFactory(type), true));
            dirty = false;
        }

    }
}
