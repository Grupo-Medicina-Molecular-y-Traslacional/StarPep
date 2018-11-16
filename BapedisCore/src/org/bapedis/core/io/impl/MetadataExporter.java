/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import org.bapedis.core.io.Exporter;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MetadataExporter implements Exporter {

    protected final AttributesModel attrModel;
    protected final char separator = ',';

    public MetadataExporter(AttributesModel attrModel) {
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
                PrintWriter pw = new PrintWriter(file);
                try {
                    pw.format("\"%s\"", "Peptide");
                    pw.write(separator);
                    pw.format("\"%s\"", "Relationship");
                    pw.write(separator);
                    pw.format("\"%s\"", "Metadata");
                    pw.write(separator);
                    pw.format("\"%s\"", "Reference");
                    pw.println();

                    NodeIterable neighbors;
                    Edge edge;
                    String xref;
                    List<Peptide> peptides = attrModel.getPeptides();
                    ticket.switchToDeterminate(peptides.size());                    
                    for (Peptide pept : peptides) {
                        if(stopRun.get()){
                            break;
                        }                        
                        for (StarPepAnnotationType aType : StarPepAnnotationType.values()) {
                            neighbors = pept.getNeighbors(aType);
                            for (Node neighbor : neighbors) {
                                edge = pept.getEdge(neighbor, aType);
                                xref = Arrays.toString((String[]) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_XREF));
                                pw.format("\"%s\"", pept.getName());
                                pw.write(separator);
                                pw.format("\"%s\"", edge.getLabel());
                                pw.write(separator);
                                pw.format("\"%s\"", neighbor.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME));
                                pw.write(separator);
                                pw.format("\"%s\"", xref);
                                pw.println();
                            }
                        }
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
