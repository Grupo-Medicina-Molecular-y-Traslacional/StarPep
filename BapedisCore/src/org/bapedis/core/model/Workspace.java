/*
 * To change this license header, choose License Headers in DataProject Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Workspace that internally stores, through its <b>Lookup</b>, various data
 * models.
 *
 * @author loge
 */
public class Workspace implements Lookup.Provider {
    public static final String PRO_NAME = "name";
    public static final String PRO_BUSY = "busy";
    protected final int id;
    protected String name;
    protected final AtomicBoolean busy;
    protected Lookup lookup;
    protected InstanceContent content;
    protected static final AtomicInteger counter = new AtomicInteger(1);
    protected static Workspace defaultWorkspace = new Workspace(0, NbBundle.getMessage(Workspace.class, "Workspace.defaultName"));
    protected final transient PropertyChangeSupport changeSupport;
    protected final NotifyDescriptor busyND;

    public static void resetDefault() {
        counter.set(1);
        defaultWorkspace = new Workspace(0, NbBundle.getMessage(Workspace.class, "Workspace.defaultName"));
    }
    
    public static String getPrefixName(){
        return NbBundle.getMessage(Workspace.class, "Workspace.prefix");
    }
    
    public static int getCount(){
        return counter.get();
    }

    public Workspace() {
        this(counter.getAndIncrement(), NbBundle.getMessage(Workspace.class, "Workspace.prefix") + " " + (counter.get() - 1));
    }
    
    public Workspace(String name){
        this(counter.getAndIncrement(), name);
    }

    public Workspace(int id, String name) {
        this.id = id;
        this.name = name;
        busy = new AtomicBoolean(false);
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
        changeSupport = new PropertyChangeSupport(this);
        busyND = new NotifyDescriptor.Message(NbBundle.getMessage(Workspace.class, "Workspace.busy.info"), NotifyDescriptor.WARNING_MESSAGE);
    }

    public static Workspace getDefault() {
        return defaultWorkspace;
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
        changeSupport.firePropertyChange(PRO_NAME, oldName, name);
    }

    public boolean isBusy() {
        return busy.get();
    }

    public void setBusy(boolean busy) {
        boolean old = this.busy.get();
        this.busy.set(busy);
        changeSupport.firePropertyChange(PRO_BUSY, old, busy);
    }        

    public NotifyDescriptor getBusyNotifyDescriptor() {
        return busyND;
    }        

    /**
     * Adds an instance to this workspaces lookup.
     *
     * @param instance the instance that is to be pushed to the lookup
     */
    public void add(Object instance) {
        content.add(instance);
    }

    /**
     * Removes an instance from this workspaces lookup.
     *
     * @param instance the instance that is to be removed from the lookup
     */
    public void remove(Object instance) {
        content.remove(instance);
    }

    /**
     * Get any instance in the current lookup.
     * <p>
     * May contains:
     * <ol><li><code>SequenceModel</code></li>
     * </ol>
     *
     * @return the workspace's lookup
     */
    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
