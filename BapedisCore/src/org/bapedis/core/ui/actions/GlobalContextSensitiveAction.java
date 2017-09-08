/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.util.Collection;
import javax.swing.AbstractAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
public abstract class GlobalContextSensitiveAction<T> extends AbstractAction implements LookupListener{

    protected Lookup.Result<T> lkpResult;
    protected Class<T> contextClass;

    public GlobalContextSensitiveAction(Class<T> contextClass, Lookup lookup) {
        this.contextClass = contextClass;
        lkpResult = lookup.lookupResult(contextClass);
        lkpResult.addLookupListener(this);        
        T context = lookup.lookup(contextClass);        
        setEnabled(context != null);
    }
    
    public GlobalContextSensitiveAction(Class<T> contextClass) {
        this(contextClass, Utilities.actionsGlobalContext());
    }    

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends T> context = lkpResult.allInstances();
        setEnabled(!context.isEmpty());
    }

}
