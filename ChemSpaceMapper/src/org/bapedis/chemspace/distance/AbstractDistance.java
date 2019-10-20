/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.distance;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Loge
 */
public abstract class AbstractDistance implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected MD_OUTPUT_OPTION option;
    protected List<MolecularDescriptor> features;
    protected AlgorithmFactory factory;
    protected boolean stopRun;
    private Peptide peptide1, peptide2;
    private double distance;

    public AbstractDistance(AlgorithmFactory factory) {
        this.factory = factory;
        option = MD_OUTPUT_OPTION.Z_SCORE;
    }

    public List<MolecularDescriptor> getFeatures() {
        return features;
    }

    public void setFeatures(List<MolecularDescriptor> features) {
        this.features = features;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        if (features == null) {
            features = new LinkedList<>();
            AttributesModel attrModel = pc.getAttributesModel(workspace);
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    features.add(attr);
                }
            }
        }
        stopRun = false;
    }

    @Override
    public void endAlgo() {
        features = null;
        peptide1 = null;
        peptide2 = null;
        distance = Double.NaN;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    public void setPeptides(Peptide peptide1, Peptide peptide2) {
        this.peptide1 = peptide1;
        this.peptide2 = peptide2;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public void run() {
        if (!stopRun && peptide1 != null && peptide2 != null) {
            try {
                distance = compute(peptide1, peptide2);
            } catch (MolecularDescriptorNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                stopRun = true;
            }
        }
    }

    public MD_OUTPUT_OPTION getOption() {
        return option;
    }

    public void setOption(MD_OUTPUT_OPTION option) {
        this.option = option;
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

    abstract double compute(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException;

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractDistance copy = (AbstractDistance) super.clone();
        return copy;
    }
}
