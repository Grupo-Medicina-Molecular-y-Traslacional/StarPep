/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.bapedis.core.ui.PeptideViewerTopComponent;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
//@ActionID(
//        category = "View",
//        id = "org.bapedis.db.ui.actions.ShowPeptideNodes"
//)
//@ActionRegistration(
//        displayName = "#CTL_ShowPeptideNodes"
//)
//@ActionReferences({
//    @ActionReference(path = "Menu/View", position = 100),
//    @ActionReference(path = "Actions/ShowDataFromLibrary/Peptides", position = 100)
//})
public class ShowPeptideNodes extends AbstractAction implements Presenter.Popup {

    public ShowPeptideNodes() {
        putValue(NAME, NbBundle.getMessage(ShowPeptideNodes.class, "CTL_ShowPeptideNodes"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final PeptideViewerTopComponent peptideTC = (PeptideViewerTopComponent) WindowManager.getDefault().findTopComponent("PeptideViewerTopComponent");
//        peptideTC.setBusyLabel(true);
        peptideTC.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        peptideTC.open();
        peptideTC.requestActive();

        final TopComponent bioCategoryTC = WindowManager.getDefault().findTopComponent("BioCategoryExplorerTopComponent");

//        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
//
//            @Override
//            protected Void doInBackground() throws Exception {
//                ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
//                Workspace currentWorkspace = pc.getCurrentWorkspace();
//
//                MetadataManager bcManager = Lookup.getDefault().lookup(MetadataManager.class);
//
//                Lookup.Result<Metadata> lkpInfo = bioCategoryTC.getLookup().lookupResult(Metadata.class);
//                Metadata[] selectedCategories = lkpInfo.allInstances().toArray(new Metadata[]{});
//                if (selectedCategories.length == 0) {
//                    selectedCategories = new Metadata[]{bcManager.getBioCategory()};
//                }
//                bcManager.setSelectedCategoriesTo(currentWorkspace, selectedCategories);
//                NeoPeptideManager npManager = Lookup.getDefault().lookup(NeoPeptideManager.class);
//                npManager.loadNeoPeptides(currentWorkspace);
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    get();
////                    peptideTC.setBusyLabel(false);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
////                    peptideTC.setErrorLabel();
//                } finally {
//                    peptideTC.setCursor(Cursor.getDefaultCursor());
//                }
//            }
//        };
//        worker.execute();
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu main = new JMenu(NbBundle.getMessage(ShowPeptideNodes.class, "CTL_ShowPeptideNodes"));
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Actions/ShowDataFromLibrary/InWorkspace");
        for (Action action : actionsForPath) {
            main.add(action);
        }
        return main;
    }
}
