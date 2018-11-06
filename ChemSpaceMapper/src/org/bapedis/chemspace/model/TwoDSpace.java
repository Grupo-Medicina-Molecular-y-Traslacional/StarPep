/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import java.awt.Dimension;
import org.bapedis.core.model.Peptide;
import javax.vecmath.Vector2f;
import org.bapedis.chemspace.util.VectorUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author loge
 */
public class TwoDSpace {

    protected final Peptide[] peptides;
    private final String[] axisLabels;
    private final float[][] coordinates;
    private int xAxis, yAxis;
    private Vector2f[] positions;
    private ChartPanel scatterPlot;
    private int width, height;

    public TwoDSpace(Peptide[] peptides, String[] axisLabels, float[][] coordinates) {
        this.peptides = peptides;
        this.axisLabels = axisLabels;
        this.coordinates = coordinates;
        this.xAxis = 0;
        this.yAxis = axisLabels.length > 1 ? 1 : 0;
        createVectorPositions();
    }

    private void createVectorPositions() {
        positions = new Vector2f[peptides.length];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Vector2f();
            positions[i].setX(coordinates[i][xAxis]);
            positions[i].setY(coordinates[i][yAxis]);
        }
        scatterPlot = createChartPanel();
        VectorUtil.normalize(positions);
    }

    public ChartPanel getScatterPlot() {
        return scatterPlot;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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
        scatterPlot = null;
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
        scatterPlot = null;
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

    public Vector2f[] getPositions() {
        if (positions == null) {
            createVectorPositions();
        }
        return positions;
    }

    private ChartPanel createChartPanel() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries serie = new XYSeries("SP");

        for (int i = 0; i < positions.length; i++) {
            serie.add(positions[i].getX(), positions[i].getY());
        }
        dataset.addSeries(serie);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "", // chart title
                axisLabels[xAxis], // domain axis label
                axisLabels[yAxis], // range axis label
                dataset, // data
                PlotOrientation.HORIZONTAL.VERTICAL, // orientation
                false, // include legend
                false, // tooltips?
                false // URLs?
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(width, height));
        chartPanel.setMinimumSize(new Dimension(width, height));

        return chartPanel;
    }

}
