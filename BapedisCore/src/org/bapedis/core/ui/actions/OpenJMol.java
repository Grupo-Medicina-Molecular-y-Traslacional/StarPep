/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.ui.StructureWindowController;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class OpenJMol extends AbstractAction{
    private static StructureWindowController strucController = Lookup.getDefault().lookup(StructureWindowController.class);
    private final String code;
    private final Peptide peptide;

    public OpenJMol(Peptide peptide, String code) {
        this.peptide = peptide;
        this.code = code;        
        putValue(NAME, code != null? code: NbBundle.getMessage(OpenJMol.class, "CTL_OpenJMol.none"));
    }

    @Override
    public boolean isEnabled() {
        return code != null;
    }
            
    @Override
    public void actionPerformed(ActionEvent e) {
        strucController.openStructureWindow(peptide, code);
    }
    
}
