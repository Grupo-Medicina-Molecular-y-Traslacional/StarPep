/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.chemspace.impl.AbstractEmbedder;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.impl.CSNEmbedder;
import org.bapedis.chemspace.impl.NetworkEmbedder;
import org.bapedis.chemspace.impl.SSNEmbedder;
import org.bapedis.chemspace.impl.TwoDEmbedder;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.bapedis.chemspace.model.Representation;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class WizardRepresentation implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, WizardDescriptor.FinishablePanel<WizardDescriptor>, PropertyChangeListener {

    private final MapperAlgorithm csMapper;
    private CSNEmbedder csnEmbedder;
    private TwoDEmbedder twoDEmbedder;
    private SSNEmbedder ssnEmbedder;
    private boolean valid;
    private final EventListenerList listeners;
    private WizardDescriptor model;

    public WizardRepresentation(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        valid = check(csMapper.getRepresentation(), csMapper.getChemSpaceOption());
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
                component.addPropertyChangeListener(this);

                twoDEmbedder = (TwoDEmbedder) csMapper.getTwoDEmbedderAlg().clone();
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
        return valid;
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
        this.model = wiz;

        // Setting fore representation
        Representation representation = (Representation) wiz.getProperty(Representation.class.getName());
        if (representation == null) {
            representation = csMapper.getRepresentation();
        }
        getComponent().setRepresentation(representation);

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
        Representation representation = component.getRepresentation();
        wiz.putProperty(Representation.class.getName(), representation);
                
        ChemSpaceOption csOption = component.getChemSpaceOption();
        wiz.putProperty(ChemSpaceOption.class.getName(), csOption);
        
        switch (csOption) {
            case TwoD_SPACE:
                wiz.putProperty(AbstractEmbedder.class.getName(), twoDEmbedder);
                break;
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

    private boolean check(Representation representation, ChemSpaceOption csSpaceoption) {
        switch (representation) {
            case COORDINATE_BASED:
                switch (csSpaceoption) {
                    case TwoD_SPACE:
                    case ThreeD_SPACE:
                        return true;
                    default:
                        return false;
                }
            case COORDINATE_FREE:
                switch (csSpaceoption) {
                    case CHEM_SPACE_NETWORK:
                    case SEQ_SIMILARITY_NETWORK:
                        return true;
                    default:
                        return false;
                }
        }
        return false;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = valid;
        if (evt.getPropertyName().equals(VisualRepresentation.CHANGED_REPRESENTATION)
                || evt.getPropertyName().equals(VisualRepresentation.CHANGED_CHEM_SPACE)) {
            model.getNotificationLineSupport().setWarningMessage(null);
            valid = check(component.getRepresentation(), component.getChemSpaceOption());
        }
        if (oldState != valid) {
            ChangeEvent srcEvt = new ChangeEvent(evt);
            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(srcEvt);
            }
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        switch (component.getRepresentation()) {
            case COORDINATE_BASED:
                switch (component.getChemSpaceOption()) {
                    case TwoD_SPACE:                        
                    case ThreeD_SPACE:
                        return;
                    default:
                        throw new WizardValidationException(component, NbBundle.getMessage(WizardFeatureExtraction.class, "VisualRepresentation.invalidNetwork.text"), null);
                }
            case COORDINATE_FREE:
                switch (component.getChemSpaceOption()) {
                    case CHEM_SPACE_NETWORK:
                    case SEQ_SIMILARITY_NETWORK:
                        return;
                    default:
                        throw new WizardValidationException(component, NbBundle.getMessage(WizardFeatureExtraction.class, "VisualRepresentation.invalidNetwork.text"), null);
                }
            default:
                throw new WizardValidationException(component, NbBundle.getMessage(WizardFeatureExtraction.class, "VisualRepresentation.invalidNetwork.text"), null);
        }
    }

    @Override
    public boolean isFinishPanel() {
        ChemSpaceOption csOption = component.getChemSpaceOption();
        Representation rep = component.getRepresentation();
        return (rep == Representation.COORDINATE_BASED && (csOption == ChemSpaceOption.TwoD_SPACE)) ||
               (rep == Representation.COORDINATE_FREE && (csOption == ChemSpaceOption.CHEM_SPACE_NETWORK || 
                                                          csOption == ChemSpaceOption.SEQ_SIMILARITY_NETWORK));
    }

}
