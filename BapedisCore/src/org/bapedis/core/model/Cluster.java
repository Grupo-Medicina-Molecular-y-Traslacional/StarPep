/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Loge
 */
public class Cluster {
    protected final int id;
    protected Peptide centroid;
    protected final List<Peptide> members;

    public Cluster(int id){
        this.id = id;
        members = new LinkedList<>();
    }           

    public int getId() {
        return id;
    }

    public Peptide getCentroid() {
        return centroid;
    }

    public void setCentroid(Peptide centroid) {
        this.centroid = centroid;
    }        
    
    public int getSize(){
        return members.size() + (centroid != null?1:0);
    }
        
    public void addMember(Peptide peptide){
        members.add(peptide);
    }

    public List<Peptide> getMembers() {
        return Collections.unmodifiableList(members);
    }
        
}
