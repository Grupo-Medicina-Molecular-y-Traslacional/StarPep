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
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public class HydrophobicMoment extends AbstractModamp {

    private final List<AlgorithmProperty> properties;
    private boolean angle100, angle160, angle180;
    private final String uH100 = "\u03BCH(angle=100)";
    private final String uH160 = "\u03BCH(angle=160)";
    private final String uH180 = "\u03BCH(angle=180)";
    private final String AVGH="maxAvgH";
    
    private int window;

    public HydrophobicMoment(HydrophobicMomentFactory factory) {
        super(factory);
        properties = new LinkedList<>();
        angle100 = true;
        angle160 = true;
        angle180 = true;
        window = 10;
        populateProperties();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, uH100, PRO_CATEGORY, "Angle", "isAngle100", "setAngle100"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, uH160, PRO_CATEGORY, "Angle", "isAngle160", "setAngle160"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, uH180, PRO_CATEGORY, "Angle", "isAngle180", "setAngle180"));
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
        return angle100;
    }

    public void setAngle100(Boolean angle100) {
        this.angle100 = angle100;
    }

    public boolean isAngle160() {
        return angle160;
    }

    public void setAngle160(Boolean angle160) {
        this.angle160 = angle160;
    }

    public boolean isAngle180() {
        return angle180;
    }

    public void setAngle180(Boolean angle180) {
        this.angle180 = angle180;
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            if (!attrModel.hasAttribute(AVGH)){
                attrModel.addAttribute(AVGH, AVGH, Double.class);
            }
            if (angle100 && !attrModel.hasAttribute(uH100)) {
                attrModel.addAttribute(uH100, uH100, Double.class);
            }
            if (angle160 && !attrModel.hasAttribute(uH160)) {
                attrModel.addAttribute(uH160, uH160, Double.class);
            }
            if (angle180 && !attrModel.hasAttribute(uH180)) {
                attrModel.addAttribute(uH180, uH180, Double.class);
            }
        }
    }

    @Override
    public void compute(Peptide peptide) {
        double val;
        val = MD.maxMeanHydrophobicity(peptide.getSequence(), window);
        peptide.setAttributeValue(attrModel.getAttribute(AVGH), val);
        if (angle100) {
            val = MD.hMoment(peptide.getSequence(), 100, window);
            peptide.setAttributeValue(attrModel.getAttribute(uH100), val);
        }
        if (angle160) {
            val = MD.hMoment(peptide.getSequence(), 160, window);
            peptide.setAttributeValue(attrModel.getAttribute(uH160), val);
        }
        if (angle180) {
            val = MD.hMoment(peptide.getSequence(), 180, window);
            peptide.setAttributeValue(attrModel.getAttribute(uH180), val);
        }
    }
    
    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }
    

}
