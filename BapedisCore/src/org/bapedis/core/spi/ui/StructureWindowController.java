/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.ui;

import org.bapedis.core.model.Peptide;
import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
public interface StructureWindowController {
    
    TopComponent getStructureWindow();

    void openStructureWindow(Peptide peptide);
    
}
