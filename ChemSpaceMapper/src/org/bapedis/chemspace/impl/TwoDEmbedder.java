/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Random;
import javax.vecmath.Vector2f;
import org.bapedis.chemspace.model.JitterModel;
import org.bapedis.chemspace.model.TwoDSpace;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.Node;
import org.bapedis.chemspace.spi.TwoDTransformer;
import org.bapedis.chemspace.spi.impl.WekaPCATransformerFactory;
import org.bapedis.chemspace.util.ArrayUtil;
import org.bapedis.chemspace.util.TwoDJittering;
import org.bapedis.chemspace.util.TwoDNNComputer;
import org.bapedis.chemspace.util.VectorUtil;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;

/**
 *
 * @author loge
 */
public class TwoDEmbedder extends DescriptorBasedEmbedder {

    private TwoDTransformer transformer;
    private TwoDSpace twoDSpace;
    private int jitterLevel;

    public TwoDEmbedder(TwoDEmbedderFactory factory) {
        super(factory);
        transformer = new WekaPCATransformerFactory().createAlgorithm();
        jitterLevel = 0;
    }

    public int getJitterLevel() {
        return jitterLevel;
    }

    public void setJitterLevel(int jitterLevel) {
        this.jitterLevel = jitterLevel;
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
        Vector2f[] positions = transformer.transform(peptides, features);

        if (positions != null) {
            VectorUtil.normalize(positions);
            twoDSpace = new TwoDSpace(peptides, positions);
            if (jitterLevel > 0) {
                JitterModel jitterModel = twoDSpace.getJitterModel();
                jitterModel.setLevel(jitterLevel);
                positions = jittering(twoDSpace);
            }
            setGraphNodePositions(graph, peptides, positions);
        }
    }

    public static void setGraphNodePositions(Graph graph, Peptide[] peptides, Vector2f[] positions) {

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

    public static Vector2f[] jittering(TwoDSpace twoDSpace) {
        Vector2f[] positions = twoDSpace.getPositions();
        Vector2f[] v = VectorUtil.arrayCopy(positions);

        JitterModel jitterModel = twoDSpace.getJitterModel();
        if (jitterModel.getMinDistances() == null) {
            TwoDNNComputer nn = new TwoDNNComputer(positions);
            nn.computeNaive();
            float dist = nn.getMinMinDist();
            float add = (nn.getMaxMinDist() - nn.getMinMinDist()) * 0.5f;
            Double log[] = ArrayUtil.logBinning(JitterModel.STEPS, 1.2);

            float minDistances[] = new float[JitterModel.STEPS + 1];
            for (int j = 1; j <= JitterModel.STEPS; j++) {
                minDistances[j] = dist + add * log[j].floatValue();
            }
            jitterModel.setMinDistances(minDistances);
        }
        TwoDJittering j = new TwoDJittering(v, jitterModel.getMinDistance(), new Random());
        j.jitter();
        VectorUtil.normalize(v);
        return v;
    }

}
