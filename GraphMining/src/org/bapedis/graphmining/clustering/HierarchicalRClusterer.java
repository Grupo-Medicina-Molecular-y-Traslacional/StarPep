/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.clustering;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class HierarchicalRClusterer extends RClusterer {

    public static final String[] methods = {"ward", "single", "complete", "average", "mcquitty", "median", "centroid"};
    
    private int n, distanceFunction;

    public HierarchicalRClusterer(AlgorithmFactory factory) {
        super(factory);        
        n = 8;
        distanceFunction = 2;
    }

    @Override
    protected void populateProperties() {
        super.populateProperties();
        try {
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(HierarchicalRClusterer.class, "HierarchicalRClusterer.N.name"), PRO_CATEGORY, NbBundle.getMessage(HierarchicalRClusterer.class, "HierarchicalRClusterer.N.desc"), "getN", "setN"));
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(HierarchicalRClusterer.class, "HierarchicalRClusterer.distanceFunction.name"), PRO_CATEGORY, NbBundle.getMessage(HierarchicalRClusterer.class, "HierarchicalRClusterer.distanceFunction.desc"), "getDistanceFunction", "setDistanceFunction"));
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

    public int getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(Integer distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    @Override
    protected void validateClusterer() throws MyClusterException {
        if (n < 2) {
            throw new MyClusterException(NbBundle.getMessage(KMeans.class, "Clusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "HierarchicalRClusterer.N.name")));
        }
        
        if (distanceFunction != 1 && distanceFunction != 2) {
            String msg = NbBundle.getMessage(KMeans.class, "Clusterer.arg.errorMsg", NbBundle.getMessage(KMeans.class, "HierarchicalRClusterer.distanceFunction.name"))
                    + "\n"
                    + NbBundle.getMessage(KMeans.class, "HierarchicalRClusterer.distanceFunction.desc");
            throw new MyClusterException(msg);
        }
    }

    @Override
    protected String getRScriptCode() {
        return "args <- commandArgs(TRUE)\n" //
                + "df <- read.csv(args[1])\n" //
                + "set.seed(1)\n" //
                + "d <- " + getDistance("df") + "\n" //
                + "fit <- hclust(d, method=\"" + methods[0] + "\")\n" //
                + "\ngroups <- cutree(fit, k=" + n + ")\n" //
                + "print(groups)\n" //
                + "write.table(groups,args[2])\n";
    }

    private String getDistance(String dataframe) {
        String selectedDistance = "";
        switch (distanceFunction) {
            case 1:
                selectedDistance = "manhattan";
                break;
            case 2:
                selectedDistance = "euclidean";
                break;
        }
        return "dist(" + dataframe + ", method=\"" + selectedDistance + "\")";
    }

}
