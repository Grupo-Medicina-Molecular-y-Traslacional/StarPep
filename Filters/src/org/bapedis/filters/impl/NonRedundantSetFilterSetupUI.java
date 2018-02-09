/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.bapedis.core.ui.components.SequenceAlignmentPanel;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public class NonRedundantSetFilterSetupUI implements FilterSetupUI {
    
    protected NonRedundantSetFilter filter;
    protected SequenceAlignmentModel alignmentModel;
    
    @Override
    public JPanel getEditPanel(Filter filter) {
        this.filter = (NonRedundantSetFilter) filter;
        try {
            alignmentModel = (SequenceAlignmentModel) this.filter.getAlignmentModel().clone();
        } catch (CloneNotSupportedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return new SequenceAlignmentPanel(alignmentModel);
    }

    @Override
    public boolean isValidState() {
        return true;
    }

    @Override
    public void saveSettings() {
        filter.setAlignmentModel(alignmentModel);
    }

    @Override
    public void cancelSettings() {
        filter = null;
    }

    @Override
    public void addValidStateListener(PropertyChangeListener listener) {        
    }

    @Override
    public void removeValidStateListener(PropertyChangeListener listener) {
    }

}
