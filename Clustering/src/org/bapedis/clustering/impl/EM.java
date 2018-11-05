/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.util.LinkedList;
import java.util.List;
import static org.bapedis.clustering.impl.WekaClusterer.PRO_CATEGORY;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class EM extends WekaClusterer<weka.clusterers.EM> {
    private final List<AlgorithmProperty> properties;
    private int maxIter;
            
    public EM(AlgorithmFactory factory) {
        super(new weka.clusterers.EM(), factory);
        maxIter = 100;
        properties = new LinkedList<>();
        populateProperties();
    }
    
    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(KMeans.class, "EM.maxIter.name"), PRO_CATEGORY, NbBundle.getMessage(KMeans.class, "EM.maxIter.desc"), "getMaxIter", "setMaxIter"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }  
    
    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }    
    
    public int getMaxIter() {
        return maxIter;
    }

    public void setMaxIter(Integer maxIter) {
        this.maxIter = maxIter;
    }    

    @Override
    protected void configureClusterer() throws Exception {
        if (maxIter < 1) {
            throw new IllegalArgumentException(NbBundle.getMessage(KMeans.class, "WekaClusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "EM.maxIter.name")));
        }      
        
        clusterer.setMaxIterations(maxIter);
    }
    
}
