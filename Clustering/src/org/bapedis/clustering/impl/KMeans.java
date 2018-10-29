/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import weka.clusterers.SimpleKMeans;
import weka.core.ChebyshevDistance;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.ManhattanDistance;

/**
 *
 * @author loge
 */
public class KMeans extends WekaClusterer<SimpleKMeans> {

    private final List<AlgorithmProperty> properties;
    private int n, maxIter, distanceFunction;

    public KMeans(AlgorithmFactory factory) {
        super(new SimpleKMeans(), factory);
        properties = new LinkedList<>();
        n = 8;
        maxIter = 500;
        distanceFunction = 2;
        populateProperties();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(KMeans.class, "KMeans.N.name"), PRO_CATEGORY, NbBundle.getMessage(KMeans.class, "KMeans.N.desc"), "getN", "setN"));
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(KMeans.class, "KMeans.maxIter.name"), PRO_CATEGORY, NbBundle.getMessage(KMeans.class, "KMeans.maxIter.desc"), "getMaxIter", "setMaxIter"));
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(KMeans.class, "KMeans.distanceFunction.name"), PRO_CATEGORY, NbBundle.getMessage(KMeans.class, "KMeans.distanceFunction.desc"), "getDistanceFunction", "setDistanceFunction"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public int getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public int getMaxIter() {
        return maxIter;
    }

    public void setMaxIter(Integer maxIter) {
        this.maxIter = maxIter;
    }

    public int getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(Integer distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

    @Override
    protected void configureClusterer() throws Exception {
        if (n < 2) {
            throw new IllegalArgumentException(NbBundle.getMessage(KMeans.class, "WekaClusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "KMeans.N.name")));
        }

        if (maxIter < 1) {
            throw new IllegalArgumentException(NbBundle.getMessage(KMeans.class, "WekaClusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "KMeans.maxIter.name")));
        }

        if (distanceFunction != 1 && distanceFunction != 2 && distanceFunction != 3) {
            String msg = NbBundle.getMessage(KMeans.class, "WekaClusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "KMeans.distanceFunction.name"))
                    + "\n"
                    + NbBundle.getMessage(KMeans.class, "KMeans.distanceFunction.desc");
            throw new IllegalArgumentException(msg);
        }

        //Set distance function
        DistanceFunction df = null;
        switch(distanceFunction){
            case 1: //MANHATTAN
                df = new ManhattanDistance();
                break;
            case 2: //EUCLIDEAN
                df = new EuclideanDistance();
                break;                
            case 3: //CHEBYSHEV
                df = new ChebyshevDistance();
                break;                
        }
        
        clusterer.setNumClusters(n);
        clusterer.setMaxIterations(maxIter);
        clusterer.setDistanceFunction(df);
    }

}
