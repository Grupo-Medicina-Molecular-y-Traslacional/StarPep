/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.task;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.DeleteDescriptorModel;
import org.bapedis.core.project.ProjectManager;
import static org.bapedis.core.task.QueryExecutor.pc;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class DeleteDescriptor extends SwingWorker<Void, String> {

    private final Set<String> keys;
    private final AttributesModel attrModel;
    private boolean stopRun = false;
    private final ProgressTicket ticket;
    private final DeleteDescriptorModel model;
    private final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final String taskName = "Deleting molecular descriptors";

    public DeleteDescriptor(DeleteDescriptorModel model, AttributesModel attrModel, Set<String> keys) {
        this.keys = keys;
        this.attrModel = attrModel;
        this.model = model;
        ticket = new ProgressTicket(NbBundle.getMessage(DeleteDescriptor.class, "DeleteDescriptor.task.name", model.getOwnerWS().getName()), new Cancellable() {
            @Override
            public boolean cancel() {
                stopRun = true;
                return true;
            }
        });        
    }

    @Override
    protected Void doInBackground() throws Exception {
        publish("start");
        pc.reportRunningTask(taskName, model.getOwnerWS());
        
        ticket.start(keys.size());
        for (String key : keys) {
            if (!stopRun) {
                if (attrModel.hasMolecularDescriptors(key)) {
                    attrModel.deleteAllMolecularDescriptors(key);
                    pc.reportMsg("Deleted: " + key, model.getOwnerWS());
                }
                ticket.progress();
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        model.setRunning(true);
    }

    @Override
    protected void done() {
        try {
            get();            
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            pc.reportError(ex.getCause().toString(), model.getOwnerWS());
        } finally {
            model.setRunning(false);
            ticket.finish();
            if (pc.getCurrentWorkspace() != model.getOwnerWS()) {
                String txt = NbBundle.getMessage(DeleteDescriptor.class, "Workspace.notify.finishedTask", taskName);
                pc.workspaceChangeNotification(txt, model.getOwnerWS());
            }
            pc.reportFinishedTask(taskName, model.getOwnerWS());
        }
    }

}