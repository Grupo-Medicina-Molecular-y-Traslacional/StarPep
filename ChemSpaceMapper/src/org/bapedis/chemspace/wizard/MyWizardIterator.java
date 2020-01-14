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
import org.bapedis.chemspace.model.SimilaritySearchingOption;
import org.openide.WizardDescriptor;

/**
 *
 * @author loge
 */
public class MyWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor>, PropertyChangeListener {

    private final EventListenerList listeners;
    private WizardDescriptor.Panel<WizardDescriptor>[] currentPanels, withSimPanels, withoutSimPanels;
    private String[] withSimSteps, withoutSimSteps;
    private WizardDescriptor wizardDesc;
    private  MapperAlgorithm csMapper;
    private SimilaritySearchingOption similarityOption;
    private int index;

    public MyWizardIterator(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        
        listeners = new EventListenerList();

        WizardDescriptor.Panel<WizardDescriptor>[] allPanels = new WizardDescriptor.Panel[]{
            new WizardInputSequence(csMapper), //0 
//            new WizardQuerySequence(csMapper), //1
//            new WizardSimilaritySearching(csMapper), //2
            new WizardFeatureExtraction(csMapper), //3
            new WizardFeatureSelection(csMapper), //4
            new WizardDistanceFunc(csMapper), //5
            new WizardNetworkModel(csMapper) //6
        };
        //Register to listener changes
//        allPanels[1].getComponent().addPropertyChangeListener(this);

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
        
        //With similarity searching option
//        withSimPanels = allPanels;
//        withSimSteps = steps;
        
        //Without similarity searching option
//        withoutSimPanels = new WizardDescriptor.Panel[]{
//            allPanels[0],
//            allPanels[1],
//            allPanels[3],
//            allPanels[4],
//            allPanels[5],
//            allPanels[6]
//        };
        
//        withoutSimSteps = new String[]{
//            steps[0],
//            steps[1],
//            steps[3],
//            steps[4],
//            steps[5],
//            steps[6]
//        };
        
        currentPanels = allPanels;
        index = 0;
    }

    public void initialize(WizardDescriptor wizardDesc) {
        this.wizardDesc = wizardDesc;
//        setSearchingOptionPanels(csMapper.getSearchingOption());
    }
    
    private void setSearchingOptionPanels(SimilaritySearchingOption similarityOption){
        this.similarityOption = similarityOption;
        String[] steps = null;         
        switch(similarityOption){
            case NO:
                currentPanels = withoutSimPanels;
                steps = withoutSimSteps;
                break;
            case YES:
                currentPanels = withSimPanels;
                steps = withSimSteps;
                break;                
       
        }   
        //update                
        for (int i = 0; i < currentPanels.length; i++) {
            Component c = currentPanels[i].getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                wizardDesc.putProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
            }
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
        if (evt.getPropertyName().equals(VisualQuerySequence.CHANGED_OPTION)) {
            SimilaritySearchingOption oldOption = this.similarityOption;
            setSearchingOptionPanels((SimilaritySearchingOption) evt.getNewValue());
            if (oldOption != this.similarityOption) {
                ChangeEvent srcEvt = new ChangeEvent(evt);
                for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                    listener.stateChanged(srcEvt);
                }
            }
        }
    }    
    
}
