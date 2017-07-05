/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class MyLabelNode extends MyLibraryNode {

    private final MyLabels label;

    public MyLabelNode(MyLabels label) {
        super(Children.LEAF,Lookups.singleton(label));
        this.label = label;
    }

    @Override
    public String getDisplayName() {
        return label.getDisplayName();
    }
}
