/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io;

import java.io.File;

/**
 *
 * @author loge
 */
public interface Exporter {
    
    void exportTo(File file) throws Exception;
    
}
