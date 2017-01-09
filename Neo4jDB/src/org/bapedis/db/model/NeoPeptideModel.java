/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideModel;

/**
 *
 * @author loge
 */
public class NeoPeptideModel extends PeptideModel {    
    @Override
    public void addPeptide(Peptide peptide) {
        objAttrsNode.add(new NeoPeptideNode((NeoPeptide)peptide)); 
    } 
    
}
