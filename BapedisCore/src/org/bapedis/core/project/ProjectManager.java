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
import org.bapedis.core.model.ClusterNavigatorModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.GraphElementNavigatorModel;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.MetadataNavigatorModel;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.SequenceTag;
import org.bapedis.core.spi.alg.impl.SequenceSearchFactory;
import org.bapedis.core.spi.filters.FilterFactory;
import org.gephi.graph.api.Configuration;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphObserver;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Origin;
import org.gephi.graph.api.Table;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
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

    public static final int MIN_AVAILABLE_FEATURES = 2;
    public static final int LARGE_NETWORK = 1000;
    private static final String NOTIFY_LARGE_NETWORK_KEY = "notifyLargeNetworkKey";

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

    public static final float GRAPH_SUPER_NODE_SIZE = 10f;
    public static final Color GRAPH_SUPER_NODE_COLOR = new Color(0.6f, 0.6f, 0.6f);
    public static final float GRAPH_SUPER_EDGE_WEIGHT = 1f;
    public static final String GRAPH_SUPER_EDGE_SIMALIRITY = "pairwise_similarity";

    public static final String NODE_TABLE_PRO_NAME = "name";
    public static final String NODE_TABLE_PRO_NAME_TITLE = NbBundle.getMessage(ProjectManager.class, "NodeTable.column.name.title");

    public static final String EDGE_TABLE_PRO_XREF = "dbRef";
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

    public synchronized void newProject() {
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
        wsListeners = new LinkedList<>();
        currentWS = Workspace.getDefault();
        content.add(currentWS);
    }

    public synchronized String getProjectName() {
        return name;
    }

    public synchronized void renameProject(String name) {
        this.name = name;
    }

    public synchronized File getProjectFolder() {
        return folder;
    }

    public synchronized Workspace getCurrentWorkspace() {
        return currentWS;
    }

    public synchronized void setCurrentWorkspace(Workspace workspace) {
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

    public synchronized Workspace getPrevWorkspace() {
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

    public synchronized Workspace getNextWorkspace() {
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

    public synchronized void clean() {
        Collection<? extends Workspace> workspaces = lookup.lookupAll(Workspace.class);
        for (Workspace ws : workspaces) {
            if (ws.isBusy()) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(ProjectManager.class, "Workspace.busy.info"), NotifyDescriptor.WARNING_MESSAGE));
                return;
            }
        }
        Workspace.resetDefault();
        Workspace defaultWorkspace = Workspace.getDefault();
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

    public synchronized void notifyWorkspaceChange(String text, Workspace ws) {
        String reason = NbBundle.getMessage(ProjectManager.class, "ChangeWorkspace.notification.reason", ws.getName(), text);
        String action = NbBundle.getMessage(ProjectManager.class, "ChangeWorkspace.notification.action");
        NotificationDisplayer.getDefault().notify(reason, ImageUtilities.loadImageIcon("org/bapedis/core/resources/balloon.png", true), action, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrentWorkspace(ws);
            }
        });
    }

    public synchronized void notifyLargeNetworkWarning(String algorithmName) {
        boolean flag = NbPreferences.forModule(ProjectManager.class).getBoolean(NOTIFY_LARGE_NETWORK_KEY, true);
        if (flag) {
            String reason = NbBundle.getMessage(ProjectManager.class, "LargeNetwork.notification.reason", algorithmName);
            String action = NbBundle.getMessage(ProjectManager.class, "LargeNetwork.notification.action");
            NotificationDisplayer.getDefault().notify(reason, ImageUtilities.loadImageIcon("org/bapedis/core/resources/balloon.png", true), action, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    NbPreferences.forModule(ProjectManager.class).putBoolean(NOTIFY_LARGE_NETWORK_KEY, false);
                }
            });
        }
    }

    public synchronized void reportRunningTask(String taskName, Workspace workspace) {
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

    public synchronized void reportFinishedTask(String taskName, Workspace workspace) {
        InputOutput io = IOProvider.getDefault().getIO(workspace.getName(), false);
        io.getOut().println();
        io.getOut().println(dataFormat.format(new Date()) + " - " + NbBundle.getMessage(ProjectManager.class, "Workspace.task.finish", taskName));
        io.getOut().println();
        io.getOut().close();
    }

    public synchronized void reportMsg(String msg, Workspace workspace) {
        InputOutput io = IOProvider.getDefault().getIO(workspace.getName(), false);
        io.getOut().println(msg);
        io.getOut().close();
    }

    public synchronized void reportError(String error, Workspace workspace) {
        InputOutput io = IOProvider.getDefault().getIO(workspace.getName(), false);
        io.getErr().println(dataFormat.format(new Date()) + " - " + error);
        io.getErr().close();
    }

    // Factory Iterators
    public synchronized Iterator<? extends FilterFactory> getFilterFactoryIterator() {
        Collection<? extends FilterFactory> factories = Lookup.getDefault().lookupAll(FilterFactory.class);
        return factories.iterator();
    }

    public synchronized Iterator<? extends AlgorithmFactory> getAlgorithmFactoryIterator() {
        Collection<? extends AlgorithmFactory> factories = Lookup.getDefault().lookupAll(AlgorithmFactory.class);
        return factories.iterator();
    }

    // Data Models
    public synchronized AttributesModel getAttributesModel() {
        return getAttributesModel(currentWS);
    }

    public synchronized AttributesModel getAttributesModel(Workspace workspace) {
        return workspace.getLookup().lookup(AttributesModel.class);
    }

    public synchronized GraphModel getGraphModel() {
        return getGraphModel(currentWS);
    }

    public synchronized GraphModel getGraphModel(Workspace workspace) {
        GraphModel model = workspace.getLookup().lookup(GraphModel.class);
        if (model == null) {
            Configuration config = new Configuration();
            model = GraphModel.Factory.newInstance();
            createColumns(model);
            workspace.add(model);
        }
        return model;
    }

    public synchronized Graph getGraphVisible() {
        return getGraphVisible(currentWS);
    }

    public synchronized Graph getGraphVisible(Workspace workspace) {
        Graph graph = workspace.getLookup().lookup(Graph.class);
        if (graph == null) {
            GraphModel graphModel = getGraphModel(workspace);
            GraphView graphView = graphModel.createView();
            graphModel.setVisibleView(graphView);
            graph = graphModel.getGraphVisible();
            workspace.add(graph);

            //Create graph observer                        
            GraphObserver observer = graphModel.createGraphObserver(graph, false);
            workspace.add(observer);
        }
        return graph;
    }

    public synchronized GraphObserver getGraphObserver() {
        return getGraphObserver(currentWS);
    }

    public synchronized GraphObserver getGraphObserver(Workspace workspace) {
        return workspace.getLookup().lookup(GraphObserver.class);
    }

    public synchronized GraphVizSetting getGraphVizSetting() {
        return getGraphVizSetting(currentWS);
    }

    public synchronized GraphVizSetting getGraphVizSetting(Workspace workspace) {
        GraphVizSetting model = workspace.getLookup().lookup(GraphVizSetting.class);
        if (model == null) {
            model = new GraphVizSetting();
            workspace.add(model);
        }
        return model;
    }
    
    public synchronized MetadataNavigatorModel getMetadataNavModel(){
        return getMetadataNavModel(currentWS);
    }
    
    public synchronized MetadataNavigatorModel getMetadataNavModel(Workspace workspace){
        MetadataNavigatorModel model = workspace.getLookup().lookup(MetadataNavigatorModel.class);
        if (model == null) {
            model = new MetadataNavigatorModel();
            workspace.add(model);
        }  
        return model;
    }
    
    public synchronized GraphElementNavigatorModel getGraphElementNavModel(){
        return getGraphElementNavModel(currentWS);
    }
    
    public synchronized GraphElementNavigatorModel getGraphElementNavModel(Workspace workspace){
        GraphElementNavigatorModel model = workspace.getLookup().lookup(GraphElementNavigatorModel.class);
        if (model == null) {
            model = new GraphElementNavigatorModel();
            workspace.add(model);
        }  
        return model;
    } 
    
    public synchronized ClusterNavigatorModel getClusterNavModel(){
        return getClusterNavModel(currentWS);
    }
    
    public synchronized ClusterNavigatorModel getClusterNavModel(Workspace workspace){
        ClusterNavigatorModel model = workspace.getLookup().lookup(ClusterNavigatorModel.class);
        if (model == null) {
            model = new ClusterNavigatorModel();
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

    public synchronized QueryModel getQueryModel() {
        return getQueryModel(getCurrentWorkspace());
    }

    public synchronized QueryModel getQueryModel(Workspace workspace) {
        QueryModel model = workspace.getLookup().lookup(QueryModel.class);
        if (model == null) {
            model = new QueryModel(workspace);
            workspace.add(model);
        }
        return model;
    }

    public synchronized FilterModel getFilterModel() {
        return getFilterModel(currentWS);
    }

    public synchronized FilterModel getFilterModel(Workspace workspace) {
        FilterModel model = workspace.getLookup().lookup(FilterModel.class);
        if (model == null) {
            model = new FilterModel(workspace);
            workspace.add(model);
        }
        return model;
    }

    public synchronized AlgorithmModel getAlgorithmModel() {
        return getAlgorithmModel(currentWS);
    }

    public synchronized AlgorithmModel getAlgorithmModel(Workspace workspace) {
        AlgorithmModel model = workspace.getLookup().lookup(AlgorithmModel.class);
        if (model == null) {
            model = new AlgorithmModel(workspace);
            //Initialize the default sequence search algorithm
            AlgorithmFactory seqSearchfactory = Lookup.getDefault().lookup(SequenceSearchFactory.class);
            if (seqSearchfactory != null) {
                Algorithm defaultAlgorithm = getOrCreateAlgorithm(seqSearchfactory);
                if (defaultAlgorithm != null) {
                    model.setTagInterface(SequenceTag.class);
                    model.setSelectedAlgorithm(defaultAlgorithm);
                }
            }
            workspace.add(model);
        }
        return model;
    }

    public synchronized Algorithm getOrCreateAlgorithm(AlgorithmFactory factory) {
        return getOrCreateAlgorithm(currentWS, factory);
    }

    public synchronized Algorithm getOrCreateAlgorithm(Workspace workspace, AlgorithmFactory factory) {
        Collection<? extends Algorithm> savedAlgo = workspace.getLookup().lookupAll(Algorithm.class);
        Algorithm algorithm = null;
        for (Algorithm algo : savedAlgo) {
            if (algo.getFactory().equals(factory)) {
                algorithm = algo;
                break;
            }
        }
        boolean addToWS = false;
        if (algorithm == null) {
            algorithm = factory.createAlgorithm();
            addToWS = true;
        }

        if (algorithm != null && addToWS) {
            workspace.add(algorithm);
        }
        return algorithm;
    }

    /**
     * Adds an abilities to this project.
     *
     * @param instance the instance that is to be added to the lookup
     */
    public synchronized void add(Object instance) {
        content.add(instance);
    }

    /**
     * Removes an abilities to this project.
     *
     * @param instance the instance that is to be removed from the lookup
     */
    public synchronized void remove(Object instance) {
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
            //Destroy graph observer
            GraphObserver observer = getGraphObserver(workspace);
            if (observer != null && !observer.isDestroyed()) {
                observer.destroy();
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
    public synchronized Lookup getLookup() {
        return lookup;
    }

    public synchronized void addWorkspaceEventListener(WorkspaceEventListener listener) {
        wsListeners.add(listener);
    }

    public synchronized void removeWorkspaceEventListener(WorkspaceEventListener listener) {
        wsListeners.remove(listener);
    }

    private void fireWorkspaceEvent(Workspace oldWs, Workspace newWs) {
        if (oldWs != newWs) {
            for (WorkspaceEventListener listener : wsListeners) {
                listener.workspaceChanged(oldWs, newWs);
            }
        }
    }

    public synchronized boolean isProjectFolder(File folder) {
        return false;
    }

    public synchronized Runnable openProject(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public synchronized Runnable saveProject(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
