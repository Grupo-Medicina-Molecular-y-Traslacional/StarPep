/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class BioCategoryChildFactory extends ChildFactory<BioCategory> {
    protected BioCategory parent;    

    public BioCategoryChildFactory(BioCategory parent) {
        this.parent = parent;
    }        
    
    @Override
    protected boolean createKeys(List<BioCategory> list) {
        list.addAll(parent.getChilds());
        return true;
    }

    @Override
    protected Node createNodeForKey(BioCategory key) {
        return new BioCategoryNode(key);
    }
    
    
    
}
