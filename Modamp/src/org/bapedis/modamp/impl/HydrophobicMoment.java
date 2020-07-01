/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.impl.AbstractMD;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.HydrophobicityScale;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author beltran, loge
 */
public class HydrophobicMoment extends AbstractMD {

    private final List<AlgorithmProperty> properties;
    private final boolean[] angle; // 100, 160, 180
    private final int[] angleVal;
    private final boolean[] scale;
    private final String[][] attrNames;
    private final String[] AVGH;

    private int window;

    public HydrophobicMoment(HydrophobicMomentFactory factory) {
        super(factory);
        properties = new LinkedList<>();

        angle = new boolean[]{true, true, true};
        angleVal = new int[]{100, 160, 180};

        scale = new boolean[]{true, true, true};

        attrNames = new String[][]{{"μH(angle_100_scale_KYTJ820101)", "μH(angle_160_scale_KYTJ820101)", "μH(angle_180_scale_KYTJ820101)"},
        {"μH(angle_100_scale_Tossi12)", "μH(angle_160_scale_Tossi12)", "μH(angle_180_scale_Tossi12)"},
        {"μH(angle_100_scale_EISD840101)", "μH(angle_160_scale_EISD840101)", "μH(angle_180_scale_EISD840101)"}};

        AVGH = new String[]{"maxAvgH(KYTJ820101)", "maxAvgH(Tossi12)", "maxAvgH(EISD840101)"};
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

            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.window.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMoment.class, "HydrophobicMoment.window.desc"), "getWindow", "setWindow"));
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
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        for (int i = 0; i < scale.length; i++) {
            if (scale[i]) {
                for (int j = 0; j < angle.length; j++) {
                    if (angle[j]) {
                        addAttribute(attrNames[i][j], attrNames[i][j], Double.class);
                    }
                }
                addAttribute(AVGH[i], AVGH[i], Double.class);
            }
        }
    }

    @Override
    protected void compute(Peptide peptide) {
        Map<String, Double>[] scaleVal = new Map[]{HydrophobicityScale.kyte_doolittle_hydrov_hash(), HydrophobicityScale.tossi_hydrov_hash(), HydrophobicityScale.eisenberg_hydrov_hash()};
        double val;
        for (int i = 0; i < scale.length; i++) {
            if (scale[i]) {
                for (int j = 0; j < angle.length; j++) {
                    if (angle[j]) {
                        val = MD.hMoment(peptide.getSequence(), angleVal[j], window, scaleVal[i]);
                        peptide.setAttributeValue(getAttribute(attrNames[i][j]), val);
                    }
                }
                val = MD.maxMeanHydrophobicity(peptide.getSequence(), window, scaleVal[i]);
                peptide.setAttributeValue(getAttribute(AVGH[i]), val);
            }
        }
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

}
