/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.network.SimilarityNetworkFactory;
import org.bapedis.core.spi.network.SimilarityNetworkSetupUI;
import org.bapedis.core.task.AlgorithmExecutor;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SimilarityNetworkWizardAction extends AbstractAction {

    protected final ProjectManager pc;
    protected final SimilarityNetworkFactory factory;
    protected final AlgorithmExecutor executor;

    public SimilarityNetworkWizardAction() {
        putValue(NAME, NbBundle.getMessage(SimilarityNetworkWizardAction.class, "SimilarityNetworkWizardAction.name"));
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/wizard.png", false));

        pc = Lookup.getDefault().lookup(ProjectManager.class);
        factory = Lookup.getDefault().lookup(SimilarityNetworkFactory.class);
        executor = Lookup.getDefault().lookup(AlgorithmExecutor.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SimilarityNetworkSetupUI setupUI = factory != null ? factory.getSetupUI() : null;
        if (setupUI != null) {
            Workspace currentWS = pc.getCurrentWorkspace();
            if (currentWS.isBusy()) {
                DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
            } else {
                boolean addToWorkspace = false;
                Collection<? extends Algorithm> savedAlgo = currentWS.getLookup().lookupAll(Algorithm.class);
                Algorithm algorithm = null;
                for (Algorithm algo : savedAlgo) {
                    if (algo.getFactory() == factory) {
                        algorithm = algo;
                        break;
                    }
                }
                if (algorithm == null) {
                    algorithm = factory.createAlgorithm();
                    addToWorkspace = true;
                }

                // Create wizard
                WizardDescriptor.Panel<WizardDescriptor>[] panels = setupUI.getWizardPanels(algorithm);

                // Set wizard steps
                String[] steps = new String[panels.length];
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

                // Open wizard
                WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
                // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
                wiz.setTitleFormat(new MessageFormat("{0}"));
                wiz.setTitle(NbBundle.getMessage(SimilarityNetworkWizardAction.class, "SimilarityNetworkWizard.title"));

                //The image in the left sidebar of the wizard is set like this:
                //wiz.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("org/demo/wizard/banner.PNG", true));
                if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                    //Save properties from wizard to algorithm
                    setupUI.finishedWizard(wiz, algorithm);

                    //Save algorithm to workspace if it is needed
                    if (addToWorkspace) {
                        currentWS.add(algorithm);
                    }

                    //Execute algorithm
                    executor.execute(algorithm);
                }

            }

        }
    }

}
