/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.impl.modamp.scales.HydrophobicityScale;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class HydrophobicMoment extends AbstractModamp {

    private final List<AlgorithmProperty> properties;
    private final boolean[] angle; // 100, 160, 180
    private final int[] angleVal;
    private final boolean[] scale;
    private final Map<String, Double>[] scaleVal;
    private final String[][] attrNames;
    private final String AVGH = "maxAvgH";

    private int window;

    public HydrophobicMoment(HydrophobicMomentFactory factory) {
        super(factory);
        properties = new LinkedList<>();

        angle = new boolean[]{true, true, true};
        angleVal = new int[]{100, 160, 180};

        scale = new boolean[]{true, true, true};
        scaleVal = new Map[]{HydrophobicityScale.kyte_doolittle_hydrov_hash(), HydrophobicityScale.tossi_hydrov_hash(), HydrophobicityScale.eisenberg_hydrov_hash()};

        attrNames = new String[][]{{"\u03BCH(angle=100,scale=KYTJ820101)", "\u03BCH(angle=160,scale=KYTJ820101)", "\u03BCH(angle=180,scale=KYTJ820101)"},
        {"\u03BCH(angle=100,scale=Tossi12)", "\u03BCH(angle=160,scale=Tossi12)", "\u03BCH(angle=180,scale=Tossi12)"},
        {"\u03BCH(angle=100,scale=EISD840101)", "\u03BCH(angle=160,scale=EISD840101)", "\u03BCH(angle=180,scale=EISD840101)"}};
        window = 10;
        populateProperties();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.angle100.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.angle100.desc"), "isAngle100", "setAngle100"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.angle160.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.angle160.desc"), "isAngle160", "setAngle160"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.angle180.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.angle180.desc"), "isAngle180", "setAngle180"));

            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.scale.KYTJ820101.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.scale.KYTJ820101.desc"), "isKYTJ820101", "setKYTJ820101"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.scale.Tossi12.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.scale.Tossi12.desc"), "isTossi12", "setTossi12"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.scale.EISD840101.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.scale.EISD840101.desc"), "isEISD840101", "setEISD840101"));
                       
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, "Window", PRO_CATEGORY, "Size of windows", "getWindow", "setWindow"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(Integer window) {
        this.window = window;
    }

    public boolean isAngle100() {
        return angle[0];
    }

    public void setAngle100(Boolean angle100) {
        this.angle[0] = angle100;
    }

    public boolean isAngle160() {
        return angle[1];
    }

    public void setAngle160(Boolean angle160) {
        this.angle[1] = angle160;
    }

    public boolean isAngle180() {
        return angle[2];
    }

    public void setAngle180(Boolean angle180) {
        this.angle[2] = angle180;
    }

    public boolean isKYTJ820101() {
        return scale[0];
    }

    public void setKYTJ820101(Boolean scale) {
        this.scale[0] = scale;
    }

    public boolean isTossi12() {
        return scale[1];
    }

    public void setTossi12(Boolean scale) {
        this.scale[1] = scale;
    }

    public boolean isEISD840101() {
        return scale[2];
    }

    public void setEISD840101(Boolean scale) {
        this.scale[2] = scale;
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            if (!attrModel.hasAttribute(AVGH)) {
                attrModel.addAttribute(AVGH, AVGH, Double.class);
            }
            for (int i = 0; i < scale.length; i++) {
                if (scale[i]) {
                    for (int j = 0; j < angle.length; j++) {
                        if (angle[j] && !attrModel.hasAttribute(attrNames[i][j])) {
                            attrModel.addAttribute(attrNames[i][j], attrNames[i][j], Double.class);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void compute(Peptide peptide) {
        double val;
        val = MD.maxMeanHydrophobicity(peptide.getSequence(), window);
        peptide.setAttributeValue(attrModel.getAttribute(AVGH), val);
        for (int i = 0; i < scale.length; i++) {
            if (scale[i]) {
                for (int j = 0; j < angle.length; j++) {
                    if (angle[j]) {
                        val = MD.hMoment(peptide.getSequence(), angleVal[j], window, scaleVal[j]);
                        peptide.setAttributeValue(attrModel.getAttribute(attrNames[i][j]), val);
                    }
                }
            }
        }
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

}
