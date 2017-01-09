/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.bapedis.core.model.ObjectAttributesNode;

/**
 *
 * @author loge
 */
public class NeoNeighborNode extends ObjectAttributesNode {
    
    public NeoNeighborNode(NeoNeighbor neighbor) {
        super(neighbor);
    }

    @Override
    public String getDisplayName() {
        return ((NeoNeighbor)objAttr).getName();
    }
    
    
    
}
