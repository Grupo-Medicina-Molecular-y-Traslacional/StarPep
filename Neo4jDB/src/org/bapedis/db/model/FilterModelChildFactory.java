/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.bapedis.db.filters.spi.Filter;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class FilterModelChildFactory extends ChildFactory<Filter> implements PropertyChangeListener {
    protected final FilterModel filterModel;

    public FilterModelChildFactory(FilterModel filterModel) {
        this.filterModel = filterModel;
        filterModel.addPropertyChangeListener(this);
    }
    
    @Override
    protected boolean createKeys(List<Filter> list) {
        Filter[] filters = filterModel.getFilters();
        for(Filter f: filters){
            list.add(f);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Filter key) {
        return new FilterNode(key);
    }
    
    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(FilterModel.ADD_CHILD)){
            refresh(true);
        } else if (evt.getPropertyName().equals(FilterModel.REMOVE_CHILD)){
            refresh(true);
        }                    
    }
    
}
