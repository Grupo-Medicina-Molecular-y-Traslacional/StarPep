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
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.chemspace.impl.CSNetworkConstruction;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.impl.NetworkConstructionAlg;
import org.bapedis.chemspace.impl.NetworkConstructionTag;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class WizardNetworkRepresentation implements WizardDescriptor.FinishablePanel<WizardDescriptor>, PropertyChangeListener {

    private static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final MapperAlgorithm csMapper;
    private NetworkConstructionAlg alg;
    private final EventListenerList listeners;
    private WizardDescriptor model;
    private static List<AlgorithmFactory> factories;

    public WizardNetworkRepresentation(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        listeners = new EventListenerList();

        if (factories == null) {
            factories = new LinkedList<>();
            for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
                final AlgorithmFactory factory = it.next();
                if (factory instanceof NetworkConstructionTag) {
                    factories.add(factory);
                }
            }
        }
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualNetworkRepresentation component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualNetworkRepresentation getComponent() {
        if (component == null) {
            try {
                alg = (NetworkConstructionAlg) csMapper.getNetworkAlg().clone();
                component = new VisualNetworkRepresentation();
                component.populateComboBox(factories, alg);
                component.addPropertyChangeListener(this);
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
        return true;
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
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state 
        alg = getComponent().getSelectedAlg();
        if (alg != null){
            wiz.putProperty(NetworkConstructionAlg.class.getName(), alg);
        }
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VisualNetworkRepresentation.CHANGED_NET_TYPE)) {
            NetworkConstructionAlg netType = (NetworkConstructionAlg)evt.getNewValue();
            if (netType instanceof CSNetworkConstruction) {
                model.getNotificationLineSupport().setWarningMessage(NbBundle.getMessage(VisualNetworkRepresentation.class, "VisualNetworkRepresentation.FNWaring.text"));
            } else{
                model.getNotificationLineSupport().setWarningMessage(null);                
            }
        }
    }

}
