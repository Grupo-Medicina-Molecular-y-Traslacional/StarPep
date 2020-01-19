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
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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
    protected MD_OUTPUT_OPTION output;

    public MDExporter(AttributesModel attrModel) {
        this.attrModel = attrModel;
        this.output = MD_OUTPUT_OPTION.Z_SCORE;
    }

    public MD_OUTPUT_OPTION getOutput() {
        return output;
    }

    public void setOutput(MD_OUTPUT_OPTION output) {
        this.output = output;
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

                List<Peptide> peptides = attrModel.getPeptides();
                Set<String> keys = attrModel.getMolecularDescriptorKeys();

                if (output != MD_OUTPUT_OPTION.None) {
                    for (String key : keys) {
                        for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                            attr.resetSummaryStats(peptides);
                        }
                    }
                }

                PrintWriter pw = new PrintWriter(file);
                try {

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
                    ticket.switchToDeterminate(peptides.size());
                    for (Peptide pept : peptides) {
                        if (stopRun.get()) {
                            break;
                        }
                        //Writed ID
                        pw.format("\"%s\"", pept.getID());

                        //Write features
                        for (String key : keys) {
                            for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                                pw.write(separator);
                                pw.format("\"%s\"", getAttributeValue(pept, attr, output));
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

            private String getAttributeValue(Peptide peptide, MolecularDescriptor attr, MD_OUTPUT_OPTION output) throws MolecularDescriptorNotFoundException {
                switch (output) {
                    case None:
                        return Double.toString(attr.getDoubleValue(peptide));
                    case Z_SCORE:
                        return Double.toString(attr.getNormalizedZscoreValue(peptide));
                    case MIN_MAX:
                        return Double.toString(attr.getNormalizedMinMaxValue(peptide));
                }
                return "";
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    if (ex.getCause() instanceof MolecularDescriptorNotFoundException) {
                        NotifyDescriptor errorND = ((MolecularDescriptorNotFoundException) ex.getCause()).getErrorNotifyDescriptor();
                        DialogDisplayer.getDefault().notify(errorND);
                    } else {
                        Exceptions.printStackTrace(ex);
                    }
                } finally {
                    ticket.finish();
                }
            }
        };
        sw.execute();
    }

}
