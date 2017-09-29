/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.beans.PropertyChangeListener;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public interface SimilarityMeasure {

    String CHANGED_THRESHOLD_VALUE = "threshold_value";
    String CHANGED_SIMILARITY_VALUES = "similarity_values";

    JQuickHistogram getHistogram();
    
    double getThreshold();

    void setThreshold(double value);

    double computeSimilarity(Peptide peptide1, Peptide peptide2);
    
    void addPropertyChangeListener(PropertyChangeListener listener);
    
    void removePropertyChangeListener(PropertyChangeListener listener);
}
