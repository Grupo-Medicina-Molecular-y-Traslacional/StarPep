/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.ui;

import javax.swing.JPanel;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public interface StructureWindowController {

    void openStructureWindow(Peptide peptide, String code);
    
    JPanel createPanelView(JPanel parent, String code);
}
