/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author loge
 */
public class Peptide {
    protected final String displayName;
    protected final String sequence;
    protected HashMap<PeptideAttribute, Object> attrsValue;

    public Peptide(String displayName, String sequence) {
        this.displayName = displayName;
        this.sequence = sequence;
        attrsValue = new LinkedHashMap<>();
    }

    public String getSequence() {
        return sequence;
    }        

    public String getDisplayName() {
        return displayName!=null?displayName: sequence;
    }

    public void setAttributeValue(PeptideAttribute attr, Object value) {
        attrsValue.put(attr, value);
    }

    public Object getAttributeValue(PeptideAttribute attr) {
        return attrsValue.get(attr);
    }

    public Set<PeptideAttribute> getAttributes() {
        return attrsValue.keySet();
    }
        
}
