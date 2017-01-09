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
import org.bapedis.core.controller.ProjectController;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.controller.BioCategoryController;
import org.bapedis.db.controller.NeoPeptideController;
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
        final NeoPeptideModelTopComponent tcPeptide = (NeoPeptideModelTopComponent) WindowManager.getDefault().findTopComponent("NeoPeptideModelTopComponent");
        tcPeptide.setBusyLabel();
        tcPeptide.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tcPeptide.open();
        tcPeptide.requestActive();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TopComponent tcExplorer = WindowManager.getDefault().findTopComponent("PeptideExplorerTopComponent");
                try {
                    ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
                    Workspace currentWorkspace = pc.getProject().getCurrentWorkspace();

                    BioCategoryController bcc = Lookup.getDefault().lookup(BioCategoryController.class);

                    Lookup.Result<BioCategory> lkpInfo = tcExplorer.getLookup().lookupResult(BioCategory.class);
                    BioCategory[] selectedCategories = lkpInfo.allInstances().toArray(new BioCategory[]{});
                    if (selectedCategories.length == 0) {
                        selectedCategories = new BioCategory[]{bcc.getRootCategory()};
                    }
                    bcc.setSelectedCategoriesTo(currentWorkspace, selectedCategories);                    
                    NeoPeptideController npc = Lookup.getDefault().lookup(NeoPeptideController.class);
                    npc.setNeoPeptidesTo(currentWorkspace, false);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    tcPeptide.setErrorLabel();
                } finally {
                    tcPeptide.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }
}
