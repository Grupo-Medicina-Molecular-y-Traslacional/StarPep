/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.chemspace.model.Position;
import org.bapedis.chemspace.spi.ThreeDTransformer;
import org.bapedis.chemspace.util.GephiScaler;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public class ThreeDEmbedder extends AbstractEmbedder {

    private ThreeDTransformer transformer;
    private final GephiScaler scaler;

    public ThreeDEmbedder(ThreeDEmbedderFactory factory) {
        super(factory);
        scaler = new GephiScaler();
    }

    public ThreeDTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(ThreeDTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    protected void embed(Peptide[] peptides, MolecularDescriptor[] features) {
        Position[] positions = transformer.transform(peptides, features);
        if (positions != null) {
            graph.writeLock();
            try {
                Node node;
                Position p;
                for (int i = 0; i < positions.length; i++) {
                    p = positions[i];
                    node = peptides[i].getGraphNode();
                    node.setX(p.getX());
                    node.setY(p.getY());
//                    node.setPosition(p.getX(), p.getY(), p.getZ());
                }
//                scaler.doScale(peptides);
            } finally {
                graph.writeUnlock();
            }
        }
    }

}
