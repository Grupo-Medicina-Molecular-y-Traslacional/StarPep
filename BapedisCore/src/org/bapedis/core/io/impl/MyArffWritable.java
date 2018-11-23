/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.util.List;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.util.ArffWritable;
import org.bapedis.core.io.MD_OUTPUT_OPTION;

/**
 *
 * @author loge
 */
public class MyArffWritable implements ArffWritable {
    
    private final Peptide[] peptides;
    private final MolecularDescriptor[] features;
    private final MD_OUTPUT_OPTION output;

    public MyArffWritable(Peptide[] peptides, MolecularDescriptor[] features, MD_OUTPUT_OPTION output) {
        this.peptides = peptides;
        this.features = features;
        this.output = output;
    }

    public MD_OUTPUT_OPTION getOutputOption() {
        return output;
    }

    @Override
    public List<String> getAdditionalInfo() {
        return null;
    }

    @Override
    public String getRelationName() {
        return "AMP - Dataset";
    }

    @Override
    public int getNumAttributes() {
        return features.length;
    }

    @Override
    public String getAttributeName(int attribute) {
        return features[attribute].getDisplayName();
    }

    @Override
    public String[] getAttributeDomain(int attribute) {
        return null;
    }

    @Override
    public int getNumInstances() {
        return peptides.length;
    }

    @Override
    public String getAttributeValue(int instance, int attribute) throws Exception {
        MolecularDescriptor attr = features[attribute];
        Peptide peptide = peptides[instance];
        switch (output) {
            case None:
                return Double.toString(attr.getDoubleValue(peptide));            
            case Z_SCORE:
                 return Double.toString(attr.getNormalizedZscoreValue(peptide));
            case MIN_MAX:
                return Double.toString(attr.getNormalizedMinMaxValue(peptide));
        }
        return "";
    }

    @Override
    public double getAttributeValueAsDouble(int instance, int attribute) throws Exception {
        return Double.valueOf(getAttributeValue(instance, attribute));
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public String getMissingValue(int attribute) {
        return "?";
    }

}
