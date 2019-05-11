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
import org.bapedis.chemspace.similarity.WekaPCATransformerFactory;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public class TwoDEmbedder {

    private TwoDTransformer transformer;
    private TwoDSpace twoDSpace;

    public TwoDEmbedder(TwoDEmbedderFactory factory) {
        transformer = new WekaPCATransformerFactory().createAlgorithm();
    }

    public TwoDSpace getTwoDSpace() {
        return twoDSpace;
    }

    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        twoDSpace = null;
    }

    public TwoDTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(TwoDTransformer transformer) {
        this.transformer = transformer;
    }


    protected void embed(Peptide[] peptides, MolecularDescriptor[] features) {
//        twoDSpace = transformer.transform(workspace, peptides, features);
//        updateGraphNodePositions(graphModel, ticket, new AtomicBoolean(stopRun));
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

}
