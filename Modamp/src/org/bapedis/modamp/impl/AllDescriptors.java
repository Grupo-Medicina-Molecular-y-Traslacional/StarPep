/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author beltran, loge
 */
public class AllDescriptors extends AbstractModamp {

    private final List<AlgorithmProperty> properties;
    private boolean aaComposition, aIndex, avgHydrophilicity, bIndex,
            dComposition, hMoment, hPeriodicity, iIndex,
            iPoint, mWeight, netCharge, raComposition,
            raDistribution, raTransition, triComposition;
    private final HashMap<String, AbstractModamp> map;

    public AllDescriptors(AlgorithmFactory factory) {
        super(factory);
        aaComposition = true;
        aIndex = true;
        avgHydrophilicity = true;
        bIndex = true;
        dComposition = true;
        hMoment = true;
        hPeriodicity = true;
        iIndex = true;
        iPoint = true;
        mWeight = true;
        netCharge = true;
        raComposition = true;
        raDistribution = true;
        raTransition = true;
        triComposition = true;
        properties = new LinkedList<>();
        populateProperties();
        map = new HashMap<>();
    }

    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(NetChargeFactory.class, "NetCharge.name"), PRO_CATEGORY, NbBundle.getMessage(NetChargeFactory.class, "NetCharge.desc"), "isNetCharge", "setNetCharge"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IsoelectricPointFactory.class, "IsoelectricPoint.name"), PRO_CATEGORY, NbBundle.getMessage(IsoelectricPointFactory.class, "IsoelectricPoint.desc"), "isiPoint", "setiPoint"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(MolecularWeightFactory.class, "MolecularWeight.name"), PRO_CATEGORY, NbBundle.getMessage(MolecularWeightFactory.class, "MolecularWeight.desc"), "ismWeight", "setmWeight"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(BomanIndexFactory.class, "BomanIndex.name"), PRO_CATEGORY, NbBundle.getMessage(BomanIndexFactory.class, "BomanIndex.desc"), "isbIndex", "setbIndex"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HydrophobicMomentFactory.class, "HydrophobicMoment.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicMomentFactory.class, "HydrophobicMoment.desc"), "ishMoment", "sethMoment"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(AverageHydrophilicityFactory.class, "AverageHydrophilicity.name"), PRO_CATEGORY, NbBundle.getMessage(AverageHydrophilicityFactory.class, "AverageHydrophilicity.desc"), "isAvgHydrophilicity", "setAvgHydrophilicity"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HydrophobicPeriodicityFactory.class, "HydrophobicPeriodicity.name"), PRO_CATEGORY, NbBundle.getMessage(HydrophobicPeriodicityFactory.class, "HydrophobicPeriodicity.desc"), "ishPeriodicity", "sethPeriodicity"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(AliphaticIndexFactory.class, "AliphaticIndex.name"), PRO_CATEGORY, NbBundle.getMessage(AliphaticIndexFactory.class, "AliphaticIndex.desc"), "isaIndex", "setaIndex"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(InestabilityIndexFactory.class, "InestabilityIndex.name"), PRO_CATEGORY, NbBundle.getMessage(InestabilityIndexFactory.class, "InestabilityIndex.desc"), "isiIndex", "setiIndex"));

            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(AACompositionFactory.class, "AAComposition.name"), PRO_CATEGORY, NbBundle.getMessage(AACompositionFactory.class, "AAComposition.desc"), "isAaComposition", "setAaComposition"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(DipeptideCompositionFactory.class, "DipeptideComposition.name"), PRO_CATEGORY, NbBundle.getMessage(DipeptideCompositionFactory.class, "DipeptideComposition.desc"), "isdComposition", "setdComposition"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RACompositionFactory.class, "RAComposition.name"), PRO_CATEGORY, NbBundle.getMessage(RACompositionFactory.class, "RAComposition.desc"), "isRaComposition", "setRaComposition"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RADistributionFactory.class, "RADistribution.name"), PRO_CATEGORY, NbBundle.getMessage(RADistributionFactory.class, "RA​​Distribution.desc"), "isRaDistribution", "setRaDistribution"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(RATransitionFactory.class, "RATransition.name"), PRO_CATEGORY, NbBundle.getMessage(RATransitionFactory.class, "RATransition.desc"), "isRaTransition", "setRaTransition"));
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(TripeptideCompositionFactory.class, "TripeptideComposition.name"), PRO_CATEGORY, NbBundle.getMessage(TripeptideCompositionFactory.class, "TripeptideComposition.desc"), "isTriComposition", "setTriComposition"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean isAaComposition() {
        return aaComposition;
    }

    public void setAaComposition(Boolean aaComposition) {
        this.aaComposition = aaComposition;
    }

    public boolean isaIndex() {
        return aIndex;
    }

    public void setaIndex(Boolean aIndex) {
        this.aIndex = aIndex;
    }

    public boolean isAvgHydrophilicity() {
        return avgHydrophilicity;
    }

    public void setAvgHydrophilicity(Boolean avgHydrophilicity) {
        this.avgHydrophilicity = avgHydrophilicity;
    }

    public boolean isbIndex() {
        return bIndex;
    }

    public void setbIndex(Boolean bIndex) {
        this.bIndex = bIndex;
    }

    public boolean isdComposition() {
        return dComposition;
    }

    public void setdComposition(Boolean dComposition) {
        this.dComposition = dComposition;
    }

    public boolean ishMoment() {
        return hMoment;
    }

    public void sethMoment(Boolean hMoment) {
        this.hMoment = hMoment;
    }

    public boolean ishPeriodicity() {
        return hPeriodicity;
    }

    public void sethPeriodicity(Boolean hPeriodicity) {
        this.hPeriodicity = hPeriodicity;
    }

    public boolean isiIndex() {
        return iIndex;
    }

    public void setiIndex(Boolean iIndex) {
        this.iIndex = iIndex;
    }

    public boolean isiPoint() {
        return iPoint;
    }

    public void setiPoint(Boolean iPoint) {
        this.iPoint = iPoint;
    }

    public boolean ismWeight() {
        return mWeight;
    }

    public void setmWeight(Boolean mWeight) {
        this.mWeight = mWeight;
    }

    public boolean isNetCharge() {
        return netCharge;
    }

    public void setNetCharge(Boolean netCharge) {
        this.netCharge = netCharge;
    }

    public boolean isRaComposition() {
        return raComposition;
    }

    public void setRaComposition(Boolean raComposition) {
        this.raComposition = raComposition;
    }

    public boolean isRaDistribution() {
        return raDistribution;
    }

    public void setRaDistribution(Boolean raDistribution) {
        this.raDistribution = raDistribution;
    }

    public boolean isRaTransition() {
        return raTransition;
    }

    public void setRaTransition(Boolean raTransition) {
        this.raTransition = raTransition;
    }

    public boolean isTriComposition() {
        return triComposition;
    }

    public void setTriComposition(Boolean triComposition) {
        this.triComposition = triComposition;
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        map.clear();
        // fill hashmap
        Workspace currentWs = pc.getCurrentWorkspace();
        for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
            final AlgorithmFactory f = it.next();
            if (this.factory != f) {
                Algorithm algorithm = f.createAlgorithm();
                if (algorithm instanceof AbstractModamp) {
                    AbstractModamp algoModamp = (AbstractModamp) algorithm;
                    // check for an existing algorithm 
                    Collection<? extends Algorithm> savedAlgo = currentWs.getLookup().lookupAll(Algorithm.class);
                    for (Algorithm algo : savedAlgo) {
                        if (algo instanceof AbstractModamp && algo.getFactory() == f) {
                            algoModamp = (AbstractModamp) algo;
                            break;
                        }
                    }
                    map.put(f.getName(), algoModamp);
                }
            }
        }

        // Remove unselected algorithms
        if (!aaComposition) {
            map.remove(NbBundle.getMessage(AACompositionFactory.class, "AAComposition.name"));
        }

        if (!aIndex) {
            map.remove(NbBundle.getMessage(AliphaticIndexFactory.class, "AliphaticIndex.name"));
        }

        if (!avgHydrophilicity) {
            map.remove(NbBundle.getMessage(AverageHydrophilicityFactory.class, "AverageHydrophilicity.name"));
        }

        if (!bIndex) {
            map.remove(NbBundle.getMessage(BomanIndexFactory.class, "BomanIndex.name"));
        }

        if (!dComposition) {
            map.remove(NbBundle.getMessage(DipeptideCompositionFactory.class, "DipeptideComposition.name"));
        }

        if (!hMoment) {
            map.remove(NbBundle.getMessage(HydrophobicMomentFactory.class, "HydrophobicMoment.name"));
        }

        if (!hPeriodicity) {
            map.remove(NbBundle.getMessage(HydrophobicPeriodicityFactory.class, "HydrophobicPeriodicity.name"));
        }

        if (!iIndex) {
            map.remove(NbBundle.getMessage(InestabilityIndexFactory.class, "InestabilityIndex.name"));
        }

        if (!iPoint) {
            map.remove(NbBundle.getMessage(IsoelectricPointFactory.class, "IsoelectricPoint.name"));
        }

        if (!mWeight) {
            map.remove(NbBundle.getMessage(MolecularWeightFactory.class, "MolecularWeight.name"));
        }

        if (!netCharge) {
            map.remove(NbBundle.getMessage(NetChargeFactory.class, "NetCharge.name"));
        }

        if (!raComposition) {
            map.remove(NbBundle.getMessage(RACompositionFactory.class, "RAComposition.name"));
        }

        if (!raDistribution) {
            map.remove(NbBundle.getMessage(RADistributionFactory.class, "RADistribution.name"));
        }

        if (!raTransition) {
            map.remove(NbBundle.getMessage(RATransitionFactory.class, "RATransition.name"));
        }

        if (!triComposition) {
            map.remove(NbBundle.getMessage(TripeptideCompositionFactory.class, "TripeptideComposition.name"));
        }

        // Init all algoritms
        for (AbstractModamp algo : map.values()) {
            algo.initAlgo();
        }

    }

    @Override
    public void compute(Peptide peptide) {
        for (AbstractModamp algo : map.values()) {
            algo.compute(peptide);
        }
    }

    @Override
    public void endAlgo() {
        super.endAlgo();
        // Init all algoritms
        for (AbstractModamp algo : map.values()) {
            algo.endAlgo();
        }
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

}
