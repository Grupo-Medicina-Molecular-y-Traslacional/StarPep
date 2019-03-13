/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.jmolDisplayer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author loge
 */
public class StructureSceneModel {
    public static final String CHANGED_ITEM = "changed_item";
    public static final String CHANGED_STRUCTURE = "changed_structure";
    public static final String CHANGED_DISPLAY_OPTION = "changed_display_option";

    private StructureData item;
    private String structure;
    private int displayOption;
    private final PropertyChangeSupport propertyChangeSupport;

    public StructureSceneModel() {
        displayOption = 0;
        item = null;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public StructureData getItem() {
        return item;
    }

    public void setItem(StructureData item) {
        StructureData oldValue = this.item;
        this.item = item;
        propertyChangeSupport.firePropertyChange(CHANGED_ITEM, oldValue, item);
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        String oldValue = this.structure;
        this.structure = structure;
        propertyChangeSupport.firePropertyChange(CHANGED_STRUCTURE, oldValue, structure);
    }

    public int getDisplayOption() {
        return displayOption;
    }

    public void setDisplayOption(int displayOption) {
        int oldValue = this.displayOption;
        this.displayOption = displayOption;
        propertyChangeSupport.firePropertyChange(CHANGED_DISPLAY_OPTION, oldValue, displayOption);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
