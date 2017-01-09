/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

/**
 *
 * @author loge
 */
public class PeptideModel extends AttributesModel{
        
    
    public void addPeptide(Peptide peptide){
        objAttrsNode.add(new PeptideNode(peptide));
    }
    
    public Peptide[] getPeptides(){
        Peptide[] peptides = new Peptide[objAttrsNode.size()];
        int cursor = 0;
        for(ObjectAttributesNode pNode: objAttrsNode){
            peptides[cursor++] = pNode.getLookup().lookup(Peptide.class);
        }
        return peptides;
    }
    
}
