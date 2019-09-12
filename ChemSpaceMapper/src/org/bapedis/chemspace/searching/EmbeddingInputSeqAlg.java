/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Loge
 */
public class EmbeddingInputSeqAlg implements Algorithm, Cloneable {

    private final ProjectManager pc;
    protected final PeptideDAO dao;
    protected final GraphWindowController graphWC;
    private final AlgorithmFactory factory;
    private Workspace workspace;
    private ProgressTicket ticket;
    private AttributesModel newAttrModel;
    private List<Node> graphNodes;
    private boolean stopRun;

    public EmbeddingInputSeqAlg(AlgorithmFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        dao = Lookup.getDefault().lookup(PeptideDAO.class);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        this.factory = factory;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.ticket = progressTicket;
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
        graphNodes = null;
        ticket = null;
        newAttrModel = null;
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
        newAttrModel = dao.getPeptides(new QueryModel(workspace), pc.getGraphModel(workspace), pc.getAttributesModel(workspace));
        graphNodes = new LinkedList<>();
        for (Peptide peptide: newAttrModel.getPeptides()) {
            graphNodes.add(peptide.getGraphNode());
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EmbeddingInputSeqAlg copy = (EmbeddingInputSeqAlg) super.clone(); //To change body of generated methods, choose Tools | Templates.
        return copy;
    }

}
