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
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.impl.NetworkEmbedder;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.project.ProjectManager;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class WizardRepresentation implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, PropertyChangeListener {

    private final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final MapperAlgorithm csMapper;
    private boolean isValid;
    private final EventListenerList listeners;

    public WizardRepresentation(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        isValid = true;
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
            component = new VisualRepresentation();
            component.setChemSpaceOption(csMapper.getChemSpaceOption());
            component.addPropertyChangeListener(this);
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
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        wiz.putProperty(ChemSpaceOption.class.getName(), component.getChemSpaceOption());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VisualRepresentation.CHANGED_OPTION)) {
            boolean oldState = isValid;
            switch ((ChemSpaceOption) evt.getNewValue()) {
//                case FULL_NETWORK:
//                    AttributesModel attrModel = pc.getAttributesModel();
//                    isValid = attrModel.getPeptides().size() <= NetworkEmbedder.MAX_NODES;                    
//                    break;
                case NONE:
                    isValid = false;
                    break;
                default:
                    isValid = true;
                    break;
            }
            if (oldState != isValid) {
                ChangeEvent srcEvt = new ChangeEvent(evt);
                for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                    listener.stateChanged(srcEvt);
                }
            }
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        switch (component.getChemSpaceOption()) {
            case FULL_NETWORK:
                AttributesModel attrModel = pc.getAttributesModel();
                if (attrModel.getPeptides().size() > NetworkEmbedder.MAX_NODES) {
                    throw new WizardValidationException(component, NbBundle.getMessage(WizardFeatureExtraction.class, "VisualRepresentation.invalidFullNetwork.text", String.valueOf(NetworkEmbedder.MAX_NODES)), null);
                }
                break;
            case NONE:
                throw new WizardValidationException(component, NbBundle.getMessage(WizardFeatureExtraction.class, "VisualRepresentation.invalidOption.text", String.valueOf(NetworkEmbedder.MAX_NODES)), null);
        }
    }

}