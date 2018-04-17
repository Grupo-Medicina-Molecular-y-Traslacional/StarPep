/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

/**
 *
 * @author loge
 */
public class CompressedModel {
    public static final int COMMUNITY_INDEX=0;
    public static final int DEFAULT_MAX_SUPER_NODES=50;
    
    private int strategyIndex;
    private int maxSuperNodes;

    public CompressedModel() {
        strategyIndex = COMMUNITY_INDEX;
        maxSuperNodes = DEFAULT_MAX_SUPER_NODES;
    }

    public int getStrategyIndex() {
        return strategyIndex;
    }

    public void setStrategyIndex(int strategyIndex) {
        this.strategyIndex = strategyIndex;
    }

    public int getMaxSuperNodes() {
        return maxSuperNodes;
    }

    public void setMaxSuperNodes(int maxSuperNodes) {
        this.maxSuperNodes = maxSuperNodes;
    }
        
}
