/*
 Copyright 2008-2013 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2013 Gephi Consortium.
 */
package org.gephi.desktop.appearance;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.spi.Transformer;
import org.gephi.appearance.spi.TransformerCategory;
import org.gephi.appearance.spi.TransformerUI;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author mbastian
 */
@ServiceProvider(service = AppearanceUIController.class)
public class AppearanceUIController implements WorkspaceEventListener, PropertyChangeListener {

    private final ProjectManager pc;
    //Classes
    public static final String NODE_ELEMENT = "nodes";
    public static final String EDGE_ELEMENT = "edges";
    public static final String[] ELEMENT_CLASSES = {NODE_ELEMENT, EDGE_ELEMENT};
    //Transformers
    protected final Map<String, Map<TransformerCategory, Set<TransformerUI>>> transformers;
    //Architecture
    protected final AppearanceController appearanceController;
    private final Set<AppearanceUIModelListener> listeners;
    //Model
    private AppearanceUIModel model;

    public AppearanceUIController() {
        appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.addWorkspaceEventListener(this);

        model = pc.getCurrentWorkspace().getLookup().lookup(AppearanceUIModel.class);
        if (model == null) {
            AppearanceModel appearanceModel = appearanceController.getModel(pc.getCurrentWorkspace());
            model = new AppearanceUIModel(this, appearanceModel);
            pc.getCurrentWorkspace().add(model);
        }

        listeners = Collections.synchronizedSet(new HashSet<AppearanceUIModelListener>());

        transformers = new HashMap<>();
        for (String ec : ELEMENT_CLASSES) {
            transformers.put(ec, new LinkedHashMap<TransformerCategory, Set<TransformerUI>>());
        }

        //Register transformers
        Map<Class, Transformer> tMap = new HashMap<>();
        for (Transformer t : Lookup.getDefault().lookupAll(Transformer.class)) {
            tMap.put(t.getClass(), t);
        }
        for (TransformerUI ui : Lookup.getDefault().lookupAll(TransformerUI.class)) {
            Transformer t = tMap.get(ui.getTransformerClass());
            if (t != null) {
                TransformerCategory c = ui.getCategory();
                if (t.isNode()) {
                    Set<TransformerUI> uis = transformers.get(NODE_ELEMENT).get(c);
                    if (uis == null) {
                        uis = new LinkedHashSet<>();
                        transformers.get(NODE_ELEMENT).put(c, uis);
                    }
                    uis.add(ui);
                }
                if (t.isEdge()) {
                    Set<TransformerUI> uis = transformers.get(EDGE_ELEMENT).get(c);
                    if (uis == null) {
                        uis = new LinkedHashSet<>();
                        transformers.get(EDGE_ELEMENT).put(c, uis);
                    }
                    uis.add(ui);
                }
            }
        }
        
        GraphVizSetting graphViz = pc.getGraphVizSetting();
        graphViz.addGraphTableChangeListener(this);        
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        AppearanceUIModel oldUIModel = oldWs != null ? oldWs.getLookup().lookup(AppearanceUIModel.class) : null;
        model = getModel(newWs);

//        if (tableObserver != null) {
//            tableObserver.destroy();
//        }
//        if (graphObserver != null) {
//            graphObserver.destroy();
//            graphObserver = null;
//        }
//        tableObserver = new TableChangeObserver(workspace);
//        tableObserver.start();
        firePropertyChangeEvent(AppearanceUIModelEvent.MODEL, oldUIModel, model);

        if (oldWs != null) {
            GraphVizSetting oldGVizModel = pc.getGraphVizSetting(oldWs);
            if (oldGVizModel != null) {
                oldGVizModel.removeGraphTableChangeListener(this);
            }
        }

        GraphVizSetting graphViz = pc.getGraphVizSetting(newWs);
        graphViz.addGraphTableChangeListener(this);

    }

    public Collection<TransformerCategory> getCategories(String elementClass) {
        return transformers.get(elementClass).keySet();
    }

    public Collection<TransformerUI> getTransformerUIs(String elementClass, TransformerCategory category) {
        return transformers.get(elementClass).get(category);
    }

    public AppearanceUIModel getModel() {
        return model;
    }

    public AppearanceUIModel getModel(Workspace workspace) {
        AppearanceUIModel model = workspace.getLookup().lookup(AppearanceUIModel.class);
        if (model == null) {
            AppearanceModel appearanceModel = appearanceController.getModel(workspace);
            model = new AppearanceUIModel(this, appearanceModel);
            workspace.add(model);
        }
        return model;
    }

    public void setSelectedElementClass(String elementClass) {
        if (!elementClass.equals(NODE_ELEMENT) && !elementClass.equals(EDGE_ELEMENT)) {
            throw new RuntimeException("Element class has to be " + NODE_ELEMENT + " or " + EDGE_ELEMENT);
        }
        if (model != null) {
            String oldValue = model.getSelectedElementClass();
            if (!oldValue.equals(elementClass)) {
                model.setSelectedElementClass(elementClass);

                firePropertyChangeEvent(AppearanceUIModelEvent.SELECTED_ELEMENT_CLASS, oldValue, elementClass);
            }
        }
    }

