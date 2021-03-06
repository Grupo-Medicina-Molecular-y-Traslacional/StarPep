/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.impl.AbstractMD;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.ChargeScale;
import org.bapedis.modamp.scales.PkaValues;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author beltran, loge
 */
public class NetCharge extends AbstractMD {

    protected final String Z5, Z7, Z9;
    protected boolean ph5, ph7, ph9;
    protected boolean KLEP840101, CHAM830107, CHAM830108;
    protected final String KLEP840101_NAME, CHAM830107_NAME, CHAM830108_NAME;
    protected final String KLEP840101_AVG, CHAM830107_AVG, CHAM830108_AVG;
    private final List<AlgorithmProperty> properties;

    public NetCharge(AlgorithmFactory factory) {
        super(factory);
        ph5 = true;
        ph7 = true;
        ph9 = true;
        KLEP840101 = true;
        CHAM830107 = true;
        CHAM830108 = true;
        KLEP840101_NAME = "NetCharge(KLEP840101)";
        KLEP840101_AVG = "AvgNetCharge(KLEP840101)";
        CHAM830107_NAME = "NetCharge(CHAM830107)";
        CHAM830107_AVG = "AvgNetCharge(CHAM830107)";
        CHAM830108_NAME = "NetCharge(CHAM830108)";
        CHAM830108_AVG = "AvgNetCharge(CHAM830108)";
        Z5 = "Z(pH=5)";
        Z7 = "Z(pH=7)";
        Z9 = "Z(pH=9)";
        properties = new LinkedList<>();
        populateProperties();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetCharge.class, "NetCharge.z5.name"), PRO_CATEGORY, NbBundle.getMessage(NetCharge.class, "NetCharge.z5.desc"), "isPh5", "setPh5"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetCharge.class, "NetCharge.z7.name"), PRO_CATEGORY, NbBundle.getMessage(NetCharge.class, "NetCharge.z7.desc"), "isPh7", "setPh7"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetCharge.class, "NetCharge.z9.name"), PRO_CATEGORY, NbBundle.getMessage(NetCharge.class, "NetCharge.z9.desc"), "isPh9", "setPh9"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetCharge.class, "NetCharge.KLEP840101.name"), PRO_CATEGORY, NbBundle.getMessage(NetCharge.class, "NetCharge.KLEP840101.desc"), "isKLEP840101", "setKLEP840101"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetCharge.class, "NetCharge.CHAM830107.name"), PRO_CATEGORY, NbBundle.getMessage(NetCharge.class, "NetCharge.CHAM830107.desc"), "isCHAM830107", "setCHAM830107"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetCharge.class, "NetCharge.CHAM830108.name"), PRO_CATEGORY, NbBundle.getMessage(NetCharge.class, "NetCharge.CHAM830108.desc"), "isCHAM830108", "setCHAM830108"));
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

    public boolean isKLEP840101() {
        return KLEP840101;
    }

    public void setKLEP840101(Boolean KLEP840101) {
        this.KLEP840101 = KLEP840101;
    }

    public boolean isCHAM830107() {
        return CHAM830107;
    }

    public void setCHAM830107(Boolean CHAM830107) {
        this.CHAM830107 = CHAM830107;
    }

    public boolean isCHAM830108() {
        return CHAM830108;
    }

    public void setCHAM830108(Boolean CHAM830108) {
        this.CHAM830108 = CHAM830108;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket); //To change body of generated methods, choose Tools | Templates.
        if (ph5) {
            addAttribute(Z5, Z5, Double.class);
        }

        if (ph7) {
            addAttribute(Z7, Z7, Double.class);
        }

        if (ph9) {
            addAttribute(Z9, Z9, Double.class);
        }

        if (KLEP840101) {
            addAttribute(KLEP840101_NAME, KLEP840101_NAME, Double.class);
            addAttribute(KLEP840101_AVG, KLEP840101_AVG, Double.class);
        }

        if (CHAM830107) {
            addAttribute(CHAM830107_NAME, CHAM830107_NAME, Double.class);
            addAttribute(CHAM830107_AVG, CHAM830107_AVG, Double.class);
        }

        if (CHAM830108) {
            addAttribute(CHAM830108_NAME, CHAM830108_NAME, Double.class);
            addAttribute(CHAM830108_AVG, CHAM830108_AVG, Double.class);
        }        
    }
    
    @Override
    protected void compute(Peptide peptide) {
        double val;
        String seq = peptide.getSequence();
        if (ph5) {
            val = MD.netCharge(seq, 5, PkaValues.IPC_peptide());
            peptide.setAttributeValue(getAttribute(Z5), val);
        }
        if (ph7) {
            val = MD.netCharge(seq, 7, PkaValues.IPC_peptide());
            peptide.setAttributeValue(getAttribute(Z7), val);
        }
        if (ph9) {
            val = MD.netCharge(seq, 9, PkaValues.IPC_peptide());
            peptide.setAttributeValue(getAttribute(Z9), val);
        }
        double[] values;
        if (KLEP840101) {
            values = MD.sumAndAvg(seq, ChargeScale.klein_hash());
            peptide.setAttributeValue(getAttribute(KLEP840101_NAME), values[0]);
            peptide.setAttributeValue(getAttribute(KLEP840101_AVG), values[1]);
        }
        if (CHAM830107) {
            values = MD.sumAndAvg(seq, ChargeScale.charton_ctc_hash());
            peptide.setAttributeValue(getAttribute(CHAM830107_NAME), values[0]);
            peptide.setAttributeValue(getAttribute(CHAM830107_AVG), values[1]);
        }
        if (CHAM830108) {
            values = MD.sumAndAvg(seq, ChargeScale.charton_ctdc_hash());
            peptide.setAttributeValue(getAttribute(CHAM830108_NAME), values[0]);
            peptide.setAttributeValue(getAttribute(CHAM830108_AVG), values[1]);
        }
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

}
