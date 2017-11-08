/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.Set;

/**
 *
 * @author loge
 */
public class FeatureSelectionModel {
    private Set<String> descriptorKeys;    
    public static final int[] ENTROPY_THRESHOLD = new int[]{10, 20, 30};
    public static final int[] CORRELATION_THRESHOLD = new int[]{10, 20, 30};

    private int entropyThreshold, correlationThreshold;

    public FeatureSelectionModel() {
        entropyThreshold = ENTROPY_THRESHOLD[0];
        correlationThreshold = CORRELATION_THRESHOLD[0];
    }
    
    public int getEntropyThreshold() {
        return entropyThreshold;
    }

    public void setEntropyThreshold(int entropyThreshold) {
        this.entropyThreshold = entropyThreshold;
    }

    public int getCorrelationThreshold() {
        return correlationThreshold;
    }

    public void setCorrelationThreshold(int correlationThreshold) {
        this.correlationThreshold = correlationThreshold;
    }

    public Set<String> getDescriptorKeys() {
        return descriptorKeys;
    }

    public void setDescriptorKeys(Set<String> descriptorKeys) {
        this.descriptorKeys = descriptorKeys;
    }
      
}
