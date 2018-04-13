/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.layout.plugin;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;


public abstract class AbstractLayoutFactory implements AlgorithmFactory {

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.NDChemSpace;
    }
    
    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }    
}
