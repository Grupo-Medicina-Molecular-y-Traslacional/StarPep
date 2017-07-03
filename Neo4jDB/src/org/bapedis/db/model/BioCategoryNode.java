/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class BioCategoryNode extends AbstractNode implements PropertyChangeListener {
    
    public BioCategoryNode(BioCategory category) {
        super(category.hasChild() ? Children.create(new BioCategoryChildFactory(category), false) : Children.LEAF, Lookups.singleton(category));
        category.addPropertyChangeListener(this);
        setDisplayName(category.getName());  
    }
    
    public BioCategory getBioCategory(){
        return getLookup().lookup(BioCategory.class);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> nodeActions =
                Utilities.actionsForPath("Actions/ShowPeptidesFromBioCategory");
        
        return nodeActions.toArray(new Action[0]);
    }
    
    

    @Override
    public String getHtmlDisplayName() {
        BioCategory category = getLookup().lookup(BioCategory.class);
        if (category.isSelected()){
           return "<b>" + getDisplayName() + "</b>";
        }
        return getDisplayName(); 
    }        

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        BioCategory category = getLookup().lookup(BioCategory.class);
        if (evt.getPropertyName().equals(BioCategory.SELECTED)){
            fireDisplayNameChange("", category.getName());
        }                
    }
}
