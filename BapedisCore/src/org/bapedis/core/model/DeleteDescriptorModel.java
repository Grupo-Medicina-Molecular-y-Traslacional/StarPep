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
public class DeleteDescriptorModel {

    protected final Workspace owner;
    public static final String RUNNING = "RUNNING";
    protected final AtomicBoolean running;
    protected transient final PropertyChangeSupport propertyChangeSupport;

    public DeleteDescriptorModel(Workspace owner) {
        this.owner = owner;
        this.running = new AtomicBoolean(false);
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    public Workspace getOwnerWS() {
        return owner;
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
