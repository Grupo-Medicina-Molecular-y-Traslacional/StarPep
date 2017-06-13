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
 * @author cicese
 */
public interface AlgorithmSetupUI {
    
    /**
     * The description of the algorithm purpose.
     * @return  a description snippet for the algorithm
     */
    public String getDescription();

    /**
     * The icon that represents the algorithm action.
     * @return  a icon for this particular algorithm
     */
    public Icon getIcon();

    /**
     * A <code>AlgorithmSetupUI</code> can have a optional settings panel, that will be
     * displayed instead of the property sheet.
     * @param algo the algorithm that require a simple panel
     * @return A simple settings panel for <code>algorithm</code> or
     * <code>null</code>
     */
    public JPanel getEditPanel(Algorithm algo);

    /**
     * An appraisal of quality for this algorithm. The rank must be between 1 and
     * 5. The rank will be displayed tousers to help them to choose a suitable
     * algorithm. Return -1 if you don't want to display a rank.
     * @return an integer between 1 and 5 or -1 if you don't want to show a rank
     */
    public int getQualityRank();

    /**
     * An appraisal of speed for this algorithm. The rank must be between 1 and
     * 5. The rank will be displayed tousers to help them to choose a suitable
     * algorithm. Return -1 if you don't want to display a rank.
     * @return an integer between 1 and 5 or -1 if you don't want to show a rank
     */
    public int getSpeedRank();    
    
}
