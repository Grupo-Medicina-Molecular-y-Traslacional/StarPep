/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.util;

import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.Node;

/**
 *
 * @author Mathieu Bastian
 */
public class GephiScaler {

    private float sizeMinimum;
    private float sizeMaximum;
    private float weightMinimum;
    private float weightMaximum;
    private float octreeLimit;

    private void setDefaults() {
        sizeMaximum = 100f;
        sizeMinimum = 4f;
        weightMinimum = 0.4f;
        weightMaximum = 2f;
        octreeLimit = 500;
    }

    public void doScale(Peptide[] peptides) {
        setDefaults();

        float sizeMin = Float.POSITIVE_INFINITY;
        float sizeMax = Float.NEGATIVE_INFINITY;
        float xMin = Float.POSITIVE_INFINITY;
        float xMax = Float.NEGATIVE_INFINITY;
        float yMin = Float.POSITIVE_INFINITY;
        float yMax = Float.NEGATIVE_INFINITY;
        float zMin = Float.POSITIVE_INFINITY;
        float zMax = Float.NEGATIVE_INFINITY;
        float sizeRatio = 0f;
        float averageSize = 2.5f;

        //Recenter
        Node node;
        double centroidX = 0;
        double centroidY = 0;
        double centroidZ = 0;
        int nodeSize = 0;
        for (Peptide peptide : peptides) {
            node = peptide.getGraphNode();
            centroidX += node.x();
            centroidY += node.y();
            centroidZ += node.z();
            nodeSize++;
        }
        centroidX /= nodeSize;
        centroidY /= nodeSize;
        centroidZ /= nodeSize;
        for (Peptide peptide : peptides) {
            node = peptide.getGraphNode();
            node.setX((float) (node.x() - centroidX));
            node.setY((float) (node.y() - centroidY));
            node.setZ((float) (node.z() - centroidZ));
        }

        //Measure
        for (Peptide peptide : peptides) {
            node = peptide.getGraphNode();
            sizeMin = Math.min(node.size(), sizeMin);
            sizeMax = Math.max(node.size(), sizeMax);
            xMin = Math.min(node.x(), xMin);
            xMax = Math.max(node.x(), xMax);
            yMin = Math.min(node.y(), yMin);
            yMax = Math.max(node.y(), yMax);
            zMin = Math.min(node.z(), zMin);
            zMax = Math.max(node.z(), zMax);
        }

        if (sizeMin != 0 && sizeMax != 0) {

            if (sizeMin == sizeMax) {
                sizeRatio = sizeMinimum / sizeMin;
            } else {
                sizeRatio = (sizeMaximum - sizeMinimum) / (sizeMax - sizeMin);
            }

            //Watch octree limit
            if (xMin * sizeRatio < -octreeLimit) {
                sizeRatio = Math.abs(octreeLimit / xMin);
            }
            if (xMax * sizeRatio > octreeLimit) {
                sizeRatio = Math.abs(octreeLimit / xMax);
            }
            if (yMin * sizeRatio < -octreeLimit) {
                sizeRatio = Math.abs(octreeLimit / yMin);
            }
            if (yMax * sizeRatio > octreeLimit) {
                sizeRatio = Math.abs(octreeLimit / yMax);
            }
            if (zMin * sizeRatio < -octreeLimit) {
                sizeRatio = Math.abs(octreeLimit / zMin);
            }
            if (zMax * sizeRatio > octreeLimit) {
                sizeRatio = Math.abs(octreeLimit / zMax);
            }

            averageSize = 0f;

            //Scale node size
            for (Peptide peptide : peptides) {
                node = peptide.getGraphNode();
                float size = (node.size() - sizeMin) * sizeRatio + sizeMinimum;
                node.setSize(size);
                node.setX(node.x() * sizeRatio);
                node.setY(node.y() * sizeRatio);
                node.setZ(node.z() * sizeRatio);
                averageSize += size;
            }
            averageSize /= nodeSize;
        }
        /*
         float weightMin = Float.POSITIVE_INFINITY;
         float weightMax = Float.NEGATIVE_INFINITY;
         float weightRatio = 0f;

         //Measure
         weightMaximum = averageSize * 0.8f;
         for (EdgeDraftGetter edge : container.getUnloader().getEdges()) {
         weightMin = Math.min(edge.getWeight(), weightMin);
         weightMax = Math.max(edge.getWeight(), weightMax);
         }
         if (weightMin == weightMax) {
         weightRatio = weightMinimum / weightMin;
         } else {
         weightRatio = Math.abs((weightMaximum - weightMinimum) / (weightMax - weightMin));
         }

         //Scale edge weight
         for (EdgeDraftGetter edge : container.getUnloader().getEdges()) {
         float weight = (edge.getWeight() - weightMin) * weightRatio + weightMinimum;
         assert !Float.isNaN(weight);
         edge.setWeight(weight);
         }*/
    }
}
