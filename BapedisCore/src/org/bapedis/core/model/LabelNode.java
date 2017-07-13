/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.neo4j.graphdb.Label;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class LabelNode extends LibraryNode {

    private final Label label;

    public LabelNode(Label label) {
        super(Children.LEAF,Lookups.singleton(label));
        this.label = label;
        transferable.setTransferData(label);
    }

    @Override
    public String getDisplayName() {
        return label.name();
    }
}
