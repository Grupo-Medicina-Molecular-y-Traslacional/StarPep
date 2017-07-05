/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.neo4j.graphdb.Label;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public enum MyLabels implements Label{
    All("all"), 
    Favorites("favorites"),
    Added("added"), 
    Tags("tags");
    
    private final String displayName;

    private MyLabels(String name) {
        displayName = NbBundle.getMessage(MyLabels.class, "MyLabels." + name);
    }

    public String getDisplayName() {
        return displayName;
    }      
}
