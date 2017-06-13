/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo;

import org.bapedis.core.model.AlgorithmProperty;

/**
 *
 * @author loge
 */
public interface Algorithm {
     /**
     * initAlgo() is called to initialize the algorithm (prepare to run).
     */
    public void initAlgo();
    
    /**
     * Run a step in the algorithm, should be called only if canAlgo() returns
     * true.
     */
    public void goAlgo();
    
    /**
     * Tests if the algorithm can run, called before each pass.
     * @return              <code>true</code> if the algorithm can run, <code>
     *                      false</code> otherwise
     */
    public boolean canAlgo();

    /**
     * Called when the algorithm is finished (canAlgo() returns false).
     */
    public void endAlgo();


    /**
     * The properties for this algorithm.
     * @return              the algorithm properties
     * @throws NoSuchMethodException 
     */
    public AlgorithmProperty[] getProperties();

    /**
     * Resets the properties values to the default values.
     */
    public void resetPropertiesValues();

    /**
     * The reference to the Factory that instanciated this algorithm.
     * @return              the reference to the factory that create this instance
     */
    public AlgorithmFactory getFactory();    
    
}
