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
public class PeptideAttribute implements Cloneable {

    public static PeptideAttribute CLUSTER_ATTR = new PeptideAttribute("cluster", "Cluster", Integer.class, true, -1);
    public static PeptideAttribute RANK_ATTR = new PeptideAttribute("rank", "Rank", Integer.class, true, -1);
    public static PeptideAttribute SCORE_ATTR = new PeptideAttribute("score", "Score", Double.class, true, -1.0);

    protected final String id;
    protected final String displayName;
    protected final Class<?> type;
    protected final boolean visible;
    protected final Object defaultValue;

    public PeptideAttribute(String id, String displayName, Class<?> type, boolean visible) {
        this(id, displayName, type, visible, null);
    }

    public PeptideAttribute(String id, String displayName, Class<?> type, boolean visible, Object defaultValue) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.defaultValue = defaultValue;
        this.visible = visible;
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

    public boolean isVisible() {
        return visible;
    }

    public Object getDefaultValue() {
        return defaultValue;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        PeptideAttribute copyObject = (PeptideAttribute) super.clone();
        return copyObject;
    }

}
