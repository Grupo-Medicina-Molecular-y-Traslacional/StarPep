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
import weka.core.ChebyshevDistance;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.ManhattanDistance;

/**
 *
 * @author loge
 */
public class KMeansWrapper extends WekaClusterer<CascadeSimpleKMeans> {

    private final List<AlgorithmProperty> properties;
    private int minNumClusters, maxNumClusters, maxIter, distanceFunction;
    private boolean manuallySelectNumClusters;

    public KMeansWrapper(AlgorithmFactory factory) {
        super(new CascadeSimpleKMeans(), factory);
        properties = new LinkedList<>();
        minNumClusters = clusterer.getMinNumClusters();
        maxNumClusters = clusterer.getMaxNumClusters();
        maxIter = clusterer.getMaxIterations();
        manuallySelectNumClusters = clusterer.isManuallySelectNumClusters();
        distanceFunction = 2;
        populateProperties();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.minNumClusters.name"), PRO_CATEGORY, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.minNumClusters.desc"), "getMinNumClusters", "setMinNumClusters"));
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.maxNumClusters.name"), PRO_CATEGORY, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.maxNumClusters.desc"), "getMaxNumClusters", "setMaxNumClusters"));
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.maxIter.name"), PRO_CATEGORY, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.maxIter.desc"), "getMaxIter", "setMaxIter"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.manuallySelectNumClusters.name"), PRO_CATEGORY, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.manuallySelectNumClusters.desc"), "isManuallySelectNumClusters", "setManuallySelectNumClusters"));
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.distanceFunction.name"), PRO_CATEGORY, NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.distanceFunction.desc"), "getDistanceFunction", "setDistanceFunction"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public int getMinNumClusters() {
        return minNumClusters;
    }

    public void setMinNumClusters(Integer minNumClusters) {
        this.minNumClusters = minNumClusters;
    }

    public int getMaxNumClusters() {
        return maxNumClusters;
    }

    public void setMaxNumClusters(Integer maxNumClusters) {
        this.maxNumClusters = maxNumClusters;
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

    public boolean isManuallySelectNumClusters() {
        return manuallySelectNumClusters;
    }

    public void setManuallySelectNumClusters(Boolean manuallySelectNumClusters) {
        this.manuallySelectNumClusters = manuallySelectNumClusters;
    }

    @Override
    protected void configureClusterer() throws Exception {
        if (minNumClusters < 2) {
            throw new IllegalArgumentException(NbBundle.getMessage(KMeansWrapper.class, "Clusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "KMeansWrapper.minNumClusters.name")));
        }

        if (maxNumClusters <= minNumClusters) {
            throw new IllegalArgumentException(NbBundle.getMessage(KMeansWrapper.class, "Clusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "KMeansWrapper.maxNumClusters.name")));
        }

        if (maxIter < 1) {
            throw new IllegalArgumentException(NbBundle.getMessage(KMeans.class, "Clusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "KMeans.maxIter.name")));
        }

        if (distanceFunction != 1 && distanceFunction != 2 && distanceFunction != 3) {
            String msg = NbBundle.getMessage(KMeansWrapper.class, "Clusterer.arg.errorMsg", NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.distanceFunction.name"))
                    + "\n"
                    + NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.distanceFunction.desc");
            throw new IllegalArgumentException(msg);
        }

        //Set distance function
        DistanceFunction df = null;
        switch (distanceFunction) {
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

        clusterer.setWorkspace(workspace);
        clusterer.setMinNumClusters(minNumClusters);
        clusterer.setMaxNumClusters(maxNumClusters);
        clusterer.setMaxIterations(maxIter);
        clusterer.setManuallySelectNumClusters(manuallySelectNumClusters);
        clusterer.setDistanceFunction(df);
    }

}
