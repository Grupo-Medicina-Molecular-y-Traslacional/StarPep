/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import org.neo4j.graphdb.Label;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class QueryModelChildFactory extends ChildFactory<Object> implements PropertyChangeListener {
    protected final QueryModel queryModel;

    public QueryModelChildFactory(QueryModel queryModel) {
        this.queryModel = queryModel;
        queryModel.addPropertyChangeListener(this);
    }
    
    @Override
    protected boolean createKeys(List<Object> list) {
        list.addAll(Arrays.asList(queryModel.getLabels()));
        list.addAll(Arrays.asList(queryModel.getMetadatas()));
        return true;
    }

    @Override
    protected Node createNodeForKey(Object key) {
        if (key instanceof  Label){
            return new LabelNode((Label) key);
        } else if (key instanceof Metadata){
            return new MetadataNode((Metadata) key);
        }
        return null;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(queryModel)){
            refresh(true);
        }                   
    }
    
}