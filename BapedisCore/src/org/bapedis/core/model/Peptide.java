/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

/**
 *
 * @author loge
 */
public class Peptide extends ObjectAttributes {
    protected final String displayName;
    protected final String sequence;

    public Peptide(String displayName, String sequence) {
        this.displayName = displayName;
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }        

    public String getDisplayName() {
        return displayName!=null?displayName: sequence;
    }
        
}
