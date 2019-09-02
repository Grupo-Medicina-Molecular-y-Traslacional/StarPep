/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

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
        menu.removeAll();
        menu.add(new CopyPeptidesToWorkspace(null));
        Workspace currWs = pc.getCurrentWorkspace();
        Workspace otherWs;
        for (Iterator<? extends Workspace> it = pc.getWorkspaceIterator(); it.hasNext();) {
            otherWs = it.next();
            if (currWs != otherWs) {
                menu.add(new CopyPeptidesToWorkspace(otherWs));
            }
        }
        return menu;
    }

}

class CopyPeptidesToWorkspace extends GlobalContextSensitiveAction<Peptide> {

    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private Workspace workspace;

    public CopyPeptidesToWorkspace(Workspace workspace) {
        super(Peptide.class);
        this.workspace = workspace;
        String name;
        if (workspace == null) {
            name = NbBundle.getMessage(CopyPeptides.class, "CopyPeptidesToWorkspace.newWorkspace.name");
        } else {
            name = workspace.getName();
        }
        putValue(NAME, name);
//        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/remove.png", false));
        putValue(SHORT_DESCRIPTION, name);
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends Peptide> context = lkpResult.allInstances();
        if (!context.isEmpty()) {
            SwingWorker worker = new CopyPeptidesWorker(context, workspace);
            worker.execute();
        }
    }
}

class CopyPeptidesWorker extends SwingWorker<Void, Void> {
    private final List<Integer> peptideIDs;
    private final Collection<? extends Peptide> context;
    private Workspace workspace;
    private AttributesModel currAttrModel; 

    public CopyPeptidesWorker(Collection<? extends Peptide> context, Workspace workspace) {
        this.context = context;
        peptideIDs = new LinkedList<>();
        currAttrModel = CopyPeptides.pc.getAttributesModel();
        this.workspace = workspace;
    }
    
    private Workspace createWorkspace() {
        String name = Workspace.getPrefixName() + " " + Workspace.getCount();
        DialogDescriptor.InputLine dd = new DialogDescriptor.InputLine("", NbBundle.getMessage(NewWorkspace.class, "NewWorkspace.dialog.title"));
        dd.setInputText(name);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION) && !dd.getInputText().isEmpty()) {
            name = dd.getInputText();
            Workspace ws = new Workspace(name);
            CopyPeptides.pc.add(ws);
            return ws;
        }
        return null;
    }    

    @Override
    protected Void doInBackground() throws Exception {
        for (Peptide peptide : context) {
            peptideIDs.add(peptide.getId());
        }
        AttributesModel newAttrModel;
        if (workspace != null || ((workspace = createWorkspace()) != null)) {
            if (workspace.isBusy()) {
                throw new MyException(NbBundle.getMessage(CopyPeptides.class, "CopyPeptidesToWorkspace.error.busyWorkspace", workspace.getName()));
            }

            newAttrModel = CopyPeptides.pc.getAttributesModel(workspace);
            if (newAttrModel == null) {
                newAttrModel = new AttributesModel(workspace);
                workspace.add(newAttrModel);
            }
            //Check duplited peptide
            Map<Integer, Peptide> map = newAttrModel.getPeptideMap();
            if (map.size() > 0) {
                for (Integer id : peptideIDs) {
                    if (map.containsKey(id)) {
                        throw new MyException(NbBundle.getMessage(CopyPeptides.class, "CopyPeptidesToWorkspace.error.duplicatedPeptide", id, workspace.getName()));
                    }
                }
            }

            //Copy peptides and graph
            currAttrModel.getBridge().copyTo(newAttrModel, peptideIDs);
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
            if (workspace != null) {
                CopyPeptides.pc.setCurrentWorkspace(workspace);
                CopyPeptides.pc.getGraphVizSetting(workspace).fireChangedGraphView();
            }
        } catch (InterruptedException | ExecutionException ex) {
            if (ex.getCause() instanceof MyException) {
                NotifyDescriptor errorND = new NotifyDescriptor.Message(((MyException) ex.getCause()).getErrorMsg(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(errorND);
            } else {
                Exceptions.printStackTrace(ex);
            }
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
