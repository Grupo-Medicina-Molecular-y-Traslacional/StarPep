/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class AbstractEmbedder implements Algorithm, Cloneable {
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected AttributesModel attrModel;
    protected GraphModel graphModel;
    protected Graph graph;
    protected ProgressTicket ticket;
    protected boolean stopRun;

    

    public AbstractEmbedder(AlgorithmFactory factory) {
        this.factory = factory;        
    }     

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        stopRun = false;
        this.workspace = workspace;
        this.ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            graphModel = pc.getGraphModel(workspace);
            graph = graphModel.getGraphVisible();
        }        
    }


    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
        graphModel = null;
        graph = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); 
    }           
}
