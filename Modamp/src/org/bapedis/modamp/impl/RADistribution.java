/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.impl.AbstractMD;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.ReduceAlphabet;
import org.bapedis.modamp.scales.ReducedAlphabets;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author beltran, loge
 */
public class RADistribution extends AbstractMD {

    protected boolean hydT, vw, pol, polz, chrg, ss, sa;
    private final List<AlgorithmProperty> properties;

    public RADistribution(AlgorithmFactory factory) {
        super(factory);
        hydT = true;
        vw = true;
        pol = true;
        polz = true;
        chrg = true;
        ss = true;
        sa = true;
        properties = new LinkedList<>();
        populateProperties();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.hydT.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.hydT.desc"), "isHydT", "setHydT"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.vw.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.vw.desc"), "isVw", "setVw"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.pol.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.pol.desc"), "isPol", "setPol"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.polz.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.polz.desc"), "isPolz", "setPolz"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.chrg.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.chrg.desc"), "isChrg", "setChrg"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.ss.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.ss.desc"), "isSs", "setSs"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.sa.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.sa.desc"), "isSa", "setSa"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean isHydT() {
        return hydT;
    }

    public void setHydT(Boolean hydT) {
        this.hydT = hydT;
    }

    public boolean isVw() {
        return vw;
    }

    public void setVw(Boolean vw) {
        this.vw = vw;
    }

    public boolean isPol() {
        return pol;
    }

    public void setPol(Boolean pol) {
        this.pol = pol;
    }

    public boolean isPolz() {
        return polz;
    }

    public void setPolz(Boolean polz) {
        this.polz = polz;
    }

    public boolean isChrg() {
        return chrg;
    }

    public void setChrg(Boolean chrg) {
        this.chrg = chrg;
    }

    public boolean isSs() {
        return ss;
    }

    public void setSs(Boolean ss) {
        this.ss = ss;
    }

    public boolean isSa() {
        return sa;
    }

    public void setSa(Boolean sa) {
        this.sa = sa;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

    private void initAlphabets(List<ReduceAlphabet> alphabets){
        if (hydT) {
            alphabets.add(ReducedAlphabets.ra_Hydrophobicity_Tomii());
        }
        if (vw) {
            alphabets.add(ReducedAlphabets.ra_NormVW_Tomii());
        }
        if (pol) {
            alphabets.add(ReducedAlphabets.ra_Polarity_Tomii());
        }
        if (polz) {
            alphabets.add(ReducedAlphabets.ra_Polarizability_Tomii());
        }
        if (chrg) {
            alphabets.add(ReducedAlphabets.ra_Charge_Tomii());
        }
        if (ss) {
            alphabets.add(ReducedAlphabets.ra_secondaryStructure_Tomii());
        }
        if (sa) {
            alphabets.add(ReducedAlphabets.ra_solventAccessibility_Tomii());
        }    
    }
    
    @Override
    protected void compute(Peptide peptide) {
        List<ReduceAlphabet> alphabets = new LinkedList<>();
        initAlphabets(alphabets);
        for (ReduceAlphabet ra : alphabets) {
            for (int percent = 0; percent <= 100; percent += 25) {
                Map<String, Double> aminoAcidComposition = MD.distributionReducedAlphabet(peptide.getSequence(), ra, percent);
                Iterator<String> it = aminoAcidComposition.keySet().iterator();
                double val;
                String attrName, key;
                PeptideAttribute attr;
                while (it.hasNext()) {
                    key = it.next();
                    val = aminoAcidComposition.get(key);
                    attrName = String.format("D_%s_%d[%s]", ra.getName(), percent, key);
                    attr = getOrAddAttribute(attrName, attrName, Double.class, null);
                    peptide.setAttributeValue(attr, val);
                }
            }
        }
    }

}
