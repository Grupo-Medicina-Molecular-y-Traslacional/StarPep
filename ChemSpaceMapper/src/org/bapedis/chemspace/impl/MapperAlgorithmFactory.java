/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.text.MessageFormat;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.chemspace.model.FeatureSelectionOption;
import org.bapedis.chemspace.model.InputSequenceOption;
import org.bapedis.chemspace.model.RemovingRedundantOption;
import org.bapedis.chemspace.model.SimilaritySearchingOption;
import org.bapedis.chemspace.searching.ChemBaseSimilaritySearchAlg;
import org.bapedis.chemspace.searching.EmbeddingQuerySeqAlg;
import org.bapedis.chemspace.wizard.MyWizardIterator;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.ChemSpaceTag;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlg;
import org.bapedis.core.spi.alg.impl.FilteringSubsetOptimization;
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
                 
        //Input sequences
        InputSequenceOption inputOption = (InputSequenceOption) wiz.getProperty(InputSequenceOption.class.getName());
        if (inputOption != null){
            csMapper.setInputOption(inputOption);
        }
        
        RemovingRedundantOption nrdOption = (RemovingRedundantOption) wiz.getProperty(RemovingRedundantOption.class.getName());
        if(nrdOption != null){
            csMapper.setNrdOption(nrdOption);
            if (nrdOption == RemovingRedundantOption.YES){
                NonRedundantSetAlg alg = (NonRedundantSetAlg) wiz.getProperty(NonRedundantSetAlg.class.getName());
                csMapper.setNonRedundantAlg(alg);
            }
        }
        
        //Similarity searching
        SimilaritySearchingOption searchingOption = (SimilaritySearchingOption) wiz.getProperty(SimilaritySearchingOption.class.getName());
        if (searchingOption != null) {
            csMapper.setSearchingOption(searchingOption);
            if (searchingOption == SimilaritySearchingOption.YES) {
                EmbeddingQuerySeqAlg embeddingQueryAlg = (EmbeddingQuerySeqAlg) wiz.getProperty(EmbeddingQuerySeqAlg.class.getName());
                csMapper.setEmbeddingQueryAlg(embeddingQueryAlg);
                
                ChemBaseSimilaritySearchAlg searchingAlg = (ChemBaseSimilaritySearchAlg) wiz.getProperty(ChemBaseSimilaritySearchAlg.class.getName());
                csMapper.setSimSearchingAlg(searchingAlg);
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
                FilteringSubsetOptimization alg = (FilteringSubsetOptimization) wiz.getProperty(FilteringSubsetOptimization.class.getName());
                csMapper.setFeatureSelectionAlg(alg);
            }
        }

        // Distance  
        AbstractDistance distFunction = (AbstractDistance) wiz.getProperty(AbstractDistance.class.getName());
        if (distFunction != null) {
            csMapper.setDistanceFunction(distFunction);
        }                
        
        //Network type
        NetworkConstructionAlg networkAlg = (NetworkConstructionAlg) wiz.getProperty(NetworkConstructionAlg.class.getName());
        if (networkAlg != null){
            csMapper.setNetworkAlg(networkAlg);
        }        
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
