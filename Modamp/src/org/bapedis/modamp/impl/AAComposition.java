/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.Iterator;
import java.util.List;
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
public class AAComposition extends AbstractModamp {

    private ReduceAlphabet ra;
    
    public AAComposition(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initMD(List<PeptideAttribute> descriptorList) {
        if (attrModel != null) {
            ra = ReducedAlphabets.stdAminoAcids();
            Iterator<String> it = ra.getCount().keySet().iterator();
            String attrName;
            PeptideAttribute descriptor;
            while (it.hasNext()) {
                attrName = String.format("%s[%s]", ra.getName(), it.next());
                if (!attrModel.hasAttribute(attrName)) {
                    descriptor = attrModel.addAttribute(attrName, attrName, Double.class);
                } else{
                    descriptor = attrModel.getAttribute(attrName);
                }
                descriptorList.add(descriptor);
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
            peptide.setAttributeValue(attrModel.getAttribute(attrName), val);
        }
    }

    @Override
    public void endMD() {
        ra = null;
    }        

}
