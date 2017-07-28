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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
    protected List<PeptideNode> nodeList;
    private final PeptideNodeContainer container;
    protected QuickFilter quickFilter;
    public static final String CHANGED_FILTER = "quickFilter";
    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected Node rootNode;

    public AttributesModel() {
        attrsMap = new LinkedHashMap<>();
        nodeList = new LinkedList<>();
        container = new PeptideNodeContainer();
        rootNode = new AbstractNode(container);
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PeptideAttribute[] getAttributes() {
        return attrsMap.values().toArray(new PeptideAttribute[0]);
    }

    public synchronized Peptide[] getPeptides() {
        List<Peptide> peptides = new LinkedList<>();
        boolean accepted;
        for (PeptideNode pNode : nodeList) {
            accepted = quickFilter == null? true: quickFilter.accept(pNode);
            if (accepted){
                peptides.add(pNode.getPeptide());
            }            
        }
        return peptides.toArray(new Peptide[0]);
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

    private class PeptideNodeContainer extends Index.ArrayChildren {

        @Override
        protected List<Node> initCollection() {
            List<Node> nodes = new ArrayList<>(nodeList.size());
            for (PeptideNode node : nodeList) {
                nodes.add(node);
            }
            return nodes;
        }

    }

}
