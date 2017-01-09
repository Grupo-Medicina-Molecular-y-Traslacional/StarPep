/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.ObjectAttributesNode;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class NeoNeighborsModel extends AttributesModel {

    public NeoNeighborsModel() {
    }

    public void addNeighbor(NeoNeighbor neighbor) {
        objAttrsNode.add(new NeoNeighborNode(neighbor));
    }

    public NeoNeighbor[] getNeighbors() {
        NeoNeighbor[] neighbors = new NeoNeighbor[objAttrsNode.size()];
        int cursor = 0;
        for (ObjectAttributesNode pNode : objAttrsNode) {
            neighbors[cursor++] = pNode.getLookup().lookup(NeoNeighbor.class);
        }
        return neighbors;
    }
}
