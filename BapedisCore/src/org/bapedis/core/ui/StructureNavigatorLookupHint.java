/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import org.netbeans.spi.navigator.NavigatorLookupHint;

/**
 *
 * @author loge
 */
public class StructureNavigatorLookupHint implements NavigatorLookupHint{

    @Override
    public String getContentType() {
        return "peptide/structure";
    }
    
}
