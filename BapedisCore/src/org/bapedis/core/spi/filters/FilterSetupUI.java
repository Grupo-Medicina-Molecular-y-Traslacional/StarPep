/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters;

import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 *
 * @author loge
 */
public interface FilterSetupUI {
    public final String VALID_STATE = "valid_state";
    
    JPanel getEditPanel(Filter filter);
    void finishSettings();
    void cancelSettings();
    boolean isValidState();
    void addValidStateListener(PropertyChangeListener listener);
    void removeValidStateListener(PropertyChangeListener listener);
}
