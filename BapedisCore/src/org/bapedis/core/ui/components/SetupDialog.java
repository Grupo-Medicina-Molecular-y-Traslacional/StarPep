/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author loge
 */
public class SetupDialog implements PropertyChangeListener {

    protected DialogDescriptor dd;

    public boolean setup(JPanel panel, ValidationSupportUI setupUI, String title) {
        dd = new DialogDescriptor(panel, title);
        dd.setValid(setupUI.isValidState());
        
        setupUI.addValidStateListener(this);
        boolean flag;
        if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
            setupUI.saveSettings();
            flag = true;
        } else {
            flag = false;
            setupUI.cancelSettings();
        }        
        setupUI.removeValidStateListener(this);
        return flag;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ValidationSupportUI.VALID_STATE) && dd != null) {
            boolean validState = (boolean) evt.getNewValue();
            dd.setValid(validState);
        }
    }

}
