/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.Objects;

/**
 * Class that represents an attribute type for the peptide
 *
 * @author loge
 */
public class PeptideAttribute {

    protected String id;
    protected String displayName;
    protected Class<?> type;
    protected Double defaultValue;

    public PeptideAttribute(String id, String displayName, Class<?> type) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        defaultValue = null;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<?> getType() {
        return type;
    }  

    public Double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Double defaultValue) {
        this.defaultValue = defaultValue;
    }        

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PeptideAttribute) {
            PeptideAttribute attr = (PeptideAttribute) obj;
            return id.equals(attr.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public String toString() {
        return displayName; //To change body of generated methods, choose Tools | Templates.
    }

}
