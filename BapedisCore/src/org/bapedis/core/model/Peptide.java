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

    public static final PeptideAttribute ID = new PeptideAttribute("id", "ID", String.class);
    public static final PeptideAttribute SEQ = new PeptideAttribute("seq", "Sequence", String.class);
    public static final PeptideAttribute LENGHT = new PeptideAttribute("length", "Length", Integer.class);
    protected HashMap<PeptideAttribute, Object> attrsValue;

    public Peptide() {
        attrsValue = new LinkedHashMap<>();
    }

    public String getId() {
        return (String) attrsValue.get(ID);
    }

    public String getSequence() {
        return (String) attrsValue.get(SEQ);
    }

    public int getLength() {
        return (int) attrsValue.get(LENGHT);
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
    
    @Override
    public String toString() {
        return getSequence();
    }

}
