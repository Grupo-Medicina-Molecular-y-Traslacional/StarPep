/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.model;

import java.util.Set;

/**
 *
 * @author loge
 */
public class MDOptionModel {

    public static final int AVAILABLE_MD = 0;
    public static final int NEW_MD = 1;

    protected static final String[] normalization = new String[]{"Z-score", "Min-max"};
    protected final Set<String> descriptorKeys;
    protected int optionIndex;

    public MDOptionModel(Set<String> descriptorKeys) {
        this.descriptorKeys = descriptorKeys;
        optionIndex = 1;        
    }

    public Set<String> getDescriptorKeys() {
        return descriptorKeys;
    }        

    public int getOptionIndex() {
        return optionIndex;
    }

    public void setOptionIndex(int optionIndex) {
        if (optionIndex != AVAILABLE_MD && optionIndex != NEW_MD)
            throw new IllegalStateException("Unknown value for option index: " + optionIndex);
        this.optionIndex = optionIndex;
    }    

}
