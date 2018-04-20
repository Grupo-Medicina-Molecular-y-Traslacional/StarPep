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
public class JitterModel {

    public static int STEPS = 10;
    private int level;
    private float[] minDistances;

    public JitterModel() {
        level = -1;
        minDistances = null;
    }
    
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level < 1 || level > JitterModel.STEPS) {
            throw new IllegalArgumentException("Jitter Level should be between 1 and " + STEPS);
        }        
        this.level = level;
    }

    public float[] getMinDistances() {
        return minDistances;
    }

    public void setMinDistances(float[] minDistances) {
        this.minDistances = minDistances;
    }
    
    public float getMinDistance(){
        return minDistances[level];
    }
        
}
