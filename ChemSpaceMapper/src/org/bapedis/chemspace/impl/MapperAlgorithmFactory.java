/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.text.MessageFormat;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.chemspace.model.FeatureFilteringOption;
import org.bapedis.chemspace.wizard.MyWizardIterator;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.ChemSpaceTag;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.FeatureFiltering;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class)
public class MapperAlgorithmFactory implements AlgorithmFactory, ChemSpaceTag {

    private MapperAlgorithmSetupUI setupUI;
    
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
        if (setupUI == null){
           setupUI  = new MapperAlgorithmSetupUI();
        }
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        MapperAlgorithm csMapper = new MapperAlgorithm(this);   
        
        // Create wizard
        WizardDescriptor wiz = createWizardDescriptor(csMapper);
        
        //The image in the left sidebar of the wizard is set like this:
        //wiz.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("org/demo/wizard/banner.PNG", true));
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            setUp(csMapper, wiz);
           return csMapper;            
        }
        return null;
    }
    
    public static void setUp(MapperAlgorithm csMapper, WizardDescriptor wiz){
        // Chemical Space Option
        ChemSpaceOption csOption = (ChemSpaceOption)wiz.getProperty(ChemSpaceOption.class.getName());
        csMapper.setChemSpaceOption(csOption);
        
        // Feature Extraction Option
        FeatureExtractionOption feOption = (FeatureExtractionOption)wiz.getProperty(FeatureExtractionOption.class.getName());
        csMapper.setFEOption(feOption);
        if (feOption == FeatureExtractionOption.NEW){
            AllDescriptors alg = (AllDescriptors)wiz.getProperty(AllDescriptors.class.getName());
            csMapper.setFeatureExtractionAlg(alg);
        }
        
        // Feature Filtering Option
        FeatureFilteringOption ffOption = (FeatureFilteringOption)wiz.getProperty(FeatureFilteringOption.class.getName());
        csMapper.setFFOption(ffOption);
        if (ffOption == FeatureFilteringOption.YES){
            FeatureFiltering alg = (FeatureFiltering)wiz.getProperty(FeatureFiltering.class.getName());
            csMapper.setFeatureFilteringAlg(alg);
        }     
        
        //Chemical Space Embbeder
        AbstractEmbedder embedder;
        switch(csOption){
            case N_DIMENSIONAL:
                embedder = (TwoDEmbedder)wiz.getProperty(TwoDEmbedder.class.getName());
                break;
            case FULL_NETWORK:
            case COMPRESSED_NETWORK:
                embedder = (NetworkEmbedder)wiz.getProperty(NetworkEmbedder.class.getName());
                break;
            default:
                embedder = null;
        }
        csMapper.setChemSpaceEmbedderAlg(embedder);
    }
    
    public static WizardDescriptor  createWizardDescriptor(MapperAlgorithm csMapper){
        // Wizard iterator
        MyWizardIterator iterator = new MyWizardIterator(csMapper);        

        // Open wizard
        WizardDescriptor wiz = new WizardDescriptor(iterator);        
        iterator.initialize(wiz);
        iterator.setChemSpaceOption(csMapper.getChemSpaceOption());
        
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(MapperAlgorithmFactory.class, "MapperAlgorithm.wizard.title")); 
        
        return wiz;
    }

}
