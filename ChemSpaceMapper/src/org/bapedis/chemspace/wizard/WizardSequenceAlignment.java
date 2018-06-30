/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import javax.swing.event.ChangeListener;
import org.bapedis.chemspace.impl.AbstractEmbedder;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.impl.SSNEmbedder;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.ui.components.SequenceAlignmentPanel;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class WizardSequenceAlignment implements WizardDescriptor.Panel<WizardDescriptor> {

    private final MapperAlgorithm csMapper;
    private SequenceAlignmentModel alignmentModel;
    private SSNEmbedder ssnEmbedder;

    public WizardSequenceAlignment(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualSequenceAlignment component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualSequenceAlignment getComponent() {
        if (component == null) {
            try {
                alignmentModel = (SequenceAlignmentModel) csMapper.getSSNEmbedderAlg().getAlignmentModel().clone();
                component = new VisualSequenceAlignment(new SequenceAlignmentPanel(alignmentModel));
            } catch (CloneNotSupportedException ex) {
                Exceptions.printStackTrace(ex);
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
        ssnEmbedder = (SSNEmbedder)wiz.getProperty(AbstractEmbedder.class.getName());
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        if(ssnEmbedder != null){
            ssnEmbedder.setAlignmentModel(alignmentModel);
        }
    }

}
