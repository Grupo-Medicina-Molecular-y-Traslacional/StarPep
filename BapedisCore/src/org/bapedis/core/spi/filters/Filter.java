/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters;

import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public interface Filter {
    String getDisplayName();
    boolean accept(Peptide peptide);
    public FilterFactory getFactory(); 
}
