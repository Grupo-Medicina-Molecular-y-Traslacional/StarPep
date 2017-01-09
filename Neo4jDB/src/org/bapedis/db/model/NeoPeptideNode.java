/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import javax.swing.Action;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.db.ui.actions.ShowPeptideDetails;

/**
 *
 * @author loge
 */
public class NeoPeptideNode extends PeptideNode{
    
    public NeoPeptideNode(NeoPeptide peptide) {
        super(peptide);
    }  

    @Override
    public Action[] getActions(boolean context) {
        Action[] nodeActions = new Action[1];
        nodeActions[0] = new ShowPeptideDetails();
        return nodeActions;        
    }    
       
}
