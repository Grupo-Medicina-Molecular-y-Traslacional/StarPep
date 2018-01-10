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
    public static final String CHANGED_THRESHOLD = "changed_threshold";
    public static final String CHANGED_GRAPH_VIEW = "changed_graph_view";
    public static final String CHANGED_DISPLAYED_METADATA = "changed_metadata";
    public static final String CHANGED_DISPLAYED_CSN = "changed_csn";

    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected float similarityThreshold;
    protected final Set<AnnotationType> displayedMetadata;
    protected boolean csnVisible;

    public GraphViz() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        displayedMetadata = new HashSet<>(AnnotationType.values().length);
//        displayedMetadata.add(AnnotationType.DATABASE);
        csnVisible = true;
        similarityThreshold = 0.7f;
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

    public boolean isCsnVisible() {
        return csnVisible;
    }

    public void setCsnVisible(boolean csnVisible) {
        boolean oldValue = this.csnVisible;
        this.csnVisible = csnVisible;
        propertyChangeSupport.firePropertyChange(CHANGED_DISPLAYED_CSN, oldValue, csnVisible);
    }

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(float threshold) {
        float oldValue = this.similarityThreshold;
        this.similarityThreshold = threshold;
        propertyChangeSupport.firePropertyChange(CHANGED_THRESHOLD, oldValue, threshold);
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
