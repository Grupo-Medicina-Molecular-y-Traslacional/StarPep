/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import javax.swing.event.ChangeListener;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.model.InputSequenceOption;
import org.bapedis.chemspace.model.RemovingRedundantOption;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlg;
import org.bapedis.core.ui.components.SequenceAlignmentPanel;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class WizardInputSequence implements WizardDescriptor.FinishablePanel<WizardDescriptor> {

    private final MapperAlgorithm csMapper;
    private  NonRedundantSetAlg alg;

    public WizardInputSequence(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualInputSequence component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualInputSequence getComponent() {
        if (component == null) {
            try {
                alg = (NonRedundantSetAlg) csMapper.getNonRedundantAlg().clone();
                component = new VisualInputSequence(new SequenceAlignmentPanel(alg.getAlignmentModel()));
            } catch (CloneNotSupportedException ex) {
                Exceptions.printStackTrace(ex);
                alg = null;
            }
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
        InputSequenceOption inputOption = (InputSequenceOption) wiz.getProperty(InputSequenceOption.class.getName());
        if (inputOption == null) {
            inputOption = csMapper.getInputOption();
        }
        getComponent().setInputOption(inputOption);
      
        RemovingRedundantOption nrdOption = (RemovingRedundantOption) wiz.getProperty(RemovingRedundantOption.class.getName());
        if (nrdOption == null) {
            nrdOption = csMapper.getNrdOption();
        }
        getComponent().setNrdOption(nrdOption);        
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        InputSequenceOption inputOption = component.getInputOption();
        wiz.putProperty(InputSequenceOption.class.getName(), inputOption);

        RemovingRedundantOption nrdOption = component.getNrdOption();
        wiz.putProperty(RemovingRedundantOption.class.getName(), nrdOption);
        
        if (nrdOption == RemovingRedundantOption.YES) {
            wiz.putProperty(NonRedundantSetAlg.class.getName(), alg);
        } else {
            wiz.putProperty(NonRedundantSetAlg.class.getName(), null);
        }
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

}
