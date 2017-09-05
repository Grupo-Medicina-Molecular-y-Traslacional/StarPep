/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.impl.modamp.scales.ReduceAlphabet;
import org.bapedis.core.spi.algo.impl.modamp.scales.ReducedAlphabets;

/**
 *
 * @author loge
 */
public class DipeptideComposition extends AbstractModamp {

    private ReduceAlphabet ra;

    public DipeptideComposition(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            ra = ReducedAlphabets.stdAminoAcids();
            Set<String> keySet = ra.getCount().keySet();
            String attrName;
            for (String key1 : keySet) {
                for (String key2 : keySet) {
                    attrName = String.format("[%s][%s]", key1, key2);
                    if (!attrModel.hasAttribute(attrName)) {
                        attrModel.addAttribute(attrName, attrName, Double.class);
                    }
                }
            }
        }

    }

    @Override
    public void compute(Peptide peptide) {
        Map<String, Double> aminoAcidComposition = MD.dipeptideComposition(peptide.getSequence(), ra);
        Iterator<String> it = aminoAcidComposition.keySet().iterator();
        String key;
        double val;
        while (it.hasNext()) {
            key = it.next();
            val = aminoAcidComposition.get(key);
            peptide.setAttributeValue(attrModel.getAttribute(key), val);
        }
    }
    
    @Override
    public void endAlgo() {
        super.endAlgo(); 
        ra = null;
    }      

}
