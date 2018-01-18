/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SimilarityNetworkWizard1 implements
        WizardDescriptor.Panel<WizardDescriptor>,
        WizardDescriptor.ValidatingPanel<WizardDescriptor>,
        PropertyChangeListener{
    
    private final EventListenerList listeners = new EventListenerList();
    private boolean isValid = false;
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SimilarityNetworkPanel1 component;    

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.    
    @Override
    public SimilarityNetworkPanel1 getComponent() {
        if (component == null) {
            component = new SimilarityNetworkPanel1();
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
        getComponent().addPropertyChangeListener(this);

    }

    @Override
    public void storeSettings(WizardDescriptor data) {
        // use wiz.putProperty to remember current panel state
//        wiz.putProperty(NewProjectVisualPanel1.PROJECT_TYPE, getComponent().getProjectInfo());        

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
    public void addChangeListener(ChangeListener cl) {
        listeners.add(ChangeListener.class, cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        listeners.remove(ChangeListener.class, cl);
    }

    @Override
    public void validate() throws WizardValidationException {
        if (getComponent().getAlgorithmFactory() == null) {
            throw new WizardValidationException(null, NbBundle.getMessage(SimilarityNetworkWizard1.class, "SimilarityNetworkWizard1.invalidNetworkType"), null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        if (evt.getPropertyName().equals(SimilarityNetworkPanel1.NETWORK_FACTORY)) {
            isValid = evt.getNewValue() != null;
        }
        if (oldState != isValid){
            ChangeEvent srcEvt = new ChangeEvent(evt);
            for(ChangeListener listener: listeners.getListeners(ChangeListener.class)){
                listener.stateChanged(srcEvt);
            }
        }

    }
    
}
