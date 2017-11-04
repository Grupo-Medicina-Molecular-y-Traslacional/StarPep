/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.gephi.graph.api.GraphView;
import org.netbeans.swing.etable.QuickFilter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 * A class that represents an attribute-based data model for peptides.
 *
 * @author loge
 */
public class AttributesModel {

    protected final LinkedHashMap<String, PeptideAttribute[]> mdMap;
    protected final Set<PeptideAttribute> displayedColumnsModel;
    private static final int MAX_AVAILABLE_COLUMNS = 6;
    protected List<PeptideNode> nodeList;
    private final PeptideNodeContainer container;
    private Peptide[] filteredPept;
    protected QuickFilter quickFilter;
    public static final String CHANGED_FILTER = "quickFilter";
    public static final String DISPLAY_ATTR_ADDED = "display_attribute_add";
    public static final String DISPLAY_ATTR_REMOVED = "display_attribute_remove";
    public static final String MD_ATTR_ADDED = "md_attribute_add";
    public static final String MD_ATTR_REMOVED = "md_attribute_remove";
    public static final String CHANGED_GVIEW = "changed_graphview";
    public static final String DEFAULT_CATEGORY = "Default";
    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected Node rootNode;
    public static final int GRAPH_DB_VIEW = 1;
    public static final int CSN_VIEW = 2;
    protected int mainGView;
    protected GraphView graphDBView, csnView;
    protected double similarityThreshold;

    public AttributesModel() {
        mdMap = new LinkedHashMap<>();
        nodeList = new LinkedList<>();
        container = new PeptideNodeContainer();
        rootNode = new AbstractNode(container);
        propertyChangeSupport = new PropertyChangeSupport(this);

        displayedColumnsModel = new LinkedHashSet<>();
        displayedColumnsModel.add(Peptide.ID);
        displayedColumnsModel.add(Peptide.SEQ);
        displayedColumnsModel.add(Peptide.LENGHT);

        mdMap.put(DEFAULT_CATEGORY, new PeptideAttribute[]{Peptide.LENGHT});

        mainGView = CSN_VIEW;
    }

    public int getMainGView() {
        return mainGView;
    }

    public void setMainGView(int mainGView) {
        int oldvalue = this.mainGView;
        if (mainGView != GRAPH_DB_VIEW && mainGView != CSN_VIEW) {
            throw new IllegalArgumentException("Unknown value for main graph view");
        }
        this.mainGView = mainGView;
        propertyChangeSupport.firePropertyChange(CHANGED_GVIEW, oldvalue, mainGView);
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public GraphView getGraphDBView() {
        return graphDBView;
    }

    public void setGraphDBView(GraphView graphDBView) {
        this.graphDBView = graphDBView;
    }

    public GraphView getCsnView() {
        return csnView;
    }

    public void setCsnView(GraphView csnView) {
        this.csnView = csnView;
    }

    public Set<PeptideAttribute> getDisplayedColumns() {
        return displayedColumnsModel;
    }

    public boolean canAddDisplayColumn() {
        return displayedColumnsModel.size() < MAX_AVAILABLE_COLUMNS;
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

    public LinkedHashMap<String, PeptideAttribute[]> getMolecularDescriptors() {
        return mdMap;
    }

    public synchronized PeptideAttribute[] getMolecularDescriptors(String category) {
        return mdMap.get(category);
    }

    public synchronized boolean hasMolecularDescriptors(String category) {
        return mdMap.containsKey(category);
    }

    public synchronized void addMolecularDescriptors(String category, PeptideAttribute[] features) {
        mdMap.put(category, features);
        propertyChangeSupport.firePropertyChange(MD_ATTR_ADDED, null, category);
    }

    public synchronized void deleteMolecularDescriptors(String category) {
        if (!category.equals(DEFAULT_CATEGORY)) {
            PeptideAttribute[] features = mdMap.remove(category);
            for(PeptideAttribute attr: features){
                deleteAttribute(attr);
            }
            propertyChangeSupport.firePropertyChange(MD_ATTR_REMOVED, category, null);
        }
    }

    private void deleteAttribute(PeptideAttribute attr) {
        for (PeptideNode pNode : nodeList) {
            pNode.getPeptide().deleteAttribute(attr);
        }
        removeDisplayedColumn(attr);
    }
    
    public synchronized Peptide[] getPeptides() {
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
        filteredPept = peptides.toArray(new Peptide[0]);
        return filteredPept;
    }

    public void refresh() {
        container.refreshNodes();
    }

    public Node getRootNode() {
        return rootNode;
    }

    public List<PeptideNode> getNodeList() {
        return nodeList;
    }

    public void addPeptide(Peptide peptide) {
        nodeList.add(new PeptideNode(this, peptide));
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

    public void addGraphViewChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(CHANGED_GVIEW, listener);
    }

    public void removeGraphViewChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(CHANGED_GVIEW, listener);
    }

    public void fireChangedGraphView() {
        propertyChangeSupport.firePropertyChange(CHANGED_GVIEW, null, mainGView);
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

}
