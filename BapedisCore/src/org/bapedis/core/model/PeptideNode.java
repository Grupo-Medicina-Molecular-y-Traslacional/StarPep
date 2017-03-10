/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class PeptideNode extends AbstractNode {
    protected Peptide peptide;
    protected boolean accepted;
    
    public PeptideNode(Peptide peptide) {
        this(peptide, Children.LEAF, Lookups.singleton(peptide));
        accepted = true;
    }
    
    public PeptideNode(Peptide peptide, Children children, Lookup lookup) {
        super(children, lookup);
        this.peptide = peptide;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    @Override
    public String getDisplayName() {
        return peptide.getId(); 
    }
    
    public PeptideNode(Peptide peptide, Children children) {
        this(peptide, children, Lookups.singleton(peptide));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/bapedis/core/resources/molecule.png", true);
    }

    public Peptide getPeptide() {
        return peptide;
    }
 
}
