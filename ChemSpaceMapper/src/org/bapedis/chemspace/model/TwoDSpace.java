/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import javax.vecmath.Vector2f;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class TwoDSpace {
    protected final Peptide[] peptides;
    protected final Vector2f[] positions;

    public TwoDSpace(Peptide[] peptides, Vector2f[] positions) {
        this.peptides = peptides;
        this.positions = positions;
    }

    public Peptide[] getPeptides() {
        return peptides;
    }

    public Vector2f[] getPositions() {
        return positions;
    }
        
}
