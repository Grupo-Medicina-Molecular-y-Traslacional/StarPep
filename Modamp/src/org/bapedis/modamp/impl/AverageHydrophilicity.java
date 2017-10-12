/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.HydrophilicityScale;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author beltran, loge
 */
public class AverageHydrophilicity extends AbstractModamp {

    protected final String HOPT810101_NAME = "AvgHydrophilicity(HOPT810101)";
    protected final String KUHL950101_NAME = "AvgHydrophilicity(KUHL950101)";
    protected boolean HOPT810101, KUHL950101;
    protected String GRAVY = "GRAVY";
    private final List<AlgorithmProperty> properties;

    public AverageHydrophilicity(AlgorithmFactory factory) {
        super(factory);
        HOPT810101 = true;
        KUHL950101 = false;
        properties = new LinkedList<>();
        populateProperties();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetCharge.class, "AverageHydrophilicity.HOPT810101.name"), PRO_CATEGORY, NbBundle.getMessage(NetCharge.class, "AverageHydrophilicity.HOPT810101.desc"), "isHOPT810101", "setHOPT810101"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetCharge.class, "AverageHydrophilicity.KUHL950101.name"), PRO_CATEGORY, NbBundle.getMessage(NetCharge.class, "AverageHydrophilicity.KUHL950101.desc"), "isKUHL950101", "setKUHL950101"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean isHOPT810101() {
        return HOPT810101;
    }

    public void setHOPT810101(Boolean HOPT810101) {
        this.HOPT810101 = HOPT810101;
    }

    public boolean isKUHL950101() {
        return KUHL950101;
    }

    public void setKUHL950101(Boolean KUHL950101) {
        this.KUHL950101 = KUHL950101;
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            PeptideAttribute descriptor;
            if (HOPT810101) {
                if (!attrModel.hasAttribute(HOPT810101_NAME)) {
                    descriptor = attrModel.addAttribute(HOPT810101_NAME, HOPT810101_NAME, Double.class);
                } else {
                    descriptor = attrModel.getAttribute(HOPT810101_NAME);
                }
                descriptorList.add(descriptor);
            }

            if (KUHL950101) {
                if (!attrModel.hasAttribute(KUHL950101_NAME)) {
                    descriptor = attrModel.addAttribute(KUHL950101_NAME, KUHL950101_NAME, Double.class);
                } else {
                    descriptor = attrModel.getAttribute(KUHL950101_NAME);
                }
                descriptorList.add(descriptor);
            }

            if (!attrModel.hasAttribute(GRAVY)) {
                descriptor = attrModel.addAttribute(GRAVY, GRAVY, Double.class);
            } else {
                descriptor = attrModel.getAttribute(GRAVY);
            }
            descriptorList.add(descriptor);
        }
    }

    @Override
    public void compute(Peptide peptide) {
        double val;
        if (HOPT810101) {
            val = MD.gravy(peptide.getSequence(), HydrophilicityScale.hopp_Woods_hydrov_hash());
            peptide.setAttributeValue(attrModel.getAttribute(HOPT810101_NAME), val);
        }

        if (KUHL950101) {
            val = MD.gravy(peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash());
            peptide.setAttributeValue(attrModel.getAttribute(KUHL950101_NAME), val);
        }

        val = MD.gravy(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(GRAVY), val);
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

}
