/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.openide.util.NbBundle;

/**
 *
 * @author cicese
 */
public enum AlgorithmCategory {
    
    GraphLayout("graphLayout");
    private final String displayName;

    private AlgorithmCategory(String name) {
        displayName = NbBundle.getMessage(AlgorithmCategory.class, "AlgorithmCategory." + name);
    }

    public String getDisplayName() {
        return displayName;
    }            
    
}