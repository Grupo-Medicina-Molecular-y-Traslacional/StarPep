/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.text.MessageFormat;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.network.wizard.CSNWizardIterator;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
//@ServiceProvider(service = AlgorithmFactory.class)
public class CSNAlgorithmFactory implements AlgorithmFactory {

    private final CSNAlgorithmPanel setupUI = new CSNAlgorithmPanel();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        CSNAlgorithm csnAlgo = new CSNAlgorithm(this);        
        WizardDescriptor wiz = createWizardDescriptor(csnAlgo);
        
        //The image in the left sidebar of the wizard is set like this:
        //wiz.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("org/demo/wizard/banner.PNG", true));
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
           return csnAlgo;            
        }
        return null;
    }
    
    public static WizardDescriptor  createWizardDescriptor(CSNAlgorithm csnAlgo){
        // Wizard iterator
        WizardDescriptor.Iterator<WizardDescriptor> iterator = new CSNWizardIterator(csnAlgo);

        // Open wizard
        WizardDescriptor wiz = new WizardDescriptor(iterator);
        
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(CSNAlgorithmFactory.class, "CSNAlgorithm.wizard.title")); 
        
        return wiz;
    }

}
