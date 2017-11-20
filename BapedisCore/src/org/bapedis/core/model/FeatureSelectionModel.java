/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author loge
 */
public class FeatureSelectionModel {
    //Entropy cut off labels: Very weak, Weak and Moderate
    public static final int[] ENTROPY_CUTOFF_REFS = new int[]{1, 10};
    //Tanimoto cut off labels: Strong and Moderate    
    public static final int[] CORRELATION_CUTOFF_REFS = new int[]{90, 98};
    
    protected final Workspace owner;
    public static final String RUNNING = "RUNNING";
    protected final AtomicBoolean running;
    protected transient final PropertyChangeSupport propertyChangeSupport;
    
    private boolean removeUseless, removeRedundant;
    private int entropyCutoff, correlationCutoff;

    public FeatureSelectionModel(Workspace owner) {
        this.owner = owner;
        entropyCutoff = ENTROPY_CUTOFF_REFS[0];
        correlationCutoff = CORRELATION_CUTOFF_REFS[1];
        removeUseless = true;
        removeRedundant = false;
        this.running = new AtomicBoolean(false);
        propertyChangeSupport = new PropertyChangeSupport(this);        
    }
    
    public Workspace getOwnerWS() {
        return owner;
    }          

    public boolean isRemoveUseless() {
        return removeUseless;
    }

    public void setRemoveUseless(boolean removeUseless) {
        this.removeUseless = removeUseless;
    }

    public boolean isRemoveRedundant() {
        return removeRedundant;
    }

    public void setRemoveRedundant(boolean removeRedundant) {
        this.removeRedundant = removeRedundant;
    }

    public int getEntropyCutoff() {
        return entropyCutoff;
    }

    public void setEntropyCutoff(int entropyCutoff) {
        this.entropyCutoff = entropyCutoff;
    }

    public int getCorrelationCutoff() {
        return correlationCutoff;
    }

    public void setCorrelationCutoff(int correlationCutoff) {
        this.correlationCutoff = correlationCutoff;
    }    
      
    public boolean isRunning() {
        return running.get();
    }

    public void setRunning(boolean running) {
        boolean oldValue = this.running.get();
        this.running.set(running);
        owner.setBusy(running);
        propertyChangeSupport.firePropertyChange(RUNNING, oldValue, running);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }    
    
}
