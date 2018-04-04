/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi;

import javax.swing.JPanel;

/**
 *
 * @author loge
 */
public interface SimilarityMeasureSetupUI {
       public JPanel getSettingPanel(SimilarityMeasure simMeasure); 
}
