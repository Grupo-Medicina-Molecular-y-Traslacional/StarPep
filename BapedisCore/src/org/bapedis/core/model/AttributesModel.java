/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.bapedis.core.bridge.AttributeModelBridge;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.event.SwingPropertyChangeSupport;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.netbeans.swing.etable.QuickFilter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * A class that represents an attribute-based data model for peptides.
 *
 * @author loge
 */
public class AttributesModel {

    protected final static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected final Workspace workspace;
    protected final Map<Integer, Peptide> peptideMap;
    protected final Map<String, List<MolecularDescriptor>> mdMap;
    protected final Set<PeptideAttribute> displayedColumnsModel;
    private static final int MAX_DISPLAYED_COLUMNS = 6;
    protected List<PeptideNode> nodeList;
    private final PeptideNodeContainer container;
    private List<Peptide> filteredPept;
    protected QuickFilter quickFilter;
    public static final String CHANGED_FILTER = "quickFilter";
    public static final String DISPLAY_ATTR_ADDED = "display_attribute_add";
    public static final String DISPLAY_ATTR_REMOVED = "display_attribute_remove";
    public static final String MD_ATTR_ADDED = "md_attribute_add";
    public static final String MD_ATTR_REMOVED = "md_attribute_remove";

    protected final AttributeModelBridgeImpl bridge;
    protected transient final SwingPropertyChangeSupport propertyChangeSupport;
    protected Node rootNode;

    public AttributesModel(Workspace workspace) {
        this.workspace = workspace;
        peptideMap = new LinkedHashMap<>();
        mdMap = new LinkedHashMap<>();
        nodeList = new LinkedList<>();
        container = new PeptideNodeContainer();
        rootNode = new AbstractNode(container);
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
        bridge = new AttributeModelBridgeImpl();

        displayedColumnsModel = new LinkedHashSet<>();
        displayedColumnsModel.add(Peptide.ID);
        displayedColumnsModel.add(Peptide.SEQ);
        displayedColumnsModel.add(Peptide.LENGHT);

        List<MolecularDescriptor> list = new LinkedList<>();
        list.add(Peptide.LENGHT);
        mdMap.put(Peptide.LENGHT.getCategory(), list);
    }

    public Workspace getOwnerWS() {
        return workspace;
    }

    public Map<Integer, Peptide> getPeptideMap() {
        return peptideMap;
    }

    public PeptideAttribute[] getDisplayedColumns() {
        return displayedColumnsModel.toArray(new PeptideAttribute[0]);
    }

    public boolean canAddDisplayColumn() {
        return displayedColumnsModel.size() < MAX_DISPLAYED_COLUMNS;
    }

    public boolean addDisplayedColumn(PeptideAttribute attr) {
        if (canAddDisplayColumn() && displayedColumnsModel.add(attr)) {
            propertyChangeSupport.firePropertyChange(DISPLAY_ATTR_ADDED, null, attr);
            return true;
        }
        return false;
    }

    public boolean removeDisplayedColumn(PeptideAttribute attr) {
        if (displayedColumnsModel.remove(attr)) {
            propertyChangeSupport.firePropertyChange(DISPLAY_ATTR_REMOVED, attr, null);
            return true;
        }
        return false;
    }

    public Set<String> getMolecularDescriptorKeys() {
        return mdMap.keySet();
    }

    public List<MolecularDescriptor> getMolecularDescriptors(String category) {
        return Collections.unmodifiableList(mdMap.get(category));
    }

    public boolean hasMolecularDescriptors(String category) {
        return mdMap.containsKey(category);
    }

    public void addMolecularDescriptors(String category, List<MolecularDescriptor> features) {
        if (mdMap.containsKey(category)) {
            for (MolecularDescriptor oldAttr : mdMap.get(category)) {
                if (!features.contains(oldAttr)) {
                    delete(oldAttr);
                }
            }
        }
        mdMap.put(category, features);
        propertyChangeSupport.firePropertyChange(MD_ATTR_ADDED, null, category);
    }

    public void deleteAllMolecularDescriptors(String category) {
        if (!category.equals(MolecularDescriptor.DEFAULT_CATEGORY)) {
            for (PeptideAttribute attr : mdMap.remove(category)) {
                delete(attr);
            }
            propertyChangeSupport.firePropertyChange(MD_ATTR_REMOVED, category, null);
        }
    }

