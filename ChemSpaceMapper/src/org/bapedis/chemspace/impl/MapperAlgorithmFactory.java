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
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.SimilaritySearchingOption;
import org.bapedis.chemspace.searching.ChemBaseSimilaritySearchAlg;
import org.bapedis.chemspace.searching.EmbeddingQuerySeqAlg;
import org.bapedis.chemspace.wizard.MyWizardIterator;
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.ChemSpaceTag;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.UnsupervisedFeatureSelection;
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
        
        //Network type
        NetworkType networkType = (NetworkType) wiz.getProperty(NetworkType.class.getName());
        if (networkType != null){
            csMapper.getNetworkEmbedderAlg().setNetworkType(networkType);
        }

        //Similarity searching
        SimilaritySearchingOption searchingOption = (SimilaritySearchingOption) wiz.getProperty(SimilaritySearchingOption.class.getName());
        if (searchingOption != null) {
            csMapper.setSearchingOption(searchingOption);
            if (searchingOption == SimilaritySearchingOption.YES) {
                EmbeddingQuerySeqAlg embeddingQueryAlg = (EmbeddingQuerySeqAlg) wiz.getProperty(EmbeddingQuerySeqAlg.class.getName());
                csMapper.setEmbeddingQueryAlg(embeddingQueryAlg);
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
                UnsupervisedFeatureSelection alg = (UnsupervisedFeatureSelection) wiz.getProperty(UnsupervisedFeatureSelection.class.getName());
                csMapper.setFeatureSelectionAlg(alg);
            }
        }

        // Similarity  
        AbstractDistance distFunction = (AbstractDistance) wiz.getProperty(AbstractDistance.class.getName());
        MD_OUTPUT_OPTION option = (MD_OUTPUT_OPTION) wiz.getProperty(MD_OUTPUT_OPTION.class.getName());
        if (distFunction != null) {
            distFunction.setOption(option);
            csMapper.setDistanceFunction(distFunction);
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
