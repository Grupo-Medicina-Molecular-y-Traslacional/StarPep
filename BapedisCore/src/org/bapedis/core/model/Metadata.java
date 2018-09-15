/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public class Metadata{
    protected final String id;
    protected Node graphNode;
    protected final Metadata parent;
    protected final String name;
    protected final StarPepAnnotationType annotationType;
    protected final List<Metadata> childs;

    public Metadata(Metadata parent, String id, String name, StarPepAnnotationType annotationType, boolean isLeaf) {
        this.parent = parent;
        this.id = id;
        this.name = name;
        this.annotationType = annotationType;
        childs = isLeaf ? null: new LinkedList<>();
    }

    public Metadata(String id, String name, StarPepAnnotationType annotationType) {
        this(null, id, name, annotationType, true);
    }

    public String getName() {
        return name;
    }

    public StarPepAnnotationType getAnnotationType() {
        return annotationType;
    }

    public void addChild(Metadata child) {        
        childs.add(child);
    }

    public List<Metadata> getChilds() {
        return childs;
    }

    public String getNodeID() {
        return id;
    }

    public Node getGraphNode() {
        return graphNode;
    }

    public void setGraphNode(Node graphNode) {
        this.graphNode = graphNode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Metadata) {
            return id.equals(((Metadata) obj).id); 
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public String toString() {
        return name.trim();
    }

    public boolean hasChilds() {
        return childs != null && childs.size() > 0;
    }

}
