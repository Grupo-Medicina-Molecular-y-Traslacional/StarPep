/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.subnet;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.PairwiseSequenceAlignment;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.graphmining.model.PeptideRankComparator;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class ScaffoldExtraction implements Algorithm {

    static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    private final ScaffoldExtractionFactory factory;
    private Workspace workspace;
    private ProgressTicket ticket;
    private Table nodeTable;
    private AttributesModel tmpAttrModel, newAttrModel;
    protected Peptide[] peptides;
    private List<Node> graphNodes;
    private final SequenceAlignmentModel alignmentModel;
    private Column column;
    private boolean stopRun;
    private final NotifyDescriptor errorND;

    public ScaffoldExtraction(ScaffoldExtractionFactory factory) {
        this.factory = factory;
        alignmentModel = new SequenceAlignmentModel();
        alignmentModel.setPercentIdentity(70);
        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(ScaffoldExtraction.class, "ScaffoldExtraction.errorND"), NotifyDescriptor.ERROR_MESSAGE);
    }      

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.ticket = progressTicket;
        tmpAttrModel = pc.getAttributesModel(workspace);
        nodeTable = pc.getGraphModel(workspace).getNodeTable();
        if (column == null || !nodeTable.hasColumn(column.getId())) {
            DialogDisplayer.getDefault().notify(errorND);
            cancel();
        } else if (tmpAttrModel != null) {
            peptides = tmpAttrModel.getPeptides().toArray(new Peptide[0]);
        }
        graphNodes = null;
        stopRun = false;
    }

    @Override
    public void endAlgo() {
        // Set new Model
        if (newAttrModel != null && !stopRun) {
            // To refresh graph view
            GraphModel graphModel = pc.getGraphModel(workspace);
            Graph graph = graphModel.getGraphVisible();
            graph.clear();
            graphWC.refreshGraphView(workspace, graphNodes, null);

            final Workspace ws = workspace;
            final AttributesModel modelToRemove = pc.getAttributesModel(workspace);
            final AttributesModel modelToAdd = newAttrModel;
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //To change attribute model
                            ws.remove(modelToRemove);
                            ws.add(modelToAdd);
                        } finally {
                            pc.getGraphVizSetting(ws).fireChangedGraphView();
                        }
                    }
                });
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        workspace = null;
        peptides = null;
        graphNodes = null;
        ticket = null;
        newAttrModel = null;
        tmpAttrModel = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        if (peptides != null && !stopRun && column != null) {
            graphNodes = new LinkedList<>();
            newAttrModel = new AttributesModel(workspace);
            tmpAttrModel.getBridge().copyTo(newAttrModel, null);

            pc.reportMsg("Diversity radio: " + String.format("%d%%", alignmentModel.getPercentIdentity()), workspace);

            //Sort peptides by centrality rank
            pc.reportMsg("Ranking by " + column.getTitle(), workspace);
            Arrays.parallelSort(peptides, new PeptideRankComparator(column.getId()));
            
            ticket.switchToDeterminate(peptides.length * (peptides.length - 1) / 2);
            pc.reportMsg("Removing redundant sequences... ", workspace);
            //Remove similar peptides under the diversity radio
            Peptide scaffold;
            for (int i = 0; i < peptides.length; i++) {
                if (peptides[i] != null) {
                    scaffold = peptides[i];
                    if (!stopRun) {
                        removeSimilarTo(scaffold, i + 1);
                    }
                    newAttrModel.addPeptide(scaffold);
                    graphNodes.add(scaffold.getGraphNode());
                }
            }
        }
    }

    private void removeSimilarTo(Peptide scaffold, int from) {
        double identityScore = alignmentModel.getIndentityScore();
        for (int i = from; i < peptides.length; i++) {
            if (peptides[i] != null) {
                try {
                    if (PairwiseSequenceAlignment.computeSequenceIdentity(scaffold.getBiojavaSeq(), peptides[i].getBiojavaSeq(), alignmentModel) >= identityScore) {
                        peptides[i] = null;
                    }
                } catch (CompoundNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            ticket.progress();
        }
    }

}
