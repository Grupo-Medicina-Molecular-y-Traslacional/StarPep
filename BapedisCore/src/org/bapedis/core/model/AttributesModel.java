/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 * An abstract class that represents an attribute-based data model for entities. 
 * Basically, it consist on a node container for instances of the class:
 * ObjectAttributesNode.
 * @author loge
 */
public abstract class AttributesModel extends Index.ArrayChildren {

    protected final List<Attribute> attributes;
    protected final HashMap<String, Attribute> attrsMap;
    protected List<ObjectAttributesNode> objAttrsNode;
    protected Node rootContext;

    public AttributesModel() {
        attributes = new LinkedList<>();
        attrsMap = new HashMap<>();
        objAttrsNode = new LinkedList<>();
        rootContext = new AbstractNode(this);
    }

    @Override
    protected List<Node> initCollection() {
        List<Node> objNodes = new ArrayList<>(objAttrsNode.size());
        for (ObjectAttributesNode node : objAttrsNode) {
            objNodes.add(node);
        }
        return objNodes;
    }

    public Attribute[] getAttributes() {
        return attributes.toArray(new Attribute[0]);
    }

    public Attribute getAttribute(String id) {
        if (!hasAttribute(id)) {
            throw new IllegalArgumentException("Attribute doesn't exist: " + id);
        }
        return attrsMap.get(id);
    }

    public Attribute addAttribute(String id, String displayName, Class<?> cclass) {
        if (hasAttribute(id)) {
            throw new IllegalArgumentException("Duplicated attribute: " + id);
        }
        Attribute attr = new Attribute(id, displayName, cclass);
        addAttribute(attr);
        return attr;
    }

    public void addAttribute(Attribute attr) {
        attributes.add(attr);
        attrsMap.put(attr.id, attr);
    }

    public boolean hasAttribute(String id) {
        return attrsMap.containsKey(id);
    }

    public Node getRootContext() {
        return rootContext;
    }

}
