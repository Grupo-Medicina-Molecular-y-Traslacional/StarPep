/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.impl.AllDescriptors;
import org.bapedis.network.impl.CSNAlgorithm;
import org.bapedis.network.model.WizardOptionModel;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class CSNWizardMolecularDescriptor implements WizardDescriptor.ValidatingPanel<WizardDescriptor>,
        PropertyChangeListener {

    private final CSNAlgorithm csnAlgo;
    private final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final EventListenerList listeners = new EventListenerList();
    private boolean isValid;

    public CSNWizardMolecularDescriptor(CSNAlgorithm csnAlgo) {
        this.csnAlgo = csnAlgo;
        isValid = true;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CSNVisualMolecularDescriptor component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public CSNVisualMolecularDescriptor getComponent() {
        if (component == null) {
            WizardOptionModel optionModel = csnAlgo.getMdOptionModel();
            AllDescriptors algo = csnAlgo.getDescriptorAlgo();
            JPanel settingPanel = algo.getFactory().getSetupUI().getSettingPanel(algo);

            component = new CSNVisualMolecularDescriptor(optionModel, settingPanel);
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
    public void readSettings(WizardDescriptor data) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor data) {
        // use wiz.putProperty to remember current panel state
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
    public void validate() throws WizardValidationException {
        if (getComponent().getSelectedOption() == WizardOptionModel.MolecularDescriptorOption.AVAILABLE &&
            getAvailableFeatureSize() < CSNAlgorithm.MIN_AVAILABLE_FEATURES){
            isValid = false;
            throw new WizardValidationException(null, NbBundle.getMessage(CSNWizardMolecularDescriptor.class, "CSNWizardMolecularDescriptor.invalidOption.text"), null);            
        }
    }

    private int getAvailableFeatureSize() {
        AttributesModel attrModel = pc.getAttributesModel();
        List<MolecularDescriptor> featureList = new LinkedList<>();
        // Populate feature list                
        for (String key : attrModel.getMolecularDescriptorKeys()) {
            for (MolecularDescriptor desc : attrModel.getMolecularDescriptors(key)) {
                featureList.add(desc);
            }
        }
        return featureList.size();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        if (evt.getPropertyName().equals(CSNVisualMolecularDescriptor.MD_OPTION)){
            switch((WizardOptionModel.MolecularDescriptorOption)evt.getNewValue()){
                case NEW:
                    isValid = true;
                    break;
            }
        }
        if (oldState != isValid) {
            ChangeEvent srcEvt = new ChangeEvent(evt);
            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(srcEvt);
            }
        }        
    }



}
