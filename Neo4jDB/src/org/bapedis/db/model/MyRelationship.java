/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author cicese
 */
public enum MyRelationship implements RelationshipType {
    is_a, 
    instance_of,
    named,
    produced_by,
    active_against,
    compiled_in,
    referenced_by,
    linked_to    
}
