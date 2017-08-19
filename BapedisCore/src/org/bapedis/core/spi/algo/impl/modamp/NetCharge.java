/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.impl.modamp.scales.PkaValues;
import org.openide.util.Exceptions;

/**
 *
 * @author Home
 */
public class NetCharge extends AbstractModamp {

    protected final String Z5, Z7, Z9;
    protected boolean ph5, ph7, ph9;
    private final List<AlgorithmProperty> properties;

    public NetCharge(AlgorithmFactory factory) {
        super(factory);
        ph5 = true;
        ph7 = true;
        ph9 = true;
        Z5 = "Z(pH=5)";
        Z7 = "Z(pH=7)";
        Z9 = "Z(pH=9)";
        properties = new LinkedList<>();
        populateProperties();
    }

    private void populateProperties() {
        final String NetCharge_CATEGORY = "NetCharge's properties";
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, Z5, NetCharge_CATEGORY, "Net charge at pH=5", "isPh5", "setPh5"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, Z7, NetCharge_CATEGORY, "Net charge at pH=7", "isPh7", "setPh7"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, Z9, NetCharge_CATEGORY, "Net charge at pH=9", "isPh9", "setPh9"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean isPh5() {
        return ph5;
    }

    public boolean isPh7() {
        return ph7;
    }

    public boolean isPh9() {
        return ph9;
    }

    public void setPh5(Boolean ph5) {
        this.ph5 = ph5;
    }

    public void setPh7(Boolean ph7) {
        this.ph7 = ph7;
    }

    public void setPh9(Boolean ph9) {
        this.ph9 = ph9;
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            if (ph5 && !attrModel.hasAttribute(Z5)) {
                attrModel.addAttribute(Z5, Z5, Double.class);
            }
            if (ph7 && !attrModel.hasAttribute(Z7)) {
                attrModel.addAttribute(Z7, Z7, Double.class);
            }
            if (ph9 && !attrModel.hasAttribute(Z9)) {
                attrModel.addAttribute(Z9, Z9, Double.class);
            }
        }
    }

    @Override
    public void compute(Peptide peptide) {
        double val;
        if (ph5) {
            val = MD.netCharge(peptide.getSequence(), 5, PkaValues.IPC_peptide());
            peptide.setAttributeValue(attrModel.getAttribute(Z5), val);
        }
        if (ph7) {
            val = MD.netCharge(peptide.getSequence(), 7, PkaValues.IPC_peptide());
            peptide.setAttributeValue(attrModel.getAttribute(Z7), val);
        }
        if (ph9) {
            val = MD.netCharge(peptide.getSequence(), 9, PkaValues.IPC_peptide());
            peptide.setAttributeValue(attrModel.getAttribute(Z9), val);
        }
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

}
