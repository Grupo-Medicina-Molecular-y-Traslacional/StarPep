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
import org.openide.WizardDescriptor;

/**
 *
 * @author loge
 */
public class MyWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor>, PropertyChangeListener {

    private final WizardDescriptor.Panel<WizardDescriptor>[] defaultPanels, twoDPanels, networkPanels;
    private WizardDescriptor.Panel<WizardDescriptor>[] currentPanels;
    private final String[] defaultSteps, twoDSteps, networkSteps;
    private WizardDescriptor wizardDesc;
    private int index;

    public MyWizardIterator(MapperAlgorithm csMapper) {
        
        WizardRepresentation wizRep = new WizardRepresentation(csMapper);
        wizRep.getComponent().addPropertyChangeListener(this);

        WizardDescriptor.Panel<WizardDescriptor>[] allPanels = new WizardDescriptor.Panel[]{
            wizRep,
            new WizardFeatureExtraction(csMapper),
            new WizardFeatureFiltering(csMapper),
            new WizardSimilarityMeasure(csMapper),
            new WizardTwoDTransformer(csMapper)            
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
        
        //Default Panels
        defaultPanels = new WizardDescriptor.Panel[]{
            allPanels[0],
            allPanels[1],
            allPanels[2]
        };        
        defaultSteps = new String[]{
            steps[0],
            steps[1],
            steps[2]
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

        //Network Panels
        networkPanels = new WizardDescriptor.Panel[]{
            allPanels[0],
            allPanels[1],
            allPanels[2],
            allPanels[3]
        };        
        networkSteps = new String[]{
            steps[0],
            steps[1],
            steps[2],
            steps[3]
        };
        
        currentPanels = defaultPanels;
        index = 0;
    }

    public void initialize(WizardDescriptor wizardDesc) {
        this.wizardDesc = wizardDesc;
        setChemSpaceOption(ChemSpaceOption.NONE);
    }        
    
    public void setChemSpaceOption(ChemSpaceOption csOption){
        String[] steps = null;
        switch(csOption){
            case THREE_DIMENSIONAL:
                currentPanels = twoDPanels;
                steps = twoDSteps;
                break;
            case FULL_NETWORK:
            case COMPRESSED_NETWORK:
                currentPanels = networkPanels;
                steps = networkSteps;
                break;
            case NONE:
                currentPanels = defaultPanels;
                steps = defaultSteps;
        }
        if (steps != null){
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
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VisualRepresentation.CHANGED_OPTION)) {
            setChemSpaceOption((ChemSpaceOption) evt.getNewValue());
        }
    }
}
