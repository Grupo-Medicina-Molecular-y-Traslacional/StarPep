/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.distance;

import static org.bapedis.chemspace.impl.MapperAlgorithm.INDEX_ATTR;
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;

/**
 *
 * @author Loge
 */
public abstract class AbstractDistance implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected AlgorithmFactory factory;
    protected boolean stopRun;
    private Peptide peptide1, peptide2;
    protected double[][] descriptorMatrix;
    protected int index1, index2;
    private double distance;

    public AbstractDistance(AlgorithmFactory factory) {
        this.factory = factory;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        stopRun = false;
    }

    @Override
    public void endAlgo() {
        peptide1 = null;
        peptide2 = null;
        descriptorMatrix = null;
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

    public void setContext(Peptide peptide1, Peptide peptide2, double[][] descriptorMatrix) {
        if (!peptide1.hasAttribute(INDEX_ATTR) || !peptide2.hasAttribute(INDEX_ATTR)) {
            throw new IllegalStateException("Not index attribute found for peptides");
        }
        this.peptide1 = peptide1;
        this.peptide2 = peptide2;
        index1 = (int) peptide1.getAttributeValue(INDEX_ATTR);
        index2 = (int) peptide2.getAttributeValue(INDEX_ATTR);
        this.descriptorMatrix = descriptorMatrix;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public void run() {
        if (!stopRun && peptide1 != null && peptide2 != null) {
            distance = compute();
            assert distance >= 0 : "Invalid distance value: " + distance;
        }
    }

    abstract double compute();

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractDistance copy = (AbstractDistance) super.clone();
        return copy;
    }
}
