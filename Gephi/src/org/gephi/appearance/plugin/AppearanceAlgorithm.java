/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.appearance.plugin;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class AppearanceAlgorithm implements Algorithm {
    private final AppearanceAlgorithmFactory factory;

    public AppearanceAlgorithm(AppearanceAlgorithmFactory factory) {
        this.factory = factory;
    }
        
    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void endAlgo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void run() {
        
    }
    
}
