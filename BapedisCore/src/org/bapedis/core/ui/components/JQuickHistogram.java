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
package org.bapedis.core.ui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Mathieu Bastian Modified by Longendri Aguilera Mendoza to use a
 * TreeMap instead of ArrayList
 */
public class JQuickHistogram {

    protected int constraintHeight = 0;
    protected int constraintWidth = 0;
    protected final boolean inclusive = true;
    //Data
    protected final TreeMap<Double, Integer> data;
    protected Double minValue;
    protected Double maxValue;
    protected Double minRange;
    protected Double maxRange;

    public JQuickHistogram() {
        data = new TreeMap<>();
    }

    public void clear() {
        data.clear();
        minValue = Double.MAX_VALUE;
        maxValue = Double.NEGATIVE_INFINITY;
        minRange = 0.;
        maxRange = 1.;        
    }

    public void addData(Double data) {
        if (data < 0 || data > 1) {
            throw new IllegalArgumentException("Invalid data value for histogram. It should be in [0,1]");
        }
        Double key = Math.floor(data * 100) / 100.0; // 2 decimals
        int previousCount = 0;
        if (this.data.containsKey(key)) {
            previousCount = this.data.get(key);
        }
        int newCount = previousCount + 1;
        this.data.put(key, newCount);
        minValue = Math.min(minValue, newCount);
        maxValue = Math.max(maxValue, newCount);
    }

    public void setLowerBound(Double lowerBound) {
        this.minRange = lowerBound;
    }

    public void setUpperBound(Double upperBound) {
        this.maxRange = upperBound;
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

    public int countInRange() {
        int count = 0;
        double d;
        for (Map.Entry<Double, Integer> entry : data.entrySet()) {
            d = entry.getKey();
            if ((inclusive && d >= minRange && d <= maxRange) || (!inclusive && d > minRange && d < maxRange)) {
                count += entry.getValue();
            }
        }
        return count;
    }

    public double getAverage() {
        double sum = 0;
        int count = 0;
        for (Map.Entry<Double, Integer> entry : data.entrySet()) {
            sum += entry.getKey() * entry.getValue();
            count += entry.getValue();
        }
        return sum / count;
    }

    public double getAverageInRange() {
        double sum = 0;
        int count = 0;
        double d;
        for (Map.Entry<Double, Integer> entry : data.entrySet()) {
            d = entry.getKey();
            if ((inclusive && d >= minRange && d <= maxRange) || (!inclusive && d > minRange && d < maxRange)) {
                sum += entry.getKey() * entry.getValue();
                count += entry.getValue();
            }
        }
        return sum / count;
    }

    public double getMedian() {
        int median = (countValues() + 1) / 2;
        double d;
        for (Map.Entry<Double, Integer> entry : data.entrySet()) {
            d = entry.getKey();
            median -= entry.getValue();
            if (median <= 0) {
                return d;
            }
        }
        return -1.;
    }

    public double getMedianInRange() {
        int median = (countInRange() + 1) / 2;
        double d;
        for (Map.Entry<Double, Integer> entry : data.entrySet()) {
            d = entry.getKey();
            if ((inclusive && d >= minRange && d <= maxRange) || (!inclusive && d > minRange && d < maxRange)) {
                median -= entry.getValue();
                if (median <= 0) {
                    return d;
                }
            }
        }
        return -1.;
    }
}
