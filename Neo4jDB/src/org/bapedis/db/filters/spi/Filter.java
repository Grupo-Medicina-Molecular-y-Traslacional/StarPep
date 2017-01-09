/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.spi;

import org.bapedis.db.model.NeoPeptide;

/**
 *
 * @author loge
 */
public interface Filter {
    String getDisplayName();
    boolean accept(NeoPeptide peptide);
}
