/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import org.bapedis.core.io.Exporter;
import org.bapedis.core.model.AttributesModel;

/**
 *
 * @author loge
 */
public class MetadataExporter implements Exporter {
    protected final AttributesModel attrModel;

    public MetadataExporter(AttributesModel attrModel) {
        this.attrModel = attrModel;
    }
    
    @Override
    public void exportTo(File file) throws Exception {
        
    }
    
}
