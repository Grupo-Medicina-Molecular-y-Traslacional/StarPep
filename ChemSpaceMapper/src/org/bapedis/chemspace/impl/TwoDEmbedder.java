/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import javax.vecmath.Vector2f;
import org.bapedis.chemspace.util.GephiScaler;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.Node;
import org.bapedis.chemspace.spi.TwoDTransformer;

/**
 *
 * @author loge
 */
public class TwoDEmbedder extends AbstractEmbedder {

    private TwoDTransformer transformer;
    private final GephiScaler scaler;

    public TwoDEmbedder(TwoDEmbedderFactory factory) {
        super(factory);
        scaler = new GephiScaler();
    }

    public TwoDTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(TwoDTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    protected void embed(Peptide[] peptides, MolecularDescriptor[] features) {
        Vector2f[] positions = transformer.transform(peptides, features);
        if (positions != null) {
            graph.writeLock();
            try {
                Node node;
                Vector2f p;
                for (int i = 0; i < positions.length; i++) {
                    p = positions[i];
                    node = peptides[i].getGraphNode();
                    node.setX(p.getX());
                    node.setY(p.getY());
                }
                scaler.doScale(peptides);
            } finally {
                graph.writeUnlock();
            }
        }
    }

}
