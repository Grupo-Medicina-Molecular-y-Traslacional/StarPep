/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.bapedis.core.spi.algo.impl.SimilarityNetworkWizard1;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SimilarityNetworkWizardAction extends AbstractAction {

    public SimilarityNetworkWizardAction() {
        putValue(NAME, NbBundle.getMessage(SimilarityNetworkWizardAction.class, "SimilarityNetworkWizardAction.name"));
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/wizard.png", false));
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new SimilarityNetworkWizard1());
//        panels.add(new NewProjectWizardPanel2());

        // Set wizard steps
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
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
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(SimilarityNetworkWizardAction.class, "SimilarityNetworkWizard.title"));
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
//            ProjectInfo pInfo = (ProjectInfo)wiz.getProperty(NewProjectVisualPanel1.PROJECT_TYPE);
//            String name = (String)wiz.getProperty(NewProjectVisualPanel2.PRO_NAME);
//            String displayName = (String)wiz.getProperty(NewProjectVisualPanel2.PRO_DISPLAY_NAME);
//            File folder = (File)wiz.getProperty(NewProjectVisualPanel2.PRO_FOLDER);
//            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
//            pc.newProject(pInfo, name, displayName, folder);
        }
        
        
    }
    
}
