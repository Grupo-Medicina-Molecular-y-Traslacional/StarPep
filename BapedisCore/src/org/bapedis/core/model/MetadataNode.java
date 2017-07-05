/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class MetadataNode extends MyLibraryNode implements PropertyChangeListener {
    
    public MetadataNode(Metadata category) {
        super(category.hasChild() ? Children.create(new MetadataChildFactory(category), false) : Children.LEAF, Lookups.singleton(category));
        category.addPropertyChangeListener(this);
        setDisplayName(category.getName());  
    }
    
    public Metadata getBioCategory(){
        return getLookup().lookup(Metadata.class);
    }
    

    @Override
    public String getHtmlDisplayName() {
        Metadata category = getLookup().lookup(Metadata.class);
        if (category.isSelected()){
           return "<b>" + getDisplayName() + "</b>";
        }
        return getDisplayName(); 
    }        

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Metadata category = getLookup().lookup(Metadata.class);
        if (evt.getPropertyName().equals(Metadata.SELECTED)){
            fireDisplayNameChange("", category.getName());
        }                
    }
}
