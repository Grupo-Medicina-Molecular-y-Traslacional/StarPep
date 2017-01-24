/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;

/**
 * A class that represents an attribute-based data model for peptides. 
 * @author loge
 */
public class AttributesModel  {

    protected final List<PeptideAttribute> attributes;
    protected final HashMap<String, PeptideAttribute> attrsMap;
    protected List<Peptide> peptides;
    protected PeptideNodeContainer container;
    protected Node rootContext;

    public AttributesModel() {
        attributes = new LinkedList<>();
        attrsMap = new HashMap<>();
        peptides = new LinkedList<>();
        container = new PeptideNodeContainer();
        rootContext = new AbstractNode(container);
    }

    public PeptideAttribute[] getAttributes() {
        return attributes.toArray(new PeptideAttribute[0]);
    }

    public List<Peptide> getPeptides() {
        return peptides;
    }

    public PeptideAttribute getAttribute(String id) {
        if (!hasAttribute(id)) {
            throw new IllegalArgumentException("Attribute doesn't exist: " + id);
        }
        return attrsMap.get(id);
    }

    public PeptideAttribute addAttribute(String id, String displayName, Class<?> cclass) {
        if (hasAttribute(id)) {
            throw new IllegalArgumentException("Duplicated attribute: " + id);
        }
        PeptideAttribute attr = new PeptideAttribute(id, displayName, cclass);
        addAttribute(attr);
        return attr;
    }

    public void addAttribute(PeptideAttribute attr) {
        attributes.add(attr);
        attrsMap.put(attr.id, attr);
    }

    public boolean hasAttribute(String id) {
        return attrsMap.containsKey(id);
    }

    public Node getRootContext() {
        return rootContext;
    }
    
    public void addPeptide(Peptide peptide){
        peptides.add(peptide);
        container.addPeptideNode(new PeptideNode(peptide));
    }    

}
