/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Loge
 */
public class Cluster {
    protected final Peptide centroid;
    protected final List<Peptide> members;

    public Cluster(Peptide centroid) {
        this.centroid = centroid;
        members = new LinkedList<>();
    }
    
    public void addMember(Peptide peptide){
        members.add(peptide);
    }

    public Peptide getCentroid() {
        return centroid;
    }

    public List<Peptide> getMembers() {
        return members;
    }
        
}
