/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.text.MessageFormat;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.chemspace.model.FeatureSelectionOption;
import org.bapedis.chemspace.model.RemovingRedundantOption;
import org.bapedis.chemspace.similarity.AbstractSimCoefficient;
import org.bapedis.chemspace.wizard.MyWizardIterator;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.ChemSpaceTag;
import org.bapedis.core.spi.alg.impl.AbstractClusterizer;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.FeatureSEFiltering;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlg;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 0)
public class MapperAlgorithmFactory implements AlgorithmFactory, ChemSpaceTag {

    private MapperAlgorithmPanel setupUI = new MapperAlgorithmPanel();

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(MapperAlgorithm.class, "MapperAlgorithm.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(MapperAlgorithm.class, "MapperAlgorithm.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new MapperAlgorithm(this);
    }

    public static void setUp(MapperAlgorithm csMapper, WizardDescriptor wiz) {
        
        //Non-redundant set
        RemovingRedundantOption nrdOption = (RemovingRedundantOption) wiz.getProperty(RemovingRedundantOption.class.getName());
        if (nrdOption != null) {
            csMapper.setNrdOption(nrdOption);
            if (nrdOption == RemovingRedundantOption.YES) {
                NonRedundantSetAlg nrdAlg = (NonRedundantSetAlg) wiz.getProperty(NonRedundantSetAlg.class.getName());
                csMapper.setNonRedundantSetAlg(nrdAlg);
            }
        }

        // Feature Extraction Option
        FeatureExtractionOption feOption = (FeatureExtractionOption) wiz.getProperty(FeatureExtractionOption.class.getName());
        if (feOption != null) {
            csMapper.setFEOption(feOption);
            if (feOption == FeatureExtractionOption.YES) {
                AllDescriptors alg = (AllDescriptors) wiz.getProperty(AllDescriptors.class.getName());
                csMapper.setFeatureExtractionAlg(alg);
            }
        }

        // Feature Filtering Option
        FeatureSelectionOption ffOption = (FeatureSelectionOption) wiz.getProperty(FeatureSelectionOption.class.getName());
        if (ffOption != null) {
            csMapper.setFSOption(ffOption);
            if (ffOption == FeatureSelectionOption.YES) {
                FeatureSEFiltering alg = (FeatureSEFiltering) wiz.getProperty(FeatureSEFiltering.class.getName());
                csMapper.setFeatureSelectionAlg(alg);
            }
        }

        // Clustering
        AbstractClusterizer clutering = (AbstractClusterizer) wiz.getProperty(AbstractClusterizer.class.getName());
        csMapper.setClusteringAlg(clutering);

        // Similarity  
        AbstractSimCoefficient simCoefficient = (AbstractSimCoefficient) wiz.getProperty(AbstractSimCoefficient.class.getName());
        csMapper.setSimCoefficientAlg(simCoefficient);
    }

    public static WizardDescriptor createWizardDescriptor(MapperAlgorithm csMapper) {
        // Wizard iterator
        MyWizardIterator iterator = new MyWizardIterator(csMapper);

        // Open wizard
        WizardDescriptor wiz = new WizardDescriptor(iterator);
        iterator.initialize(wiz);

        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(MapperAlgorithmFactory.class, "MapperAlgorithm.wizard.title"));

        return wiz;
    }

}