    public void deleteAttribute(MolecularDescriptor attr) {
        String category = attr.getCategory();
        if (mdMap.containsKey(category)) {
            List<MolecularDescriptor> list = mdMap.get(category);
            delete(attr);
            list.remove(attr);
        } else {
            throw new IllegalArgumentException("Unknown molecular descriptor category: " + category);
        }
    }

    private void delete(PeptideAttribute attr) {
        for (PeptideNode pNode : nodeList) {
            pNode.getPeptide().deleteAttribute(attr);
        }
        removeDisplayedColumn(attr);
    }

    public synchronized List<Peptide> getPeptides() {
        if (filteredPept != null) {
            return filteredPept;
        }
        List<Peptide> peptides = new LinkedList<>();
        boolean accepted;
        for (PeptideNode pNode : nodeList) {
            accepted = quickFilter == null ? true : quickFilter.accept(pNode);
            if (accepted) {
                peptides.add(pNode.getPeptide());
            }
        }
        filteredPept = Collections.unmodifiableList(peptides);
        return peptides;
    }

    public AttributeModelBridge getBridge() {
        return bridge;
    }

    public void refresh(int a) {
        container.refreshNodes();
    }

    public Node getRootNode() {
        return rootNode;
    }

    public List<PeptideNode> getNodeList() {
        return Collections.unmodifiableList(nodeList);
    }

    public void addPeptide(Peptide peptide) {
        nodeList.add(new PeptideNode(peptide));
        peptideMap.put(peptide.getId(), peptide);
    }

    public QuickFilter getQuickFilter() {
        return quickFilter;
    }

    public void setQuickFilter(QuickFilter quickFilter) {
        QuickFilter oldFilter = this.quickFilter;
        this.quickFilter = quickFilter;
        filteredPept = null;
        propertyChangeSupport.firePropertyChange(CHANGED_FILTER, oldFilter, quickFilter);
    }

