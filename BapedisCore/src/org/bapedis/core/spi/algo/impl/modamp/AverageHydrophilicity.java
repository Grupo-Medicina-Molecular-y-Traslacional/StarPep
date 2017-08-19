/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.impl.modamp.scales.HydrophilicityScale;

/**
 *
 * @author loge
 */
public class AverageHydrophilicity extends AbstractModamp {

    protected String HOPT810101 = "AvgHydrophilicity(HOPT810101)";
    protected String KUHL950101 = "AvgHydrophilicity(KUHL950101)";
    protected String GRAVY = "GRAVY";

    public AverageHydrophilicity(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null && !attrModel.hasAttribute(HOPT810101)) {
            attrModel.addAttribute(HOPT810101, HOPT810101, Double.class);
        }
        if (attrModel != null && !attrModel.hasAttribute(KUHL950101)) {
            attrModel.addAttribute(KUHL950101, KUHL950101, Double.class);
        }
        if (attrModel != null && !attrModel.hasAttribute(GRAVY)) {
            attrModel.addAttribute(GRAVY, GRAVY, Double.class);
        }
    }

    @Override
    public void compute(Peptide peptide) {
        double val = MD.gravy(peptide.getSequence(), HydrophilicityScale.hopp_Woods_hydrov_hash());
        peptide.setAttributeValue(attrModel.getAttribute(HOPT810101), val);
        
        val = MD.gravy(peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash());
        peptide.setAttributeValue(attrModel.getAttribute(KUHL950101), val);        
        
        val = MD.gravy(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(GRAVY), val);        
    }

}
