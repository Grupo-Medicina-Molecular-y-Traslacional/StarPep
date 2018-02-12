/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.wizard;

import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.AllDescriptorTable;
import org.bapedis.network.impl.CSNAlgorithm;
import org.bapedis.network.model.WizardOptionModel;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public class CSNWizardMolecularDescriptor implements WizardDescriptor.Panel<WizardDescriptor> {

    private final CSNAlgorithm csnAlgo;
    private final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public CSNWizardMolecularDescriptor(CSNAlgorithm csnAlgo) {
        this.csnAlgo = csnAlgo;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CSNVisualMolecularDescriptor component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public CSNVisualMolecularDescriptor getComponent() {
        if (component == null) {
            AttributesModel attrModel = pc.getAttributesModel();
            List<MolecularDescriptor> featureList = new LinkedList<>();
            // Populate feature list                
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor desc : attrModel.getMolecularDescriptors(key)) {
                    featureList.add(desc);
                }
            }
            WizardOptionModel optionModel = csnAlgo.getMdOptionModel();

            component = new CSNVisualMolecularDescriptor(optionModel, new AllDescriptorTable(), featureList.size());
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
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
    }

}
