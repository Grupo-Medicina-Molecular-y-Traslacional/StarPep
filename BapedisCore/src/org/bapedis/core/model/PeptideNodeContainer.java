/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 * A node container for instances of PeptideNode.
 * @author loge
 */
public class PeptideNodeContainer extends Index.ArrayChildren {

    protected List<PeptideNode> list;

    public PeptideNodeContainer() {
        list = new LinkedList<>();
    }

    @Override
    protected List<Node> initCollection() {
        List<Node> nodes = new ArrayList<>(list.size());
        for (PeptideNode node : list) {
            nodes.add(node);
        }
        return nodes;
    }

    public void addPeptideNode(PeptideNode node) {
        list.add(node);
    }
    
//    
//    public Peptide[] getPeptides(){
//        Peptide[] peptides = new Peptide[objAttrsNode.size()];
//        int cursor = 0;
//        for(ObjectAttributesNode pNode: objAttrsNode){
//            peptides[cursor++] = pNode.getLookup().lookup(Peptide.class);
//        }
//        return peptides;
//    }

}
