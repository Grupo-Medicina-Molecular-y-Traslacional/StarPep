/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Mathieu Bastian
 */
public class JQuickHistogramPanel extends JPanel {

    private final Color fillColor = new Color(0xCFD2D3);
    private final Color fillInRangeColor = new Color(0x3B4042);
    private final JQuickHistogram histogram;
    private int currentHeight = 0;
    private int currentWidth = 0;
    private final Double[] keys;

    public JQuickHistogramPanel(JQuickHistogram histogram, double stepSize) {
        this.histogram = histogram;
        int dataSize = (int) (1 / stepSize)+1;
        this.keys = new Double[dataSize];
        this.keys[0] = new Double(0);
        for (int i= 1; i<keys.length; i++) {
            this.keys[i] = Math.floor((this.keys[i-1] + stepSize)*100)/100;
        }
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setCurrentDimension();
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(0, currentHeight);
        g2d.scale(1, -1);
        drawHisto(g2d);
        g2d.dispose();
    }

    private void drawHisto(Graphics2D g2d) {

        if (histogram.minRange == null || histogram.maxRange == null) {
            return;
        }

//        if (dataSize < currentWidth) {
        int rectWidth = (int) (currentWidth / keys.length);
        int leftover = (int) (currentWidth - rectWidth * keys.length);
        int xPosition = 0;
        for (Double d: keys) {
            int value = (histogram.data.containsKey(d))? histogram.data.get(d): 0;
            int rectangleWidth = rectWidth + (leftover > 0 ? 1 : 0);
            leftover--;
            int rectangleHeight = (int) ((value - histogram.minValue) / (histogram.maxValue - histogram.minValue) * currentHeight);
            if (d >= histogram.minRange && d <= histogram.maxRange) {
                g2d.setColor(fillInRangeColor);
            } else {
                g2d.setColor(fillColor);
            }
            g2d.fillRect(xPosition, 0, rectangleWidth, rectangleHeight);

            xPosition += rectangleWidth;
        }
//        } 
//        else {
//            int xPosition = 0;
//            int sizeOfSmallSublists = dataSize / currentWidth;
//            int sizeOfLargeSublists = sizeOfSmallSublists + 1;
//            int numberOfLargeSublists = dataSize % currentWidth;
//            int numberOfSmallSublists = currentWidth - numberOfLargeSublists;
//
//            int numberOfElementsHandled = 0;
//            for (int i = 0; i < currentWidth; i++) {
//                int size = i < numberOfSmallSublists ? sizeOfSmallSublists : sizeOfLargeSublists;
//                double average = 0.0;
//                for (int j = 0; j < size; j++) {
//                    Double d = histogram.data.get(numberOfElementsHandled++);
//                    average += d;
//                }
//                average /= size;
//                int rectangleHeight = (int) ((average - histogram.minValue) / (histogram.maxValue - histogram.minValue) * currentHeight);
//
//                if (average >= histogram.minRange && average <= histogram.maxRange) {
//                    g2d.setColor(fillInRangeColor);
//                } else {
//                    g2d.setColor(fillColor);
//                }
//                g2d.fillRect(xPosition, 0, 1, rectangleHeight);
//                xPosition++;
//            }
//        }
    }

    private void setCurrentDimension() {
        currentHeight = (histogram.constraintHeight > 0 ? histogram.constraintHeight : getHeight());
        currentWidth = (histogram.constraintWidth > 0 ? histogram.constraintWidth : getWidth());
        setPreferredSize(new Dimension(currentWidth, currentHeight));
        setMinimumSize(new Dimension(currentWidth, currentHeight));
    }
}
