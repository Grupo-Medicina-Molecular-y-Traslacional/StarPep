/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo;

import org.bapedis.core.model.AlgorithmCategory;

/**
 *
 * @author loge
 */
public interface AlgorithmFactory {
    
     /**
     * The category of the algorithm's provided by this factory.
     * @return  the category of the algorithm
     */
    public AlgorithmCategory getCategory();
    
     /**
     * The name of the behaviour of the algorithm's provided by this factory.
     * @return  the display name of the algorithm
     */
    public String getName();
    
    /**
     * The description of the algorithm purpose.
     * @return  a description snippet for the algorithm
     */
    public String getDescription();

    /**
     * User interface attributes (name, description, icon...) for all algorithms
     * built by this factory.
     * @return a <code>LayoutUI</code> instance
     */
    public AlgorithmSetupUI getSetupUI();

    /**
     * Builds an instance of the algorithm.
     * @return  a new <code>Layout</code> instance
     */
    public Algorithm createAlgorithm();
    
    /**
     * An appraisal of quality for this algorithm. The rank must be between 1 and
     * 5. The rank will be displayed tousers to help them to choose a suitable
     * algorithm. Return -1 if you don't want to display a rank.
     * @return an integer between 1 and 5 or -1 if you don't want to show a rank
     */
    default public int getQualityRank(){ return -1;}

    /**
     * An appraisal of speed for this algorithm. The rank must be between 1 and
     * 5. The rank will be displayed tousers to help them to choose a suitable
     * algorithm. Return -1 if you don't want to display a rank.
     * @return an integer between 1 and 5 or -1 if you don't want to show a rank
     */
    default public int getSpeedRank() {return -1;}
    
}
