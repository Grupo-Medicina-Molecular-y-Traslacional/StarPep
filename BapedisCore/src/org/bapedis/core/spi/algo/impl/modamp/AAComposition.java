/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import java.util.Iterator;
import java.util.Map;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.impl.modamp.scales.ReduceAlphabet;
import org.bapedis.core.spi.algo.impl.modamp.scales.ReducedAlphabets;

/**
 *
 * @author loge
 */
public class AAComposition extends AbstractModamp {

    public AAComposition(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            ReduceAlphabet ra = ReducedAlphabets.stdAminoAcids();
            Iterator<String> it = ra.getCount().keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (!attrModel.hasAttribute(key)) {
                    attrModel.addAttribute(key, key, Double.class);
                }
            }
        }
    }

    @Override
    public void compute(Peptide peptide) {
        Map<String, Double> aminoAcidComposition = MD.compositionReducedAlphabet(peptide.getSequence(), ReducedAlphabets.stdAminoAcids());
        Iterator<String> it = aminoAcidComposition.keySet().iterator();
        double val;
        while (it.hasNext()) {
            String key = it.next();
            val = aminoAcidComposition.get(key);
            peptide.setAttributeValue(attrModel.getAttribute(key), val);
        }
    }

}
