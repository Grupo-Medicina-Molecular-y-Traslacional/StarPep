/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphView;

/**
 *
 * @author loge
 */
public class NeoPeptideModel extends AttributesModel {

    protected final Graph graph;
    protected GraphView currentView;

    public NeoPeptideModel(Graph graph) {
        this.graph = graph;
        currentView = graph.getView();
    }

    public Graph getGraph() {
        return graph;
    }

    @Override
    public void addPeptide(Peptide peptide) {
        container.addPeptideNode(new NeoPeptideNode((NeoPeptide) peptide));
    }
    
    public GraphView getCurrentView() {
        return currentView;
    }

    public void setCurrentView(GraphView currentView) {
        this.currentView = currentView;
    }

}
