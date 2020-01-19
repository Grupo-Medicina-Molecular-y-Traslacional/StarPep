/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import org.bapedis.core.io.Exporter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FastaExporter implements Exporter {

    protected final AttributesModel attrModel;

    public FastaExporter(AttributesModel attrModel) {
        this.attrModel = attrModel;
    }

    @Override
    public void exportTo(File file) throws Exception {
        SwingWorker sw = new SwingWorker() {
            private final AtomicBoolean stopRun = new AtomicBoolean(false);
            private final ProgressTicket ticket = new ProgressTicket(NbBundle.getMessage(FastaExporter.class, "FastaExporter.task.name"), new Cancellable() {
                @Override
                public boolean cancel() {
                    stopRun.set(true);
                    return true;
                }
            });

            @Override
            protected Object doInBackground() throws Exception {
                ticket.start();
                List<ProteinSequence> sequences = new LinkedList<>();
                ProteinSequence seq;
                for (Peptide pept : attrModel.getPeptides()) {
                    seq = new ProteinSequence(pept.getSequence());
                    seq.setOriginalHeader(pept.getID());
                    sequences.add(seq);
                }
                if (!stopRun.get()) {
                    FastaWriterHelper.writeProteinSequence(file, sequences);
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
