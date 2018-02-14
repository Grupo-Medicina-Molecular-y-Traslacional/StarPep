/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author loge
 */
public class GraphViz {
    public static final String CHANGED_GRAPH_VIEW = "changed_graph_view";
    public static final String CHANGED_DISPLAYED_METADATA = "changed_metadata";

    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected final Set<AnnotationType> displayedMetadata;
    protected boolean csnVisible;

    public GraphViz() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        displayedMetadata = new HashSet<>(AnnotationType.values().length);
//        displayedMetadata.add(AnnotationType.DATABASE);
        csnVisible = true;
    }
    
    public boolean isDisplayedMetadata(AnnotationType aType){
        return displayedMetadata.contains(aType);
    }
    
    public void addDisplayedMetadata(AnnotationType aType){
        if (displayedMetadata.add(aType)){
            propertyChangeSupport.firePropertyChange(CHANGED_DISPLAYED_METADATA, null, aType);
        }
    }
    
    public void removeDisplayedMetadata(AnnotationType aType){
        if (displayedMetadata.remove(aType)){
            propertyChangeSupport.firePropertyChange(CHANGED_DISPLAYED_METADATA, aType, null);
        }
    }    

    public void addDisplayedMetadataChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(CHANGED_DISPLAYED_METADATA, listener);
    }

    public void removeDisplayedMetadataChangeListener(PropertyChangeListener listener){
         propertyChangeSupport.removePropertyChangeListener(CHANGED_DISPLAYED_METADATA, listener);
    }
    
    public void addGraphViewChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(CHANGED_GRAPH_VIEW, listener);
    }

    public void removeGraphViewChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(CHANGED_GRAPH_VIEW, listener);
    }

    public void fireChangedGraphView() {
        propertyChangeSupport.firePropertyChange(CHANGED_GRAPH_VIEW, null, null);
    }
    
}
