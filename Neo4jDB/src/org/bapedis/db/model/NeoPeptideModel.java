/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.GraphModel;

/**
 *
 * @author loge
 */
public class NeoPeptideModel extends AttributesModel {   
    protected GraphModel graphModel;

    public GraphModel getGraphModel() {
        return graphModel;
    }

    public void setGraphModel(GraphModel graphModel) {
        this.graphModel = graphModel;
    }
    
    
    @Override
    public void addPeptide(Peptide peptide) {
        container.addPeptideNode(new NeoPeptideNode((NeoPeptide)peptide)); 
    } 
    
}
