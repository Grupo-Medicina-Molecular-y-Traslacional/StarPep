/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui;

import java.awt.Image;
import java.io.Serializable;
import org.bapedis.db.model.NeoPeptide;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author bioadmin
 */
public class NeoPeptideTextViewDescription implements MultiViewDescription, Serializable  {
    private final NeoPeptide neoPeptide; 

    public NeoPeptideTextViewDescription(NeoPeptide neoPeptide) {
        this.neoPeptide = neoPeptide;
    }
        
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(NeoPeptideTextViewDescription.class, "NeoPeptideTextView.title");
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
        return "TextView";
    }

    @Override
    public MultiViewElement createElement() {
        return new NeoPeptideTextView(neoPeptide);
    }
    
}
