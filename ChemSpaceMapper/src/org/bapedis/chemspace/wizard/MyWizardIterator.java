   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.awt.Component;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.openide.WizardDescriptor;

/**
 *
 * @author loge
 */
public class MyWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor> {

    private final EventListenerList listeners;
    private final WizardDescriptor.Panel<WizardDescriptor>[] panels;
    private final String[] steps;
    private WizardDescriptor wizardDesc;
    private int index;

    public MyWizardIterator(MapperAlgorithm csMapper) {

        listeners = new EventListenerList();

        panels = new WizardDescriptor.Panel[]{
            new WizardInputSequence(csMapper), 
//            new WizardQuerySequence(csMapper),
            new WizardFeatureExtraction(csMapper), 
            new WizardFeatureSelection(csMapper), 
            new WizardDistanceFunc(csMapper),
            new WizardNetworkRepresentation(csMapper)
        };
        
//        allPanels[0].getComponent().addPropertyChangeListener(this);

        steps = new String[panels.length];
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        index = 0;
    }

    public void initialize(WizardDescriptor wizardDesc) {
        this.wizardDesc = wizardDesc;
        wizardDesc.putProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
    }
    
    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return panels[index];
    }

    @Override
    public String name() {
        return panels[index].getComponent().getName();
    }

    @Override
    public boolean hasNext() {
        return index < panels.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        if (evt.getPropertyName().equals(VisualRepresentation.CHANGED_CHEM_SPACE)) {
//            ChemSpaceOption oldOption = this.csOption;
//            setChemSpaceOption((ChemSpaceOption) evt.getNewValue());
//            if (oldOption != this.csOption) {
//                ChangeEvent srcEvt = new ChangeEvent(evt);
//                for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
//                    listener.stateChanged(srcEvt);
//                }
//            }
//        }
//    }
    
//    public void setChemSpaceOption(ChemSpaceOption csOption) {
//        this.csOption = csOption;
//        String[] steps = null;
//        switch (csOption) {
//            case CHEM_SPACE_NETWORK:
//                currentPanels = csnPanels;
//                steps = csnSteps;
//                break;
//            case SEQ_SIMILARITY_NETWORK:
//                currentPanels = ssnPanels;
//                steps = ssnSteps;
//                break;
//            default:
//                currentPanels = defaultPanels;
//                steps = defaultSteps;
//        }
//        if (steps != null) {
//            wizardDesc.putProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
//        }
//    }
    
}
