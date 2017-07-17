/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author loge
 */
public class Metadata {
    public static final String SELECTED = "Selected";
    protected boolean selected;
    protected final Long underlyingNodeID;
    protected final String name;
    protected final AnnotationType annotationType;
    protected List<Metadata> childs;    
    private transient final PropertyChangeSupport changeSupport =
            new PropertyChangeSupport(this);

    public Metadata(Long underlyingNodeID, String name, AnnotationType annotationType) {
        this.underlyingNodeID = underlyingNodeID;
        this.name = name;
        this.annotationType = annotationType;
        selected = false;
        childs = new LinkedList<>();
    }
    
    public String getName(){
        return name;
    }

    public AnnotationType getAnnotationType() {
        return annotationType;
    }        

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldState = this.selected;
        this.selected = selected;      
        for(Metadata child: childs){
            child.setSelected(selected);
        }
        changeSupport.firePropertyChange(SELECTED, oldState, selected);
    }       
    
    public void addChild(Metadata child){
        childs.add(child);
    }
    
    public void removeChild(Metadata child){
        childs.remove(child);
    }
    
    public  List<Metadata> getChilds(){
        return childs;
    }
    
    public boolean hasChild(){
        return childs.size() > 0;
    }
    
    
    public Long getUnderlyingNodeID(){
        return underlyingNodeID;
    }
    
    public void addPropertyChangeListener(
            final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(
            final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }    

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Metadata){
            return name.equals(((Metadata)obj).name);
        }
        return false;
    }        

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
}
