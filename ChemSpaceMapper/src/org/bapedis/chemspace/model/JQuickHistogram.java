/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.bapedis.chemspace.model;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.TreeMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Mathieu Bastian 
 * Modified by Longendri Aguilera Mendoza to use a
 * TreeMap instead of ArrayList
 */
public class JQuickHistogram {

    protected int constraintHeight = 0;
    protected int constraintWidth = 0;
    protected final boolean inclusive = true;
    protected float sum;
    //Data
    protected final TreeMap<String, Integer> data;
    protected DecimalFormat df;
    protected float minValue;
    protected float maxValue;

    public JQuickHistogram() {
        data = new TreeMap<>();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.0", symbols);
        initValues();
    }

    private void initValues() {
        minValue = Float.MAX_VALUE;
        maxValue = Float.NEGATIVE_INFINITY;
        sum = 0;
    }

    public void clear() {
        data.clear();
        initValues();
    }

    public void addData(float data) {
        if (data < 0 || data > 1) {
            throw new IllegalArgumentException("Invalid data value for histogram. It should be in [0,1]");
        }
        String key = df.format(Math.floor(data * 10) / 10);
        int previousCount = 0;
        if (this.data.containsKey(key)) {
            previousCount = this.data.get(key);
        }
        int newCount = previousCount + 1;
        this.data.put(key, newCount);
        minValue = Math.min(minValue, data);
        maxValue = Math.max(maxValue, data);
        sum += data;
    }      

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }        
    
    public float getAverage(){
        return sum / countValues();
    }

    public void setConstraintHeight(int constraintHeight) {
        this.constraintHeight = constraintHeight;

    }

    public void setConstraintWidth(int constraintWidth) {
        this.constraintWidth = constraintWidth;
    }

    public int countValues() {
        int count = 0;
        for (Integer c : data.values()) {
            count += c;
        }
        return count;
    }
    
    public int countValues(float cuttof){
        int count = 0;        
        for(String key: data.keySet()){
            if (Float.parseFloat(key) >= cuttof){
                count += data.get(key);
            }
        }
        return count;        
    }

    private String getChartColumnKey(String key) {
        switch (key) {
            case "0.0":
                return "[0.0-0.1)";
            case "0.1":
                return "[0.1-0.2)";
            case "0.2":
                return "[0.2-0.3)";
            case "0.3":
                return "[0.3-0.4)";
            case "0.4":
                return "[0.4-0.5)";
            case "0.5":
                return "[0.5-0.6)";
            case "0.6":
                return "[0.6-0.7)";
            case "0.7":
                return "[0.7-0.8)";
            case "0.8":
                return "[0.8-0.9)";
            case "0.9":
                return "[0.9-1.0)";
        }
        return key;
    }

    public ChartPanel createChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String serie = "serie";
        for (String key : data.keySet()) {
            dataset.addValue(data.get(key), serie, getChartColumnKey(key));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "", // chart title
                "", // domain axis label
                "Frequency", // range axis label
                dataset, // data
                PlotOrientation.HORIZONTAL.VERTICAL, // orientation
                false, // include legend
                false, // tooltips?
                false // URLs?
        );

        ChartPanel chartPanel = new ChartPanel(chart);

        int currentHeight = (constraintHeight > 0 ? constraintHeight : chartPanel.getHeight());
        int currentWidth = (constraintWidth > 0 ? constraintWidth : chartPanel.getWidth());
        chartPanel.setPreferredSize(new Dimension(currentWidth, currentHeight));
        chartPanel.setMinimumSize(new Dimension(currentWidth, currentHeight));

        CategoryAxis axis = chart.getCategoryPlot().getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        return chartPanel;
    }

}
