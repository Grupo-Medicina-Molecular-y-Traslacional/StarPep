/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo;

/**
 *
 * @author loge
 */
public interface AlgorithmFactory {
     /**
     * The name of the behaviour of the algorithm's provided by this factory.
     * @return  the display name of the algorithm
     */
    public String getName();

    /**
     * User interface attributes (name, description, icon...) for all algorithms
     * built by this factory.
     * @return a <code>LayoutUI</code> instance
     */
    public AlgorithmSetupUI getSetupUI();

    /**
     * Builds an instance of the Layout.
     * @return  a new <code>Layout</code> instance
     */
    public Algorithm createAlgorithm();
}
