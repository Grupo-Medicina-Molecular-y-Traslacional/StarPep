/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bapedis.db.filters.spi.Filter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class FilterModel {
    protected int id;
    protected String name;
    protected final List<Filter> filters;
    protected transient final PropertyChangeSupport propertyChangeSupport;
    public static final String ADD_CHILD = "ADD";
    public static final String REMOVE_CHILD = "REMOVE";
    public static final String IS_EMPTY = "IS_EMPTY";
    public static final String NAME = "NAME";
    protected static final AtomicInteger counter = new AtomicInteger(1);
    protected RestrictionLevel restriction;
    protected Node rootContext;
    
    public enum RestrictionLevel{
        MATCH_ALL{

            @Override
            public String toString() {
                return NbBundle.getMessage(FilterModel.class, "FilterModel.restrictiveMode.matchAll"); 
            }
         
        },
        MATCH_ANY{

            @Override
            public String toString() {
                return NbBundle.getMessage(FilterModel.class, "FilterModel.restrictiveMode.matchAny");
            }
            
        };                
        
    }
    
    public static String getPrefixName(){
        return NbBundle.getMessage(FilterModel.class, "FilterModel.prefix");
    }
    
    public static int getCount(){
        return counter.get();
    }
    
    public FilterModel() {
        this(counter.getAndIncrement(), NbBundle.getMessage(FilterModel.class, "FilterModel.prefix") + " " + (counter.get() - 1));
    }
    
    public FilterModel(String name){
        this(counter.getAndIncrement(), name);
    }

    private FilterModel(int id, String name) {
        this.id = id;
        this.name = name;
        this.restriction = RestrictionLevel.MATCH_ALL;
        filters = new LinkedList<>();
        propertyChangeSupport = new PropertyChangeSupport(this);
        rootContext = new AbstractNode(Children.create(new FilterModelChildFactory(this), true), Lookups.singleton(this));
    }

    public Node getRootContext() {
        return rootContext;
    }        

    public void addFilter(Filter filter) {
        boolean oldIsEmpty = filters.isEmpty();
        filters.add(filter);
        propertyChangeSupport.firePropertyChange(IS_EMPTY, oldIsEmpty, filters.isEmpty());
        propertyChangeSupport.firePropertyChange(ADD_CHILD, null, filter);
    }

    public void removeFilter(Filter filter) {
        boolean oldIsEmpty = filters.isEmpty();
        filters.remove(filter);
        propertyChangeSupport.firePropertyChange(IS_EMPTY, oldIsEmpty, filters.isEmpty());
        propertyChangeSupport.firePropertyChange(REMOVE_CHILD, filter, null);
    }
    
    public boolean isEmpty(){
        return filters.isEmpty();
    }
    
    public Filter[] getFilters(){
        return filters.toArray(new Filter[0]);
    }

    public int getId() {
        return id;
    }        

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(NAME, oldName, name);
    }

    public RestrictionLevel getRestriction() {
        return restriction;
    }

    public void setRestriction(RestrictionLevel restriction) {
        this.restriction = restriction;
    }        

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return name;
    }   

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FilterModel other = (FilterModel) obj;
        return id == other.id;
    }   
    
}
