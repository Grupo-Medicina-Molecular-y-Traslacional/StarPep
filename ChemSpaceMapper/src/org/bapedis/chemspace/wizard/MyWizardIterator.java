/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.bapedis.chemspace.model.Representation;
import org.openide.WizardDescriptor;

/**
 *
 * @author loge
 */
public class MyWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor>, PropertyChangeListener {

    private final EventListenerList listeners;
    private final WizardDescriptor.Panel<WizardDescriptor>[] defaultPanels, twoDPanels, csnPanels, ssnPanels;
    private WizardDescriptor.Panel<WizardDescriptor>[] currentPanels;
    private final String[] defaultSteps, twoDSteps, csnSteps, ssnSteps;
    private WizardDescriptor wizardDesc;
    private int index;
    private Representation representation;
    private ChemSpaceOption csOption;

    public MyWizardIterator(MapperAlgorithm csMapper) {

        listeners = new EventListenerList();

        WizardRepresentation wizRep = new WizardRepresentation(csMapper);
        wizRep.getComponent().addPropertyChangeListener(this);

        WizardDescriptor.Panel<WizardDescriptor>[] allPanels = new WizardDescriptor.Panel[]{
            wizRep, //0
            new WizardFeatureExtraction(csMapper), //1
            new WizardFeatureSelection(csMapper), //2
            new WizardSimilarityMeasure(csMapper), //3
            new WizardTwoDTransformer(csMapper), //4
            new WizardSequenceAlignment(csMapper) //5
        };

        String[] steps = new String[allPanels.length];
        for (int i = 0; i < allPanels.length; i++) {
            Component c = allPanels[i].getComponent();
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
        
        // Chem Space Option
        representation = Representation.COORDINATE_BASED;
        csOption = ChemSpaceOption.TwoD_SPACE;

        //Default Panels
        defaultPanels = new WizardDescriptor.Panel[]{
            allPanels[0]
        };
        defaultSteps = new String[]{
            steps[0]
        };

        //TwoDimensional Panels
        twoDPanels = new WizardDescriptor.Panel[]{
            allPanels[0],
            allPanels[1],
            allPanels[2],
            allPanels[4]
        };
        twoDSteps = new String[]{
            steps[0],
            steps[1],
            steps[2],
            steps[4]
        };

        //CSN Panels
        csnPanels = new WizardDescriptor.Panel[]{
            allPanels[0],
            allPanels[1],
            allPanels[2],
            allPanels[3]
        };
        csnSteps = new String[]{
            steps[0],
            steps[1],
            steps[2],
            steps[3]
        };

        //SSN Panels
        ssnPanels = new WizardDescriptor.Panel[]{
            allPanels[0],
            allPanels[5]
        };
        ssnSteps = new String[]{
            steps[0],
            steps[5]
        };
        currentPanels = defaultPanels;
        index = 0;
    }

    public void initialize(WizardDescriptor wizardDesc) {
        this.wizardDesc = wizardDesc;
        setChemSpaceOption(representation, csOption);
    }

    public void setChemSpaceOption(Representation representation, ChemSpaceOption csOption) {
        this.representation = representation;
        this.csOption = csOption;
        String[] steps = null;
        switch (representation) {
            case COORDINATE_BASED:
                switch (csOption) {
                    case TwoD_SPACE:
                        currentPanels = twoDPanels;
                        steps = twoDSteps;
                        break;
                    default:
                        currentPanels = defaultPanels;
                        steps = defaultSteps;
                }
                break;
            case COORDINATE_FREE:
                switch (csOption) {
                    case CHEM_SPACE_NETWORK:
                        currentPanels = csnPanels;
                        steps = csnSteps;
                        break;
                    case SEQ_SIMILARITY_NETWORK:
                        currentPanels = ssnPanels;
                        steps = ssnSteps;
                        break;
                    default:
                        currentPanels = defaultPanels;
                        steps = defaultSteps;
                }
                break;
            default:
                currentPanels = defaultPanels;
                steps = defaultSteps;
                break;
        }
        if (steps != null) {
            wizardDesc.putProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
        }
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return currentPanels[index];
    }

    @Override
    public String name() {
        return currentPanels[index].getComponent().getName();
    }

    @Override
    public boolean hasNext() {
        return index < currentPanels.length - 1;
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VisualRepresentation.CHANGED_REPRESENTATION)) {
            Representation oldRep = this.representation;
            setChemSpaceOption((Representation) evt.getNewValue(), csOption);
            if (oldRep != this.representation) {
                ChangeEvent srcEvt = new ChangeEvent(evt);
                for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                    listener.stateChanged(srcEvt);
                }
            }
        } else if (evt.getPropertyName().equals(VisualRepresentation.CHANGED_CHEM_SPACE)) {
            ChemSpaceOption oldOption = this.csOption;
            setChemSpaceOption(representation, (ChemSpaceOption) evt.getNewValue());
            if (oldOption != this.csOption) {
                ChangeEvent srcEvt = new ChangeEvent(evt);
                for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                    listener.stateChanged(srcEvt);
                }
            }
        }
    }
}
