/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.Iterator;
import java.util.Map;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.ReduceAlphabet;
import org.bapedis.modamp.scales.ReducedAlphabets;

/**
 *
 * @author beltran, loge
 */
public class AAComposition extends AbstractModamp {

    private ReduceAlphabet ra;

    public AAComposition(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    protected void initMD() {
        ra = ReducedAlphabets.stdAminoAcids();
        Iterator<String> it = ra.getCount().keySet().iterator();
        String attrName;
        while (it.hasNext()) {
            attrName = String.format("%s[%s]", ra.getName(), it.next());
            if (!hasAttribute(attrName)) {
                addAttribute(attrName, attrName, Double.class);
            }
        }
    }

    @Override
    protected void compute(Peptide peptide) {
        Map<String, Double> aminoAcidComposition = MD.compositionReducedAlphabet(peptide.getSequence(), ra);
        Iterator<String> it = aminoAcidComposition.keySet().iterator();
        double val;
        String attrName, key;
        while (it.hasNext()) {
            key = it.next();
            attrName = String.format("%s[%s]", ra.getName(), key);
            val = aminoAcidComposition.get(key);
            peptide.setAttributeValue(getAttribute(attrName), val);
        }
    }

    @Override
    public void endMD() {
        ra = null;
    }

}
