/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.jmolDisplayer.desktop;

import java.awt.Image;
import java.io.Serializable;
import org.bapedis.core.model.Peptide;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
public class StructureSceneDescription implements MultiViewDescription, Serializable{

    private final Peptide peptide;
    
    public StructureSceneDescription(Peptide peptide) {
        this.peptide = peptide;
    }
    
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public String getDisplayName() {
        return peptide.getID();
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
        return NbBundle.getMessage(StructureSceneDescription.class, "CTL_StructureTC_title");
    }

    @Override
    public MultiViewElement createElement() {
        return new StructureScene();
    }
    
}
