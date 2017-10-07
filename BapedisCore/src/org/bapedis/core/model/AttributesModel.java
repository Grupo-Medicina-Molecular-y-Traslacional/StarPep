/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.spi.data.PeptideDAO;
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

    protected final HashMap<String, PeptideAttribute> attrsMap;
    protected final Set<PeptideAttribute> availableColumnsModel;
    private static final int MAX_AVAILABLE_COLUMNS = 6;
    protected List<PeptideNode> nodeList;
    private final PeptideNodeContainer container;
    protected QuickFilter quickFilter;
    public static final String CHANGED_FILTER = "quickFilter";
    public static final String AVAILABLE_ATTR_ADDED = "attribute_add";
    public static final String AVAILABLE_ATTR_REMOVED = "attribute_remove";
    public static final String CHANGED_GVIEW = "graphview";
    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected Node rootNode;
    public static final int GRAPH_DB_VIEW = 1;
    public static final int CSN_VIEW = 2;
    protected int mainGView;
    protected GraphView graphDBView, csnView;
    protected double similarityThreshold;

    public AttributesModel() {
        attrsMap = new LinkedHashMap<>();
        nodeList = new LinkedList<>();
        container = new PeptideNodeContainer();
        rootNode = new AbstractNode(container);
        propertyChangeSupport = new PropertyChangeSupport(this);

        availableColumnsModel = new LinkedHashSet<>();
        availableColumnsModel.add(PeptideDAO.ID);
        availableColumnsModel.add(PeptideDAO.SEQ);
        availableColumnsModel.add(PeptideDAO.LENGHT);
        
        mainGView = GRAPH_DB_VIEW;
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

    public Iterator<PeptideAttribute> getAttributeIterator() {
        return attrsMap.values().iterator();
    }

    public Set<PeptideAttribute> getAvailableColumnsModel() {
        return availableColumnsModel;
    }

    public boolean canAddAvailableColumn() {
        return availableColumnsModel.size() < MAX_AVAILABLE_COLUMNS;
    }

    public boolean addAvailableColumn(PeptideAttribute attr) {
        if (canAddAvailableColumn() && availableColumnsModel.add(attr)) {
            propertyChangeSupport.firePropertyChange(AVAILABLE_ATTR_ADDED, null, attr);
            return true;
        }
        return false;
    }

    public boolean removeAvailableColumn(PeptideAttribute attr) {
        if (availableColumnsModel.remove(attr)) {
            propertyChangeSupport.firePropertyChange(AVAILABLE_ATTR_REMOVED, attr, null);
            return true;
        }
        return false;
    }

    public void deleteAttribute(PeptideAttribute attr) {
        for (PeptideNode pNode : nodeList) {
            pNode.getPeptide().deleteAttribute(attr);
        }
        removeAvailableColumn(attr);
        attrsMap.remove(attr.id);
    }

    public synchronized Peptide[] getPeptides() {
        List<Peptide> peptides = new LinkedList<>();
        boolean accepted;
        for (PeptideNode pNode : nodeList) {
            accepted = quickFilter == null ? true : quickFilter.accept(pNode);
            if (accepted) {
                peptides.add(pNode.getPeptide());
            }
        }
        return peptides.toArray(new Peptide[0]);
    }

    public void refresh() {
        container.refreshNodes();
    }

    public PeptideAttribute getAttribute(String id) {
        if (!hasAttribute(id)) {
            throw new IllegalArgumentException("Attribute doesn't exist: " + id);
        }
        return attrsMap.get(id);
    }

    public PeptideAttribute addAttribute(String id, String displayName, Class<?> cclass) {
        PeptideAttribute attr = new PeptideAttribute(id, displayName, cclass);
        addAttribute(attr);
        return attr;
    }

    public void addAttribute(PeptideAttribute attr) {
        if (hasAttribute(attr.getId())) {
            throw new IllegalArgumentException("Duplicated attribute: " + attr.getId());
        }
        attrsMap.put(attr.id, attr);
    }

    public boolean hasAttribute(String id) {
        return attrsMap.containsKey(id);
    }

    public Node getRootNode() {
        return rootNode;
    }

    public List<PeptideNode> getNodeList() {
        return nodeList;
    }

    public void addPeptide(Peptide peptide) {
        nodeList.add(new PeptideNode(peptide));
    }

    public QuickFilter getQuickFilter() {
        return quickFilter;
    }

    public void setQuickFilter(QuickFilter quickFilter) {
        QuickFilter oldFilter = this.quickFilter;
        this.quickFilter = quickFilter;
        propertyChangeSupport.firePropertyChange(CHANGED_FILTER, oldFilter, quickFilter);
    }

    public void addQuickFilterChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(CHANGED_FILTER, listener);
    }

    public void removeQuickFilterChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(CHANGED_FILTER, listener);
    }

    public void addAvailableColumnChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(AVAILABLE_ATTR_ADDED, listener);
        propertyChangeSupport.addPropertyChangeListener(AVAILABLE_ATTR_REMOVED, listener);
    }

    public void removeAvailableColumnChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(AVAILABLE_ATTR_ADDED, listener);
        propertyChangeSupport.removePropertyChangeListener(AVAILABLE_ATTR_REMOVED, listener);
    }
    
    public void addGraphViewChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(CHANGED_GVIEW, listener);
    }

    public void removeGraphViewChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(CHANGED_GVIEW, listener);
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
