/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.impl.NetworkEmbedderAlg;
import org.bapedis.chemspace.model.NetworkType;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class WizardNetworkRepresentation implements WizardDescriptor.FinishablePanel<WizardDescriptor>, PropertyChangeListener {

    private final MapperAlgorithm csMapper;
    private NetworkEmbedderAlg alg;
    private final EventListenerList listeners;
    private WizardDescriptor model;

    public WizardNetworkRepresentation(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        listeners = new EventListenerList();
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
                alg = (NetworkEmbedderAlg) csMapper.getNetworkEmbedderAlg().clone();
                component = new VisualNetworkRepresentation();
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
        //Setting for Network embedder
        NetworkType netType = (NetworkType) wiz.getProperty(NetworkType.class.getName());
        if (netType == null) {
            netType = alg.getNetworkType();
        }
        getComponent().setNetworkType(netType);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state                
        NetworkType netType = component.getNetworkType();
        wiz.putProperty(NetworkType.class.getName(), netType);
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VisualNetworkRepresentation.CHANGED_NET_TYPE)) {
            NetworkType netType = component.getNetworkType();
            if (netType == NetworkType.HSP) {
                model.getNotificationLineSupport().setWarningMessage(null);
            } else{
                model.getNotificationLineSupport().setWarningMessage(NbBundle.getMessage(VisualNetworkRepresentation.class, "VisualNetworkRepresentation.FNWaring.text"));
            }
        }
    }

}
