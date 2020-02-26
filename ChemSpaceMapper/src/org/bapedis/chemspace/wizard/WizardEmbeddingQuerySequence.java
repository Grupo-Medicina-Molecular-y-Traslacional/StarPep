/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.model.SimilaritySearchingOption;
import org.bapedis.chemspace.searching.EmbeddingQuerySeqAlg;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 *
 * @author Loge
 */
public class WizardEmbeddingQuerySequence implements WizardDescriptor.FinishablePanel<WizardDescriptor> {

    private final MapperAlgorithm csMapper;
    private EmbeddingQuerySeqAlg alg;
    private VisualEmbeddingQuerySequence component;
    
    public WizardEmbeddingQuerySequence(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
    }
    
    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public VisualEmbeddingQuerySequence getComponent() {
        if (component == null) {
            try {
                alg = (EmbeddingQuerySeqAlg) csMapper.getEmbeddingQueryAlg().clone();
                JPanel settingPanel = alg.getFactory().getSetupUI().getSettingPanel(alg);
                component = new VisualEmbeddingQuerySequence(settingPanel);
//                component.setSearchingOption(csMapper.getSearchingOption());
            } catch (CloneNotSupportedException ex) {
                Exceptions.printStackTrace(ex);
                alg = null;
            }
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
        SimilaritySearchingOption searchingOption = (SimilaritySearchingOption) wiz.getProperty(SimilaritySearchingOption.class.getName());
        if (searchingOption == null) {
//            searchingOption = csMapper.getSearchingOption();
        }
        getComponent().setSearchingOption(searchingOption);        
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {        
        SimilaritySearchingOption searchingOption = getComponent().getSearchingOption();
        wiz.putProperty(SimilaritySearchingOption.class.getName(), searchingOption);
        if (searchingOption == SimilaritySearchingOption.YES) {
            wiz.putProperty(EmbeddingQuerySeqAlg.class.getName(), alg);
        } else{
            wiz.putProperty(EmbeddingQuerySeqAlg.class.getName(), null);
        }        
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }
    
}
