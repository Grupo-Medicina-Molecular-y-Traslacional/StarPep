/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.ReduceAlphabet;
import org.bapedis.modamp.scales.ReducedAlphabets;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class DipeptideComposition extends AbstractModamp {

    protected boolean hyR, b50, cs, hydT, vw, pol, polz, chrg, ss, sa;
    private final List<AlgorithmProperty> properties;
    private final List<ReduceAlphabet> alphabets;

    public DipeptideComposition(AlgorithmFactory factory) {
        super(factory);
        hyR = true;
        b50 = true;
        cs = true;
        hydT = true;
        vw = true;
        pol = true;
        polz = true;
        chrg = true;
        ss = true;
        sa = true;
        properties = new LinkedList<>();
        populateProperties();
        alphabets = new LinkedList<>();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.hyR.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.hyR.desc"), "isHyR", "setHyR"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.b50.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.b50.desc"), "isB50", "setB50"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RAComposition.class, "RAComposition.cs.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "RAComposition.cs.desc"), "isCs", "setCs"));
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

    public boolean isHyR() {
        return hyR;
    }

    public void setHyR(Boolean hyR) {
        this.hyR = hyR;
    }

    public boolean isB50() {
        return b50;
    }

    public void setB50(Boolean b50) {
        this.b50 = b50;
    }

    public boolean isCs() {
        return cs;
    }

    public void setCs(Boolean cs) {
        this.cs = cs;
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
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            if (hyR) {
                alphabets.add(ReducedAlphabets.ra_hydrop_Rose());
            }
            if (b50) {
                alphabets.add(ReducedAlphabets.ra_Blosum50_Murphy());
            }
            if (cs) {
                alphabets.add(ReducedAlphabets.ra_cSimilarity_chakrabarty());
            }
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

            for (ReduceAlphabet ra : alphabets) {
                Set<String> keySet = ra.getCount().keySet();
                String attrName;
                for (String key1 : keySet) {
                    for (String key2 : keySet) {
                        attrName = String.format("%s([%s][%s])", ra.getName(),key1, key2);
                        if (!attrModel.hasAttribute(attrName)) {
                            attrModel.addAttribute(attrName, attrName, Double.class);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void compute(Peptide peptide) {
        String attrName;
        for (ReduceAlphabet ra : alphabets) {
            Map<String, Double> aminoAcidComposition = MD.dipeptideComposition(peptide.getSequence(), ra);
            Iterator<String> it = aminoAcidComposition.keySet().iterator();
            String key;
            double val;
            while (it.hasNext()) {
                key = it.next();
                attrName = String.format("%s(%s)", ra.getName(),key);
                val = aminoAcidComposition.get(key);
                peptide.setAttributeValue(attrModel.getAttribute(attrName), val);
            }
        }
    }

    @Override
    public void endAlgo() {
        super.endAlgo();
        alphabets.clear();
    }
    
    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }     

}
