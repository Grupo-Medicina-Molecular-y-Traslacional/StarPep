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
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class QueryModelChildFactory extends ChildFactory<Metadata> implements PropertyChangeListener {
    protected final QueryModel queryModel;

    public QueryModelChildFactory(QueryModel queryModel) {
        this.queryModel = queryModel;
        queryModel.addPropertyChangeListener(this);
    }
    
    @Override
    protected boolean createKeys(List<Metadata> list) {
        list.addAll(queryModel.getMetadataList());
        return true;
    }

    @Override
    protected Node createNodeForKey(Metadata key) {
        return new QueryNode(queryModel,key);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(queryModel)){
            refresh(true);
        }                   
    }
    
}