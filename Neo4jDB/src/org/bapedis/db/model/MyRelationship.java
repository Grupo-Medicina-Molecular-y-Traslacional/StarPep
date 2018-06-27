/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author loge
 */
public enum MyRelationship implements RelationshipType {
    is_a, 
    related_to,
//    named,
    produced_by,
    assessed_against,
    compiled_in,
//    referenced_by,
    linked_to    
}
