/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.impl.AllDescriptors;
import org.bapedis.modamp.impl.AllDescriptorsFactory;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ChemicalSpaceNetwork extends SimilarityNetworkAlgo {

    protected final AllDescriptors descriptorAlgo;
    protected List<PeptideAttribute> descriptorList;
    protected int buttonGroupIndex;

    public ChemicalSpaceNetwork(AlgorithmFactory factory) {
        super(factory);
        AllDescriptorsFactory descriptorFactory = Lookup.getDefault().lookup(AllDescriptorsFactory.class);
        descriptorAlgo = (AllDescriptors) descriptorFactory.createAlgorithm();
        buttonGroupIndex = 0;
    }

    public AllDescriptors getDescriptorAlgorithm() {
        return descriptorAlgo;
    }

    public int getButtonGroupIndex() {
        return buttonGroupIndex;
    }

    public void setButtonGroupIndex(int buttonGroupIndex) {
        this.buttonGroupIndex = buttonGroupIndex;
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            descriptorAlgo.initAlgo();
            descriptorList = new LinkedList<>();
        }
    }

    @Override
    public void run() {
        if (attrModel != null) {
            Peptide[] peptides = attrModel.getPeptides();
            progressTicket.progress(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.md.running"));
            progressTicket.switchToDeterminate(peptides.length);
            for (int i = 0; i < peptides.length && !stopRun; i++) {
                descriptorAlgo.compute(peptides[i]);
                progressTicket.progress();
            }
            for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
                PeptideAttribute attr = it.next();
                if (attr.isMolecularDescriptor()) {
                    descriptorList.add(attr);
                }
            }
            progressTicket.progress(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.task.running"));
            super.run();
        }
    }

    @Override
    public void endAlgo() {
        if (attrModel != null) {
            descriptorAlgo.endAlgo();
            descriptorList = null;
        }
        super.endAlgo();
    }

    @Override
    public double computeSimilarity(Peptide peptide1, Peptide peptide2) {
        double val1, val2, diff, squareSum = 0;
        for (PeptideAttribute descriptor : descriptorList) {
            val1 = (double) convertToDouble(peptide1.getAttributeValue(descriptor));
            val2 = (double) convertToDouble(peptide2.getAttributeValue(descriptor));
            diff = val2 - val1;
            squareSum += diff * diff;
        }
        double distance = Math.sqrt(squareSum);
        return 1 / (1 + distance);
    }

    private double convertToDouble(Object obj) {
        if (obj instanceof Double) {
            return (double) obj;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        }
        throw new IllegalArgumentException("Unknown value for molecular descriptor:" + obj);
    }

}
