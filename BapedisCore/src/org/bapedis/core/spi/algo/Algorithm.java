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
public interface Algorithm extends Runnable {    
    
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
