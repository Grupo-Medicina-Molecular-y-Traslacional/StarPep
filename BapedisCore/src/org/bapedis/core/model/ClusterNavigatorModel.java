/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 *
 * @author loge
 */
public class ClusterNavigatorModel {
    public static final String RUNNING = "running";
    public static final String CHANGED_CLUSTER = "changed";
    protected boolean running;
    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected Cluster[] clusters;

    public ClusterNavigatorModel() {
        running = false;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setClusters(Cluster[] clusters) {
        this.clusters = clusters;
        propertyChangeSupport.firePropertyChange(CHANGED_CLUSTER, null, clusters);
    }
    
    public Cluster[] getClusters() {
        return clusters;
    }
    
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        boolean oldValue = this.running;
        this.running = running;
        propertyChangeSupport.firePropertyChange(RUNNING, oldValue, running);
    }    
        
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }    
}
