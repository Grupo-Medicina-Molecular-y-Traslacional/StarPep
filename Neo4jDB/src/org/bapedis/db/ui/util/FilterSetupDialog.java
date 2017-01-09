/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.filters.spi.FilterSetupUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author loge
 */
public class FilterSetupDialog implements PropertyChangeListener{
    protected DialogDescriptor dd;
    
    public boolean setup(Filter filter, FilterSetupUI setupUI, String title){        
        dd = new DialogDescriptor(setupUI.getEditPanel(filter), title);
        boolean listenValidState = !setupUI.isValidState();
        dd.setValid(setupUI.isValidState());
        if (listenValidState) {
            setupUI.addValidStateListener(this);
        }
        if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
            setupUI.finishSettings();
            return true;
        } else {
            setupUI.cancelSettings();
        }
        if (listenValidState) {
            setupUI.removeValidStateListener(this);
        }        
        return false;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == FilterSetupUI.VALID_STATE && dd != null) {
            boolean validState = (boolean) evt.getNewValue();
            dd.setValid(validState);
        }
    }
    
}
