/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class WizardFeatureExtraction implements WizardDescriptor.ValidatingPanel<WizardDescriptor>,
        WizardDescriptor.FinishablePanel<WizardDescriptor>, PropertyChangeListener {

    private final MapperAlgorithm csMapper;
    private AllDescriptors alg;
    private final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final EventListenerList listeners;
    private boolean isValid;
    private WizardDescriptor model;

    public WizardFeatureExtraction(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        isValid = true;
        listeners = new EventListenerList();
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualFeatureExtraction component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualFeatureExtraction getComponent() {
        if (component == null) {
            try {
                alg = (AllDescriptors) csMapper.getFeatureExtractionAlg().clone();
                JPanel settingPanel = alg.getFactory().getSetupUI().getSettingPanel(alg);
                component = new VisualFeatureExtraction(settingPanel);
                component.addPropertyChangeListener(this);                
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
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
        this.model = wiz;
        FeatureExtractionOption feOption = (FeatureExtractionOption)wiz.getProperty(FeatureExtractionOption.class.getName());
        if (feOption == null){
            feOption = csMapper.getFEOption();
        }
        getComponent().setFEOption(feOption);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        wiz.putProperty(FeatureExtractionOption.class.getName(), component.getFEOption());
        wiz.putProperty(AllDescriptors.class.getName(), alg);
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
        if (component.getFEOption() == FeatureExtractionOption.AVAILABLE
                && getAvailableFeatureSize() < ProjectManager.MIN_AVAILABLE_FEATURES) {
            isValid = false;
            throw new WizardValidationException(null, NbBundle.getMessage(WizardFeatureExtraction.class, "WizardFeatureExtraction.invalidOption.text"), null);
        } else if (component.getFEOption() == FeatureExtractionOption.NEW) {
            if (alg.getDescriptorKeys().isEmpty()){
                throw new WizardValidationException(null, NbBundle.getMessage(WizardFeatureExtraction.class, "WizardFeatureExtraction.emptyKeys.info"), null);
            }
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
        if (evt.getPropertyName().equals(VisualFeatureExtraction.CHANGED_OPTION)) {
            boolean oldState = isValid;
            switch ((FeatureExtractionOption) evt.getNewValue()) {
                case NEW:
                    isValid = true;
                    break;
            }
            if (oldState != isValid) {
                ChangeEvent srcEvt = new ChangeEvent(evt);
                for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                    listener.stateChanged(srcEvt);
                }
            }
            if (isValid){
                model.getNotificationLineSupport().setErrorMessage(null);
            }
        }

    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

}
