/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import org.bapedis.core.io.Exporter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MDExporter implements Exporter {

    protected final AttributesModel attrModel;
    protected final char separator = ',';

    public MDExporter(AttributesModel attrModel) {
        this.attrModel = attrModel;
    }

    @Override
    public void exportTo(File file) throws Exception {
        SwingWorker sw = new SwingWorker() {
            private final AtomicBoolean stopRun = new AtomicBoolean(false);
            private final ProgressTicket ticket = new ProgressTicket(NbBundle.getMessage(FastaExporter.class, "MDExporter.task.name"), new Cancellable() {
                @Override
                public boolean cancel() {
                    stopRun.set(true);
                    return true;
                }
            });

            @Override
            protected Object doInBackground() throws Exception {
                ticket.start();                
                PrintWriter pw = new PrintWriter(file);
                try {
                    Set<String> keys = attrModel.getMolecularDescriptorKeys();
                    //Write header            
                    pw.format("\"%s\"", Peptide.ID.getDisplayName());
                    for (String key : keys) {
                        for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                            pw.write(separator);
                            pw.format("\"%s\"", attr.getDisplayName());
                        }
                    }
                    pw.println();
                    // Write data
                    List<Peptide> peptides = attrModel.getPeptides();
                    ticket.switchToDeterminate(peptides.size());
                    for (Peptide pept : peptides) {
                        if(stopRun.get()){
                            break;
                        }
                        pw.format("\"%s\"", pept.getId());
                        for (String key : keys) {
                            for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                                Object val = pept.getAttributeValue(attr);
                                pw.write(separator);
                                pw.format("\"%s\"", val != null ? val.toString() : "");
                            }
                        }
                        pw.println();
                        ticket.progress();
                    }
                } finally {
                    pw.flush();
                    pw.close();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ticket.finish();
                }
            }
        };
        sw.execute();
    }

}
