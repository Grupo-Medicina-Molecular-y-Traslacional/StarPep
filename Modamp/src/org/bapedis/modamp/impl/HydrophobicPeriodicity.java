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
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.MD;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author beltran, loge
 */
public class HydrophobicPeriodicity extends AbstractModamp {
    private final String PRO_NAME;
    private final List<AlgorithmProperty> properties;
    private int window;
    
    public HydrophobicPeriodicity(AlgorithmFactory factory) {
        super(factory);
        PRO_NAME = "A(m=%d)";
        properties = new LinkedList<>();
        window = 10;
        populateProperties();
    }
    
    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(HydrophobicPeriodicity.class, "HydrophobicPeriodicity.window.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicPeriodicity.class, "HydrophobicPeriodicity.window.desc"), "getWindow", "setWindow"));
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
    
    
    @Override
    public void compute(Peptide peptide) {
        double val = MD.A_m(peptide.getSequence(), window);
        String name = getProName();
        peptide.setAttributeValue(attrModel.getAttribute(name), val);
    }
    
    private String getProName(){
        return String.format(PRO_NAME, window);
    }
    
    @Override
    public void initAlgo() {
        super.initAlgo();
        String name = getProName();
        if (attrModel != null && !attrModel.hasAttribute(name)) {
            attrModel.addAttribute(name, name, Double.class);
        }        
    }    
    
    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }    
    
}
