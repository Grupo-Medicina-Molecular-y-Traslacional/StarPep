/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.bapedis.chemspace.impl.MapperAlgorithm;
import org.bapedis.chemspace.model.FeatureFilteringOption;
import org.bapedis.core.spi.alg.impl.FeatureSEFiltering;
import org.bapedis.core.spi.alg.impl.FeatureSEFilteringFactory;
import org.bapedis.core.spi.alg.impl.FeatureSEFilteringPanel;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class WizardFeatureSelection implements WizardDescriptor.Panel<WizardDescriptor>,
        WizardDescriptor.FinishablePanel<WizardDescriptor> {

    private final MapperAlgorithm csMapper;
    private FeatureSEFiltering alg;

    public WizardFeatureSelection(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private VisualFeatureSelection component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public VisualFeatureSelection getComponent() {
        if (component == null) {
            try {
                if (csMapper.getFeatureSelectionAlg() == null) {
                    alg = (FeatureSEFiltering) new FeatureSEFilteringFactory().createAlgorithm();
                } else {
                    alg = (FeatureSEFiltering) csMapper.getFeatureSelectionAlg().clone();
                }
                JPanel settingPanel = alg.getFactory().getSetupUI().getSettingPanel(alg);
                ((FeatureSEFilteringPanel) settingPanel).setShannonDistributionPanel(false);
                component = new VisualFeatureSelection(settingPanel);
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
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
        FeatureFilteringOption ffOption = (FeatureFilteringOption) wiz.getProperty(FeatureFilteringOption.class.getName());
        if (ffOption == null) {
            ffOption = csMapper.getFFOption();
        }
        getComponent().setFFOption(ffOption);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        FeatureFilteringOption ffOption = getComponent().getFFOption();
        wiz.putProperty(FeatureFilteringOption.class.getName(), ffOption);
        if (ffOption == FeatureFilteringOption.YES) {
            wiz.putProperty(FeatureSEFiltering.class.getName(), alg);
        } else{
            wiz.putProperty(FeatureSEFiltering.class.getName(), null);
        }
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }
}
