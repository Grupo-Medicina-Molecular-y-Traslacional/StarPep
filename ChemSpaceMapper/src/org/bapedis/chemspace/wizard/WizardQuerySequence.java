/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.core.spi.alg.impl.EmbeddingAlgorithm;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 *
 * @author Loge
 */
public class WizardQuerySequence implements WizardDescriptor.FinishablePanel<WizardDescriptor> {

    private final MapperAlgorithm csMapper;
    private EmbeddingAlgorithm alg;
    private VisualQuerySequence component;
    
    public WizardQuerySequence(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
    }
    
    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            try {
                alg = (EmbeddingAlgorithm) csMapper.getEmbeddingAlg().clone();
                JPanel settingPanel = alg.getFactory().getSetupUI().getSettingPanel(alg);
                component = new VisualQuerySequence(settingPanel);
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
    public void readSettings(WizardDescriptor data) {
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(EmbeddingAlgorithm.class.getName(), alg);
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
