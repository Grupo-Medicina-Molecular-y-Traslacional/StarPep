/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author loge
 */
public class MyLibraryNode extends AbstractNode {
    
    private final MyLabels label;
    
    public MyLibraryNode(MyLabels label) {
        super(Children.LEAF);
        this.label = label;
    }

    @Override
    public String getDisplayName() {
        return label.getDisplayName();
    }
    
    
}
