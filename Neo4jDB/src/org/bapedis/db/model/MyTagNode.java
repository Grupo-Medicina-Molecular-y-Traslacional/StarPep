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
public class MyTagNode extends AbstractNode {
    
    public MyTagNode() {
        super(Children.create(new MyTagChildFactory(), true));
    }

    @Override
    public String getDisplayName() {
        return MyLabels.Tags.getDisplayName();
    }
        
}
