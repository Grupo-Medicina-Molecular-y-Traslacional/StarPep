/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;

/**
 * A class that represents an attribute-based data model for peptides. 
 * @author loge
 */
public class AttributesModel  {
    protected final HashMap<String, PeptideAttribute> attrsMap;
    protected PeptideNodeContainer container;
    protected Node rootNode;

    public AttributesModel() {
        attrsMap = new LinkedHashMap<>();
        container = new PeptideNodeContainer();
        rootNode = new AbstractNode(container);
    }

    public PeptideAttribute[] getAttributes() {
        return attrsMap.values().toArray(new PeptideAttribute[0]);
    }

    public Peptide[] getPeptides() {
        return container.getPeptides();
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
    
    public void addPeptide(Peptide peptide){
        container.addPeptideNode(new PeptideNode(peptide));
    }    

}
