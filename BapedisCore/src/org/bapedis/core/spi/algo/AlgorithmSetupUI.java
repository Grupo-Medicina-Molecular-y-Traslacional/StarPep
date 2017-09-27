/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 *
 * @author loge
 */
public interface AlgorithmSetupUI {   

    /**
     * A <code>AlgorithmSetupUI</code> can have a optional settings panel, that will be
     * displayed instead of the property sheet.
     * @param algo the algorithm that require a simple panel
     * @return A simple settings panel for <code>algorithm</code> or
     * <code>null</code>
     */
    public JPanel getEditPanel(Algorithm algo);  
    
}
