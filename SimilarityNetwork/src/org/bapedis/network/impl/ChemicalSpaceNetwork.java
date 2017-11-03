/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ChemicalSpaceNetwork extends SimilarityNetworkAlgo {

    protected final Set<String> selectedKeys;
    protected final boolean normalize;
    protected final NotifyDescriptor emptyKeys;

    public ChemicalSpaceNetwork(AlgorithmFactory factory) {
        super(factory);
        normalize = true;
        selectedKeys = new LinkedHashSet<>();
        emptyKeys = new NotifyDescriptor.Message(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.emptyKeys.info"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public Set<String> getSelectedKeys() {
        return selectedKeys;
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (selectedKeys.isEmpty()) {
            DialogDisplayer.getDefault().notify(emptyKeys);
            cancel();
        }
    }

    @Override
    public float computeSimilarity(Peptide peptide1, Peptide peptide2) {
        // Evaluates the continuous Tanimoto coefficient
        double ab = 0.0;
        double a2 = 0.0;
        double b2 = 0.0;
        double val1, val2;
        for (String key : selectedKeys) {
            for (PeptideAttribute descriptor : attrModel.getMolecularDescriptors(key)) {
                val1 = normalize ? descriptor.normalize(peptide1.getAttributeValue(descriptor)) : PeptideAttribute.convertToDouble(peptide1.getAttributeValue(descriptor));
                val2 = normalize ? descriptor.normalize(peptide2.getAttributeValue(descriptor)) : PeptideAttribute.convertToDouble(peptide2.getAttributeValue(descriptor));
                ab += val1 * val2;
                a2 += val1 * val1;
                b2 += val2 * val2;
            }
        }
        return (float) ab / (float) (a2 + b2 - ab);
    }

}
