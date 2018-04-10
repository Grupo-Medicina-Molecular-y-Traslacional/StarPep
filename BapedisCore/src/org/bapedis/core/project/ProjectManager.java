/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.project;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.GraphViz;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.filters.FilterFactory;
import org.gephi.graph.api.Configuration;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Origin;
import org.gephi.graph.api.Table;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

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
    public static final DateFormat dataFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

    public static final float GRAPH_NODE_SIZE = 10f;
    public static final Color GRAPH_NODE_COLOR = new Color(0.6f, 0.6f, 0.6f);
    public static final float GRAPH_EDGE_WEIGHT = 1f;
    public static final String GRAPH_EDGE_SIMALIRITY = "pairwise_similarity";
    public static final String NODE_TABLE_PRO_NAME = "name";
    public static final String NODE_TABLE_PRO_NAME_TITLE = NbBundle.getMessage(ProjectManager.class, "NodeTable.column.name.title");
    public static final String EDGE_TABLE_PRO_XREF = "xref";
    public static final String EDGE_TABLE_PRO_XREF_TITLE = NbBundle.getMessage(ProjectManager.class, "EdgeTable.column.xref.title");
    public static final String EDGE_TABLE_PRO_SIMILARITY = "similarity";
    public static final String EDGE_TABLE_PRO_SIMILARITY_TITLE = NbBundle.getMessage(ProjectManager.class, "EdgeTable.column.similarity.title");

    final String OUTPUT_ID = "output";
    TopComponent outputWindow;

    public ProjectManager() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                outputWindow = WindowManager.getDefault().findTopComponent(OUTPUT_ID);
            }
        });
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
            if (outputWindow != null && outputWindow.isOpened()) {
                InputOutput io = IOProvider.getDefault().getIO(currentWS.getName(), false);
                io.select();
            }
        }
    }

    public Workspace getPrevWorkspace() {
        Workspace prev = null;
        for (Iterator<? extends Workspace> it = getWorkspaceIterator(); it.hasNext();) {
            Workspace w = it.next();
            if (w == currentWS) {
                return prev;
            }
            prev = w;
        }
        return null;
    }

    public Workspace getNextWorkspace() {
        Workspace prev = null;
        for (Iterator<? extends Workspace> it = getWorkspaceIterator(); it.hasNext();) {
            Workspace w = it.next();
            if (prev == currentWS) {
                return w;
            }
            prev = w;
        }
        return null;
    }

    public synchronized Iterator<? extends Workspace> getWorkspaceIterator() {
        Collection<? extends Workspace> workspaces = lookup.lookupAll(Workspace.class);
        return workspaces.iterator();
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

    public void workspaceChangeNotification(String text, Workspace ws) {
        String reason = NbBundle.getMessage(ProjectManager.class, "ChangeWorkspace.notification.reason", ws.getName(), text);
        String action = NbBundle.getMessage(ProjectManager.class, "ChangeWorkspace.notification.action");
        NotificationDisplayer.getDefault().notify(reason, ImageUtilities.loadImageIcon("org/bapedis/core/resources/balloon.png", true), action, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrentWorkspace(ws);
            }
        });
    }

    public void reportRunningTask(String taskName, Workspace workspace) {
        InputOutput io = IOProvider.getDefault().getIO(workspace.getName(), false);
        String text = dataFormat.format(new Date()) + " - " + NbBundle.getMessage(ProjectManager.class, "Workspace.task.begin", taskName);
        for (int i = 0; i < text.length(); i++) {
            if (i == text.length() - 1) {
                io.getOut().println('-');
            } else {
                io.getOut().print('-');
            }
        }
        io.getOut().println(text);
        io.getOut().println();
        io.getOut().close();
    }

    public void reportFinishedTask(String taskName, Workspace workspace) {
        InputOutput io = IOProvider.getDefault().getIO(workspace.getName(), false);
        io.getOut().println();
        io.getOut().println(dataFormat.format(new Date()) + " - " + NbBundle.getMessage(ProjectManager.class, "Workspace.task.finish", taskName));
        io.getOut().println();
        io.getOut().close();
    }

    public void reportMsg(String msg, Workspace workspace) {
        InputOutput io = IOProvider.getDefault().getIO(workspace.getName(), false);
        io.getOut().println(msg);
        io.getOut().close();
    }

    public void reportError(String error, Workspace workspace) {
        InputOutput io = IOProvider.getDefault().getIO(workspace.getName(), false);
        io.getErr().println(dataFormat.format(new Date()) + " - " + error);
        io.getErr().close();
    }

    // Factory Iterators
    public Iterator<? extends FilterFactory> getFilterFactoryIterator() {
        Collection<? extends FilterFactory> factories = Lookup.getDefault().lookupAll(FilterFactory.class);
        return factories.iterator();
    }

    public Iterator<? extends AlgorithmFactory> getAlgorithmFactoryIterator() {
        Collection<? extends AlgorithmFactory> factories = Lookup.getDefault().lookupAll(AlgorithmFactory.class);
        return factories.iterator();
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
    
    public GraphViz getGraphViz(){
        return getGraphViz(currentWS);
    }

    public GraphViz getGraphViz(Workspace workspace) {
        GraphViz model = workspace.getLookup().lookup(GraphViz.class);
        if (model == null) {
            model = new GraphViz();
            workspace.add(model);
        }
        return model;
    }

    protected void createColumns(GraphModel graphModel) {
        Table nodeTable = graphModel.getNodeTable();
        if (!nodeTable.hasColumn(NODE_TABLE_PRO_NAME)) {
            nodeTable.addColumn(NODE_TABLE_PRO_NAME, NODE_TABLE_PRO_NAME_TITLE, String.class, Origin.DATA, "", false);
        }

        Table edgeTable = graphModel.getEdgeTable();
        if (!edgeTable.hasColumn(EDGE_TABLE_PRO_XREF)) {
            edgeTable.addColumn(EDGE_TABLE_PRO_XREF, EDGE_TABLE_PRO_XREF_TITLE, String[].class, Origin.DATA, new String[]{}, false);
        }
        if (!edgeTable.hasColumn(EDGE_TABLE_PRO_SIMILARITY)) {
            edgeTable.addColumn(EDGE_TABLE_PRO_SIMILARITY, EDGE_TABLE_PRO_SIMILARITY_TITLE, Float.class, Origin.DATA, null, false);
        }
    }

    public QueryModel getQueryModel() {
        return getQueryModel(getCurrentWorkspace());
    }

    public QueryModel getQueryModel(Workspace workspace) {
        QueryModel model = workspace.getLookup().lookup(QueryModel.class);
        if (model == null) {
            model = new QueryModel(workspace);
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
            model = new FilterModel(workspace);
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
            model = new AlgorithmModel(workspace);
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
