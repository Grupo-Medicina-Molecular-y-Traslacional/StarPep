/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.centrality;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.NetworkTag;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class ScaffoldExtractionFactory implements NetworkTag {

    @Override
    public String getCategory() {
        return NbBundle.getMessage(ScaffoldExtraction.class, "ScaffoldExtraction.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ScaffoldExtraction.class, "ScaffoldExtraction.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ScaffoldExtraction.class, "ScaffoldExtraction.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
