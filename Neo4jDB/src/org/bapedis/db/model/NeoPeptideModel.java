/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class NeoPeptideModel extends AttributesModel {    
    @Override
    public void addPeptide(Peptide peptide) {
        container.addPeptideNode(new NeoPeptideNode((NeoPeptide)peptide)); 
    } 
    
}
