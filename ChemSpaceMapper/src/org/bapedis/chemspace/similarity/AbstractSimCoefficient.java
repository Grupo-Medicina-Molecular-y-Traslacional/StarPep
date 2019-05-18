/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.similarity;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
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
 * @author loge
 */
public abstract class AbstractSimCoefficient implements Algorithm, Cloneable {

    static protected final String PRO_CATEGORY = "Properties";
    protected ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected Peptide[] peptides;
    protected List<MolecularDescriptor> features;
    protected boolean stopRun;
    private Peptide peptide1, peptide2;
    private float similarityVal;

    public AbstractSimCoefficient(AlgorithmFactory factory) {
        this.factory = factory;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        peptides = attrModel.getPeptides().toArray(new Peptide[0]);
        features = new LinkedList<>();
        for (String key : attrModel.getMolecularDescriptorKeys()) {
            for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                features.add(attr);
            }
        }
        stopRun = false;
    }

    public void setPeptidesToCompare(Peptide peptide1, Peptide peptide2) {
        this.peptide1 = peptide1;
        this.peptide2 = peptide2;
        similarityVal = -1;
    }

    public float getSimilarityValue() {
        if (similarityVal < 0 || similarityVal > 1) {
            throw new IllegalStateException("Unexpected similarity value: " + similarityVal);
        }
        return similarityVal;
    }

    public List<MolecularDescriptor> getFeatures() {
        return features;
    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        features = null;
        peptide1 = null;
        peptide2 = null;
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
    public void run() {
        try {
            similarityVal = computeSimilarity(peptide1, peptide2);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected abstract float computeSimilarity(Peptide peptide1, Peptide peptide2) throws Exception;

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractSimCoefficient copy = (AbstractSimCoefficient) super.clone();
        return copy;
    }
}
