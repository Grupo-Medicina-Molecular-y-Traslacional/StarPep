/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.statistics.plugin;

import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class InvalidNetworkMeasureException extends Exception {
    private final String measure;
    protected final NotifyDescriptor errorND;

    public InvalidNetworkMeasureException(String measure) {
        this.measure = measure;
        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(InvalidNetworkMeasureException.class, measure), NotifyDescriptor.ERROR_MESSAGE);
    }

    public NotifyDescriptor getErrorND() {
        return errorND;
    }                
            
}
