/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.spi.ui.StructureWindowController;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class View3DStructure extends AbstractAction {

    private static final StructureWindowController strucController = Lookup.getDefault().lookup(StructureWindowController.class);
    private final Peptide peptide;
    private final boolean display;

    public View3DStructure(Peptide peptide) {
        this.peptide = peptide;
        String text = NbBundle.getMessage(View3DStructure.class, "CTL_View3DStructure");
        display = hasStructure();
        if (display) {
            putValue(NAME, NbBundle.getMessage(View3DStructure.class, "CTL_View3DStructure"));
        } else {
            putValue(NAME, NbBundle.getMessage(View3DStructure.class, "CTL_View3DStructure.none"));
        }
    }
    
    @Override
    public boolean isEnabled() {
        return display;
    }    

    private boolean hasStructure() {
        String[] crossRefs = peptide.getAnnotationValues(StarPepAnnotationType.CROSSREF);
        StringTokenizer tokenizer;
        for (String crossRef : crossRefs) {
            tokenizer = new StringTokenizer(crossRef, ":");
            if (tokenizer.hasMoreTokens() && tokenizer.nextToken().equals("PDB")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        strucController.openStructureWindow(peptide);
    }


}
