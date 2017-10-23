/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.spi.algo.Algorithm;

/**
 *
 * @author loge
 */
public class AlgorithmModel {
    protected final Workspace owner;
    protected AlgorithmCategory category;
    protected Algorithm selectedAlgorithm;
    protected transient final PropertyChangeSupport propertyChangeSupport;
    public static final String CHANGED_CATEGORY = "CATEGORY";
    public static final String CHANGED_ALGORITHM = "ALGORITHM";
    public static final String RUNNING = "RUNNING";
    protected final AtomicBoolean running;

    public AlgorithmModel(Workspace owner) {
        this.owner = owner;
        propertyChangeSupport = new PropertyChangeSupport(this);
        running = new AtomicBoolean(false);
    }

    public Workspace getOwnerWS() {
        return owner;
    }        

    public AlgorithmCategory getCategory() {
        return category;
    }

    public void setCategory(AlgorithmCategory category) {
        AlgorithmCategory oldValue = this.category;
        this.category = category;
        propertyChangeSupport.firePropertyChange(CHANGED_CATEGORY, oldValue, category);
    }

    public Algorithm getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public void setSelectedAlgorithm(Algorithm selectedAlgorithm) {
        Algorithm oldAlgo = this.selectedAlgorithm;
        this.selectedAlgorithm = selectedAlgorithm;
        propertyChangeSupport.firePropertyChange(CHANGED_ALGORITHM, oldAlgo, selectedAlgorithm);
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
    
}
