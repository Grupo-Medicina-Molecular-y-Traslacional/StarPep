/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.impl.AllDescriptors;
import org.bapedis.modamp.impl.AllDescriptorsFactory;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class ChemicalSpaceNetwork extends SimilarityNetworkAlgo {

    protected final AllDescriptors descriptorAlgo;
    protected int buttonGroupIndex;

    public ChemicalSpaceNetwork(AlgorithmFactory factory) {
        super(factory);
        AllDescriptorsFactory descriptorFactory = Lookup.getDefault().lookup(AllDescriptorsFactory.class);
        descriptorAlgo = (AllDescriptors)descriptorFactory.createAlgorithm();
        buttonGroupIndex = 0;
    }

    public AllDescriptors getDescriptorAlgorithm() {
        return descriptorAlgo;
    }

    public int getButtonGroupIndex() {
        return buttonGroupIndex;
    }

    public void setButtonGroupIndex(int buttonGroupIndex) {
        this.buttonGroupIndex = buttonGroupIndex;
    }
        

    @Override
    public double computeSimilarity(Peptide peptide1, Peptide peptide2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
