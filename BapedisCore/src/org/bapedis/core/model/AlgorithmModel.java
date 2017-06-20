/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.bapedis.core.spi.algo.Algorithm;

/**
 *
 * @author loge
 */
public class AlgorithmModel {
    protected AlgorithmCategory category;
    protected Algorithm selectedAlgorithm;
    protected transient final PropertyChangeSupport propertyChangeSupport;
    public static final String CHANGED_CATEGORY = "CATEGORY";
    public static final String CHANGED_ALGORITHM = "ALGORITHM";
    public static final String RUNNING = "RUNNING";
    protected boolean running;

    public AlgorithmModel() {
        category = AlgorithmCategory.GraphLayout;
        propertyChangeSupport = new PropertyChangeSupport(this);
        running = false;
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
