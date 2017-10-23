/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.spi.filters.Filter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class FilterModel {
    protected final Workspace owner;
    protected final List<Filter> filters;
    protected transient final PropertyChangeSupport propertyChangeSupport;
    public static final String ADDED_FILTER = "ADD";
    public static final String EDITED_FILTER = "EDITED";
    public static final String REMOVED_FILTER = "REMOVE";
    public static final String CHANGED_RESTRICTION = "RESTRICTION";
    public static final String RUNNING = "RUNNING";
    protected final AtomicBoolean running;
    protected RestrictionLevel restriction;
    protected Node rootContext;

    public FilterModel(Workspace owner) {
        this.owner = owner;
        restriction = RestrictionLevel.MATCH_ALL;
        filters = new LinkedList<>();
        propertyChangeSupport = new PropertyChangeSupport(this);
        rootContext = new AbstractNode(Children.create(new FilterModelChildFactory(this), true), Lookups.singleton(this));
        running = new AtomicBoolean(false);
    }

    public Workspace getOwnerWS() {
        return owner;
    }
    
    public Node getRootContext() {
        return rootContext;
    }

    public void add(Filter filter) {
        if (filters.add(filter)) {
            propertyChangeSupport.firePropertyChange(ADDED_FILTER, null, filter);
        }
    }

    public void remove(Filter filter) {
        boolean removed = filters.remove(filter);
        if (removed) {
            propertyChangeSupport.firePropertyChange(REMOVED_FILTER, filter, null);
        }
    }

    public void remove(Filter[] filters) {
        boolean removed = false;
        for (Filter filter : filters) {
            removed = this.filters.remove(filter);
        }
        if (removed) {
            propertyChangeSupport.firePropertyChange(REMOVED_FILTER, filters, null);
        }
    }

    public void removeAll() {
        boolean removed = filters.size() > 0;
        this.filters.clear();
        if (removed) {
            propertyChangeSupport.firePropertyChange(REMOVED_FILTER, null, null);
        }
    }

    public boolean isEmpty() {
        return filters.isEmpty();
    }

    public Iterator<Filter> getFilterIterator() {
        return filters.iterator();
    }

    public RestrictionLevel getRestriction() {
        return restriction;
    }

    public void setRestriction(RestrictionLevel restriction) {
        RestrictionLevel old = this.restriction;
        this.restriction = restriction;
        propertyChangeSupport.firePropertyChange(CHANGED_RESTRICTION, old, restriction);
    }

    public boolean isRunning() {
        return running.get();
    }

    public void setRunning(boolean running) {
        boolean oldValue = this.running.get();
        this.running.set(running);
        propertyChangeSupport.firePropertyChange(RUNNING, oldValue, running);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void fireEditedEvent(Filter filter) {
        propertyChangeSupport.firePropertyChange(EDITED_FILTER, null, filter);
    }

}
