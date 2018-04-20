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
import org.bapedis.chemspace.spi.SimilarityMeasureFactory;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class WizardSimilarityMeasure implements WizardDescriptor.ValidatingPanel<WizardDescriptor>,
        PropertyChangeListener {

    private final MapperAlgorithm csMapper;
    private CSNEmbedder alg;
    private final EventListenerList listeners = new EventListenerList();
    private boolean isValid;
    private WizardDescriptor model;

    public WizardSimilarityMeasure(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        isValid = true;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualSimilarityMeasure component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualSimilarityMeasure getComponent() {
        if (component == null) {
            component = new VisualSimilarityMeasure();
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
        this.model = wiz;
        alg = (CSNEmbedder) wiz.getProperty(AbstractEmbedder.class.getName());
        if (alg.getSimMeasure() != null) {
            getComponent().setFactory(alg.getSimMeasure().getFactory());
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        SimilarityMeasureFactory factory = component.getSimilarityMeasureFactory();
        if (factory != null) {
            alg.setSimMeasure(factory.createAlgorithm());
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        if (getComponent().getSimilarityMeasureFactory() == null) {
            isValid = false;
            throw new WizardValidationException(null, NbBundle.getMessage(WizardSimilarityMeasure.class, "VisualSimilarityMeasure.invalid.text"), null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {        
        if (evt.getPropertyName().equals(VisualSimilarityMeasure.NETWORK_FACTORY)) {
            boolean oldState = isValid;
            isValid = evt.getNewValue() != null;
            if (oldState != isValid) {
                ChangeEvent srcEvt = new ChangeEvent(evt);
                for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                    listener.stateChanged(srcEvt);
                }
            }
            if (isValid) {
                model.getNotificationLineSupport().setErrorMessage(null);
            }
        }
    }

}
