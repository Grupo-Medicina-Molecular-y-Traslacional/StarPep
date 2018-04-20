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
import org.bapedis.chemspace.model.CompressedModel;
import org.bapedis.chemspace.model.NetworkType;
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
    private boolean isValid;
    private final EventListenerList listeners;
    private WizardDescriptor model;

    public WizardRepresentation(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        isValid = csMapper.getChemSpaceOption() != ChemSpaceOption.NONE;
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
        return isValid;
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
            CompressedModel compressedModel = netEmbedder.getCompressedModel();
            component.setCompressedStrategyIndex(compressedModel.getStrategyIndex());
            component.setCompressedMaxSuperNodes(compressedModel.getMaxSuperNodes());
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        ChemSpaceOption csOption = component.getChemSpaceOption();
        wiz.putProperty(ChemSpaceOption.class.getName(), csOption);
        CompressedModel compressedModel;
        switch (csOption) {
            case N_DIMENSIONAL_SPACE:
                wiz.putProperty(AbstractEmbedder.class.getName(), twoDEmbedder);
                break;            
            case CHEM_SPACE_NETWORK:
                csnEmbedder.setNetworkType(component.getNetworkType());
                compressedModel = csnEmbedder.getCompressedModel();
                compressedModel.setStrategyIndex(component.getCompressedStrategyIndex());
                compressedModel.setMaxSuperNodes(component.getCompressedMaxSuperNodes());
                wiz.putProperty(AbstractEmbedder.class.getName(), csnEmbedder);
                break;
            case SEQ_SIMILARITY_NETWORK:
                ssnEmbedder.setNetworkType(component.getNetworkType());
                compressedModel = ssnEmbedder.getCompressedModel();
                compressedModel.setStrategyIndex(component.getCompressedStrategyIndex());
                compressedModel.setMaxSuperNodes(component.getCompressedMaxSuperNodes());
                wiz.putProperty(AbstractEmbedder.class.getName(), ssnEmbedder);     
                break;
            case NONE:
                wiz.putProperty(AbstractEmbedder.class.getName(), null);
                break;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        if (evt.getPropertyName().equals(VisualRepresentation.CHANGED_CHEM_SPACE)) {
            model.getNotificationLineSupport().setWarningMessage(null);
            switch ((ChemSpaceOption) evt.getNewValue()) {
                case N_DIMENSIONAL_SPACE:
                    isValid = true;
                    break;
                case CHEM_SPACE_NETWORK:
                case SEQ_SIMILARITY_NETWORK:
                    if (component.getNetworkType() == NetworkType.FULL) {
                        model.getNotificationLineSupport().setWarningMessage(NbBundle.getMessage(WizardRepresentation.class, "VisualRepresentation.FNWaring.text"));
                    }
                    isValid = true;
                    break;
                case NONE:
                    isValid = false;
                    break;
                default:
                    isValid = false;
                    break;
            }
        } else if (evt.getPropertyName().equals(VisualRepresentation.CHANGED_NETWORK_TYPE)) {
            switch ((NetworkType) evt.getNewValue()) {
                case FULL:
                    ChemSpaceOption csOption = component.getChemSpaceOption();
                    if (csOption == ChemSpaceOption.CHEM_SPACE_NETWORK
                            || csOption == ChemSpaceOption.SEQ_SIMILARITY_NETWORK) {
                        model.getNotificationLineSupport().setWarningMessage(NbBundle.getMessage(WizardRepresentation.class, "VisualRepresentation.FNWaring.text"));
                    }
                    break;
                default:
                    model.getNotificationLineSupport().setWarningMessage(null);
            }
        }
        if (oldState != isValid) {
            ChangeEvent srcEvt = new ChangeEvent(evt);
            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(srcEvt);
            }
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        switch (component.getChemSpaceOption()) {            
            case CHEM_SPACE_NETWORK:
            case SEQ_SIMILARITY_NETWORK:
                switch (component.getNetworkType()) {
                    case NONE:
                        throw new WizardValidationException(component, NbBundle.getMessage(WizardFeatureExtraction.class, "VisualRepresentation.invalidOption.text"), null);
                }
                break;
            case NONE:
                throw new WizardValidationException(component, NbBundle.getMessage(WizardFeatureExtraction.class, "VisualRepresentation.invalidOption.text"), null);
        }
    }

    @Override
    public boolean isFinishPanel() {
        return component.getChemSpaceOption() != ChemSpaceOption.NONE;
    }

}
