/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.vecmath.Vector2f;
import org.bapedis.chemspace.model.TwoDSpace;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.chemspace.spi.TwoDTransformer;
import org.bapedis.chemspace.spi.impl.WekaPCATransformerFactory;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public class TwoDEmbedder extends DescriptorBasedEmbedder {

    private TwoDTransformer transformer;
    private TwoDSpace twoDSpace;

    public TwoDEmbedder(TwoDEmbedderFactory factory) {
        super(factory);
        transformer = new WekaPCATransformerFactory().createAlgorithm();
    }

    public TwoDSpace getTwoDSpace() {
        return twoDSpace;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        twoDSpace = null;
    }

    public TwoDTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(TwoDTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    protected void embed(Peptide[] peptides, MolecularDescriptor[] features) {
        twoDSpace = transformer.transform(workspace, peptides, features);
        updateGraphNodePositions(graphModel, ticket, new AtomicBoolean(stopRun));
    }
    
    public void updateGraphNodePositions(GraphModel graphModel, ProgressTicket ticket, AtomicBoolean atomicBoolean) {        
        Vector2f[] positions = twoDSpace.getPositions();
        Peptide[] peptides = twoDSpace.getPeptides();
        ticket.switchToDeterminate(peptides.length);
        Graph graphVisible = graphModel.getGraphVisible();
        graphVisible.readLock();
        try {            
            Node node;
            Vector2f p;
            for (int i = 0; i < positions.length && !atomicBoolean.get(); i++) {
                p = positions[i];
                node = peptides[i].getGraphNode();
                node.setX((float) ((0.01 + p.getX()) * 1000) - 500);
                node.setY((float) ((0.01 + p.getY()) * 1000) - 500);
                node.setZ(0); // 2D  
                ticket.progress();
            }
        } finally {
            graphVisible.readUnlock();
        }
    }    

//    public static Vector2f[] jittering(TwoDSpace twoDSpace) {
//        Vector2f[] positions = twoDSpace.getPositions();
//        Vector2f[] v = VectorUtil.arrayCopy(positions);
//
//        JitterModel jitterModel = twoDSpace.getJitterModel();
//        if (jitterModel.getMinDistances() == null) {
//            TwoDNNComputer nn = new TwoDNNComputer(positions);
//            nn.computeNaive();
//            float dist = nn.getMinMinDist();
//            float add = (nn.getMaxMinDist() - nn.getMinMinDist()) * 0.5f;
//            Double log[] = ArrayUtil.logBinning(JitterModel.STEPS, 1.2);
//
//            float minDistances[] = new float[JitterModel.STEPS + 1];
//            for (int j = 1; j <= JitterModel.STEPS; j++) {
//                minDistances[j] = dist + add * log[j].floatValue();
//            }
//            jitterModel.setMinDistances(minDistances);
//        }
//        TwoDJittering j = new TwoDJittering(v, jitterModel.getMinDistance(), new Random());
//        j.jitter();
//        VectorUtil.normalize(v);
//        return v;
//    }

}
