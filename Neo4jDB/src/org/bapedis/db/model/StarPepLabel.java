/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.neo4j.graphdb.Label;

/**
 *
 * @author loge
 */
public enum StarPepLabel implements Label {
    Peptide,
    Function,
//    Name,
    Origin,
    Target,
    Database,
//    Literature,
    CrossRef
}