    public void setSelectedCategory(TransformerCategory category) {
        if (model != null) {
            TransformerCategory oldValue = model.getSelectedCategory();
            if (!oldValue.equals(category)) {
                model.setSelectedCategory(category);

                firePropertyChangeEvent(AppearanceUIModelEvent.SELECTED_CATEGORY, oldValue, category);
            }
        }
    }

    public void setSelectedTransformerUI(TransformerUI ui) {
        if (model != null) {
            TransformerUI oldValue = model.getSelectedTransformerUI();
            if (!oldValue.equals(ui)) {
                model.setSelectedTransformerUI(ui);

                firePropertyChangeEvent(AppearanceUIModelEvent.SELECTED_TRANSFORMER_UI, oldValue, ui);
            }
        }
    }

    public void setSelectedFunction(Function function) {
        if (model != null) {
            Function oldValue = model.getSelectedFunction();
            if ((oldValue == null && function != null) || (oldValue != null && function == null) || (function != null && oldValue != null && !oldValue.equals(function))) {
                model.setSelectedFunction(function);

                firePropertyChangeEvent(AppearanceUIModelEvent.SELECTED_FUNCTION, oldValue, function);
            }
        }
    }

    public AppearanceController getAppearanceController() {
        return appearanceController;
    }

    public void addPropertyChangeListener(AppearanceUIModelListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(AppearanceUIModelListener listener) {
        listeners.remove(listener);
    }

    protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
        AppearanceUIModelEvent event = new AppearanceUIModelEvent(this, propertyName, oldValue, newValue);
        for (AppearanceUIModelListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(GraphVizSetting.CHANGED_GRAPH_TABLE)) {
            Function oldValue = model.getSelectedFunction();
            model.refreshSelectedFunction();
            Function newValue = model.getSelectedFunction();
            firePropertyChangeEvent(AppearanceUIModelEvent.SELECTED_FUNCTION, oldValue, newValue);
        }
    }

//    private class GraphChangeObserver extends TimerTask {
//
//        private static final int INTERVAL = 2000;
//        private final Timer timer;
//        private final ColumnObserver columnObserver;
//        private final GraphObserver graphObserver;
//
//        public GraphChangeObserver(Graph graph, Column column) {
//            timer = new Timer("GraphChangeObserver", true);
//            graphObserver = graph.getModel().createGraphObserver(graph, false);
//            columnObserver = column != null ? column.createColumnObserver(false) : null;
//        }
//
//        @Override
//        public void run() {
//            boolean graphChanged = graphObserver.hasGraphChanged();
//            boolean columnChanged = columnObserver != null ? columnObserver.hasColumnChanged() : false;
//            if (graphChanged || columnChanged) {
//                Function oldValue = model.getSelectedFunction();
//                model.refreshSelectedFunction();
//                Function newValue = model.getSelectedFunction();
//                firePropertyChangeEvent(AppearanceUIModelEvent.SELECTED_FUNCTION, oldValue, newValue);
//            }
//        }
//
//        public void start() {
//            timer.schedule(this, INTERVAL, INTERVAL);
//        }
//
//        public void stop() {
//            timer.cancel();
//        }
//
//        public void destroy() {
//            stop();
//            if (!graphObserver.isDestroyed()) {
//                graphObserver.destroy();
//            }
//            if (columnObserver != null && !columnObserver.isDestroyed()) {
//                columnObserver.destroy();
//            }
//        }
//    }
//
//    private class TableChangeObserver extends TimerTask {
//
//        private final ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
//        private static final int INTERVAL = 500;
//        private final Timer timer;
//        private final TableObserver nodeObserver;
//        private final TableObserver edgeObserver;
//
//        public TableChangeObserver(Workspace workspace) {
//            timer = new Timer("AppearanceColumnObserver", true);
//            GraphModel graphModel = pm.getGraphModel(workspace);
//            nodeObserver = graphModel.getNodeTable().createTableObserver(false);
//            edgeObserver = graphModel.getEdgeTable().createTableObserver(false);
//        }
//
//        @Override
//        public void run() {
//            if (nodeObserver.hasTableChanged() || edgeObserver.hasTableChanged()) {
//                Function oldValue = model.getSelectedFunction();
//                model.refreshSelectedFunction();
//                Function newValue = model.getSelectedFunction();
//                firePropertyChangeEvent(AppearanceUIModelEvent.SELECTED_FUNCTION, oldValue, newValue);
//            }
//        }
//
//        public void start() {
//            timer.schedule(this, INTERVAL, INTERVAL);
//        }
//
//        public void stop() {
//            timer.cancel();
//        }
//
//        public void destroy() {
//            stop();
//            if (!nodeObserver.isDestroyed()) {
//                nodeObserver.destroy();
//            }
//            if (!edgeObserver.isDestroyed()) {
//                edgeObserver.destroy();
//            }
//        }
//    }
}