    public void addQuickFilterChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(CHANGED_FILTER, listener);
    }

    public void removeQuickFilterChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(CHANGED_FILTER, listener);
    }

    public void addDisplayColumnChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(DISPLAY_ATTR_ADDED, listener);
        propertyChangeSupport.addPropertyChangeListener(DISPLAY_ATTR_REMOVED, listener);
    }

    public void removeDisplayColumnChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(DISPLAY_ATTR_ADDED, listener);
        propertyChangeSupport.removePropertyChangeListener(DISPLAY_ATTR_REMOVED, listener);
    }

    public void addMolecularDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(MD_ATTR_ADDED, listener);
        propertyChangeSupport.addPropertyChangeListener(MD_ATTR_REMOVED, listener);
    }

    public void removeMolecularDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(MD_ATTR_ADDED, listener);
        propertyChangeSupport.removePropertyChangeListener(MD_ATTR_REMOVED, listener);
    }

    private class PeptideNodeContainer extends Index.ArrayChildren {

        @Override
        protected List<Node> initCollection() {
            List<Node> nodes = new ArrayList<>(nodeList.size());
            for (PeptideNode node : nodeList) {
                nodes.add(node);
            }
            return nodes;
        }

        public void refreshNodes() {
            refresh();
        }

    }

    private class AttributeModelBridgeImpl implements AttributeModelBridge {

        @Override
        public void copyTo(AttributesModel attrModel, List<Integer> peptideIDs) {
            if (workspace.equals(attrModel.workspace)) {
                interCopy(attrModel, peptideIDs);
            } else {
                intraCopy(attrModel, peptideIDs);
            }
        }

        private void interCopy(AttributesModel attrModel, List<Integer> peptideIDs) {
            attrModel.mdMap.putAll(mdMap);
            attrModel.displayedColumnsModel.addAll(displayedColumnsModel);

            if (peptideIDs != null) {
                for (Integer id : peptideIDs) {
                    if (!peptideMap.containsKey(id)) {
                        throw new IllegalArgumentException("Invalid peptide id: " + id);
                    }
                    attrModel.addPeptide(peptideMap.get(id));
                }
                attrModel.filteredPept = null;
            }
        }

        private void intraCopy(AttributesModel attrModel, List<Integer> peptideIDs) {
            try {
                copyMdMapTo(attrModel);
                copyDisplayedColumnsTo(attrModel);
//                copyGraphNodesTo(attrModel);
                copyDataTo(attrModel, peptideIDs);
            } catch (CloneNotSupportedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        private void copyMdMapTo(AttributesModel attrModel) throws CloneNotSupportedException {
            String key;
            List<MolecularDescriptor> currentValue;
            List<MolecularDescriptor> newValue;
            for (Map.Entry<String, List<MolecularDescriptor>> entry : mdMap.entrySet()) {
                key = entry.getKey();
                currentValue = entry.getValue();
                newValue = new LinkedList<>();
                for (MolecularDescriptor md : currentValue) {
                    newValue.add((MolecularDescriptor) md.clone());
                }
                attrModel.mdMap.put(key, newValue);
            }
        }

        private void copyDisplayedColumnsTo(AttributesModel attrModel) throws CloneNotSupportedException {
            for (PeptideAttribute attr : displayedColumnsModel) {
                attrModel.displayedColumnsModel.add((PeptideAttribute) attr.clone());
            }
        }

        private void copyGraphNodesTo(AttributesModel attrModel) {
            GraphModel currentGraphModel = pc.getGraphModel(workspace);
            org.gephi.graph.api.Node[] nodes = currentGraphModel.getGraph().getNodes().toArray();

            Workspace targetWorkspace = attrModel.getOwnerWS();
            GraphModel targetGraphModel = pc.getGraphModel(targetWorkspace);

            currentGraphModel.getGraph().readLock();
            try {
                targetGraphModel.bridge().copyNodes(nodes);
                Graph targetGraph = targetGraphModel.getGraph();

                Graph visibleCurrentGraph = currentGraphModel.getGraphVisible();
                List<Edge> edgesToRemove = new LinkedList<>();
                for (Edge edge : targetGraph.getEdges()) {
                    if (!visibleCurrentGraph.hasEdge(edge.getId())) {
                        edgesToRemove.add(edge);
                    }
                }

                if (!edgesToRemove.isEmpty()) {
                    targetGraph.removeAllEdges(edgesToRemove);
                }

            } finally {
                currentGraphModel.getGraph().readUnlockAll();
            }
        }

        private void copyDataTo(AttributesModel attrModel, List<Integer> peptideIDs) throws CloneNotSupportedException {
            Workspace targetWorkspace = attrModel.getOwnerWS();
            GraphModel targetGraphModel = pc.getGraphModel(targetWorkspace);

            Peptide currentPeptide, targetPeptide;
            org.gephi.graph.api.Node currentNode, targetNode;

            if (peptideIDs != null) {
                //Copy nodes
                List<org.gephi.graph.api.Node> toAddNodes = new LinkedList<>();
                for (Integer id : peptideIDs) {
                    if (!peptideMap.containsKey(id)) {
                        throw new IllegalArgumentException("Invalid peptide id: " + id);
                    }
                    if (!attrModel.peptideMap.containsKey(id)) {
                        currentNode = peptideMap.get(id).getGraphNode();
                        toAddNodes.add(currentNode);
                    }
                }
                targetGraphModel.bridge().copyNodes(toAddNodes.toArray(new org.gephi.graph.api.Node[0]));

                //Copy peptides
                toAddNodes.clear();
                Map<PeptideAttribute, Object> currAttrsValue, targetAttrsValue;
                for (Integer id : peptideIDs) {
                    if (!peptideMap.containsKey(id)) {
                        throw new IllegalArgumentException("Invalid peptide id: " + id);
                    }

                    if (!attrModel.peptideMap.containsKey(id)) {
                        currentPeptide = peptideMap.get(id);
                        currAttrsValue = currentPeptide.attrsValue;

                        targetNode = targetGraphModel.getGraph().getNode(currentPeptide.getGraphNode().getId());
                        targetPeptide = new Peptide(targetNode, targetGraphModel.getGraph());
                        targetAttrsValue = targetPeptide.attrsValue;

                        for (Map.Entry<PeptideAttribute, Object> entry : currAttrsValue.entrySet()) {
                            PeptideAttribute key = entry.getKey();
                            Object value = entry.getValue();
                            targetAttrsValue.put((PeptideAttribute) key.clone(), value);
                        }

                        attrModel.addPeptide(targetPeptide);
                        toAddNodes.add(targetNode);
                    }
                    attrModel.filteredPept = null;
                }
                graphWC.refreshGraphView(targetWorkspace, toAddNodes, null);
            }
        }
    }

}
