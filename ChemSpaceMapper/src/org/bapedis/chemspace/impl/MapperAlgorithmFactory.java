/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.text.MessageFormat;
import org.bapedis.chemspace.wizard.MyWizardIterator;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class)
public class MapperAlgorithmFactory implements AlgorithmFactory {

    private final MapperAlgorithmPanel setupUI = new MapperAlgorithmPanel();
    
    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.ChemicalSpace;
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
        MapperAlgorithm csnAlgo = new MapperAlgorithm(this);   
        
        // Create wizard
        WizardDescriptor wiz = createWizardDescriptor(csnAlgo);
        
        //The image in the left sidebar of the wizard is set like this:
        //wiz.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("org/demo/wizard/banner.PNG", true));
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            setUp(csnAlgo, wiz);
           return csnAlgo;            
        }
        return null;
    }
    
    public static void setUp(MapperAlgorithm csnAlgo, WizardDescriptor wiz){
        
    }
    
    public static WizardDescriptor  createWizardDescriptor(MapperAlgorithm csnAlgo){
        // Wizard iterator
        WizardDescriptor.Iterator<WizardDescriptor> iterator = new MyWizardIterator(csnAlgo);

        // Open wizard
        WizardDescriptor wiz = new WizardDescriptor(iterator);                        
        
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(MapperAlgorithmFactory.class, "MapperAlgorithm.wizard.title")); 
        
        return wiz;
    }

}
