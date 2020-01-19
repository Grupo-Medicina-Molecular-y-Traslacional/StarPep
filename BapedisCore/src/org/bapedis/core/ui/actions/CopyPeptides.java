/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.windows.WindowManager;

/**
 *
 * @author Loge
 */
@ActionID(
        category = "Edit",
        id = "org.bapedis.core.ui.actions.CopyPeptides"
)
@ActionRegistration(
        displayName = "CopyPeptides.name",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditPeptides", position = 10)
})
public class CopyPeptides extends AbstractAction implements LookupListener, Presenter.Popup {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected Lookup.Result<Workspace> lkpResult;
    protected JMenu menu;

    public CopyPeptides() {
        menu = new JMenu(NbBundle.getMessage(CopyPeptides.class, "CopyPeptides.name"));

        lkpResult = pc.getLookup().lookupResult(Workspace.class);
        lkpResult.addLookupListener(this);

//        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/removeWorkspace.png", false));
        putValue(NAME, NbBundle.getMessage(CopyPeptides.class, "CopyPeptides.name"));

//        menu.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/copy.gif", false));
        menu.setToolTipText(NbBundle.getMessage(RemoveWorkspace.class, "CopyPeptides.toolTipText"));
        menu.add(new CopyPeptidesToWorkspace());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void resultChanged(LookupEvent le) {
        menu.setEnabled(lkpResult.allInstances().size() > 1);
    }

    @Override
    public JMenuItem getPopupPresenter() {        
        return menu;
    }
}

class CopyPeptidesToWorkspace extends GlobalContextSensitiveAction<Peptide> {

    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public CopyPeptidesToWorkspace() {
        super(Peptide.class);
        String name = NbBundle.getMessage(CopyPeptides.class, "CopyPeptidesToWorkspace.newWorkspace.name");
        putValue(NAME, name);
//        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/remove.png", false));
        putValue(SHORT_DESCRIPTION, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends Peptide> context = lkpResult.allInstances();
        if (!context.isEmpty()) {
            SwingWorker worker = new CopyPeptidesWorker(context);
            WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            worker.execute();            
        }
    }
}

class CopyPeptidesWorker extends SwingWorker<Workspace, Void> {

    private final Collection<? extends Peptide> context;

    public CopyPeptidesWorker(Collection<? extends Peptide> context) {
        this.context = context;
    }

    private Workspace createWorkspace() {
        String name = Workspace.getPrefixName() + " " + Workspace.getCount();
        DialogDescriptor.InputLine dd = new DialogDescriptor.InputLine("", NbBundle.getMessage(NewWorkspace.class, "NewWorkspace.dialog.title"));
        dd.setInputText(name);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION) && !dd.getInputText().isEmpty()) {
            name = dd.getInputText();
            boolean exist = false;
            for (Iterator<? extends Workspace> it = CopyPeptides.pc.getWorkspaceIterator(); it.hasNext();) {
                Workspace ws = it.next();
                if (ws.getName().equals(name)) {
                    exist = true;
                }
            }
            if (exist) {
                DialogDisplayer.getDefault().notify(NewWorkspace.ErrorWS);
                return null;
            } 
            return new Workspace(name);
        }
        return null;
    }

    @Override
    protected Workspace doInBackground() throws Exception {
        Workspace currentWorkspace = CopyPeptides.pc.getCurrentWorkspace();
        if (currentWorkspace.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWorkspace.getBusyNotifyDescriptor());
        }

        Workspace newWorkspace = createWorkspace();
        if (newWorkspace != null) {
            AttributesModel newAttrModel = new AttributesModel(newWorkspace);
            newWorkspace.add(newAttrModel);

            //Copy peptides and graph
            List<String> peptideIDs = new LinkedList<>();
            for (Peptide peptide : context) {
                peptideIDs.add(peptide.getID());
            }
            AttributesModel currAttrModel = CopyPeptides.pc.getAttributesModel(currentWorkspace);
            currAttrModel.getBridge().copyTo(newAttrModel, peptideIDs);
        }
        return newWorkspace;
    }

    @Override
    protected void done() {
        try {
            Workspace newWorkspace = get();
            if (newWorkspace != null) {
                CopyPeptides.pc.add(newWorkspace);
                CopyPeptides.pc.setCurrentWorkspace(newWorkspace);
                CopyPeptides.pc.getGraphVizSetting(newWorkspace).fireChangedGraphView();
            }
        } catch (InterruptedException | ExecutionException ex) {
            if (ex.getCause() instanceof MyException) {
                NotifyDescriptor errorND = new NotifyDescriptor.Message(((MyException) ex.getCause()).getErrorMsg(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(errorND);
            } else {
                Exceptions.printStackTrace(ex);
            }
        }
        finally{
            WindowManager.getDefault().getMainWindow().setCursor(Cursor.getDefaultCursor());
        }
    }

    private class MyException extends RuntimeException {

        private final String errorMsg;

        MyException(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }
}
