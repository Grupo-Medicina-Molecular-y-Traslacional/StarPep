/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Arrays;
import java.util.Random;
import javax.vecmath.Vector2f;
import org.bapedis.chemspace.util.GephiScaler;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.Node;
import org.bapedis.chemspace.spi.TwoDTransformer;
import org.bapedis.chemspace.util.Jittering;
import org.bapedis.chemspace.util.NNComputer;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class TwoDEmbedder extends AbstractEmbedder {

    private TwoDTransformer transformer;
    private final GephiScaler scaler;
    private int maxSize;
    private int size;

    public TwoDEmbedder(TwoDEmbedderFactory factory) {
        super(factory);
        scaler = new GephiScaler();
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        maxSize = attrModel.getPeptides().size();
        size = graph.getNodeCount();
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
//            jittering(positions);
            scaling(positions);

            graph.readLock();
            try {
                Node node;
                Vector2f p;
                for (int i = 0; i < positions.length; i++) {
                    p = positions[i];
                    node = peptides[i].getGraphNode();
                    node.setX(p.getX());
                    node.setY(p.getY());
                }                
            } finally {
                graph.readUnlock();
            }
        }
    }


    private void scaling(Vector2f[] v) {
        // the average min distance mean distance of each compound to its closest neighbor compound
        float d = avgMinDist(v);
        if (d == 0) {
            d = 1;
        }

        // the smaller the distance, the higher the scale factor
        // the neigbhor should be on average 30units away
        float s = 1 / d * 30;
        //Settings.LOGGER.debug("min avg distance: " + d + " -> scale: " + s);

        // we want to limit the scale based on the max dist
        float maxD = maxDist(v);
        float max_scale = 100 / maxD;
        if (max_scale < s) {
            //Settings.LOGGER.debug("override scale\nmax distance: " + maxD + " -> scale: " + max_scale);
            s = max_scale;
        }

        // convert "int range 0 - COMPOUND_SIZE_MAX" to "float range 4.0 - 0.1"  
        float density = (float) (((1 - size / ((double) maxSize)) * 3.9f) + 0.1f);
        //Settings.LOGGER.debug("compound size: " + ClusteringUtil.COMPOUND_SIZE + " -> scale multiplier: " + density);

        // scale is multiplied with the DENSITY, which is configurable by the user
        float scale = s * density;
        for(Vector2f v1 : v){
            v1.scale(scale);
        }        
    }

    private void jittering(Vector2f[] v) {
        int STEPS = Jittering.STEPS;
        int Level = 1;

        NNComputer nn = new NNComputer(v);
        nn.computeNaive();

        float dist = nn.getMinMinDist();
        float add = (nn.getMaxMinDist() - nn.getMinMinDist()) * 0.5f;
        Double log[] = logBinning(STEPS, 1.2);

        float minDistances[] = new float[STEPS + 1];
        for (int j = 1; j <= STEPS; j++) {
            minDistances[j] = dist + add * log[j].floatValue();
        }

        Jittering j = new Jittering(v, minDistances[Level], new Random());
        j.jitter();
    }

    public static Double[] logBinning(int numBins, double base) {
        Double[] d = new Double[numBins + 1];
        for (int i = 0; i < numBins + 1; i++) {
            d[i] = Math.pow(base, i);
        }
        //return d;
        return normalize(d, 0, 1, false);

    }

    /**
     * normalizes to MIN-MAX<br>
     * REPLACES NULL VALUES WITH MEDIAN(MIN,MAX)
     *
     * @param array
     * @return
     */
    public static Double[] normalize(Double array[], double min, double max,
            boolean replaceNullWithMedian) {
        double arrMax = max(array);
        double arrMin = min(array);
        double arrMean = mean(array);
        double deltaVal = arrMax - arrMin;
        double delta = (max - min);
        Double a[] = new Double[array.length];
        if (arrMin == arrMax) {
            Arrays.fill(a, min + delta / 2.0);
            return a;
        } else {
            for (int i = 0; i < a.length; i++) {
                Double v = array[i];
                if (replaceNullWithMedian && (v == null || Double.isNaN(v))) {
                    v = arrMean;
                }

                if (v == null) {
                    a[i] = null;
                } else {
                    a[i] = (v - arrMin) / deltaVal * delta + min;
                }
            }
            return a;
        }
    }

    public static double max(Double[] data) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < data.length; i++) {
            if (Double.isNaN(data[i])) {
                return Double.NaN;
            }
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    public static double min(Double[] data) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < data.length; i++) {
            if (Double.isNaN(data[i])) {
                return Double.NaN;
            }
            if (data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    public static double mean(Double[] data) {
        if (data.length == 0) {
            return Double.NaN;
        }
        double sum = 0.0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return sum / data.length;
    }

    public static float avgMinDist(Vector2f[] vectors) {
        float dist = 0;
        for (int i = 0; i < vectors.length; i++) {
            float min = Float.MAX_VALUE;
            for (int j = 0; j < vectors.length; j++) {
                if (i != j) {
                    min = Math.min(min, NNComputer.dist(vectors[i], vectors[j]));
                }
            }
            dist += min;
        }
        dist /= vectors.length;
        return dist;
    }

    public static float maxDist(Vector2f[] vectors) {
        float max = 0;
        for (int i = 0; i < vectors.length - 1; i++) {
            for (int j = i + 1; j < vectors.length; j++) {
                max = Math.max(max, NNComputer.dist(vectors[i], vectors[j]));
            }
        }
        return max;
    }

}
