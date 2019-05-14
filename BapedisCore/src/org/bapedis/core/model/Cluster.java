/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author Loge
 */
public class Cluster {
    protected final int id;
    protected double percentage;
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
        return members.size();
    }
        
    public void addMember(Peptide peptide){
        members.add(peptide);
    }

    public List<Peptide> getMembers() {
        return Collections.unmodifiableList(members);
    }           

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
        
}
