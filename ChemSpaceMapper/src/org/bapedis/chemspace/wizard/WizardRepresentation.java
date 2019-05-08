/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.chemspace.impl.AbstractEmbedder;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.impl.CSNEmbedder;
import org.bapedis.chemspace.impl.NetworkEmbedder;
import org.bapedis.chemspace.impl.SSNEmbedder;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class WizardRepresentation implements WizardDescriptor.FinishablePanel<WizardDescriptor> {

    private final MapperAlgorithm csMapper;
    private CSNEmbedder csnEmbedder;
    private SSNEmbedder ssnEmbedder;
    private final EventListenerList listeners;

    public WizardRepresentation(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        listeners = new EventListenerList();
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualRepresentation component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualRepresentation getComponent() {
        if (component == null) {
            try {
                component = new VisualRepresentation();

                csnEmbedder = (CSNEmbedder) csMapper.getCSNEmbedderAlg().clone();
                ssnEmbedder = (SSNEmbedder) csMapper.getSSNEmbedderAlg().clone();
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
        listeners.add(ChangeListener.class, l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state

        //Setting for Chemspace Option
        ChemSpaceOption csOption = (ChemSpaceOption) wiz.getProperty(ChemSpaceOption.class.getName());
        if (csOption == null) {
            csOption = csMapper.getChemSpaceOption();
        }
        getComponent().setChemSpaceOption(csOption);

        //Setting for Network embedder  
        NetworkEmbedder netEmbedder = null;
        switch (csOption) {
            case CHEM_SPACE_NETWORK:
                netEmbedder = csnEmbedder;
                break;
            case SEQ_SIMILARITY_NETWORK:
                netEmbedder = ssnEmbedder;
                break;
        }
        if (netEmbedder != null) {
            component.setNetworkType(netEmbedder.getNetworkType());
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state                
        ChemSpaceOption csOption = component.getChemSpaceOption();
        wiz.putProperty(ChemSpaceOption.class.getName(), csOption);
        
        switch (csOption) {
            case CHEM_SPACE_NETWORK:
                csnEmbedder.setNetworkType(component.getNetworkType());
                wiz.putProperty(AbstractEmbedder.class.getName(), csnEmbedder);
                break;
            case SEQ_SIMILARITY_NETWORK:
                ssnEmbedder.setNetworkType(component.getNetworkType());
                wiz.putProperty(AbstractEmbedder.class.getName(), ssnEmbedder);
                break;
            default:
                wiz.putProperty(AbstractEmbedder.class.getName(), null);
                break;
        }
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

}
