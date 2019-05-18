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
    private int xAxis, yAxis, zAxis;
    private Vector3f[] positions;

    public CoordinateSpace(Peptide[] peptides, String[] axisLabels, float[][] coordinates) {
        this.peptides = peptides;
        this.axisLabels = axisLabels;
        this.coordinates = coordinates;
        this.xAxis = 0;
        this.yAxis = axisLabels.length > 1 ? 1 : xAxis;
        this.zAxis = axisLabels.length > 2 ? 2 : yAxis;
        createVectorPositions();
    }

    private void createVectorPositions() {
        positions = new Vector3f[peptides.length];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Vector3f();
            positions[i].setX(coordinates[i][xAxis]);
            positions[i].setY(coordinates[i][yAxis]);
            positions[i].setZ(coordinates[i][zAxis]);
        }
        VectorUtil.normalize(positions);
    }


    public int getxAxis() {
        return xAxis;
    }

    public void setxAxis(int xAxis) {
        if (xAxis < 0 || xAxis >= coordinates[0].length) {//Invalid value
            throw new IllegalArgumentException("Invalid value for xAxis: " + xAxis);
        }
        this.xAxis = xAxis;
        positions = null;
    }

    public int getyAxis() {
        return yAxis;
    }

    public void setyAxis(int yAxis) {
        if (yAxis < 0 || yAxis >= coordinates[0].length) {//Invalid value
            throw new IllegalArgumentException("Invalid value for yAxis: " + yAxis);
        }
        this.yAxis = yAxis;
        positions = null;
    }
    
    public int getzAxis() {
        return zAxis;
    }

    public void setzAxis(int zAxis) {
        if (zAxis < 0 || zAxis >= coordinates[0].length) {//Invalid value
            throw new IllegalArgumentException("Invalid value for zAxis: " + zAxis);
        }
        this.zAxis = zAxis;
        positions = null;
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
        if (positions == null) {
            createVectorPositions();
        }
        return positions;
    }

//    private ChartPanel createChartPanel() {
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        XYSeries serie = new XYSeries("SP");
//
//        for (int i = 0; i < positions.length; i++) {
//            serie.add(positions[i].getX(), positions[i].getY());
//        }
//        dataset.addSeries(serie);
//
//        JFreeChart chart = ChartFactory.createScatterPlot(
//                "", // chart title
//                axisLabels[xAxis], // domain axis label
//                axisLabels[yAxis], // range axis label
//                dataset, // data
//                PlotOrientation.HORIZONTAL.VERTICAL, // orientation
//                false, // include legend
//                false, // tooltips?
//                false // URLs?
//        );
//
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new Dimension(width, height));
//        chartPanel.setMinimumSize(new Dimension(width, height));
//
//        return chartPanel;
//    }

}
