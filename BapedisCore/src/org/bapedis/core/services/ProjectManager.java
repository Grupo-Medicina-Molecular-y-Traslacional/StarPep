/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.services;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.task.AlgorithmExecutor;
import org.gephi.graph.api.Configuration;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Origin;
import org.gephi.graph.api.Table;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = ProjectManager.class)
public class ProjectManager implements Lookup.Provider {

    protected String name;
    protected File folder;
    protected Lookup lookup;
    protected InstanceContent content;
    protected Workspace currentWS;
    protected List<WorkspaceEventListener> wsListeners;
    protected final AlgorithmExecutor executor;
    private final String PRO_NAME = "name";
    private final String PRO_NAME_TITLE=NbBundle.getMessage(ProjectManager.class, "NodeTable.column.name.title");
    private final String PRO_XREF = "xref";
    private final String PRO_XREF_TITLE=NbBundle.getMessage(ProjectManager.class, "EdgeTable.column.xref.title");
    

    public ProjectManager() {
        executor = new AlgorithmExecutor();
    }

    public void newProject() {
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
        wsListeners = new LinkedList<>();
        currentWS = Workspace.getDefault();
        content.add(currentWS);
    }

    public String getProjectName() {
        return name;
    }

    public void renameProject(String name) {
        this.name = name;
    }

    public File getProjectFolder() {
        return folder;
    }

    public Workspace getCurrentWorkspace() {
        return currentWS;
    }

    public void setCurrentWorkspace(Workspace workspace) {
        if (!currentWS.equals(workspace)) {
            Collection<? extends Workspace> workspaces = lookup.lookupAll(Workspace.class);
            if (workspace != null && !workspaces.contains(workspace)) {
                throw new IllegalArgumentException(String.format("The workspace %s does not has been added to the project", workspace.getName()));
            }
            Workspace oldWs = currentWS;
            currentWS = workspace;
            fireWorkspaceEvent(oldWs, currentWS);
        }
    }

    public Workspace getPrevWorkspace() {
        Workspace prev = null;
        Workspace[] workspaces = getWorkspaces();
        for (Workspace w : workspaces) {
            if (w == currentWS) {
                return prev;
            }
            prev = w;
        }
        return null;
    }

    public Workspace getNextWorkspace() {
        Workspace prev = null;
        Workspace[] workspaces = getWorkspaces();
        for (Workspace w : workspaces) {
            if (prev == currentWS) {
                return w;
            }
            prev = w;
        }
        return null;
    }

    public synchronized Workspace[] getWorkspaces() {
        Collection<? extends Workspace> workspaces = lookup.lookupAll(Workspace.class);
        return workspaces.toArray(new Workspace[0]);
    }

    public AlgorithmExecutor getExecutor() {
        return executor;
    }

    public void clean() {
        Workspace.resetDefault();
        Workspace defaultWorkspace = Workspace.getDefault();
        Collection<? extends Workspace> workspaces = lookup.lookupAll(Workspace.class);
        if (!workspaces.contains(defaultWorkspace)) {
            content.add(defaultWorkspace);
        }
        for (Workspace ws : workspaces) {
            if (ws != defaultWorkspace) {
                content.remove(ws);
            }
        }
        setCurrentWorkspace(defaultWorkspace);
    }

    public FilterFactory[] getFilterFactories() {
        Collection<? extends FilterFactory> factories = Lookup.getDefault().lookupAll(FilterFactory.class);
        return factories.toArray(new FilterFactory[0]);
    }

    // Data Models
    public AttributesModel getAttributesModel() {
        return getAttributesModel(currentWS);
    }

    public AttributesModel getAttributesModel(Workspace workspace) {
        return workspace.getLookup().lookup(AttributesModel.class);
    }

    public GraphModel getGraphModel() {
        return getGraphModel(currentWS);
    }

    public GraphModel getGraphModel(Workspace workspace) {
        GraphModel model = workspace.getLookup().lookup(GraphModel.class);
        if (model == null) {
            Configuration config = new Configuration();
            model = GraphModel.Factory.newInstance();
            createColumns(model);
            workspace.add(model);
        }
        return model;
    }
    
    protected void createColumns(GraphModel graphModel) {
        Table nodeTable = graphModel.getNodeTable();
        if (!nodeTable.hasColumn(PRO_NAME)) {
            nodeTable.addColumn(PRO_NAME, PRO_NAME_TITLE, String.class, Origin.DATA, "", false);
        }                

        Table edgeTable = graphModel.getEdgeTable();
        if (!edgeTable.hasColumn(PRO_XREF)) {
            edgeTable.addColumn(PRO_XREF, PRO_XREF_TITLE, String[].class, Origin.DATA, new String[]{}, false);
        }
    }    

    public QueryModel getQueryModel() {
        return getQueryModel(getCurrentWorkspace());
    }

    public QueryModel getQueryModel(Workspace workspace) {
        QueryModel model = workspace.getLookup().lookup(QueryModel.class);
        if (model == null) {
            model = new QueryModel();
            workspace.add(model);
        }
        return model;
    }

    public FilterModel getFilterModel() {
        return getFilterModel(currentWS);
    }

    public FilterModel getFilterModel(Workspace workspace) {
        FilterModel model = workspace.getLookup().lookup(FilterModel.class);
        if (model == null) {
            model = new FilterModel();
            workspace.add(model);
        }
        return model;
    }

    public AlgorithmModel getAlgorithmModel() {
        return getAlgorithmModel(currentWS);
    }

    public AlgorithmModel getAlgorithmModel(Workspace workspace) {
        AlgorithmModel model = workspace.getLookup().lookup(AlgorithmModel.class);
        if (model == null) {
            model = new AlgorithmModel();
            workspace.add(model);
        }
        return model;
    }

    /**
     * Adds an abilities to this project.
     *
     * @param instance the instance that is to be added to the lookup
     */
    public void add(Object instance) {
        content.add(instance);
    }

    /**
     * Removes an abilities to this project.
     *
     * @param instance the instance that is to be removed from the lookup
     */
    public void remove(Object instance) {
        if (instance instanceof Workspace) {
            Workspace workspace = (Workspace) instance;
            if (currentWS == workspace) {
                Workspace prev = getPrevWorkspace();
                if (prev != null) {
                    setCurrentWorkspace(prev);
                } else {
                    Workspace next = getNextWorkspace();
                    setCurrentWorkspace(next);
                }
            }
        }
        content.remove(instance);
    }

    /**
     * Gets any optional abilities of this project.
     * <p>
     * May contains:
     * <ol><li>{@link ProjectInformation}</li>
     * <li>{@link ProjectMetaData}</li>
     * <li>{@link WorkspaceProvider}</li></ol>
     *
     * @return the project's lookup
     */
    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public void addWorkspaceEventListener(WorkspaceEventListener listener) {
        wsListeners.add(listener);
    }

    public void removeWorkspaceEventListener(WorkspaceEventListener listener) {
        wsListeners.remove(listener);
    }

    private void fireWorkspaceEvent(Workspace oldWs, Workspace newWs) {
        if (oldWs != newWs) {
            for (WorkspaceEventListener listener : wsListeners) {
                listener.workspaceChanged(oldWs, newWs);
            }
        }
    }

    public boolean isProjectFolder(File folder) {
        return false;
    }

    public Runnable openProject(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Runnable saveProject(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
