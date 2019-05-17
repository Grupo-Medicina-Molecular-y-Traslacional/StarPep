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
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.impl.AbstractClusterizer;
import org.openide.util.Exceptions;

public class WizardClusterize implements WizardDescriptor.ValidatingPanel<WizardDescriptor>,
        PropertyChangeListener {

    private final MapperAlgorithm csMapper;
    private AbstractClusterizer alg;
    private final EventListenerList listeners = new EventListenerList();
    private boolean isValid;
    private WizardDescriptor model;

    public WizardClusterize(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        isValid = true;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualClusterize component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualClusterize getComponent() {
        if (component == null) {
            try {
                alg = (AbstractClusterizer) csMapper.getClusteringAlg().clone();
                component = new VisualClusterize();
                component.setClustering(alg);
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
        alg = (AbstractClusterizer) wiz.getProperty(AbstractClusterizer.class.getName());
        if (alg != null) {
            getComponent().setClustering(alg);
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        alg = component.getClustering();
        wiz.putProperty(AbstractClusterizer.class.getName(), alg);
    }

    @Override
    public void validate() throws WizardValidationException {
        if (getComponent().getClustering() == null) {
            isValid = false;
            throw new WizardValidationException(null, NbBundle.getMessage(WizardClusterize.class, "VisualClusterize.invalid.text"), null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VisualClusterize.CLUSTERING_FACTORY)) {
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
