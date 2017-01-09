/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.Objects;

/**
 *
 * @author loge
 */
public class Attribute {
    protected String id;
    protected String displayName;
    protected Class<?> type;
    protected boolean visible;
    
    public Attribute(String id, String displayName, Class<?> type, boolean visible) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.visible = visible;
    }    

    public Attribute(String id, String displayName, Class<?> type) {
        this(id, displayName, type,true);
    }
    
    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Attribute){
           Attribute column = (Attribute)obj;
           return id.equals(column.id);
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
