/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.neo4j.graphdb.Label;

/**
 *
 * @author cicese
 */
public enum MyLabel implements Label {
    Peptide,
    BioCategory,
    Name,
    Origin,
    Target,
    Database,
    Literature,
    CrossRef
}
