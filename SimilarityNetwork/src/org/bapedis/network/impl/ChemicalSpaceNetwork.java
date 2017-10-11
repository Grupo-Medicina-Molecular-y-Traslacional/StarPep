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
import org.bapedis.core.spi.data.PeptideDAO;
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
            progressTicket.switchToDeterminate(peptides.length + 1);
            for (int i = 0; i < peptides.length && !stopRun; i++) {
                descriptorAlgo.compute(peptides[i]);
                progressTicket.progress();
            }

            for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext() && !stopRun;) {
                PeptideAttribute attr = it.next();
                if (attr.isMolecularDescriptor()) {
                    descriptorList.add(attr);
                }
            }
            progressTicket.progress();
            // normalizing
            progressTicket.progress("normalizing");
            double val, max, min;
            for (PeptideAttribute descriptor : descriptorList) {
                max = Double.MIN_VALUE;
                min = Double.MAX_VALUE;
                for (Peptide peptide : peptides) {
                    val = convertToDouble(descriptor, peptide.getAttributeValue(descriptor));
                    if (val < min) {
                        min = val;
                    }
                    if (val > max) {
                        max = val;
                    }
                }
                descriptor.setMaxValue(max);
                descriptor.setMinValue(min);
                if (descriptor != PeptideDAO.LENGHT) {
                    for (Peptide peptide : peptides) {
                        val = convertToDouble(descriptor, peptide.getAttributeValue(descriptor));
                        val = (val - min) / (max - min);
                        peptide.setAttributeValue(descriptor, val);
                    }
                }
            }

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
    public double computeSimilarity(Peptide peptide1, Peptide peptide2) {
        return tanimotoBased(peptide1, peptide2);
    }

    private float tanimotoBased(Peptide peptide1, Peptide peptide2) {
        double ab = 0.0;
        double a2 = 0.0;
        double b2 = 0.0;
        double val1, val2;
        for (PeptideAttribute descriptor : descriptorList) {
            val1 = (double) convertToDouble(descriptor, peptide1.getAttributeValue(descriptor));
            val2 = (double) convertToDouble(descriptor, peptide2.getAttributeValue(descriptor));
            ab += val1 * val2;
            a2 += val1 * val1;
            b2 += val2 * val2;
        }
        return (float) ab / (float) (a2 + b2 - ab);
    }

    private double distanceBased(Peptide peptide1, Peptide peptide2) {
        double val1, val2, diff, squareSum = 0;
        for (PeptideAttribute descriptor : descriptorList) {
            val1 = (double) convertToDouble(descriptor, peptide1.getAttributeValue(descriptor));
            val2 = (double) convertToDouble(descriptor, peptide2.getAttributeValue(descriptor));
            diff = val2 - val1;
            squareSum += diff * diff;
        }
        double distance = Math.sqrt(squareSum);
        return 1 / (1 + distance);
    }

    private double convertToDouble(PeptideAttribute descritor, Object value) {
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        throw new IllegalArgumentException("Unknown value for molecular descriptor: " + descritor.getDisplayName() + "=" + value);
    }

}
