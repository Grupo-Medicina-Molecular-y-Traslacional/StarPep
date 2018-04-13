/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Random;
import javax.vecmath.Vector2f;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.Node;
import org.bapedis.chemspace.spi.TwoDTransformer;
import org.bapedis.chemspace.util.ArrayUtil;
import org.bapedis.chemspace.util.TwoDJittering;
import org.bapedis.chemspace.util.TwoDNNComputer;
import org.bapedis.chemspace.util.VectorUtil;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class TwoDEmbedder extends AbstractEmbedder {
    
    private TwoDTransformer transformer;
    
    public TwoDEmbedder(TwoDEmbedderFactory factory) {
        super(factory);
    }
    
    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
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
            jittering(positions);
            VectorUtil.normalize(positions);
            
            graph.readLock();
            try {
                Node node;
                Vector2f p;
                for (int i = 0; i < positions.length; i++) {
                    p = positions[i];
                    node = peptides[i].getGraphNode();
                    node.setX(p.getX());
                    node.setY(p.getY());
                    node.setZ(0); // 2D
                    node.setX((float) ((0.01 + p.getX()) * 1000) - 500);
                    node.setY((float) ((0.01 + p.getY()) * 1000) - 500);
                }
            } finally {
                graph.readUnlock();
            }
        }
    }        
    
    private void jittering(Vector2f[] v) {
        int STEPS = TwoDJittering.STEPS;
        int Level = 1;
        
        TwoDNNComputer nn = new TwoDNNComputer(v);
        nn.computeNaive();
        
        float dist = nn.getMinMinDist();
        float add = (nn.getMaxMinDist() - nn.getMinMinDist()) * 0.5f;
        Double log[] = ArrayUtil.logBinning(STEPS, 1.2);
        
        float minDistances[] = new float[STEPS + 1];
        for (int j = 1; j <= STEPS; j++) {
            minDistances[j] = dist + add * log[j].floatValue();
        }
        
        TwoDJittering j = new TwoDJittering(v, minDistances[Level], new Random());
        j.jitter();
    }
        
}
