/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters;

import javax.swing.JPanel;
import org.bapedis.core.ui.components.ValidationSupportUI;

/**
 *
 * @author loge
 */
public interface FilterSetupUI extends ValidationSupportUI {

    JPanel getEditPanel(Filter filter);

}
