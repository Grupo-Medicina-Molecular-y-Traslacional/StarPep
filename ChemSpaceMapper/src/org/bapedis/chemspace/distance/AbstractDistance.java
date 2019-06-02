/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.distance;

import java.util.List;
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author Loge
 */
public abstract class AbstractDistance implements DistanceFunction, Cloneable {

    protected MD_OUTPUT_OPTION option;
    protected List<MolecularDescriptor> features;
    protected String name;

    public AbstractDistance(String name) {
        this.name = name;
        option = MD_OUTPUT_OPTION.Z_SCORE;
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

    public MD_OUTPUT_OPTION getOption() {
        return option;
    }

    public void setOption(MD_OUTPUT_OPTION option) {
        this.option = option;
    }        

    public void setFeatures(List<MolecularDescriptor> features) {
        this.features = features;
    }

    protected double normalizedValue(Peptide peptide, MolecularDescriptor attr) throws MolecularDescriptorNotFoundException {
        switch (option) {
            case Z_SCORE:
                return Math.abs(attr.getNormalizedZscoreValue(peptide));
            case MIN_MAX:
                return attr.getNormalizedMinMaxValue(peptide);
        }
        throw new IllegalArgumentException("Unknown value for normalization index: " + option);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractDistance copy = (AbstractDistance) super.clone();
        return copy;
    }
}
