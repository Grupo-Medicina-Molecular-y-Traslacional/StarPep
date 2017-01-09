/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class PeptideNode extends ObjectAttributesNode {
    
    public PeptideNode(Peptide peptide) {
        this(peptide, Children.LEAF, Lookups.singleton(peptide));
    }
    
    public PeptideNode(Peptide peptide, Children children, Lookup lookup){
        super(peptide, children, lookup);
    }
    
    @Override
    public String getDisplayName() {
        return ((Peptide)objAttr).getDisplayName(); 
    }
    
    
}
