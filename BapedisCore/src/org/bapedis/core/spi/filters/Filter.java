/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters;

import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.Algorithm;

/**
 *
 * @author loge
 */
public interface Filter {
    /*Return a variant of the display name containing HTML
    * or null; it should not return the non-HTML display name.
    */
    String getHTMLDisplayName();
    
    //Gets the localized display name of this filter
    String getDisplayName();
    
    // The preprocessing algorithm to data. It can be null.
    Algorithm getPreprocessing(Peptide[] targets);
    
    boolean accept(Peptide peptide);
    
    
    public FilterFactory getFactory(); 
}
