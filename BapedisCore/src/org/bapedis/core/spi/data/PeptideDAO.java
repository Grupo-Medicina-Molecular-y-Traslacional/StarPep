/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.data;

import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.QueryModel;
import org.gephi.graph.api.GraphModel;

/**
 *
 * @author loge
 */
public interface PeptideDAO {
    PeptideAttribute ID = new PeptideAttribute("id", "ID", String.class);
    PeptideAttribute SEQ = new PeptideAttribute("seq", "Sequence", String.class);
    PeptideAttribute LENGHT = new PeptideAttribute("length", "Length", Integer.class);
    
    AttributesModel getPeptides(QueryModel queryModel, GraphModel graphModel);
}
