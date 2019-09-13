/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import org.bapedis.core.model.Peptide;
import javax.vecmath.Vector3f;
import org.bapedis.chemspace.util.VectorUtil;

/**
 *
 * @author loge
 */
public class CoordinateSpace {

    protected final Peptide[] peptides;
    private final String[] axisLabels;
    private final float[][] coordinates;
    private final double[] explainedVar;
    private int xAxis, yAxis, zAxis;
    private final Vector3f[] positions;

    public CoordinateSpace(Peptide[] peptides, String[] axisLabels, float[][] coordinates, double[] explainedVar) {
        this.peptides = peptides;
        this.axisLabels = axisLabels;
        this.coordinates = coordinates;
        this.explainedVar = explainedVar;
        this.xAxis = 0;
        this.yAxis = axisLabels.length > 1 ? 1 : xAxis;
        this.zAxis = axisLabels.length > 2 ? 2 : yAxis;
        positions = new Vector3f[peptides.length];
        for (int i = 0; i < positions.length; i++){
            positions[i] = new Vector3f();
            positions[i].x=coordinates[i][xAxis];
            positions[i].y=coordinates[i][yAxis];
            positions[i].z=coordinates[i][zAxis];            
        }
        VectorUtil.normalize(positions); 
    }

    public void updatePositions(int xAxis, int yAxis, int zAxis) {  
        if (xAxis < 0 || xAxis >= coordinates[0].length) {//Invalid value
            throw new IllegalArgumentException("Invalid value for xAxis: " + xAxis);
        }
        this.xAxis = xAxis;
   
        if (yAxis < 0 || yAxis >= coordinates[0].length) {//Invalid value
            throw new IllegalArgumentException("Invalid value for yAxis: " + yAxis);
        }
        this.yAxis = yAxis;
        
        if (zAxis < 0 || zAxis >= coordinates[0].length) {//Invalid value
            throw new IllegalArgumentException("Invalid value for zAxis: " + zAxis);
        }
        this.zAxis = zAxis;
        
        for (int i = 0; i < positions.length; i++) {            
            positions[i].x=coordinates[i][xAxis];
            positions[i].y=coordinates[i][yAxis];
            positions[i].z=coordinates[i][zAxis];
        }
        VectorUtil.normalize(positions);        
    }


    public int getxAxis() {
        return xAxis;
    }

    public int getyAxis() {
        return yAxis;
    }
    
    public int getzAxis() {
        return zAxis;
    }

    public Peptide[] getPeptides() {
        return peptides;
    }

    public String[] getAxisLabels() {
        return axisLabels;
    }

    public float[][] getCoordinates() {
        return coordinates;
    }

    public Vector3f[] getPositions() {
        return positions;
    }

    public double[] getExplainedVariance() {
        return explainedVar;
    }

    
}
