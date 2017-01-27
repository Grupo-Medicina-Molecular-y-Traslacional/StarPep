/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.services.BioCategoryManager;
import org.bapedis.db.services.NeoPeptideManager;
import org.bapedis.db.model.BioCategory;
import org.bapedis.db.ui.NeoPeptideModelTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
@ActionID(
        category = "View",
        id = "org.bapedis.db.ui.actions.ShowPeptideNodes"
)
@ActionRegistration(
        displayName = "#CTL_ShowPeptideNodes"
)
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 100),
    @ActionReference(path = "Actions/ShowPeptidesFromBioCategory", position = 100)
})
public class ShowPeptideNodes extends AbstractAction {

    public ShowPeptideNodes() {
        putValue(NAME, NbBundle.getMessage(ShowPeptideNodes.class, "CTL_ShowPeptideNodes"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final NeoPeptideModelTopComponent peptideTC = (NeoPeptideModelTopComponent) WindowManager.getDefault().findTopComponent("NeoPeptideModelTopComponent");
        peptideTC.setBusyLabel();
        peptideTC.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        peptideTC.open();
        peptideTC.requestActive();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TopComponent bioCategoryTC = WindowManager.getDefault().findTopComponent("BioCategoryExplorerTopComponent");
                try {
                    ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
                    Workspace currentWorkspace = pc.getCurrentWorkspace();

                    BioCategoryManager bcManager = Lookup.getDefault().lookup(BioCategoryManager.class);

                    Lookup.Result<BioCategory> lkpInfo = bioCategoryTC.getLookup().lookupResult(BioCategory.class);
                    BioCategory[] selectedCategories = lkpInfo.allInstances().toArray(new BioCategory[]{});
                    if (selectedCategories.length == 0) {
                        selectedCategories = new BioCategory[]{bcManager.getRootCategory()};
                    }
                    bcManager.setSelectedCategoriesTo(currentWorkspace, selectedCategories);                    
                    NeoPeptideManager npManager = Lookup.getDefault().lookup(NeoPeptideManager.class);
                    npManager.setNeoPeptidesTo(currentWorkspace, false);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    peptideTC.setErrorLabel();
                } finally {
                    peptideTC.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }
}
