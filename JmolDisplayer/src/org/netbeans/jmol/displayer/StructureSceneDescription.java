/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jmol.displayer;

import java.awt.Image;
import java.io.Serializable;
import org.bapedis.core.model.Peptide;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
public class StructureSceneDescription implements MultiViewDescription, Serializable{
    private final Peptide peptide;
    private final String code;

    public StructureSceneDescription(Peptide peptide, String code) {
        this.peptide = peptide;
        this.code = code;
    }
    
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public String getDisplayName() {
        return code;
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public String preferredID() {
        return code;
    }

    @Override
    public MultiViewElement createElement() {
        return new StructureScene(peptide, code);
    }
    
}
