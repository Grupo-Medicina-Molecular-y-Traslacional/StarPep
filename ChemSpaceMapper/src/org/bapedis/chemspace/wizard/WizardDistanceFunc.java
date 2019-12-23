/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.bapedis.chemspace.distance.DistanceFunctionTag;
import org.bapedis.core.io.MD_OUTPUT_OPTION;

public class WizardDistanceFunc implements WizardDescriptor.ValidatingPanel<WizardDescriptor>,
        WizardDescriptor.FinishablePanel<WizardDescriptor>,
        PropertyChangeListener {

    private static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final MapperAlgorithm csMapper;
    private final EventListenerList listeners = new EventListenerList();
    private boolean isValid;
    private WizardDescriptor model;
    private static List<AlgorithmFactory> factories;

    public WizardDistanceFunc(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        isValid = true;

        if (factories == null) {
            factories = new LinkedList<>();
            for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
                final AlgorithmFactory factory = it.next();
                if (factory instanceof DistanceFunctionTag) {
                    factories.add(factory);
                }
            }
        }
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualDistanceFunc component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualDistanceFunc getComponent() {
        if (component == null) {
            component = new VisualDistanceFunc();
            component.populateJTree(factories);
            component.setDistanceFactory((AlgorithmFactory) csMapper.getDistanceFactory());
            component.setOption(csMapper.getMdOption());
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
        AlgorithmFactory factory = (AlgorithmFactory) wiz.getProperty(AbstractDistance.class.getName());
        MD_OUTPUT_OPTION option = (MD_OUTPUT_OPTION) wiz.getProperty(MD_OUTPUT_OPTION.class.getName());
        if (factory != null && option != null) {
            getComponent().setDistanceFactory(factory);
            getComponent().setOption(option);
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        AlgorithmFactory factory = component.getDistanceFactory();
        if (factory != null) {
            wiz.putProperty(AbstractDistance.class.getName(), factory);
            wiz.putProperty(MD_OUTPUT_OPTION.class.getName(), component.getOption());
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        if (getComponent().getDistanceFactory() == null) {
            isValid = false;
            throw new WizardValidationException(null, NbBundle.getMessage(WizardDistanceFunc.class, "DistanceFunction.invalid.text"), null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VisualDistanceFunc.DISTANCE_FUNCTION)) {
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

    @Override
    public boolean isFinishPanel() {
        return isValid;
    }

}
