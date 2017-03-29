/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.beans.PropertyChangeListener;

/**
 *
 * @author loge
 */
public interface ValidationSupportUI {

    public final String VALID_STATE = "valid_state";

    boolean isValidState();

    void finishSettings();

    void cancelSettings();

    void addValidStateListener(PropertyChangeListener listener);

    void removeValidStateListener(PropertyChangeListener listener);
}
