/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashSet;
import java.util.Set;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class RemoveDescriptorAlgo implements Algorithm {

    private final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final RemoveDescriptorFactory factory;

    private final Set<String> descriptorKeys;
    protected Workspace workspace;
    private AttributesModel attrModel;
    private boolean stopRun = false;
    private ProgressTicket ticket;

    public static final String RUNNING = "RUNNING";
    protected boolean running;
    protected transient final PropertyChangeSupport propertyChangeSupport;

    public RemoveDescriptorAlgo(RemoveDescriptorFactory factory) {
        descriptorKeys = new LinkedHashSet<>();
        running = false;
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.factory = factory;
    }

    public Set<String> getDescriptorKeys() {
        return descriptorKeys;
    }

    public void includeAlgorithm(String algoName) {
        descriptorKeys.add(algoName);
    }

    public void excludeAlgorithm(String algoName) {
        descriptorKeys.remove(algoName);
    }

    public boolean isIncluded(String algoName) {
        return descriptorKeys.contains(algoName);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        boolean oldValue = this.running;
        this.running = running;
        if (workspace != null) {
            workspace.setBusy(running);
        }
        propertyChangeSupport.firePropertyChange(RUNNING, oldValue, running);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        stopRun = false;
        setRunning(true);
    }

    @Override
    public void endAlgo() {
        setRunning(false);
        workspace = null;
        attrModel = null;
        ticket = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        for (String key : descriptorKeys) {
            if (!stopRun) {
                if (attrModel.hasMolecularDescriptors(key)) {
                    attrModel.deleteAllMolecularDescriptors(key);
                    pc.reportMsg("Deleted: " + key, workspace);
                }
            }
        }
        descriptorKeys.clear();
    }

}
