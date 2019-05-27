/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.distance;

import java.util.List;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author Loge
 */
public abstract class AbstractDistance implements DistanceFunction, Cloneable{

    protected List<MolecularDescriptor> features;
    protected String name;
    protected int normalizationIndex;
    
    public AbstractDistance(String name) {
        this.name = name;
        normalizationIndex = 1;
    }

    public String getName() {
        return name;
    }        

    public String getDescription() {
        return name;
    }        
    
    public List<MolecularDescriptor> getFeatures() {
        return features;
    }

    public void setFeatures(List<MolecularDescriptor> features) {
        this.features = features;
    }        

    protected double normalizedValue(Peptide peptide, MolecularDescriptor attr) throws MolecularDescriptorNotFoundException {
        switch (normalizationIndex) {
            case 0:
                return Math.abs(attr.getNormalizedZscoreValue(peptide));
            case 1:
                return attr.getNormalizedMinMaxValue(peptide);
        }
        throw new IllegalArgumentException("Unknown value for normalization index: " + normalizationIndex);
    }   

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractDistance copy = (AbstractDistance) super.clone();
        return copy;
    }               
}
