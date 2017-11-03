/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.data;

import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.QueryModel;
import org.gephi.graph.api.GraphModel;

/**
 *
 * @author loge
 */
public interface PeptideDAO {
    
    AttributesModel getPeptides(QueryModel queryModel, GraphModel graphModel);
}
