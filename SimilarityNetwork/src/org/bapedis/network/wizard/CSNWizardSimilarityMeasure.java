/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.network.impl.CSNAlgorithm;
import org.bapedis.network.spi.SimilarityMeasureFactory;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class CSNWizardSimilarityMeasure implements WizardDescriptor.ValidatingPanel<WizardDescriptor>,
        PropertyChangeListener {

    private final CSNAlgorithm csnAlgo;
    private final EventListenerList listeners = new EventListenerList();
    private boolean isValid;

    public CSNWizardSimilarityMeasure(CSNAlgorithm csnAlgo) {
        this.csnAlgo = csnAlgo;
        isValid = true;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CSNVisualSimilarityMeasure component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public CSNVisualSimilarityMeasure getComponent() {
        if (component == null) {
            component = new CSNVisualSimilarityMeasure();
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
        SimilarityMeasureFactory factory = component.getSimilarityMeasureFactory();
        if (factory != null) {
            csnAlgo.setSimMeasure(factory.createAlgorithm());
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        if (getComponent().getSimilarityMeasureFactory() == null) {
            isValid = false;
            throw new WizardValidationException(null, NbBundle.getMessage(CSNWizardSimilarityMeasure.class, "CSNVisualSimilarityMeasure.invalid.text"), null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        if (evt.getPropertyName().equals(CSNVisualSimilarityMeasure.NETWORK_FACTORY)) {
            isValid = evt.getNewValue() != null;
        }
        if (oldState != isValid) {
            ChangeEvent srcEvt = new ChangeEvent(evt);
            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(srcEvt);
            }
        }
    }

}
