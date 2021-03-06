/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author beltran, loge
 */
public abstract class AbstractMD implements Algorithm {

    protected final ProjectManager pc;
    protected AttributesModel attrModel;
    protected Workspace workspace;
    protected boolean stopRun;
    protected final AlgorithmFactory factory;
    protected ProgressTicket progressTicket;
    protected final String PRO_CATEGORY = "Properties";
    private final Map<String, MolecularDescriptor> map;
    public static final String MD_ADDED = "md_added";
    protected final PropertyChangeSupport propertyChangeSupport;
    protected boolean includeUseless;
    protected boolean silentMode;

    public AbstractMD(AlgorithmFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;
        map = Collections.synchronizedMap(new LinkedHashMap<>());
        propertyChangeSupport = new PropertyChangeSupport(this);
        includeUseless = false;
        silentMode = false;
    }

    public boolean isIncludeUseless() {
        return includeUseless;
    }

    public void setIncludeUseless(boolean includeUseless) {
        this.includeUseless = includeUseless;
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    protected void addAttribute(String id, String displayName, Class<?> type) {
        MolecularDescriptor attr = new MolecularDescriptor(id, displayName, type, factory.getName());
        addAttribute(attr);
    }

    public MolecularDescriptor getOrAddAttribute(String id, String displayName, Class<?> type, Double defaultValue) {
        synchronized (map) {
            MolecularDescriptor attr = map.get(id);
            if (attr == null) {
                attr = new MolecularDescriptor(id, displayName, type, factory.getName(), defaultValue);
                map.put(id, attr);
                propertyChangeSupport.firePropertyChange(MD_ADDED, null, attr);
            }
            return attr;
        }
    }

    protected void addAttribute(MolecularDescriptor attr) {
        synchronized (map) {
            if (map.containsKey(attr.getId())) {
                throw new IllegalArgumentException("Duplicated attribute: " + attr.getId());
            }
            map.put(attr.getId(), attr);
            propertyChangeSupport.firePropertyChange(MD_ADDED, null, attr);
        }
    }

    public MolecularDescriptor getAttribute(String id) {
        return map.get(id);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
        map.clear();
        attrModel = pc.getAttributesModel(workspace);
        this.workspace = workspace;
        stopRun = false;
    }

    @Override
    public void endAlgo() {
        attrModel = null;
        progressTicket = null;
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
    public final void run() {
        if (attrModel != null) {
            List<Peptide> peptides = attrModel.getPeptides();
            progressTicket.switchToDeterminate(peptides.size() + 1);

            try {
                peptides.parallelStream().forEach(peptide -> {
                    if (!stopRun) {
                        compute(peptide);
                        progressTicket.progress();
                    }
                });
            } finally {
                if (!stopRun) {
                    //Add molecular descriptors to attributes model
                    HashMap<String, List<MolecularDescriptor>> byCategory = new LinkedHashMap<>();
                    String category;
                    List<MolecularDescriptor> list;
                    double[] data = new double[peptides.size()];
                    double min, max;
                    int ignoredAttr = 0;
                    try {
                        for (MolecularDescriptor attr : map.values()) {
                            int pos = 0;
                            for (Peptide pept : peptides) {
                                data[pos++] = MolecularDescriptor.getDoubleValue(pept, attr);
                            }
                            min = MolecularDescriptor.min(data);
                            max = MolecularDescriptor.max(data);
                            // Add non-constant molecular features
                            if (includeUseless || (!Double.isNaN(min) && !Double.isNaN(max) && min != max)) {
                                category = attr.getCategory();
                                if (!byCategory.containsKey(category)) {
                                    byCategory.put(category, new LinkedList<>());
                                }
                                list = byCategory.get(category);
                                list.add(attr);
                            } else {
                                for (Peptide pept : peptides) {
                                    pept.deleteAttribute(attr);
                                }
                                ignoredAttr++;
                            }
                        }
                    } catch (MolecularDescriptorNotFoundException ex) {
                        DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                        Exceptions.printStackTrace(ex);
                        cancel();
                    }
                    String key;
                    int maxKeyLength = 0;
                    for (Map.Entry<String, List<MolecularDescriptor>> entry : byCategory.entrySet()) {
                        key = entry.getKey();
                        attrModel.addMolecularDescriptors(key, entry.getValue());
                        if (key.length() > maxKeyLength) {
                            maxKeyLength = key.length();
                        }
                    }
                    //Report messages
                    StringBuilder msg;
                    int total = 0, size;
                    for (Map.Entry<String, List<MolecularDescriptor>> entry : byCategory.entrySet()) {
                        key = entry.getKey();
                        size = entry.getValue().size();
                        msg = new StringBuilder(key);
                        for (int i = key.length() + 1; i <= maxKeyLength; i++) {
                            msg.append(' ');
                        }
                        msg.append(" : ");
                        msg.append(size);
                        msg.append(size > 1 ? " features" : " feature");
                        if (!silentMode) {
                            pc.reportMsg(msg.toString(), workspace);
                        }
                        total += size;
                    }
                    if (!silentMode) {
                        pc.reportMsg("Ignored attributes: " + ignoredAttr, workspace);
                        pc.reportMsg("Total of calculated features: " + total, workspace);
                    }
                }
                progressTicket.progress();
            }
        }
    }

    public void addMolecularDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(MD_ADDED, listener);
    }

    public void removeMolecularDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(MD_ADDED, listener);
    }

    protected abstract void compute(Peptide peptide);

}
