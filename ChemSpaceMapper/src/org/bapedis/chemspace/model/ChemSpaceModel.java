/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;

/**
 *
 * @author loge
 */
public class ChemSpaceModel {
    protected final Workspace workspace;
    protected Algorithm selectedAlgorithm;
    protected transient final PropertyChangeSupport propertyChangeSupport;
    public static final String CHANGED_ALGORITHM = "ALGORITHM";
    public static final String RUNNING = "RUNNING";
    private boolean running;

    public ChemSpaceModel(Workspace workspace) {
        this.workspace = workspace;
        propertyChangeSupport = new PropertyChangeSupport(this);
        running = false;
    }

    public Workspace getWorkspace() {
        return workspace;
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
        workspace.setBusy(running);
        propertyChangeSupport.firePropertyChange(RUNNING, oldValue, running);
    } 
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }      
}
