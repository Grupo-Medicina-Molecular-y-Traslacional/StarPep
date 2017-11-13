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
    //Entropy cut off labels: Very weak, Weak and Moderate
    public static final int[] ENTROPY_CUTOFF_REFS = new int[]{1, 10};
    //Tanimoto cut off labels: Strong and Moderate    
    public static final int[] TANIMOTO_CUTOFF_REFS = new int[]{90, 98};

    private boolean removeUseless, removeRedundant;
    private int entropyCutoff, tanimotoCutoff;

    public FeatureSelectionModel() {
        entropyCutoff = ENTROPY_CUTOFF_REFS[0];
        tanimotoCutoff = TANIMOTO_CUTOFF_REFS[1];
        removeUseless = true;
        removeRedundant = true;
    }

    public boolean isRemoveUseless() {
        return removeUseless;
    }

    public void setRemoveUseless(boolean removeUseless) {
        this.removeUseless = removeUseless;
    }

    public boolean isRemoveRedundant() {
        return removeRedundant;
    }

    public void setRemoveRedundant(boolean removeRedundant) {
        this.removeRedundant = removeRedundant;
    }

    public int getEntropyCutoff() {
        return entropyCutoff;
    }

    public void setEntropyCutoff(int entropyCutoff) {
        this.entropyCutoff = entropyCutoff;
    }

    public int getTanimotoCutoff() {
        return tanimotoCutoff;
    }

    public void setTanimotoCutoff(int tanimotoCutoff) {
        this.tanimotoCutoff = tanimotoCutoff;
    }    

    public Set<String> getDescriptorKeys() {
        return descriptorKeys;
    }

    public void setDescriptorKeys(Set<String> descriptorKeys) {
        this.descriptorKeys = descriptorKeys;
    }
      
}
