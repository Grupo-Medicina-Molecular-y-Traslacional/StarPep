/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.algo.impl.AbstractMD;
import java.util.Iterator;
import java.util.Map;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.ReduceAlphabet;
import org.bapedis.modamp.scales.ReducedAlphabets;

/**
 *
 * @author beltran, loge
 */
public class AAComposition extends AbstractMD {

    public AAComposition(AlgorithmFactory factory) {
        super(factory);
    }    
    
    @Override
    protected void compute(Peptide peptide) {
        ReduceAlphabet ra = ReducedAlphabets.stdAminoAcids();
        Map<String, Double> aminoAcidComposition = MD.compositionReducedAlphabet(peptide.getSequence(), ra);
        Iterator<String> it = aminoAcidComposition.keySet().iterator();
        String attrName, key;
        double val;
        PeptideAttribute attr;
        while (it.hasNext()) {
            key = it.next();
            val = aminoAcidComposition.get(key);
            if (val > 0) {
                attrName = String.format("%s[%s]", ra.getName(), key);
                attr = getOrAddAttribute(attrName, attrName, Double.class, 0.);
                peptide.setAttributeValue(attr, val);
            }
        }
    }


}
