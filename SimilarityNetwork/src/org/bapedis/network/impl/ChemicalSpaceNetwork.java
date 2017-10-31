/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

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
    protected boolean normalize;

    public ChemicalSpaceNetwork(AlgorithmFactory factory) {
        super(factory);
        AllDescriptorsFactory descriptorFactory = Lookup.getDefault().lookup(AllDescriptorsFactory.class);
        descriptorAlgo = (AllDescriptors) descriptorFactory.createAlgorithm();
        buttonGroupIndex = 0;
        normalize = true;
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
            descriptorList = descriptorAlgo.getDescriptorList();
        }
    }

    @Override
    public boolean cancel() {
        if (attrModel != null) {
            descriptorAlgo.cancel();
        }
        return super.cancel();
    }

    @Override
    public void run() {
        if (attrModel != null) {
            progressTicket.progress(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.md.running"));
            descriptorAlgo.setProgressTicket(progressTicket);
            descriptorAlgo.run();
            descriptorAlgo.setProgressTicket(null);

            progressTicket.progress(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.task.running"));
            if (!stopRun) {
                super.run();
            }
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
    public float computeSimilarity(Peptide peptide1, Peptide peptide2) {
        // Evaluates the continuous Tanimoto coefficient
        double ab = 0.0;
        double a2 = 0.0;
        double b2 = 0.0;
        double val1, val2;
        for (PeptideAttribute descriptor : descriptorList) {
            val1 = normalize ? descriptor.normalize(peptide1.getAttributeValue(descriptor)) : PeptideAttribute.convertToDouble(peptide1.getAttributeValue(descriptor));
            val2 = normalize ? descriptor.normalize(peptide2.getAttributeValue(descriptor)) : PeptideAttribute.convertToDouble(peptide2.getAttributeValue(descriptor));
            ab += val1 * val2;
            a2 += val1 * val1;
            b2 += val2 * val2;
        }
        return (float) ab / (float) (a2 + b2 - ab);
    }

//    private double distanceBased(Peptide peptide1, Peptide peptide2) {
//        double val1, val2, diff, squareSum = 0;
//        for (PeptideAttribute descriptor : descriptorList) {
//            val1 = (double) convertToDouble(descriptor, peptide1.getAttributeValue(descriptor));
//            val2 = (double) convertToDouble(descriptor, peptide2.getAttributeValue(descriptor));
//            diff = val2 - val1;
//            squareSum += diff * diff;
//        }
//        double distance = Math.sqrt(squareSum);
//        return 1 / (1 + distance);
//    }
}
